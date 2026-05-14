import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import Listahan from './Listahan';
import api from '../../core/api/api';

// Mock API
vi.mock('../../core/api/api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  }
}));

global.alert = vi.fn();

describe('Listahan Component', () => {
  const mockUser = { id: 1, name: 'Vendor Juan' };
  const mockCustomers = [
    { id: 1, fullName: 'Mang Jose', currentDebt: 500.0, status: 'Outstanding', email: 'jose@test.com', lastUpdate: '2024-01-01T10:00:00' },
    { id: 2, fullName: 'Aling Nena', currentDebt: 0, status: 'Paid', email: 'nena@test.com', lastUpdate: '2024-01-02T10:00:00' },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    api.get.mockResolvedValue({ data: mockCustomers });
  });

  it('renders total collectibles and customer list', async () => {
    render(<Listahan user={mockUser} />);
    
    // Total Debt = 500 + 0 = 500
    await waitFor(() => {
      expect(screen.getAllByText('₱500.00').length).toBeGreaterThan(0);
      expect(screen.getByText('Mang Jose')).toBeInTheDocument();
      expect(screen.getByText('Aling Nena')).toBeInTheDocument();
    });
  });

  it('adds a new borrower', async () => {
    render(<Listahan user={mockUser} />);
    
    fireEvent.click(screen.getByText(/Add New Borrower/i));
    
    expect(screen.getByText(/Register/i)).toBeInTheDocument();
    
    fireEvent.change(screen.getByPlaceholderText('e.g. Mang Jose'), { target: { value: 'New Guy' } });
    fireEvent.change(screen.getByPlaceholderText('e.g. mangjose@gmail.com'), { target: { value: 'guy@test.com' } });
    
    api.post.mockResolvedValue({ data: {} });
    fireEvent.click(screen.getByText('Add to Listahan'));
    
    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/customers', expect.objectContaining({
        fullName: 'New Guy',
        email: 'guy@test.com'
      }));
    });
  });

  it('processes payment (Bayad) for a customer', async () => {
    render(<Listahan user={mockUser} />);
    
    await waitFor(() => {
      const bayadButtons = screen.getAllByText('Bayad');
      // The first button belongs to Mang Jose who has debt
      fireEvent.click(bayadButtons[0]);
    });
    
    expect(screen.getByText('Record Payment')).toBeInTheDocument();
    expect(screen.getByText('₱500')).toBeInTheDocument(); // Debt display
    
    const payInput = screen.getByPlaceholderText('Enter amount paid');
    fireEvent.change(payInput, { target: { value: '200' } });
    
    api.post.mockResolvedValue({ data: {} });
    fireEvent.click(screen.getByText('Complete Payment'));
    
    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/customers/1/pay', { amount: 200 });
    });
  });

  it('shows history modal and switches tabs', async () => {
    // Mock history endpoints
    api.get.mockImplementation((url) => {
      if (url.includes('/orders/history')) return Promise.resolve({ data: [] });
      if (url.includes('/payments')) return Promise.resolve({ data: [] });
      return Promise.resolve({ data: mockCustomers });
    });

    render(<Listahan user={mockUser} />);
    
    await waitFor(() => {
      const historyButtons = screen.getAllByRole('button', { name: /View history/i });
      fireEvent.click(historyButtons[0]);
    });
    
    await waitFor(() => {
      expect(screen.getByText('Statement of Account')).toBeInTheDocument();
      expect(screen.getByText('Utang History')).toBeInTheDocument();
      expect(screen.getByText('Payment Logs')).toBeInTheDocument();
    });
    
    // Switch to Payment Logs
    fireEvent.click(screen.getByText('Payment Logs'));
    expect(screen.getByText('No payment logs found.')).toBeInTheDocument();
  });
});
