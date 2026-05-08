import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { portfolioApi } from '../api';

const mockAssets = [
  { symbol: 'BTC', name: 'Bitcoin',   price: 65432.10, change24h: 2.41,  high: 66100, low: 63800, vol: '31.2B', mcap: '1.28T' },
  { symbol: 'ETH', name: 'Ethereum',  price: 3456.78,  change24h: 1.87,  high: 3510,  low: 3380,  vol: '18.4B', mcap: '415B' },
  { symbol: 'SOL', name: 'Solana',    price: 145.20,   change24h: 5.32,  high: 149,   low: 137,   vol: '5.1B',  mcap: '63B' },
  { symbol: 'ADA', name: 'Cardano',   price: 0.612,    change24h: -0.94, high: 0.63,  low: 0.60,  vol: '0.9B',  mcap: '21B' },
  { symbol: 'XRP', name: 'XRP',       price: 0.874,    change24h: 3.10,  high: 0.891, low: 0.845, vol: '2.3B',  mcap: '48B' },
  { symbol: 'DOT', name: 'Polkadot',  price: 9.37,     change24h: -1.44, high: 9.62,  low: 9.21,  vol: '0.4B',  mcap: '12B' },
];

const fmt = (v: number) =>
  new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(v);
const pct = (v: number) => (v > 0 ? '+' : '') + v.toFixed(2) + '%';


const Exchange: React.FC = () => {
  const [assets, setAssets] = useState(mockAssets);
  const [selected, setSelected] = useState(assets[0]);
  const [orderType, setOrderType] = useState<'BUY' | 'SELL'>('BUY');
  const [qty, setQty] = useState('');
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    const fetchLivePrices = async () => {
      try {
        const symbols = mockAssets.map(a => a.symbol);
        const prices = await portfolioApi.getPrices(symbols);
        setAssets(prev => prev.map(a => ({
          ...a,
          price: prices[a.symbol] || a.price
        })));
        setSelected(prev => ({
          ...prev,
          price: prices[prev.symbol] || prev.price
        }));
      } catch (err) {
        console.error('Failed to fetch live prices:', err);
      }
    };
    fetchLivePrices();
    
    // Refresh prices every 30s
    const interval = setInterval(fetchLivePrices, 30000);
    return () => clearInterval(interval);
  }, []);

  const total = parseFloat(qty || '0') * selected.price;

  const handleOrder = (e: React.FormEvent) => {
    e.preventDefault();
    if (!qty || parseFloat(qty) <= 0) return;
    setSubmitted(true);
    setTimeout(() => { setSubmitted(false); setQty(''); }, 3000);
  };

  return (
    <div className="page">
      <div style={{ borderBottom: '1px solid var(--hairline)', padding: '48px 0 40px' }}>
        <div className="wrap">
          <p className="t-caption" style={{ marginBottom: 12 }}>LIVE MARKETS</p>
          <div className="flex items-center justify-between">
            <h1 className="t-display-lg">EXCHANGE</h1>
            <Link to="/portfolio" className="btn btn--pill">View Portfolio</Link>
          </div>
        </div>
      </div>

      <div className="wrap" style={{ paddingTop: 48, paddingBottom: 80 }}>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 340px', gap: 48, alignItems: 'start' }}>

          {/* Market table */}
          <div>
            <div className="section-head">
              <h2 className="t-display-sm">MARKET OVERVIEW</h2>
            </div>
            <table className="data-table">
              <thead>
                <tr>
                  <th style={{ textAlign: 'left' }}>ASSET</th>
                  <th>PRICE</th>
                  <th>24H</th>
                  <th>HIGH</th>
                  <th>LOW</th>
                  <th>VOLUME</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {assets.map((a) => (
                  <tr
                    key={a.symbol}
                    style={{ cursor: 'pointer', background: selected.symbol === a.symbol ? 'rgba(255,255,255,0.03)' : undefined }}
                    onClick={() => setSelected(a)}
                  >
                    <td style={{ textAlign: 'left' }}>
                      <p style={{ fontFamily: 'var(--display)', fontSize: 16, letterSpacing: 1, color: 'var(--white)', textTransform: 'uppercase' }}>{a.symbol}</p>
                      <p className="t-caption" style={{ marginTop: 2 }}>{a.name}</p>
                    </td>
                    <td>{fmt(a.price)}</td>
                    <td><span className={`badge ${a.change24h >= 0 ? 'badge--green' : 'badge--red'}`}>{pct(a.change24h)}</span></td>
                    <td className="c-muted">{fmt(a.high)}</td>
                    <td className="c-muted">{fmt(a.low)}</td>
                    <td className="c-muted">${a.vol}</td>
                    <td><button className="btn btn--ghost t-caption" style={{ fontSize: 10, letterSpacing: 2 }} onClick={() => setSelected(a)}>SELECT →</button></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Order panel */}
          <div className="panel" style={{ position: 'sticky', top: 80 }}>
            <div style={{ marginBottom: 32, paddingBottom: 24, borderBottom: '1px solid var(--hairline)' }}>
              <p className="t-caption" style={{ marginBottom: 8 }}>SELECTED ASSET</p>
              <p style={{ fontFamily: 'var(--display)', fontSize: 28, letterSpacing: 2, color: 'var(--white)', textTransform: 'uppercase' }}>{selected.symbol}</p>
              <p className="t-num" style={{ marginTop: 8 }}>{fmt(selected.price)}</p>
              <p className={`t-num-sm mt-8 ${selected.change24h >= 0 ? 'c-green' : 'c-red'}`}>{pct(selected.change24h)} today</p>
            </div>

            <div className="tabs" style={{ marginBottom: 32 }}>
              <button className={`tab-btn ${orderType === 'BUY' ? 'tab-btn--active' : ''}`} onClick={() => setOrderType('BUY')} style={{ flex: 1 }}>BUY</button>
              <button className={`tab-btn ${orderType === 'SELL' ? 'tab-btn--active' : ''}`} onClick={() => setOrderType('SELL')} style={{ flex: 1 }}>SELL</button>
            </div>

            <form onSubmit={handleOrder}>
              <div className="field" style={{ marginBottom: 24 }}>
                <label className="field__label">Asset</label>
                <select className="field__select" value={selected.symbol} onChange={(e) => { const a = assets.find((x) => x.symbol === e.target.value); if (a) setSelected(a); }}>
                  {assets.map((a) => <option key={a.symbol} value={a.symbol}>{a.symbol} — {a.name}</option>)}
                </select>
              </div>

              <div className="field" style={{ marginBottom: 24 }}>
                <label className="field__label">Quantity ({selected.symbol})</label>
                <input className="field__input" type="number" step="0.00000001" min="0" placeholder="0.00000000" value={qty} onChange={(e) => setQty(e.target.value)} />
              </div>

              <div style={{ background: 'var(--surface)', padding: '20px', marginBottom: 24 }}>
                <div className="flex justify-between mb-12"><p className="t-caption">PRICE</p><p className="t-num-sm c-white">{fmt(selected.price)}</p></div>
                <div className="flex justify-between mb-12"><p className="t-caption">FEE (0.1%)</p><p className="t-num-sm c-muted">{fmt(total * 0.001)}</p></div>
                <hr className="rule" style={{ margin: '12px 0' }} />
                <div className="flex justify-between"><p className="t-caption">TOTAL</p><p className="t-num-sm c-white" style={{ fontSize: 16 }}>{fmt(total + total * 0.001)}</p></div>
              </div>

              {submitted ? (
                <div style={{ textAlign: 'center', padding: '12px 0' }}>
                  <p className="t-caption c-green">ORDER EXECUTED SUCCESSFULLY</p>
                </div>
              ) : (
                <button type="submit" className="btn btn--pill-fill w-full" style={{ justifyContent: 'center', background: orderType === 'BUY' ? 'var(--white)' : 'transparent', color: orderType === 'BUY' ? 'var(--canvas)' : 'var(--white)', borderColor: 'var(--white)' }}>
                  {orderType} {selected.symbol}
                </button>
              )}
            </form>
          </div>

        </div>
      </div>
    </div>
  );
};

export default Exchange;
