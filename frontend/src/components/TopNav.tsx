import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

interface NavProps {
  isAuth?: boolean;
  onLogout?: () => void;
}

const TopNav: React.FC<NavProps> = ({ isAuth = false, onLogout }) => {
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 20);
    window.addEventListener('scroll', onScroll);
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  return (
    <>
      <nav className={`nav${scrolled ? ' nav--scrolled' : ''}`}>
        {/* Left */}
        <div className="nav__links">
          <Link to="/" className="nav__link t-nav">Markets</Link>
          <Link to="/exchange" className="nav__link t-nav">Exchange</Link>
        </div>

        {/* Centre — Wordmark */}
        <Link to="/" className="t-wordmark" style={{ textDecoration: 'none' }}>
          CRYPTONEST X
        </Link>

        {/* Right */}
        <div className="nav__right">
          {isAuth ? (
            <>
              <Link to="/portfolio" className="nav__link t-nav">Portfolio</Link>
              <Link to="/dashboard" className="nav__link t-nav">Dashboard</Link>
              <button className="btn btn--pill-sm" onClick={onLogout}>Sign Out</button>
            </>
          ) : (
            <>
              <Link to="/login" className="nav__link t-nav">Sign In</Link>
              <Link to="/login?tab=register" className="btn btn--pill-sm">Get Started</Link>
            </>
          )}
        </div>
      </nav>
    </>
  );
};

export default TopNav;
