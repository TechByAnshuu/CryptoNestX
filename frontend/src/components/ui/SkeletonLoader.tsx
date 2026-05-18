import React from 'react';

interface SkeletonLoaderProps {
  width?: string | number;
  height?: string | number;
  borderRadius?: string | number;
  dark?: boolean;
  style?: React.CSSProperties;
}

const shimmerKeyframes = `
@keyframes skeleton-shimmer {
  0%   { background-position: -800px 0; }
  100% { background-position: 800px 0; }
}
`;

/**
 * SkeletonLoader — shimmer placeholder while content is loading.
 *
 * Props:
 *  - dark: use the Bugatti dark palette (#1a1a1a → #262626)
 *          default: Coinbase light palette (#f0f0f0 → #e0e0e0)
 */
const SkeletonLoader: React.FC<SkeletonLoaderProps> = ({
  width = '100%',
  height = 20,
  borderRadius = 8,
  dark = false,
  style,
}) => {
  const lightGradient =
    'linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%)';
  const darkGradient =
    'linear-gradient(90deg, #1a1a1a 25%, #262626 50%, #1a1a1a 75%)';

  return (
    <>
      <style>{shimmerKeyframes}</style>
      <div
        style={{
          width,
          height,
          borderRadius,
          background: dark ? darkGradient : lightGradient,
          backgroundSize: '800px 100%',
          animation: 'skeleton-shimmer 1.4s infinite linear',
          display: 'inline-block',
          ...style,
        }}
        aria-label="Loading…"
        role="status"
      />
    </>
  );
};

export default SkeletonLoader;
