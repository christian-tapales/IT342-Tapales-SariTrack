import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import Settings from './Settings';

describe('Settings Component', () => {
  it('renders all settings sections', () => {
    render(<Settings />);
    
    expect(screen.getByText('Platform Branding')).toBeInTheDocument();
    expect(screen.getByText('API & Integrations')).toBeInTheDocument();
    expect(screen.getByText('Security Policy')).toBeInTheDocument();
  });

  it('renders input fields with default values', () => {
    render(<Settings />);
    
    // Check for Platform Name input
    expect(screen.getByDisplayValue('SariTrack')).toBeInTheDocument();
    expect(screen.getByDisplayValue('admin@saritrack.com')).toBeInTheDocument();
  });

  it('renders the danger zone', () => {
    render(<Settings />);
    
    expect(screen.getByText('Maintenance Mode')).toBeInTheDocument();
    expect(screen.getByText('Enable Lockdown')).toBeInTheDocument();
  });

  it('renders the save changes button', () => {
    render(<Settings />);
    
    expect(screen.getByRole('button', { name: /Save Changes/i })).toBeInTheDocument();
  });
});
