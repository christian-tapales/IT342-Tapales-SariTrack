import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import PaymentSuccess from './PaymentSuccess';
import PaymentCancel from './PaymentCancel';

describe('Payment Status Pages', () => {
  it('renders PaymentSuccess correctly', () => {
    render(
      <MemoryRouter>
        <PaymentSuccess />
      </MemoryRouter>
    );

    expect(screen.getByText('Payment Received!')).toBeInTheDocument();
    expect(screen.getByText('Back to POS')).toBeInTheDocument();
  });

  it('renders PaymentCancel correctly', () => {
    render(
      <MemoryRouter>
        <PaymentCancel />
      </MemoryRouter>
    );

    expect(screen.getByText('Payment Cancelled')).toBeInTheDocument();
    expect(screen.getByText('Return to Cart')).toBeInTheDocument();
  });
});
