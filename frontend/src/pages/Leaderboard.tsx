import React, { useEffect, useState } from 'react';
import styles from './Leaderboard.module.css';
import ErrorBanner from '../components/ui/ErrorBanner';
import SkeletonLoader from '../components/ui/SkeletonLoader';

interface LeaderboardEntry {
  rank: number;
  userId: string;
  username: string;
  totalValue: number;
  pnl: number;
}

const Leaderboard: React.FC = () => {
  const [entries, setEntries] = useState<LeaderboardEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchLeaderboard = async () => {
    try {
      setLoading(true);
      setError(null);
      // Fallback for demo if API isn't running
      const res = await fetch('/api/portfolio/api/leaderboard', {
        headers: { 'X-User-Id': localStorage.getItem('userId') || '' }
      });
      if (!res.ok) throw new Error('Failed to fetch leaderboard');
      const data = await res.json();
      setEntries(data);
    } catch (err) {
      console.warn('API error, using mock data for demo', err);
      // Fallback data if API fails to avoid breaking UI completely
      setEntries([
        { rank: 1, userId: '1', username: 'Whale123', totalValue: 543000.5, pnl: 43000.5 },
        { rank: 2, userId: '2', username: 'CryptoKing', totalValue: 320000.0, pnl: 15000.0 },
        { rank: 3, userId: '3', username: 'DiamondHands', totalValue: 210000.2, pnl: 5000.2 },
        { rank: 4, userId: '4', username: 'Anon99', totalValue: 80000.0, pnl: -2000.0 },
      ]);
      setError('Live data currently unavailable. Showing mock data.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLeaderboard();
  }, []);

  if (loading) {
    return (
      <div className={styles.container}>
        <h2>Top Portfolios</h2>
        {[...Array(5)].map((_, i) => (
          <SkeletonLoader key={i} width="100%" height={60} style={{ marginBottom: 12 }} />
        ))}
      </div>
    );
  }

  return (
    <div className={styles.container}>
      <h2>Top Portfolios</h2>
      {error && <ErrorBanner message={error} onRetry={fetchLeaderboard} />}
      
      <div className={styles.tableWrapper}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Rank</th>
              <th>User</th>
              <th style={{ textAlign: 'right' }}>Total Value</th>
              <th style={{ textAlign: 'right' }}>PnL</th>
            </tr>
          </thead>
          <tbody>
            {entries.map(entry => {
              const pnlColor = entry.pnl >= 0 ? '#05b169' : '#cf202f';
              const pnlPrefix = entry.pnl > 0 ? '+' : '';
              
              let rankStyle = '';
              if (entry.rank === 1) rankStyle = styles.rankFirst;
              else if (entry.rank === 2) rankStyle = styles.rankSecond;
              else if (entry.rank === 3) rankStyle = styles.rankThird;
              
              return (
                <tr key={entry.userId} className={`${styles.row} ${rankStyle}`}>
                  <td className={styles.rankCol}>
                    <span className={styles.rankBadge}>{entry.rank}</span>
                  </td>
                  <td className={styles.userCol}>{entry.username}</td>
                  <td className={styles.valCol}>${entry.totalValue.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</td>
                  <td className={styles.pnlCol} style={{ color: pnlColor }}>
                    {pnlPrefix}${Math.abs(entry.pnl).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Leaderboard;
