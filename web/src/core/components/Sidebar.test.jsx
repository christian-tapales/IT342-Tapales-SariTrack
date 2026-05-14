import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import Sidebar from './Sidebar';

describe('Sidebar Component', () => {
  it('renders branding text correctly', () => {
    render(
      <MemoryRouter>
        <Sidebar onLogout={() => {}} />
      </MemoryRouter>
    );
    expect(screen.getByText(/Sari/i)).toBeInTheDocument();
    expect(screen.getAllByText(/Admin/i).length).toBeGreaterThan(0);
  });

  it('renders navigation links', () => {
    render(
      <MemoryRouter>
        <Sidebar onLogout={() => {}} />
      </MemoryRouter>
    );
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Vendors')).toBeInTheDocument();
    expect(screen.getByText('Global Products')).toBeInTheDocument();
    expect(screen.getByText('Settings')).toBeInTheDocument();
  });

  it('highlights active link based on current path', () => {
    render(
      <MemoryRouter initialEntries={['/admin/vendors']}>
        <Sidebar onLogout={() => {}} />
      </MemoryRouter>
    );
    
    const vendorsLink = screen.getByText('Vendors').closest('a');
    // Active links in this sidebar have specific classes like bg-teal-50
    expect(vendorsLink).toHaveClass('bg-teal-50');
  });
});
