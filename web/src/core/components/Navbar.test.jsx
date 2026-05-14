import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import Navbar from './Navbar';
import api from '../api/api';

// Mock the API module
vi.mock('../api/api', () => ({
  default: {
    get: vi.fn(() => Promise.resolve({ data: [] })),
    post: vi.fn(() => Promise.resolve({ data: {} })),
  }
}));

describe('Navbar Component', () => {
  const mockUser = { id: 1, name: 'Vendor Juan', role: 'VENDOR' };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders branding and links correctly', () => {
    render(
      <MemoryRouter>
        <Navbar user={mockUser} onLogout={() => {}} />
      </MemoryRouter>
    );
    
    expect(screen.getByText(/Sari/i)).toBeInTheDocument();
    expect(screen.getByText(/Track/i)).toBeInTheDocument();
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Products')).toBeInTheDocument();
    expect(screen.getByText('Sales')).toBeInTheDocument();
  });

  it('displays user name and role', () => {
    render(
      <MemoryRouter>
        <Navbar user={mockUser} onLogout={() => {}} />
      </MemoryRouter>
    );
    
    expect(screen.getByText('Vendor Juan')).toBeInTheDocument();
    expect(screen.getByText(/Store Owner/i)).toBeInTheDocument();
  });

  it('toggles profile menu', () => {
    render(
      <MemoryRouter>
        <Navbar user={mockUser} onLogout={() => {}} />
      </MemoryRouter>
    );
    
    // Menu is hidden
    expect(screen.queryByText('Log Out')).not.toBeInTheDocument();
    
    // Click profile button
    const profileButton = screen.getByText('Vendor Juan').closest('button');
    fireEvent.click(profileButton);
    
    expect(screen.getByText('Log Out')).toBeInTheDocument();
  });

  it('calls onLogout when clicking logout', () => {
    const handleLogout = vi.fn();
    render(
      <MemoryRouter>
        <Navbar user={mockUser} onLogout={handleLogout} />
      </MemoryRouter>
    );
    
    // Open menu
    const profileButton = screen.getByText('Vendor Juan').closest('button');
    fireEvent.click(profileButton);
    
    // Click logout
    const logoutButton = screen.getByText('Log Out');
    fireEvent.click(logoutButton);
    
    expect(handleLogout).toHaveBeenCalledTimes(1);
  });

  it('fetches notifications on mount', async () => {
    render(
      <MemoryRouter>
        <Navbar user={mockUser} onLogout={() => {}} />
      </MemoryRouter>
    );

    // Should sync and then fetch
    expect(api.post).toHaveBeenCalledWith('/notifications/sync?vendorId=1');
    
    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith('/notifications?vendorId=1');
    });
  });
});
