import React from 'react';
import { Link } from 'react-router-dom';

const Footer: React.FC = () => (
  <footer style={{ background: 'var(--canvas)', borderTop: '1px solid var(--hairline)', padding: '72px 0 40px' }}>
    <div className="wrap">
      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1fr 1fr', gap: 48, marginBottom: 64 }}>
        {/* Brand */}
        <div>
          <p className="t-wordmark" style={{ marginBottom: 20 }}>CRYPTONEST X</p>
          <p className="t-body-sm" style={{ maxWidth: 260, lineHeight: 1.8 }}>
            A production-grade digital asset platform. Portfolio management,
            live exchange, and institutional-grade analytics.
          </p>
        </div>
        {/* Platform */}
        <div>
          <p className="t-caption" style={{ marginBottom: 20 }}>Platform</p>
          <div className="flex-col gap-12">
            <Link to="/exchange" className="t-nav" style={{ textDecoration: 'none', color: 'var(--muted)', fontSize: 11 }}>Exchange</Link>
            <Link to="/portfolio" className="t-nav" style={{ textDecoration: 'none', color: 'var(--muted)', fontSize: 11 }}>Portfolio</Link>
            <Link to="/dashboard" className="t-nav" style={{ textDecoration: 'none', color: 'var(--muted)', fontSize: 11 }}>Dashboard</Link>
          </div>
        </div>
        {/* Legal */}
        <div>
          <p className="t-caption" style={{ marginBottom: 20 }}>Legal</p>
          <div className="flex-col gap-12">
            <a href="#" className="t-nav" style={{ textDecoration: 'none', color: 'var(--muted)', fontSize: 11 }}>Terms</a>
            <a href="#" className="t-nav" style={{ textDecoration: 'none', color: 'var(--muted)', fontSize: 11 }}>Privacy</a>
            <a href="#" className="t-nav" style={{ textDecoration: 'none', color: 'var(--muted)', fontSize: 11 }}>Cookies</a>
          </div>
        </div>
        {/* Connect */}
        <div>
          <p className="t-caption" style={{ marginBottom: 20 }}>Connect</p>
          <div className="flex-col gap-12">
            <a href="#" className="t-nav" style={{ textDecoration: 'none', color: 'var(--muted)', fontSize: 11 }}>Twitter</a>
            <a href="#" className="t-nav" style={{ textDecoration: 'none', color: 'var(--muted)', fontSize: 11 }}>Discord</a>
            <a href="#" className="t-nav" style={{ textDecoration: 'none', color: 'var(--muted)', fontSize: 11 }}>GitHub</a>
          </div>
        </div>
      </div>

      <hr className="rule" />
      <div className="flex items-center justify-between mt-24">
        <p className="t-caption">© {new Date().getFullYear()} CRYPTONEST X. ALL RIGHTS RESERVED.</p>
        <p className="t-caption">BUILT WITH PRECISION</p>
      </div>
    </div>
  </footer>
);

export default Footer;
