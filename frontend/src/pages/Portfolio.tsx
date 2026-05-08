import React from 'react';
import { Link } from 'react-router-dom';

const mockPortfolio = {
  totalValue: 124567.89,
  totalPnl: 15234.50,
  totalPnlPct: 13.93,
  dayChange: 2341.12,
  dayChangePct: 1.91,
  holdings: [
    { symbol: 'BTC', name: 'Bitcoin',  qty: 1.5,   avg: 45000,  price: 65432.10, value: 98148.15, pnl: 30648.15, pnlPct: 45.40, day: 2.41 },
    { symbol: 'ETH', name: 'Ethereum', qty: 5.0,   avg: 2800,   price: 3456.78,  value: 17283.90, pnl: 3283.90,  pnlPct: 23.45, day: 1.87 },
    { symbol: 'SOL', name: 'Solana',   qty: 50.0,  avg: 120,    price: 145.20,   value: 7260.00,  pnl: 1260.00,  pnlPct: 21.00, day: 5.32 },
    { symbol: 'DOT', name: 'Polkadot', qty: 200.0, avg: 15,     price: 9.37,     value: 1874.00,  pnl: -1126.00, pnlPct: -37.53, day: -1.44 },
  ],
};

const fmt = (v: number) =>
  new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(v);
const pct = (v: number) => (v > 0 ? '+' : '') + v.toFixed(2) + '%';

const Portfolio: React.FC = () => {
  const p = mockPortfolio;
  return (
    <div className="page">
      {/* ── Page header ─────────────────────────────────────── */}
      <div style={{ borderBottom: '1px solid var(--hairline)', padding: '48px 0 40px' }}>
        <div className="wrap">
          <p className="t-caption" style={{ marginBottom: 12 }}>MY PORTFOLIO</p>
          <div className="flex items-center justify-between">
            <h1 className="t-display-lg">ASSET HOLDINGS</h1>
            <Link to="/exchange" className="btn btn--pill">Place Order</Link>
          </div>
        </div>
      </div>

      <div className="wrap" style={{ padding: '48px 48px' }}>
        {/* ── Summary stats ───────────────────────────────────── */}
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(4, 1fr)',
            gap: 0,
            borderTop: '1px solid var(--hairline)',
            borderLeft: '1px solid var(--hairline)',
            marginBottom: 64,
          }}
        >
          {[
            { label: 'PORTFOLIO VALUE', val: fmt(p.totalValue), delta: null },
            { label: 'TOTAL P&L',       val: fmt(p.totalPnl),   delta: pct(p.totalPnlPct),  up: p.totalPnl > 0 },
            { label: 'TODAY\'S CHANGE', val: fmt(p.dayChange),  delta: pct(p.dayChangePct), up: p.dayChange > 0 },
            { label: 'NO. OF ASSETS',   val: `${p.holdings.length}`, delta: null },
          ].map((s, i) => (
            <div
              key={i}
              style={{ padding: '32px 28px', borderRight: '1px solid var(--hairline)', borderBottom: '1px solid var(--hairline)' }}
            >
              <p className="t-caption" style={{ marginBottom: 12 }}>{s.label}</p>
              <p className="t-num">{s.val}</p>
              {s.delta && (
                <p
                  className={`t-num-sm mt-8 ${s.up ? 'c-green' : 'c-red'}`}
                  style={{ fontFamily: 'var(--mono)' }}
                >
                  {s.delta}
                </p>
              )}
            </div>
          ))}
        </div>

        {/* ── Holdings table ──────────────────────────────────── */}
        <div className="section-head">
          <h2 className="t-display-sm">CURRENT POSITIONS</h2>
          <p className="t-caption" style={{ opacity: 0.5 }}>{p.holdings.length} ASSETS</p>
        </div>

        <div style={{ overflowX: 'auto' }}>
          <table className="data-table">
            <thead>
              <tr>
                <th style={{ textAlign: 'left' }}>ASSET</th>
                <th>HOLDINGS</th>
                <th>AVG COST</th>
                <th>CURRENT PRICE</th>
                <th>CURRENT VALUE</th>
                <th>P&L</th>
                <th>P&L %</th>
                <th>24H</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {p.holdings.map((h) => (
                <tr key={h.symbol}>
                  <td style={{ textAlign: 'left' }}>
                    <div>
                      <p style={{ fontFamily: 'var(--display)', fontSize: 18, letterSpacing: 1, color: 'var(--white)', textTransform: 'uppercase' }}>
                        {h.symbol}
                      </p>
                      <p className="t-caption" style={{ marginTop: 2 }}>{h.name}</p>
                    </div>
                  </td>
                  <td>{h.qty.toFixed(4)}</td>
                  <td>{fmt(h.avg)}</td>
                  <td>{fmt(h.price)}</td>
                  <td>{fmt(h.value)}</td>
                  <td className={h.pnl >= 0 ? 'c-green' : 'c-red'}>{fmt(h.pnl)}</td>
                  <td>
                    <span className={`badge ${h.pnlPct >= 0 ? 'badge--green' : 'badge--red'}`}>
                      {pct(h.pnlPct)}
                    </span>
                  </td>
                  <td className={h.day >= 0 ? 'c-green' : 'c-red'}>{pct(h.day)}</td>
                  <td>
                    <Link to="/exchange" className="btn btn--ghost t-caption" style={{ fontSize: 10, letterSpacing: 2 }}>
                      TRADE →
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* ── Allocation visual ───────────────────────────────── */}
        <div style={{ marginTop: 80 }}>
          <div className="section-head">
            <h2 className="t-display-sm">ALLOCATION</h2>
          </div>
          <div style={{ display: 'flex', gap: 24, flexWrap: 'wrap' }}>
            {p.holdings.map((h) => {
              const pct = (h.value / p.totalValue) * 100;
              return (
                <div key={h.symbol} style={{ flex: '1 1 140px' }}>
                  <div style={{ height: 2, background: 'var(--hairline-md)', marginBottom: 16, position: 'relative' }}>
                    <div style={{ position: 'absolute', left: 0, top: 0, height: '100%', width: `${pct}%`, background: 'var(--white)' }} />
                  </div>
                  <p className="t-display-sm">{h.symbol}</p>
                  <p className="t-num-sm c-muted" style={{ marginTop: 4 }}>{pct.toFixed(1)}%</p>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Portfolio;
