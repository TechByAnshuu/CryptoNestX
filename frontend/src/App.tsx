import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import TopNav from './components/TopNav';
import Footer from './components/Footer';
import Home from './pages/Home';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Portfolio from './pages/Portfolio';
import Exchange from './pages/Exchange';
import './index.css';

const AppContent: React.FC = () => {
  const [isAuth, setIsAuth] = useState(false);

  return (
    <Router>
      <RouteWrapper isAuth={isAuth} onLogin={() => setIsAuth(true)} onLogout={() => setIsAuth(false)} />
    </Router>
  );
};

interface WrapperProps {
  isAuth: boolean;
  onLogin: () => void;
  onLogout: () => void;
}

// Separate so we can access useLocation inside the Router
const RouteWrapper: React.FC<WrapperProps> = ({ isAuth, onLogin, onLogout }) => {
  return (
    <>
      <TopNav isAuth={isAuth} onLogout={onLogout} />
      <Routes>
        {/* Public */}
        <Route path="/" element={<><Home /><Footer /></>} />
        <Route path="/login" element={<Login onLogin={onLogin} />} />
        <Route path="/exchange" element={<><Exchange /><Footer /></>} />

        {/* Protected */}
        <Route
          path="/dashboard"
          element={isAuth ? <><Dashboard /><Footer /></> : <Navigate to="/login" replace />}
        />
        <Route
          path="/portfolio"
          element={isAuth ? <><Portfolio /><Footer /></> : <Navigate to="/login" replace />}
        />

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  );
};

export default AppContent;
