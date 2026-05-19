import { render } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import Skeleton from './Skeleton';

describe('Skeleton Component', () => {
  it('renders with correct default class structure', () => {
    const { container } = render(<Skeleton />);
    const skeleton = container.firstChild;
    expect(skeleton).toBeInTheDocument();
  });

  it('renders with animate-pulse class', () => {
    const { container } = render(<Skeleton />);
    const skeleton = container.firstChild;
    expect(skeleton).toHaveClass('animate-pulse');
  });

  it('renders with default background bg-slate-200 class', () => {
    const { container } = render(<Skeleton />);
    const skeleton = container.firstChild;
    expect(skeleton).toHaveClass('bg-slate-200');
  });

  it('renders with default rounded-2xl class', () => {
    const { container } = render(<Skeleton />);
    const skeleton = container.firstChild;
    expect(skeleton).toHaveClass('rounded-2xl');
  });

  it('applies custom width classes via className', () => {
    const { container } = render(<Skeleton className="w-24" />);
    const skeleton = container.firstChild;
    expect(skeleton).toHaveClass('w-24');
  });

  it('applies custom height classes via className', () => {
    const { container } = render(<Skeleton className="h-12" />);
    const skeleton = container.firstChild;
    expect(skeleton).toHaveClass('h-12');
  });

  it('applies custom display layouts via className', () => {
    const { container } = render(<Skeleton className="inline-block" />);
    const skeleton = container.firstChild;
    expect(skeleton).toHaveClass('inline-block');
  });

  it('preserves other custom tailwind utility classes', () => {
    const { container } = render(<Skeleton className="mt-4 shadow-sm" />);
    const skeleton = container.firstChild;
    expect(skeleton).toHaveClass('mt-4');
    expect(skeleton).toHaveClass('shadow-sm');
  });

  it('does not crash when additional empty className is provided', () => {
    const { container } = render(<Skeleton className="" />);
    const skeleton = container.firstChild;
    expect(skeleton).toBeInTheDocument();
    expect(skeleton).toHaveClass('bg-slate-200');
  });

  it('renders correctly as a child element inside containers', () => {
    const { container } = render(
      <div data-testid="parent-container">
        <Skeleton className="w-full" />
      </div>
    );
    const parent = screen => container.querySelector('[data-testid="parent-container"]');
    expect(parent).toBeDefined();
    expect(container.querySelector('.animate-pulse')).toBeInTheDocument();
  });
});
