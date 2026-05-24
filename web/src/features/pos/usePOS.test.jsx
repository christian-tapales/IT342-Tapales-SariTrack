import { renderHook, act, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { usePOS } from './usePOS';
import api from '../../core/api/api';
import toast from 'react-hot-toast';

// Mock the API module
vi.mock('../../core/api/api', () => ({
  default: {
    get: vi.fn(),
  }
}));

// Mock react-hot-toast
vi.mock('react-hot-toast', () => ({
  default: {
    error: vi.fn(),
    success: vi.fn()
  }
}));

describe('usePOS Hook', () => {
  const mockUser = { id: 100, name: 'Vendor A' };
  const mockProducts = [
    { id: 1, name: 'Item A', price: 10.0, stockQuantity: 5 },
    { id: 2, name: 'Item B', price: 15.0, stockQuantity: 0 }
  ];
  const mockCustomers = [
    { id: 'c1', fullName: 'Customer X' }
  ];
  const mockRates = { PHP: 1.0, USD: 0.018, EUR: 0.016, JPY: 2.65 };

  beforeEach(() => {
    vi.clearAllMocks();
    api.get.mockImplementation((url) => {
      if (url.includes('/products')) return Promise.resolve({ data: mockProducts });
      if (url.includes('/customers')) return Promise.resolve({ data: mockCustomers });
      if (url.includes('/currency/rates')) return Promise.resolve({ data: mockRates });
      return Promise.reject(new Error('Unknown URL'));
    });
  });

  it('initializes with default states', () => {
    const { result } = renderHook(() => usePOS(null));
    expect(result.current.products).toEqual([]);
    expect(result.current.customers).toEqual([]);
    expect(result.current.cart).toEqual([]);
    expect(result.current.loading).toBe(true);
    expect(result.current.selectedCurrency).toBe('PHP');
    expect(result.current.total).toBe(0);
  });

  it('does not fetch data if user id is missing', () => {
    renderHook(() => usePOS(null));
    expect(api.get).not.toHaveBeenCalled();
  });

  it('fetches products, customers, and currency rates when user is provided', async () => {
    const { result } = renderHook(() => usePOS(mockUser));
    
    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(api.get).toHaveBeenCalledWith('/products?vendorId=100');
    expect(api.get).toHaveBeenCalledWith('/customers?vendorId=100');
    expect(api.get).toHaveBeenCalledWith('/currency/rates');
    expect(result.current.products).toEqual(mockProducts);
    expect(result.current.customers).toEqual(mockCustomers);
  });

  it('handles error gracefully when fetch fails', async () => {
    api.get.mockRejectedValue(new Error('Network Error'));
    const { result } = renderHook(() => usePOS(mockUser));

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.products).toEqual([]);
    expect(result.current.customers).toEqual([]);
  });

  it('addToCart adds product to cart when stock is available', async () => {
    const { result } = renderHook(() => usePOS(mockUser));
    
    await waitFor(() => expect(result.current.loading).toBe(false));

    act(() => {
      result.current.addToCart(mockProducts[0]);
    });

    expect(result.current.cart).toHaveLength(1);
    expect(result.current.cart[0]).toEqual({ ...mockProducts[0], quantity: 1 });
    expect(result.current.total).toBe(10.0);
  });

  it('addToCart increments quantity of existing product if within stock limit', async () => {
    const { result } = renderHook(() => usePOS(mockUser));
    await waitFor(() => expect(result.current.loading).toBe(false));

    act(() => {
      result.current.addToCart(mockProducts[0]);
    });
    act(() => {
      result.current.addToCart(mockProducts[0]);
    });

    expect(result.current.cart[0].quantity).toBe(2);
    expect(result.current.total).toBe(20.0);
  });

  it('addToCart shows error and does not increment if stock limit is reached', async () => {
    const { result } = renderHook(() => usePOS(mockUser));
    await waitFor(() => expect(result.current.loading).toBe(false));

    // Stock is 5
    for (let i = 0; i < 6; i++) {
      act(() => {
        result.current.addToCart(mockProducts[0]);
      });
    }

    expect(result.current.cart[0].quantity).toBe(5);
    expect(toast.error).toHaveBeenCalledWith('Only 5 units available.');
  });

  it('addToCart shows out of stock error if product stock is 0', async () => {
    const { result } = renderHook(() => usePOS(mockUser));
    await waitFor(() => expect(result.current.loading).toBe(false));

    act(() => {
      result.current.addToCart(mockProducts[1]); // Stock is 0
    });

    expect(result.current.cart).toHaveLength(0);
    expect(toast.error).toHaveBeenCalledWith('Out of stock!');
  });

  it('updateQuantity increments quantity of product in cart', async () => {
    const { result } = renderHook(() => usePOS(mockUser));
    await waitFor(() => expect(result.current.loading).toBe(false));

    act(() => {
      result.current.addToCart(mockProducts[0]);
    });

    act(() => {
      result.current.updateQuantity(mockProducts[0].id, 1);
    });

    expect(result.current.cart[0].quantity).toBe(2);
  });

  it('updateQuantity decrements quantity of product in cart', async () => {
    const { result } = renderHook(() => usePOS(mockUser));
    await waitFor(() => expect(result.current.loading).toBe(false));

    act(() => {
      result.current.addToCart(mockProducts[0]);
    });
    act(() => {
      result.current.addToCart(mockProducts[0]);
    });

    expect(result.current.cart[0].quantity).toBe(2);

    act(() => {
      result.current.updateQuantity(mockProducts[0].id, -1);
    });

    expect(result.current.cart[0].quantity).toBe(1);
  });

  it('updateQuantity does not decrement below 1', async () => {
    const { result } = renderHook(() => usePOS(mockUser));
    await waitFor(() => expect(result.current.loading).toBe(false));

    act(() => {
      result.current.addToCart(mockProducts[0]);
    });

    act(() => {
      result.current.updateQuantity(mockProducts[0].id, -1);
    });

    expect(result.current.cart[0].quantity).toBe(1);
  });

  it('updateQuantity does not increment beyond stock limit', async () => {
    const { result } = renderHook(() => usePOS(mockUser));
    await waitFor(() => expect(result.current.loading).toBe(false));

    // Limit is 5
    act(() => {
      result.current.addToCart(mockProducts[0]);
    });
    act(() => {
      result.current.updateQuantity(mockProducts[0].id, 4);
    });
    expect(result.current.cart[0].quantity).toBe(5);

    act(() => {
      result.current.updateQuantity(mockProducts[0].id, 1);
    });
    expect(result.current.cart[0].quantity).toBe(5);
  });

  it('handleQuantityInput updates quantity to custom value', async () => {
    const { result } = renderHook(() => usePOS(mockUser));
    await waitFor(() => expect(result.current.loading).toBe(false));

    act(() => {
      result.current.addToCart(mockProducts[0]);
    });

    act(() => {
      result.current.handleQuantityInput(mockProducts[0].id, '3');
    });

    expect(result.current.cart[0].quantity).toBe(3);
  });

  it('handleQuantityInput sets quantity to empty string on empty input', async () => {
    const { result } = renderHook(() => usePOS(mockUser));
    await waitFor(() => expect(result.current.loading).toBe(false));

    act(() => {
      result.current.addToCart(mockProducts[0]);
    });

    act(() => {
      result.current.handleQuantityInput(mockProducts[0].id, '');
    });

    expect(result.current.cart[0].quantity).toBe('');
  });

  it('handleQuantityInput ignores invalid values like negative numbers', async () => {
    const { result } = renderHook(() => usePOS(mockUser));
    await waitFor(() => expect(result.current.loading).toBe(false));

    act(() => {
      result.current.addToCart(mockProducts[0]);
    });

    act(() => {
      result.current.handleQuantityInput(mockProducts[0].id, '-5');
    });

    expect(result.current.cart[0].quantity).toBe(1);
  });

  it('handleQuantityInput limits quantity to stockQuantity and shows toast error', async () => {
    const { result } = renderHook(() => usePOS(mockUser));
    await waitFor(() => expect(result.current.loading).toBe(false));

    act(() => {
      result.current.addToCart(mockProducts[0]);
    });

    act(() => {
      result.current.handleQuantityInput(mockProducts[0].id, '10');
    });

    expect(result.current.cart[0].quantity).toBe(5);
    expect(toast.error).toHaveBeenCalledWith('Only 5 units available.');
  });

  it('removeFromCart removes product from cart', async () => {
    const { result } = renderHook(() => usePOS(mockUser));
    await waitFor(() => expect(result.current.loading).toBe(false));

    act(() => {
      result.current.addToCart(mockProducts[0]);
    });

    expect(result.current.cart).toHaveLength(1);

    act(() => {
      result.current.removeFromCart(mockProducts[0].id);
    });

    expect(result.current.cart).toHaveLength(0);
    expect(result.current.total).toBe(0);
  });

  it('resetSale clears cart and refetches data', async () => {
    const { result } = renderHook(() => usePOS(mockUser));
    await waitFor(() => expect(result.current.loading).toBe(false));

    act(() => {
      result.current.addToCart(mockProducts[0]);
    });

    expect(result.current.cart).toHaveLength(1);

    act(() => {
      result.current.resetSale();
    });

    expect(result.current.cart).toHaveLength(0);
    expect(api.get).toHaveBeenCalledTimes(6); // 3 (initial) + 3 (refetch)
  });
});
