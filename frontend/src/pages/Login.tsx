import React, { useState } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';

interface LoginProps {
  onLogin: () => void;
}

const Login: React.FC<LoginProps> = ({ onLogin }) => {
  const [params] = useSearchParams();
  const [tab, setTab] = useState<'signin' | 'register'>(
    params.get('tab') === 'register' ? 'register' : 'signin'
  );
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    // Simulate API call — replace with real auth-service call
    await new Promise((r) => setTimeout(r, 900));
    if (email && password) {
      onLogin();
      navigate('/dashboard');
    } else {
      setError('Please fill in all required fields.');
    }
    setLoading(false);
  };

  return (
    <div className="auth-page">
      {/* ── Visual side ─────────────────────────────────────── */}
      <div
        className="auth-page__visual"
        style={{
          backgroundImage:
            'url(https://images.unsplash.com/photo-1642790106117-e829e14a795f?q=80&w=2232&auto=format&fit=crop)',
        }}
      >
        <div className="auth-page__visual-overlay">
          <p className="t-caption" style={{ marginBottom: 16, letterSpacing: 4 }}>
            CRYPTONEST X
          </p>
          <h2 className="t-display-lg" style={{ maxWidth: 340, lineHeight: 1.05 }}>
            ENGINEERED FOR MASTERY
          </h2>
        </div>
      </div>

      {/* ── Form side ───────────────────────────────────────── */}
      <div className="auth-page__form-wrap">
        {/* Wordmark */}
        <Link to="/" style={{ textDecoration: 'none', display: 'block', marginBottom: 64 }}>
          <p className="t-wordmark">CRYPTONEST X</p>
        </Link>

        {/* Tabs */}
        <div className="tabs" style={{ marginBottom: 48 }}>
          <button
            className={`tab-btn ${tab === 'signin' ? 'tab-btn--active' : ''}`}
            onClick={() => setTab('signin')}
          >
            Sign In
          </button>
          <button
            className={`tab-btn ${tab === 'register' ? 'tab-btn--active' : ''}`}
            onClick={() => setTab('register')}
          >
            Create Account
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          {/* Register-only fields */}
          {tab === 'register' && (
            <div className="grid-2" style={{ marginBottom: 32 }}>
              <div className="field">
                <label className="field__label">First Name</label>
                <input
                  className="field__input"
                  placeholder="John"
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                />
              </div>
              <div className="field">
                <label className="field__label">Last Name</label>
                <input
                  className="field__input"
                  placeholder="Doe"
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                />
              </div>
            </div>
          )}

          <div className="field" style={{ marginBottom: 32 }}>
            <label className="field__label">Email Address</label>
            <input
              className="field__input"
              type="email"
              placeholder="john@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <div className="field" style={{ marginBottom: 48 }}>
            <label className="field__label">Password</label>
            <input
              className="field__input"
              type="password"
              placeholder="••••••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          {error && (
            <p
              className="t-num-sm c-red"
              style={{ marginBottom: 24, fontFamily: 'var(--mono)' }}
            >
              {error}
            </p>
          )}

          <button
            type="submit"
            className="btn btn--pill-fill w-full"
            disabled={loading}
            style={{ justifyContent: 'center' }}
          >
            {loading ? 'AUTHENTICATING...' : tab === 'signin' ? 'SIGN IN' : 'CREATE ACCOUNT'}
          </button>

          {tab === 'signin' && (
            <p className="t-label text-center" style={{ marginTop: 24 }}>
              <a href="#" style={{ color: 'var(--muted)', textDecoration: 'none' }}>
                Forgot Password?
              </a>
            </p>
          )}
        </form>

        <p className="t-caption" style={{ marginTop: 80, color: 'var(--faint)' }}>
          By continuing you agree to our Terms of Service and Privacy Policy.
        </p>
      </div>
    </div>
  );
};

export default Login;
