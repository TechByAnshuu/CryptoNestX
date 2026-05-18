import React from 'react';

interface ErrorBannerProps {
  message: string;
  onRetry?: () => void;
}

/**
 * ErrorBanner — non-breaking error display for API failures.
 * Renders above card content; never crashes or hides the rest of the UI.
 * Light theme: amber warning style (Coinbase design system).
 */
const ErrorBanner: React.FC<ErrorBannerProps> = ({ message, onRetry }) => {
  return (
    <div
      role="alert"
      style={{
        background: '#fff3cd',
        borderLeft: '4px solid #f4b000',
        borderRadius: '0 8px 8px 0',
        padding: '12px 16px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: 12,
        marginBottom: 16,
        fontSize: 14,
        color: '#7a5800',
        fontFamily: 'Inter, sans-serif',
      }}
    >
      <span>
        <strong>⚠ Error:</strong> {message}
      </span>
      {onRetry && (
        <button
          onClick={onRetry}
          style={{
            background: 'none',
            border: '1px solid #f4b000',
            borderRadius: 6,
            padding: '4px 12px',
            cursor: 'pointer',
            color: '#7a5800',
            fontFamily: 'Inter, sans-serif',
            fontSize: 13,
            fontWeight: 600,
            whiteSpace: 'nowrap',
            flexShrink: 0,
          }}
        >
          Retry
        </button>
      )}
    </div>
  );
};

export default ErrorBanner;
