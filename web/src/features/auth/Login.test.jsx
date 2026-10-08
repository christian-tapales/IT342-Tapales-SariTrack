import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import Login from './Login';
import api from '../../core/api/api';

vi.mock('../../core/api/api', () => ({
  default: {
    post: vi.fn(),
  },
  API_BASE_URL: 'http://localhost:8080',
}));

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

global.alert = vi.fn();

describe('Login Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

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

    // Check for login button and forgot password button
    expect(screen.getByRole('button', { name: /^Login$/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Forgot password\?/i })).toBeInTheDocument();
  });

  it('renders Google Login button', () => {
    render(
      <MemoryRouter>
        <Login onLoginSuccess={vi.fn()} />
      </MemoryRouter>
    );

    expect(screen.getByText(/Google Account/i)).toBeInTheDocument();
  });

  it('opens Forgot Password modal and sends reset token', async () => {
    api.post.mockResolvedValueOnce({ data: { message: 'Password reset instructions have been sent to your email.' } });

    render(
      <MemoryRouter>
        <Login onLoginSuccess={vi.fn()} />
      </MemoryRouter>
    );

    // Click forgot password button
    fireEvent.click(screen.getByRole('button', { name: /Forgot password\?/i }));

    // Modal should be open
    expect(screen.getByText(/Enter your account email to receive a 15-minute reset token/i)).toBeInTheDocument();

    const emailInput = screen.getByPlaceholderText(/Your Account Email/i);
    fireEvent.change(emailInput, { target: { value: 'vendor@example.com' } });

    fireEvent.click(screen.getByRole('button', { name: /Send Reset Token/i }));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/auth/forgot-password', { email: 'vendor@example.com' });
      expect(screen.getByText(/Password reset instructions have been sent to your email/i)).toBeInTheDocument();
    });
  });

  it('allows switching to reset password step and submitting new password', async () => {
    api.post.mockResolvedValueOnce({ data: { message: 'Password reset successfully!' } });

    render(
      <MemoryRouter>
        <Login onLoginSuccess={vi.fn()} />
      </MemoryRouter>
    );

    fireEvent.click(screen.getByRole('button', { name: /Forgot password\?/i }));

    // Switch to step 2
    fireEvent.click(screen.getByRole('button', { name: /Already have a reset token\? Click here/i }));

    expect(screen.getByPlaceholderText(/Reset Token/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/^New Password$/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/Confirm New Password/i)).toBeInTheDocument();

    fireEvent.change(screen.getByPlaceholderText(/Reset Token/i), { target: { value: 'tok-123' } });
    fireEvent.change(screen.getByPlaceholderText(/^New Password$/i), { target: { value: 'newPass123' } });
    fireEvent.change(screen.getByPlaceholderText(/Confirm New Password/i), { target: { value: 'newPass123' } });

    fireEvent.click(screen.getByRole('button', { name: /Set New Password/i }));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/auth/reset-password', {
        token: 'tok-123',
        newPassword: 'newPass123',
      });
      expect(screen.getByText(/Password reset successfully!/i)).toBeInTheDocument();
    });
  });

  it('displays success banner when redirected after registration', () => {
    sessionStorage.setItem('registered_success', 'true');

    render(
      <MemoryRouter>
        <Login onLoginSuccess={vi.fn()} />
      </MemoryRouter>
    );

    expect(screen.getByText(/Account created successfully! Please sign in with your email and password./i)).toBeInTheDocument();
    expect(sessionStorage.getItem('registered_success')).toBeNull();
  });
});

