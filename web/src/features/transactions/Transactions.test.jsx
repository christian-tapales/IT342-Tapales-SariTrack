import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import Transactions from './Transactions';
import api from '../../core/api/api';

// Mock API
vi.mock('../../core/api/api', () => ({
  default: {
    get: vi.fn(),
  }
}));

// Mock jsPDF
vi.mock('jspdf', () => ({
  default: vi.fn().mockImplementation(() => ({
    setFontSize: vi.fn(),
    text: vi.fn(),
    setTextColor: vi.fn(),
    save: vi.fn(),
  })),
}));

// Mock autoTable
vi.mock('jspdf-autotable', () => ({
  default: vi.fn(),
}));

global.URL.createObjectURL = vi.fn();

describe('Transactions Component', () => {
  const mockUser = { id: 1, name: 'Vendor Juan' };
  const mockOrders = [
    { id: 101, timestamp: '2024-05-14T10:00:00', totalAmount: 150.0, status: 'PAID', items: [{ productId: 1, quantity: 2, priceAtSale: 75.0 }] },
    { id: 102, timestamp: '2024-05-14T11:00:00', totalAmount: 50.0, status: 'CANCELLED', items: [{ productId: 2, quantity: 1, priceAtSale: 50.0 }] },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    api.get.mockResolvedValue({ data: mockOrders });
  });

  it('renders transactions list', async () => {
    render(<Transactions user={mockUser} />);
    
    await waitFor(() => {
      expect(screen.getByText('#101')).toBeInTheDocument();
      expect(screen.getByText('#102')).toBeInTheDocument();
      expect(screen.getByText('₱150.00')).toBeInTheDocument();
    });
  });

  it('filters by status', async () => {
    render(<Transactions user={mockUser} />);
    
    await waitFor(() => {
      expect(screen.getByText('#102')).toBeInTheDocument();
    });

    // Click PAID filter
    fireEvent.click(screen.getByRole('button', { name: 'PAID' }));
    
    expect(screen.getByText('#101')).toBeInTheDocument();
    expect(screen.queryByText('#102')).not.toBeInTheDocument();
  });

  it('filters by search term (Order ID)', async () => {
    render(<Transactions user={mockUser} />);
    
    await waitFor(() => {
      expect(screen.getByText('#101')).toBeInTheDocument();
    });

    const searchInput = screen.getByPlaceholderText(/Search Order ID/i);
    fireEvent.change(searchInput, { target: { value: '102' } });
    
    expect(screen.getByText('#102')).toBeInTheDocument();
    expect(screen.queryByText('#101')).not.toBeInTheDocument();
  });

  it('opens details modal', async () => {
    render(<Transactions user={mockUser} />);
    
    await waitFor(() => {
      const detailsButtons = screen.getAllByText('Details');
      fireEvent.click(detailsButtons[0]); // First order details
    });
    
    expect(screen.getByText('Order #101')).toBeInTheDocument();
    expect(screen.getByText('2 x ₱75.00')).toBeInTheDocument();
    expect(screen.getByText('Grand Total')).toBeInTheDocument();
  });
});
