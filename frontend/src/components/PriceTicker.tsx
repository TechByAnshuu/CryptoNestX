import React, { useEffect, useState } from 'react';
import { useLivePrices } from '../hooks/useLivePrices';

interface PriceTickerProps {
  symbol: string;
}

export const PriceTicker: React.FC<PriceTickerProps> = ({ symbol }) => {
  const { prices } = useLivePrices();
  const priceData = prices[symbol];
  
  const [flash, setFlash] = useState<'up' | 'down' | null>(null);
  const [prevPrice, setPrevPrice] = useState<number | null>(null);

  useEffect(() => {
    if (priceData && priceData.priceUsd !== prevPrice) {
      if (prevPrice !== null) {
        setFlash(priceData.priceUsd > prevPrice ? 'up' : 'down');
        const timer = setTimeout(() => setFlash(null), 1000);
        return () => clearTimeout(timer);
      }
      setPrevPrice(priceData.priceUsd);
    }
  }, [priceData, prevPrice]);

  if (!priceData) {
    return <span style={{ fontFamily: 'IBM Plex Mono, monospace' }}>Loading...</span>;
  }

  const color = flash === 'up' ? '#05b169' : flash === 'down' ? '#cf202f' : 'inherit';
  const transition = 'color 0.3s ease-in-out';

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
      <span style={{ 
        fontFamily: 'IBM Plex Mono, monospace', 
        fontSize: '1.2rem', 
        fontWeight: 'bold',
        color,
        transition
      }}>
        ${priceData.priceUsd.toFixed(2)}
      </span>
      <span style={{ 
        fontFamily: 'IBM Plex Mono, monospace', 
        fontSize: '0.9rem',
        color: priceData.change24h >= 0 ? '#05b169' : '#cf202f'
      }}>
        {priceData.change24h >= 0 ? '+' : ''}{priceData.change24h.toFixed(2)}%
      </span>
    </div>
  );
};
