import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import AuthLayout from './AuthLayout';

describe('AuthLayout Component', () => {
  it('renders auth layout wrapper correctly', () => {
    const { container } = render(
      <MemoryRouter>
        <AuthLayout />
      </MemoryRouter>
    );
    const wrapper = container.firstChild;
    expect(wrapper).toHaveClass('min-h-screen');
    expect(wrapper).toHaveClass('bg-slate-900');
  });

  it('renders overlay div for readability', () => {
    const { container } = render(
      <MemoryRouter>
        <AuthLayout />
      </MemoryRouter>
    );
    const overlay = container.querySelector('.bg-black\\/50');
    expect(overlay).toBeInTheDocument();
    expect(overlay).toHaveClass('backdrop-blur-[2px]');
  });

  it('contains target card container', () => {
    const { container } = render(
      <MemoryRouter>
        <AuthLayout />
      </MemoryRouter>
    );
    const cardContainer = container.querySelector('.max-w-md');
    expect(cardContainer).toBeInTheDocument();
  });

  it('renders nested child routes inside the outlet', () => {
    render(
      <MemoryRouter initialEntries={['/auth']}>
        <Routes>
          <Route path="/" element={<AuthLayout />}>
            <Route path="auth" element={<div data-testid="child-page">Login Page</div>} />
          </Route>
        </Routes>
      </MemoryRouter>
    );
    expect(screen.getByTestId('child-page')).toBeInTheDocument();
    expect(screen.getByText('Login Page')).toBeInTheDocument();
  });

  it('has proper layout width rules', () => {
    const { container } = render(
      <MemoryRouter>
        <AuthLayout />
      </MemoryRouter>
    );
    const cardContainer = container.querySelector('.max-w-md');
    expect(cardContainer).toHaveClass('w-full');
  });
});
