import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import Dashboard from './Dashboard';
import api from '../../core/api/api';

// Mock API
vi.mock('../../core/api/api', () => ({
  default: {
    get: vi.fn(),
  }
}));

// Mock useNavigate
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

// Mock Recharts to avoid layout issues in tests
vi.mock('recharts', () => ({
  ResponsiveContainer: ({ children }) => <div>{children}</div>,
  AreaChart: ({ children }) => <div>{children}</div>,
  Area: () => <div />,
  XAxis: () => <div />,
  YAxis: () => <div />,
  CartesianGrid: () => <div />,
  Tooltip: () => <div />,
}));

describe('Dashboard Component', () => {
  const mockVendor = { id: 1, name: 'Vendor Juan', role: 'VENDOR' };
  const mockAdmin = { id: 99, name: 'Super Admin', role: 'ADMIN' };

  const mockStats = {
    todaySales: 1500.50,
    lowStockCount: 5,
    totalDebt: 200.00,
    recentTransactions: [
      { id: 1, timestamp: new Date().toISOString(), totalAmount: 100, status: 'PAID' }
    ],
    topSelling: [{ name: 'Coke', sold: 10 }],
    weeklySales: [{ day: 'Mon', sales: 500 }]
  };

  const mockPlatformStats = {
    totalPlatformSales: 50000.00,
    totalVendors: 10,
    systemHealth: 99.9,
    weeklySales: [{ day: 'Mon', sales: 10000 }],
    topVendors: [{ name: 'Store A', sales: 5000 }],
    recentTransactions: [
      { id: 101, timestamp: new Date().toISOString(), totalAmount: 500, status: 'PAID', vendorId: 1 }
    ]
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders vendor dashboard correctly', async () => {
    api.get.mockResolvedValue({ data: mockStats });
    
    render(
      <MemoryRouter>
        <Dashboard user={mockVendor} />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByRole('heading', { name: /Kumusta, Vendor!/i })).toBeInTheDocument();
      expect(screen.getByText('₱1,500.50')).toBeInTheDocument();
      expect(screen.getByText('5 Items')).toBeInTheDocument();
      expect(screen.getByText('₱200.00')).toBeInTheDocument();
    });
  });

  it('renders admin dashboard with platform stats', async () => {
    // Admin triggers two fetches
    api.get.mockImplementation((url) => {
      if (url.includes('/admin/stats')) return Promise.resolve({ data: mockPlatformStats });
      return Promise.resolve({ data: mockStats });
    });

    render(
      <MemoryRouter>
        <Dashboard user={mockAdmin} />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/Platform Control/i)).toBeInTheDocument();
      expect(screen.getByText('₱50,000.00')).toBeInTheDocument();
      expect(screen.getByText('10 Stores')).toBeInTheDocument();
      expect(screen.getByText('99.9%')).toBeInTheDocument();
    });
  });

  it('navigates to different features when clicking action cards', async () => {
    api.get.mockResolvedValue({ data: mockStats });
    
    render(
      <MemoryRouter>
        <Dashboard user={mockVendor} />
      </MemoryRouter>
    );

    await waitFor(() => {
        const posBtn = screen.getByText('POS');
        fireEvent.click(posBtn);
        expect(mockNavigate).toHaveBeenCalledWith('/sales');
    });
  });

  it('handles empty stats gracefully', async () => {
    api.get.mockResolvedValue({ data: { ...mockStats, recentTransactions: [], topSelling: [] } });

    render(
      <MemoryRouter>
        <Dashboard user={mockVendor} />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('No recent activity.')).toBeInTheDocument();
      expect(screen.getByText('No sales data yet.')).toBeInTheDocument();
    });
  });
});
