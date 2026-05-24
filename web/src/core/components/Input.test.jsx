import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import Input from './Input';
import { Mail } from 'lucide-react';

describe('Input Component', () => {
  it('renders with correct default class structure', () => {
    const { container } = render(<Input value="" onChange={() => {}} />);
    const wrapper = container.firstChild;
    expect(wrapper).toHaveClass('relative');
  });

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

  it('applies the required attribute to input by default', () => {
    render(<Input placeholder="Required field" value="" onChange={() => {}} />);
    const input = screen.getByPlaceholderText('Required field');
    expect(input).toHaveAttribute('required');
  });

  it('applies custom type attributes like number', () => {
    render(<Input type="number" placeholder="Quantity" value="" onChange={() => {}} />);
    const input = screen.getByPlaceholderText('Quantity');
    expect(input).toHaveAttribute('type', 'number');
  });

  it('sets input value to empty string when passed empty value', () => {
    render(<Input placeholder="Empty input" value="" onChange={() => {}} />);
    const input = screen.getByPlaceholderText('Empty input');
    expect(input.value).toBe('');
  });

  it('does not render icon when icon prop is omitted', () => {
    const { container } = render(<Input placeholder="No icon" value="" onChange={() => {}} />);
    expect(container.querySelector('svg')).not.toBeInTheDocument();
  });

  it('does not render password toggle button when showPasswordButton is omitted', () => {
    render(<Input type="password" placeholder="No toggle" value="" onChange={() => {}} />);
    const toggleButton = screen.queryByRole('button');
    expect(toggleButton).not.toBeInTheDocument();
  });
});
