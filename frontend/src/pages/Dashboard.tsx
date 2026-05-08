import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { portfolioApi } from '../api';

const fmt = (v: number) =>
  new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(v || 0);
const pct = (v: number) => (v > 0 ? '+' : '') + (v || 0).toFixed(2) + '%';

// Use a fixed demo user ID for now until authentication is fully wired
const DEMO_USER_ID = '123e4567-e89b-12d3-a456-426614174000';

const Dashboard: React.FC = () => {
  const [portfolio, setPortfolio] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPortfolio = async () => {
      try {
        const data = await portfolioApi.getSummary(DEMO_USER_ID);
        setPortfolio(data);
      } catch (err) {
        console.error('Failed to load portfolio:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchPortfolio();
  }, []);

  if (loading) {
    return <div className="page wrap" style={{ paddingTop: 100 }}>Loading live data from CoinMarketCap...</div>;
  }

  // Fallback to empty state if no portfolio found
  const d = portfolio || {
    totalValue: 0,
    totalPnl: 0,
    totalPnlPercentage: 0,
    holdings: []
  };

  // Mock day change and recent orders until exchange service is fully connected
  const dayChange = d.totalPnl * 0.1; // Simulated daily change
  const dayChangePct = d.totalPnlPercentage * 0.1;
  const walletBalance = 10000.00; // Simulated cash balance
  
  const recentOrders = [
    { id: 'ORD-001', symbol: 'BTC', type: 'BUY',  qty: 0.5,  price: 64100, status: 'EXECUTED', time: '14:22' },
    { id: 'ORD-002', symbol: 'ETH', type: 'BUY',  qty: 2.0,  price: 3390,  status: 'EXECUTED', time: '11:05' },
  ];

  return (
    <div className="page">
      {/* ── Page Header ──────────────────────────────────────── */}
      <div style={{ borderBottom: '1px solid var(--hairline)', padding: '48px 0 40px' }}>
        <div className="wrap">
          <p className="t-caption" style={{ marginBottom: 12 }}>
            <span className="notif-dot" style={{ marginRight: 8 }} />
            LIVE
          </p>
          <div className="flex items-center justify-between">
            <h1 className="t-display-lg">DASHBOARD</h1>
            <Link to="/exchange" className="btn btn--pill">Trade Now</Link>
          </div>
        </div>
      </div>

      <div className="wrap" style={{ paddingTop: 48, paddingBottom: 80 }}>

        {/* ── Top Stats ────────────────────────────────────────── */}
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
            { label: 'PORTFOLIO VALUE', val: fmt(d.totalValue),      sub: `${pct(d.totalPnlPercentage)} all time`,   up: d.totalPnlPercentage >= 0 },
            { label: 'TODAY\'S P&L',    val: fmt(dayChange),         sub: pct(dayChangePct),                up: dayChange >= 0 },
            { label: 'TOTAL RETURN',    val: fmt(d.totalPnl),        sub: pct(d.totalPnlPercentage),                 up: d.totalPnl >= 0 },
            { label: 'WALLET BALANCE',  val: fmt(walletBalance),     sub: 'AVAILABLE CASH',                   up: true },
          ].map((s, i) => (
            <div
              key={i}
              style={{ padding: '32px 28px', borderRight: '1px solid var(--hairline)', borderBottom: '1px solid var(--hairline)' }}
            >
              <p className="t-caption" style={{ marginBottom: 12 }}>{s.label}</p>
              <p className="t-num">{s.val}</p>
              <p className={`t-num-sm mt-8 ${s.up ? 'c-green' : 'c-red'}`} style={{ fontFamily: 'var(--mono)' }}>
                {s.sub}
              </p>
            </div>
          ))}
        </div>

        {/* ── Two Column Layout ─────────────────────────────────── */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 380px', gap: 48 }}>

          {/* LEFT — Holdings snapshot */}
          <div>
            <div className="section-head">
              <h2 className="t-display-sm">POSITIONS</h2>
              <Link to="/portfolio" className="btn btn--ghost t-caption" style={{ fontSize: 10, letterSpacing: 2 }}>VIEW ALL →</Link>
            </div>
            <table className="data-table">
              <thead>
                <tr>
                  <th style={{ textAlign: 'left' }}>ASSET</th>
                  <th>QUANTITY</th>
                  <th>PRICE</th>
                  <th>P&L %</th>
                  <th>24H</th>
                </tr>
              </thead>
              <tbody>
                {d.holdings.length === 0 && (
                  <tr>
                    <td colSpan={5} style={{ textAlign: 'center', padding: '40px 0', color: 'var(--muted)' }}>
                      No positions yet. Go to Exchange to start trading!
                    </td>
                  </tr>
                )}
                {d.holdings.map((h: any) => (
                  <tr key={h.symbol}>
                    <td style={{ textAlign: 'left' }}>
                      <p style={{ fontFamily: 'var(--display)', fontSize: 18, letterSpacing: 1, color: 'var(--white)', textTransform: 'uppercase' }}>
                        {h.symbol}
                      </p>
                    </td>
                    <td>{h.quantity.toFixed(4)}</td>
                    <td>{fmt(h.currentPrice)}</td>
                    <td>
                      <span className={`badge ${h.pnlPercentage >= 0 ? 'badge--green' : 'badge--red'}`}>
                        {pct(h.pnlPercentage)}
                      </span>
                    </td>
                    <td className={h.pnlPercentage >= 0 ? 'c-green' : 'c-red'}>{pct(h.pnlPercentage * 0.2)}</td>
                  </tr>
                ))}
              </tbody>
            </table>

            {/* Allocation bar */}
            <div style={{ marginTop: 48 }}>
              <p className="t-caption" style={{ marginBottom: 20 }}>ALLOCATION BREAKDOWN</p>
              {d.holdings.map((h: any) => {
                const total = d.totalValue > 0 ? d.totalValue : 1;
                const share = (h.currentValue / total) * 100;
                return (
                  <div key={h.symbol} style={{ marginBottom: 16 }}>
                    <div className="flex justify-between mb-8">
                      <p className="t-num-sm c-white" style={{ fontFamily: 'var(--display)', letterSpacing: 1, textTransform: 'uppercase' }}>{h.symbol}</p>
                      <p className="t-num-sm c-muted">{share.toFixed(1)}%</p>
                    </div>
                    <div style={{ height: 1, background: 'var(--hairline-md)', position: 'relative' }}>
                      <div style={{ position: 'absolute', left: 0, top: 0, height: '100%', width: `${share}%`, background: 'var(--white)' }} />
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* RIGHT — Recent orders + quick actions */}
          <div>
            {/* Quick actions */}
            <div className="panel" style={{ marginBottom: 32 }}>
              <p className="t-caption" style={{ marginBottom: 24 }}>QUICK ACTIONS</p>
              <div className="flex-col gap-12">
                <Link to="/exchange" className="btn btn--pill w-full" style={{ justifyContent: 'center' }}>Place Order</Link>
                <Link to="/portfolio" className="btn btn--pill w-full" style={{ justifyContent: 'center' }}>View Portfolio</Link>
                <button className="btn btn--ghost w-full t-caption" style={{ fontSize: 10, letterSpacing: 2, textAlign: 'center', color: 'var(--muted)', padding: '10px 0' }}>
                  Top Up Wallet
                </button>
              </div>
            </div>

            {/* Recent orders */}
            <div>
              <div className="section-head" style={{ marginBottom: 24 }}>
                <p className="t-caption">RECENT ORDERS</p>
              </div>
              <div className="flex-col gap-0">
                {recentOrders.map((o, i) => (
                  <div
                    key={i}
                    style={{ padding: '16px 0', borderBottom: '1px solid var(--hairline)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}
                  >
                    <div>
                      <div className="flex items-center gap-8" style={{ marginBottom: 4 }}>
                        <p style={{ fontFamily: 'var(--display)', fontSize: 15, letterSpacing: 1, color: 'var(--white)', textTransform: 'uppercase' }}>
                          {o.symbol}
                        </p>
                        <span className={`badge ${o.type === 'BUY' ? 'badge--green' : 'badge--red'}`}>{o.type}</span>
                      </div>
                      <p className="t-caption">{o.qty} @ {fmt(o.price)}</p>
                    </div>
                    <div style={{ textAlign: 'right' }}>
                      <p className="t-caption c-green" style={{ marginBottom: 4 }}>{o.status}</p>
                      <p className="t-caption">{o.time}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>

        </div>

        {/* ── Market Pulse strip ──────────────────────────────── */}
        <div style={{ marginTop: 80, padding: '40px 0', borderTop: '1px solid var(--hairline)' }}>
          <p className="t-caption" style={{ marginBottom: 24 }}>MARKET PULSE</p>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 0, borderLeft: '1px solid var(--hairline)' }}>
            {[
              { label: 'FEAR & GREED', val: '72', note: 'GREED', up: true },
              { label: 'BTC DOMINANCE', val: '54.3%', note: '↑ 0.4%', up: true },
              { label: 'TOTAL MKT CAP', val: '$2.41T', note: '+2.1%', up: true },
              { label: 'ACTIVE TRADERS', val: '142K', note: 'ONLINE', up: true },
            ].map((m, i) => (
              <div key={i} style={{ padding: '24px 28px', borderRight: '1px solid var(--hairline)' }}>
                <p className="t-caption" style={{ marginBottom: 10 }}>{m.label}</p>
                <p className="t-num" style={{ fontSize: 22 }}>{m.val}</p>
                <p className={`t-num-sm mt-8 ${m.up ? 'c-green' : 'c-red'}`} style={{ fontFamily: 'var(--mono)', fontSize: 11 }}>{m.note}</p>
              </div>
            ))}
          </div>
        </div>

      </div>
    </div>
  );
};

export default Dashboard;
