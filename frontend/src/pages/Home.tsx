import React from 'react';
import { Link } from 'react-router-dom';

const TICKER = [
  { sym: 'BTC', price: '$65,432', change: '+2.41%', up: true },
  { sym: 'ETH', price: '$3,456', change: '+1.87%', up: true },
  { sym: 'SOL', price: '$145.20', change: '+5.32%', up: true },
  { sym: 'ADA', price: '$0.612', change: '-0.94%', up: false },
  { sym: 'XRP', price: '$0.874', change: '+3.10%', up: true },
  { sym: 'DOT', price: '$9.37', change: '-1.44%', up: false },
  { sym: 'DOGE', price: '$0.182', change: '+7.22%', up: true },
  { sym: 'AVAX', price: '$38.91', change: '+4.08%', up: true },
];

const TickerBar: React.FC = () => {
  const items = [...TICKER, ...TICKER]; // duplicate for seamless loop
  return (
    <div className="ticker">
      <div className="ticker__track">
        {items.map((t, i) => (
          <div key={i} className="ticker__item">
            <span className="t-num-sm c-white" style={{ fontWeight: 600 }}>{t.sym}</span>
            <span className="t-num-sm c-muted">{t.price}</span>
            <span className={`t-num-sm ${t.up ? 'c-green' : 'c-red'}`}>{t.change}</span>
          </div>
        ))}
      </div>
    </div>
  );
};

const Home: React.FC = () => {
  return (
    <div>
      {/* ── Hero ────────────────────────────────────────────────── */}
      <section className="hero">
        <div
          className="hero__bg"
          style={{
            backgroundImage: 'url(https://images.unsplash.com/photo-1639762681057-408e52192e55?q=80&w=2232&auto=format&fit=crop)',
          }}
        />
        <div className="hero__overlay" />
        <div className="hero__content animate-up">
          <p className="t-caption hero__eyebrow" style={{ color: 'rgba(255,255,255,0.5)', letterSpacing: 4 }}>
            PRECISION. PERFORMANCE. CONTROL.
          </p>
          <h1 className="t-display-xl hero__title" style={{ marginBottom: 16 }}>
            THE DIGITAL<br />ASSET EXCHANGE
          </h1>
          <p className="t-body" style={{ maxWidth: 480, margin: '0 auto 48px', textAlign: 'center', color: 'rgba(255,255,255,0.55)' }}>
            Engineered for those who refuse to compromise. A platform of unrelenting precision — where every trade is an act of mastery.
          </p>
          <div className="hero__ctas">
            <Link to="/login?tab=register" className="btn btn--pill-fill">Enter the Nest</Link>
            <Link to="/exchange" className="btn btn--pill">Explore Markets</Link>
          </div>
        </div>

        {/* Scroll indicator */}
        <div style={{ position: 'absolute', bottom: 40, left: '50%', transform: 'translateX(-50%)', opacity: 0.4 }}>
          <p className="t-caption" style={{ letterSpacing: 3 }}>SCROLL</p>
        </div>
      </section>

      {/* ── Ticker ──────────────────────────────────────────────── */}
      <TickerBar />

      {/* ── Stats Band ──────────────────────────────────────────── */}
      <section className="band--dark">
        <div className="wrap">
          <div className="section-head">
            <p className="t-caption">PLATFORM STATISTICS</p>
            <p className="t-caption" style={{ opacity: 0.4 }}>LIVE</p>
          </div>
          <div className="stats-grid" style={{ borderTop: '1px solid var(--hairline)', borderLeft: '1px solid var(--hairline)' }}>
            {[
              { label: 'TOTAL VOLUME', value: '$2.4B', delta: '+18.3%', up: true },
              { label: 'ACTIVE TRADERS', value: '142K', delta: '+9.1%', up: true },
              { label: 'ASSETS LISTED', value: '340+', delta: '', up: true },
              { label: 'AVG EXECUTION', value: '< 12ms', delta: '', up: true },
            ].map((s, i) => (
              <div key={i} className="stat-cell" style={{ borderRight: '1px solid var(--hairline)', borderBottom: '1px solid var(--hairline)' }}>
                <p className="t-caption stat-cell__label">{s.label}</p>
                <p className="t-num stat-cell__value">{s.value}</p>
                {s.delta && (
                  <div className="stat-cell__change">
                    <span className={`t-num-sm ${s.up ? 'c-green' : 'c-red'}`}>{s.delta}</span>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── Feature Grid ────────────────────────────────────────── */}
      <section className="band">
        <div className="wrap">
          <div className="section-head">
            <h2 className="t-display-md">THE ARCHITECTURE</h2>
          </div>
        </div>
        <div className="feature-grid">
          {[
            {
              n: '01',
              title: 'REAL-TIME PRICING',
              body: 'CoinGecko-powered live price feeds cached via Redis, updating every 5 minutes. Your data is always current, always precise.',
            },
            {
              n: '02',
              title: 'PORTFOLIO ANALYTICS',
              body: 'Weighted average cost basis, unrealised P&L, per-asset breakdown — calculated with eight decimal places of precision.',
            },
            {
              n: '03',
              title: 'INSTANT EXECUTION',
              body: 'Orders route through a Kafka event bus to a dedicated exchange engine. Execution in milliseconds. Confirmation via email.',
            },
            {
              n: '04',
              title: 'INSTITUTIONAL SECURITY',
              body: 'Stateless JWT authentication, BCrypt password hashing, Redis token blacklisting, and Spring Security at every layer.',
            },
          ].map((f) => (
            <div key={f.n} className="feature-cell">
              <p className="t-caption" style={{ marginBottom: 32, opacity: 0.4 }}>{f.n}</p>
              <h3 className="t-display-sm" style={{ marginBottom: 24 }}>{f.title}</h3>
              <p className="t-body">{f.body}</p>
            </div>
          ))}
        </div>
      </section>

      {/* ── Full-Bleed Photography Band ──────────────────────────── */}
      <section style={{ position: 'relative', height: '70vh', overflow: 'hidden' }}>
        <div
          style={{
            position: 'absolute',
            inset: 0,
            backgroundImage: 'url(https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?q=80&w=2340&auto=format&fit=crop)',
            backgroundSize: 'cover',
            backgroundPosition: 'center 30%',
          }}
        />
        <div style={{ position: 'absolute', inset: 0, background: 'linear-gradient(90deg, rgba(0,0,0,0.85) 40%, rgba(0,0,0,0.2) 100%)' }} />
        <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', padding: '0 80px' }}>
          <div style={{ maxWidth: 540 }}>
            <p className="t-caption" style={{ marginBottom: 24, letterSpacing: 4 }}>THE NEST ADVANTAGE</p>
            <h2 className="t-display-lg" style={{ marginBottom: 32 }}>
              TRADE WITH<br />CLARITY
            </h2>
            <p className="t-body" style={{ marginBottom: 40, maxWidth: 400 }}>
              No noise. No distraction. Just the asset, the price, and your decision.
              The interface is the edge.
            </p>
            <Link to="/login?tab=register" className="btn btn--pill">Open an Account</Link>
          </div>
        </div>
      </section>

      {/* ── CTA Band ────────────────────────────────────────────── */}
      <section className="band text-center">
        <div className="wrap">
          <p className="t-caption" style={{ marginBottom: 24, letterSpacing: 4 }}>BEGIN NOW</p>
          <h2 className="t-display-lg" style={{ marginBottom: 48 }}>ENTER THE NEST</h2>
          <Link to="/login?tab=register" className="btn btn--pill">Create Account</Link>
        </div>
      </section>
    </div>
  );
};

export default Home;
