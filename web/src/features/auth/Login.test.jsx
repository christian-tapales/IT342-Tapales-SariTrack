import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import Login from './Login';

describe('Login Component', () => {
  it('renders login form correctly', () => {
    render(
      <MemoryRouter>
        <Login onLoginSuccess={vi.fn()} />
      </MemoryRouter>
    );

    // Check for logo text
    expect(screen.getByText(/Sari/i)).toBeInTheDocument();
    expect(screen.getByText(/Track/i)).toBeInTheDocument();

    // Check for inputs
    expect(screen.getByPlaceholderText(/Email Address/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/Password/i)).toBeInTheDocument();

    // Check for login button
    expect(screen.getByRole('button', { name: /Login/i })).toBeInTheDocument();
  });

  it('renders Google Login button', () => {
    render(
      <MemoryRouter>
        <Login onLoginSuccess={vi.fn()} />
      </MemoryRouter>
    );

    expect(screen.getByText(/Google Account/i)).toBeInTheDocument();
  });
});
