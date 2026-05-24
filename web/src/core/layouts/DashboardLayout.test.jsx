import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import DashboardLayout from './DashboardLayout';

vi.mock('../components/Sidebar', () => ({
  default: ({ onLogout }) => (
    <div data-testid="mock-sidebar">
      Sidebar
      <button onClick={onLogout}>Logout Sidebar</button>
    </div>
  )
}));

vi.mock('../components/Topbar', () => ({
  default: ({ user, onLogout }) => (
    <div data-testid="mock-topbar">
      Topbar: {user?.name}
      <button onClick={onLogout}>Logout Topbar</button>
    </div>
  )
}));

vi.mock('../components/Navbar', () => ({
  default: ({ user, onLogout }) => (
    <div data-testid="mock-navbar">
      Navbar: {user?.name}
      <button onClick={onLogout}>Logout Navbar</button>
    </div>
  )
}));

describe('DashboardLayout Component', () => {
  const mockLogout = vi.fn();

  it('renders Navbar when user is a VENDOR', () => {
    const user = { id: 1, role: 'VENDOR', name: 'Vendor Juan' };
    render(
      <MemoryRouter>
        <DashboardLayout user={user} onLogout={mockLogout} />
      </MemoryRouter>
    );

    expect(screen.getByTestId('mock-navbar')).toBeInTheDocument();
    expect(screen.queryByTestId('mock-sidebar')).not.toBeInTheDocument();
    expect(screen.queryByTestId('mock-topbar')).not.toBeInTheDocument();
    expect(screen.getByText('Navbar: Vendor Juan')).toBeInTheDocument();
  });

  it('defaults to VENDOR layout when user role is missing', () => {
    const user = { id: 1, name: 'Vendor Juan' }; // No role
    render(
      <MemoryRouter>
        <DashboardLayout user={user} onLogout={mockLogout} />
      </MemoryRouter>
    );

    expect(screen.getByTestId('mock-navbar')).toBeInTheDocument();
    expect(screen.queryByTestId('mock-sidebar')).not.toBeInTheDocument();
  });

  it('renders Sidebar and Topbar when user is an ADMIN', () => {
    const user = { id: 2, role: 'ADMIN', name: 'Admin Jane' };
    render(
      <MemoryRouter>
        <DashboardLayout user={user} onLogout={mockLogout} />
      </MemoryRouter>
    );

    expect(screen.getByTestId('mock-sidebar')).toBeInTheDocument();
    expect(screen.getByTestId('mock-topbar')).toBeInTheDocument();
    expect(screen.queryByTestId('mock-navbar')).not.toBeInTheDocument();
    expect(screen.getByText('Topbar: Admin Jane')).toBeInTheDocument();
  });

  it('renders nested child routes under VENDOR layout', () => {
    const user = { id: 1, role: 'VENDOR' };
    render(
      <MemoryRouter initialEntries={['/dashboard/nested']}>
        <Routes>
          <Route path="/dashboard" element={<DashboardLayout user={user} onLogout={mockLogout} />}>
            <Route path="nested" element={<div data-testid="vendor-child">Vendor Child Route</div>} />
          </Route>
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByTestId('vendor-child')).toBeInTheDocument();
  });

  it('renders nested child routes under ADMIN layout', () => {
    const user = { id: 2, role: 'ADMIN' };
    render(
      <MemoryRouter initialEntries={['/dashboard/nested']}>
        <Routes>
          <Route path="/dashboard" element={<DashboardLayout user={user} onLogout={mockLogout} />}>
            <Route path="nested" element={<div data-testid="admin-child">Admin Child Route</div>} />
          </Route>
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByTestId('admin-child')).toBeInTheDocument();
  });

  it('passes onLogout prop to Navbar in VENDOR layout', () => {
    const user = { id: 1, role: 'VENDOR' };
    render(
      <MemoryRouter>
        <DashboardLayout user={user} onLogout={mockLogout} />
      </MemoryRouter>
    );

    screen.getByText('Logout Navbar').click();
    expect(mockLogout).toHaveBeenCalledTimes(1);
  });

  it('passes onLogout prop to Sidebar and Topbar in ADMIN layout', () => {
    const user = { id: 2, role: 'ADMIN' };
    render(
      <MemoryRouter>
        <DashboardLayout user={user} onLogout={mockLogout} />
      </MemoryRouter>
    );

    screen.getByText('Logout Sidebar').click();
    screen.getByText('Logout Topbar').click();
    expect(mockLogout).toHaveBeenCalledTimes(3); // 1 from previous test, 2 from this test
  });

  it('applies correct background classes to layouts', () => {
    const vendorUser = { id: 1, role: 'VENDOR' };
    const { container: vendorContainer } = render(
      <MemoryRouter>
        <DashboardLayout user={vendorUser} onLogout={mockLogout} />
      </MemoryRouter>
    );
    expect(vendorContainer.firstChild).toHaveClass('bg-[#F8FAFB]');

    const adminUser = { id: 2, role: 'ADMIN' };
    const { container: adminContainer } = render(
      <MemoryRouter>
        <DashboardLayout user={adminUser} onLogout={mockLogout} />
      </MemoryRouter>
    );
    expect(adminContainer.firstChild).toHaveClass('bg-slate-50');
  });
});
