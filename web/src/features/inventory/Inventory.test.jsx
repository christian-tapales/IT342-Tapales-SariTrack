import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import Inventory from './Inventory';
import api from '../../core/api/api';

// Mock API
vi.mock('../../core/api/api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  }
}));

// Mock Supabase
vi.mock('../../core/api/supabaseClient', () => ({
  supabase: {
    storage: {
      from: () => ({
        upload: vi.fn(),
        getPublicUrl: vi.fn(() => ({ data: { publicUrl: 'http://test.com/img.jpg' } })),
      }),
    },
  },
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

global.alert = vi.fn();
global.confirm = vi.fn(() => true);

describe('Inventory Component', () => {
  const mockUser = { id: 1, name: 'Vendor Juan' };
  const mockProducts = [
    { id: 1, name: 'Coke', barcode: '12345', price: 20.0, stockQuantity: 10, imageUrl: null },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    api.get.mockResolvedValue({ data: mockProducts });
  });

  it('renders product grid correctly', async () => {
    render(<Inventory user={mockUser} />);
    await waitFor(() => {
      expect(screen.getByText('Coke')).toBeInTheDocument();
      expect(screen.getByText('12345')).toBeInTheDocument();
    });
  });

  it('opens add product modal and submits new product', async () => {
    render(<Inventory user={mockUser} />);
    
    // Click Add Product
    fireEvent.click(screen.getByText(/Add Product/i));
    
    expect(screen.getByText('New Product')).toBeInTheDocument();
    
    // Fill form
    fireEvent.change(screen.getByPlaceholderText('Product Name'), { target: { value: 'Pepsi' } });
    fireEvent.change(screen.getByPlaceholderText('Price (₱)'), { target: { value: '18' } });
    fireEvent.change(screen.getByPlaceholderText('Stock Qty'), { target: { value: '20' } });
    
    // Submit
    api.post.mockResolvedValue({ data: { id: 2 } });
    fireEvent.click(screen.getByText('Save Product'));
    
    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/products', expect.objectContaining({
        name: 'Pepsi',
        price: '18',
        stockQuantity: '20'
      }));
    });
  });

  it('handles barcode lookup', async () => {
    render(<Inventory user={mockUser} />);
    
    fireEvent.click(screen.getByText(/Add Product/i));
    
    const barcodeInput = screen.getByPlaceholderText('Barcode');
    fireEvent.change(barcodeInput, { target: { value: '888' } });
    
    // Mock lookup response
    api.get.mockImplementation((url) => {
      if (url.includes('/lookup/888')) return Promise.resolve({ data: { productName: 'San Miguel' } });
      return Promise.resolve({ data: mockProducts });
    });
    
    // Click Lookup (Wand icon button)
    const lookupButton = screen.getByTitle('Lookup Name via Barcode');
    fireEvent.click(lookupButton);
    
    await waitFor(() => {
      expect(screen.getByDisplayValue('San Miguel')).toBeInTheDocument();
    });
  });

  it('deletes a product after confirmation', async () => {
    render(<Inventory user={mockUser} />);
    
    await waitFor(() => {
      expect(screen.getByText('Coke')).toBeInTheDocument();
    });
    
    // Find button with Delete product aria-label
    const deleteButton = screen.getByRole('button', { name: /Delete product/i });
    
    fireEvent.click(deleteButton);
    
    expect(global.confirm).toHaveBeenCalled();
    await waitFor(() => {
      expect(api.delete).toHaveBeenCalledWith('/products/1?vendorId=1');
    });
  });
});
