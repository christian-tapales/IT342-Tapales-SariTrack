import { render, screen, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import Vendors from './Vendors';
import api from '../../core/api/api';

// Mock the API module
vi.mock('../../core/api/api', () => ({
  default: {
    get: vi.fn(),
  }
}));

describe('Vendors Component', () => {
  const mockVendors = [
    { id: 1, name: 'Juan', email: 'juan@test.com', status: 'Active', totalSales: 5000.0, registrationDate: '2024-01-01' },
    { id: 2, name: 'Maria', email: 'maria@test.com', status: 'Top Seller', totalSales: 15000.0, registrationDate: '2024-02-01' },
    { id: 3, name: 'Pedro', email: 'pedro@test.com', status: 'New Member', totalSales: 0, registrationDate: '2024-03-01' },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders loading state initially', () => {
    api.get.mockReturnValue(new Promise(() => {})); // Never resolves
    const { container } = render(<Vendors />);
    // Check for skeletons (animate-pulse classes)
    expect(container.querySelectorAll('.animate-pulse').length).toBeGreaterThan(0);
  });

  it('renders vendor data and stats correctly', async () => {
    api.get.mockResolvedValue({ data: mockVendors });
    
    render(<Vendors />);

    // Wait for data to load
    await waitFor(() => {
      expect(screen.getByText("Juan's Store")).toBeInTheDocument();
    });

    // Check Stats
    expect(screen.getByText('Total Vendors')).toBeInTheDocument();
    expect(screen.getByText('3')).toBeInTheDocument(); // Total count

    // Active Today = status !== 'New Member' (Juan and Maria)
    expect(screen.getByText('Active Today')).toBeInTheDocument();
    expect(screen.getByText('2')).toBeInTheDocument();

    // Top Sellers = status === 'Top Seller' (Maria)
    expect(screen.getByText('Top Sellers')).toBeInTheDocument();
    expect(screen.getByText('1')).toBeInTheDocument();

    // Check table content
    expect(screen.getByText(/juan@test.com/i)).toBeInTheDocument();
    expect(screen.getByText(/maria@test.com/i)).toBeInTheDocument();
    expect(screen.getByText(/₱15000.00/)).toBeInTheDocument();
  });

  it('handles API error gracefully', async () => {
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
    api.get.mockRejectedValue(new Error('API Error'));
    
    render(<Vendors />);

    await waitFor(() => {
      expect(screen.queryByText('juan@test.com')).not.toBeInTheDocument();
    });
    
    expect(consoleSpy).toHaveBeenCalled();
    consoleSpy.mockRestore();
  });
});
