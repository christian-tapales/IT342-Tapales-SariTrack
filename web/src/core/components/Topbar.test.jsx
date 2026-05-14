import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import Topbar from './Topbar';

describe('Topbar Component', () => {
  const mockUser = { name: 'Juan Dela Cruz', role: 'VENDOR' };

  it('renders user name correctly', () => {
    render(<Topbar user={mockUser} onLogout={() => {}} />);
    expect(screen.getByText('Juan Dela Cruz')).toBeInTheDocument();
    expect(screen.getByText('Store Owner')).toBeInTheDocument();
  });

  it('toggles menu when clicking profile', () => {
    render(<Topbar user={mockUser} onLogout={() => {}} />);
    
    // Initially menu is hidden
    expect(screen.queryByText('Log Out')).not.toBeInTheDocument();
    
    // Click profile button (contains the initial)
    const profileButton = screen.getByText('J').closest('button');
    fireEvent.click(profileButton);
    
    // Menu should be visible
    expect(screen.getByText('Log Out')).toBeInTheDocument();
    
    // Click again to close
    fireEvent.click(profileButton);
    expect(screen.queryByText('Log Out')).not.toBeInTheDocument();
  });

  it('calls onLogout when clicking logout button', () => {
    const handleLogout = vi.fn();
    render(<Topbar user={mockUser} onLogout={handleLogout} />);
    
    // Open menu
    const profileButton = screen.getByText('J').closest('button');
    fireEvent.click(profileButton);
    
    // Click logout
    const logoutButton = screen.getByText('Log Out');
    fireEvent.click(logoutButton);
    
    expect(handleLogout).toHaveBeenCalledTimes(1);
  });

  it('renders Admin labels when user is ADMIN', () => {
    const adminUser = { name: 'Admin User', role: 'ADMIN' };
    render(<Topbar user={adminUser} onLogout={() => {}} />);
    
    expect(screen.getByText('Platform Control')).toBeInTheDocument();
    expect(screen.getByText('Root Administrator')).toBeInTheDocument();
  });
});
