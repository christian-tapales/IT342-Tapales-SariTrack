import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import Register from './Register';
import api from '../../core/api/api';

// Mock api
vi.mock('../../core/api/api', () => ({
  default: {
    post: vi.fn(),
  },
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

global.alert = vi.fn();

describe('Register Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders registration form correctly', () => {
    render(
      <MemoryRouter>
        <Register />
      </MemoryRouter>
    );

    expect(screen.getByPlaceholderText(/Full Name/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/Email Address/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/^Password$/)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/Confirm Password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Create Account/i })).toBeInTheDocument();
  });

  it('shows alert if passwords do not match', () => {
    render(
      <MemoryRouter>
        <Register />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByPlaceholderText(/Full Name/i), { target: { value: 'Juan' } });
    fireEvent.change(screen.getByPlaceholderText(/Email Address/i), { target: { value: 'juan@test.com' } });
    fireEvent.change(screen.getByPlaceholderText(/^Password$/), { target: { value: 'password123' } });
    fireEvent.change(screen.getByPlaceholderText(/Confirm Password/i), { target: { value: 'password456' } });
    
    fireEvent.click(screen.getByRole('button', { name: /Create Account/i }));

    expect(global.alert).toHaveBeenCalledWith('Passwords do not match!');
  });

  it('successfully registers a user', async () => {
    api.post.mockResolvedValue({ data: 'User registered successfully!' });

    render(
      <MemoryRouter>
        <Register />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByPlaceholderText(/Full Name/i), { target: { value: 'Juan Dela Cruz' } });
    fireEvent.change(screen.getByPlaceholderText(/Email Address/i), { target: { value: 'juan@example.com' } });
    fireEvent.change(screen.getByPlaceholderText(/^Password$/), { target: { value: 'password123' } });
    fireEvent.change(screen.getByPlaceholderText(/Confirm Password/i), { target: { value: 'password123' } });

    fireEvent.click(screen.getByRole('button', { name: /Create Account/i }));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/auth/register', {
        name: 'Juan Dela Cruz',
        email: 'juan@example.com',
        password: 'password123'
      });
      expect(global.alert).toHaveBeenCalledWith('Registration Successful!');
      expect(mockNavigate).toHaveBeenCalledWith('/login');
    });
  });

  it('handles registration failure', async () => {
    api.post.mockRejectedValue(new Error('API Error'));

    render(
      <MemoryRouter>
        <Register />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByPlaceholderText(/Full Name/i), { target: { value: 'Juan' } });
    fireEvent.change(screen.getByPlaceholderText(/Email Address/i), { target: { value: 'juan@test.com' } });
    fireEvent.change(screen.getByPlaceholderText(/^Password$/), { target: { value: 'password123' } });
    fireEvent.change(screen.getByPlaceholderText(/Confirm Password/i), { target: { value: 'password123' } });

    fireEvent.click(screen.getByRole('button', { name: /Create Account/i }));

    await waitFor(() => {
      expect(global.alert).toHaveBeenCalledWith('Registration failed. Check backend connection.');
    });
  });
});
