import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import Input from './Input';
import { Mail } from 'lucide-react';

describe('Input Component', () => {
  it('renders with placeholder and value', () => {
    render(
      <Input 
        placeholder="Enter email" 
        value="test@example.com" 
        onChange={() => {}} 
      />
    );
    const input = screen.getByPlaceholderText(/Enter email/i);
    expect(input).toBeInTheDocument();
    expect(input.value).toBe('test@example.com');
  });

  it('calls onChange when typing', () => {
    const handleChange = vi.fn();
    render(
      <Input 
        placeholder="Enter name" 
        value="" 
        onChange={handleChange} 
      />
    );
    const input = screen.getByPlaceholderText(/Enter name/i);
    fireEvent.change(input, { target: { value: 'John' } });
    expect(handleChange).toHaveBeenCalledTimes(1);
  });

  it('renders icon when provided', () => {
    const { container } = render(
      <Input 
        icon={Mail} 
        placeholder="Email" 
        value="" 
        onChange={() => {}} 
      />
    );
    // Lucide icons render as svg
    expect(container.querySelector('svg')).toBeInTheDocument();
  });

  it('renders password toggle button and calls onTogglePassword', () => {
    const handleToggle = vi.fn();
    render(
      <Input 
        type="password"
        placeholder="Password"
        value=""
        onChange={() => {}}
        showPasswordButton={<span>Show</span>}
        onTogglePassword={handleToggle}
      />
    );
    const toggleButton = screen.getByRole('button');
    expect(toggleButton).toBeInTheDocument();
    expect(screen.getByText(/Show/i)).toBeInTheDocument();
    
    fireEvent.click(toggleButton);
    expect(handleToggle).toHaveBeenCalledTimes(1);
  });
});
