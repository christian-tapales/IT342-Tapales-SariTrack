import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import GlobalProducts from './GlobalProducts';
import api from '../../core/api/api';

// Mock the API module
vi.mock('../../core/api/api', () => ({
  default: {
    get: vi.fn(),
  }
}));

// Mock URL and Blob for CSV export
global.URL.createObjectURL = vi.fn();
global.URL.revokeObjectURL = vi.fn();

describe('GlobalProducts Component', () => {
  const mockStats = { totalSKUs: 100, totalStock: 5000 };
  const mockProducts = [
    { id: 1, name: 'Coke', category: 'Drinks', stockQuantity: 50, price: 20.0, vendorId: 1 },
    { id: 2, name: 'Lucky Me', category: 'Noodles', stockQuantity: 5, price: 15.0, vendorId: 1 },
    { id: 3, name: 'Out of Stock Item', category: 'Misc', stockQuantity: 0, price: 10.0, vendorId: 2 },
  ];
  const mockVendors = [
    { id: 1, name: 'Juan Store' },
    { id: 2, name: 'Maria Store' },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    api.get.mockImplementation((url) => {
      if (url === '/admin/stats') return Promise.resolve({ data: mockStats });
      if (url === '/products') return Promise.resolve({ data: mockProducts });
      if (url === '/admin/vendors/analytics') return Promise.resolve({ data: mockVendors });
      return Promise.reject(new Error('Unknown URL'));
    });
  });

  it('renders stats correctly from API', async () => {
    render(<GlobalProducts />);

    await waitFor(() => {
      expect(screen.getByText('100')).toBeInTheDocument(); // totalSKUs
      expect(screen.getByText('5,000')).toBeInTheDocument(); // totalStock
    });

    // Logic-based stats:
    // Low Stock (stock < 10) = 1 (Lucky Me)
    expect(screen.getAllByText(/Low Stock/i).length).toBeGreaterThan(0);
    
    // Out of Stock = 1
    expect(screen.getAllByText(/Out of Stock/i).length).toBeGreaterThan(0);
  });

  it('filters products correctly when searching', async () => {
    render(<GlobalProducts />);

    await waitFor(() => {
      expect(screen.getByText('Coke')).toBeInTheDocument();
    });

    const searchInput = screen.getByPlaceholderText(/Search by product name/i);
    
    // Search for Lucky Me
    fireEvent.change(searchInput, { target: { value: 'Lucky' } });
    expect(screen.getByText('Lucky Me')).toBeInTheDocument();
    expect(screen.queryByText('Coke')).not.toBeInTheDocument();

    // Search for vendor Juan
    fireEvent.change(searchInput, { target: { value: 'Juan' } });
    expect(screen.getByText('Coke')).toBeInTheDocument();
  });

  it('triggers CSV export when clicking button', async () => {
    const clickSpy = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
    
    render(<GlobalProducts />);

    await waitFor(() => {
      expect(screen.getByText('Export CSV')).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText('Export CSV'));
    
    expect(clickSpy).toHaveBeenCalled();
    clickSpy.mockRestore();
  });
});
