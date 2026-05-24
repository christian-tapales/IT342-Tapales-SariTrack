import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import PointOfSale from './PointOfSale';
import api from '../../core/api/api';
import toast from 'react-hot-toast';

// Mock the API module
vi.mock('../../core/api/api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  }
}));

// Mock react-hot-toast
vi.mock('react-hot-toast', () => ({
  default: {
    success: vi.fn(),
    error: vi.fn(),
  }
}));

// Mock window.alert
global.alert = vi.fn();

describe('PointOfSale Component', () => {
  const mockUser = { id: 1, name: 'Vendor Juan' };
  const mockProducts = [
    { id: 1, name: 'Coke', price: 20.0, stockQuantity: 10, imageUrl: null },
    { id: 2, name: 'Bread', price: 5.0, stockQuantity: 2, imageUrl: null },
  ];
  const mockCustomers = [
    { id: 'c1', fullName: 'Customer A' },
  ];
  const mockRates = { PHP: 1.0, USD: 0.018 };

  beforeEach(() => {
    vi.clearAllMocks();
    api.get.mockImplementation((url) => {
      if (url.includes('/products')) return Promise.resolve({ data: mockProducts });
      if (url.includes('/customers')) return Promise.resolve({ data: mockCustomers });
      if (url.includes('/currency/rates')) return Promise.resolve({ data: mockRates });
      return Promise.reject(new Error('Unknown URL'));
    });
  });

  it('renders products and handles adding to cart', async () => {
    render(<PointOfSale user={mockUser} />);

    // Wait for products to load
    await waitFor(() => {
      expect(screen.getByText('Coke')).toBeInTheDocument();
    });

    // Add Coke to cart
    fireEvent.click(screen.getByText('Coke'));

    // Verify cart updates
    expect(screen.getByText('New Transaction')).toBeInTheDocument();
    expect(screen.getAllByText(/Coke/i).length).toBeGreaterThan(1); // One in list, one in cart
    expect(screen.getAllByText('₱20.00').length).toBeGreaterThan(0); // Total
  });

  it('handles quantity updates and stock limits', async () => {
    render(<PointOfSale user={mockUser} />);

    await waitFor(() => {
      fireEvent.click(screen.getByText('Bread'));
    });

    // Cart shows Bread with qty 1
    const qtyInput = screen.getByDisplayValue('1');
    expect(qtyInput).toBeInTheDocument();

    // Increment qty to 2
    const plusButton = screen.getByRole('button', { name: /Increase quantity/i });
    fireEvent.click(plusButton);
    expect(screen.getByDisplayValue('2')).toBeInTheDocument();

    // Increment again (Bread only has 2 stock)
    fireEvent.click(plusButton);
    expect(screen.getByDisplayValue('2')).toBeInTheDocument(); // Should still be 2
    expect(screen.getAllByText('₱10.00').length).toBeGreaterThan(0); // 2 * 5.00
  });

  it('removes item from cart', async () => {
    render(<PointOfSale user={mockUser} />);

    await waitFor(() => {
      fireEvent.click(screen.getByText('Coke'));
    });

    const removeButton = screen.getByRole('button', { name: /Remove item/i });
    fireEvent.click(removeButton);

    expect(screen.queryByText('Cart is empty')).not.toBeNull();
  });

  it('processes a cash sale successfully', async () => {
    api.post.mockResolvedValue({ data: { id: 101 } });
    render(<PointOfSale user={mockUser} />);

    await waitFor(() => {
      fireEvent.click(screen.getByText('Coke'));
    });

    // Click Complete Sale
    fireEvent.click(screen.getByText(/Complete Sale/i));

    // Modal should show
    expect(screen.getByText(/Checkout/i)).toBeInTheDocument();

    // Complete anonymous sale
    fireEvent.click(screen.getByText(/Complete Anonymous Sale/i));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/orders', expect.objectContaining({
        status: 'PAID',
        totalAmount: 20.0
      }));
      expect(toast.success).toHaveBeenCalledWith('Sale Completed Successfully!');
    });
  });

  it('processes a debt sale correctly', async () => {
    api.post.mockResolvedValue({ data: { id: 102 } });
    render(<PointOfSale user={mockUser} />);

    await waitFor(() => {
      fireEvent.click(screen.getByText('Coke'));
    });

    // Click Record as Listahan
    fireEvent.click(screen.getByText(/Record as Listahan/i));

    // Modal should show
    expect(screen.getByText(/Listahan Selection/i)).toBeInTheDocument();

    // Select Customer A
    fireEvent.click(screen.getByText('Customer A'));

    // Confirm
    fireEvent.click(screen.getByText(/Confirm Listahan Entry/i));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/orders', expect.objectContaining({
        status: 'DEBT',
        customerId: 'c1'
      }));
    });
  });
});
