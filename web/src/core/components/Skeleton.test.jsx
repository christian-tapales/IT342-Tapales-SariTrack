import { render } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import Skeleton from './Skeleton';

describe('Skeleton Component', () => {
  it('renders with correct classes', () => {
    const { container } = render(<Skeleton className="w-10 h-10" />);
    const skeleton = container.firstChild;
    expect(skeleton).toHaveClass('animate-pulse');
    expect(skeleton).toHaveClass('bg-slate-200');
    expect(skeleton).toHaveClass('w-10');
    expect(skeleton).toHaveClass('h-10');
  });
});
