import { useEffect, useState } from 'react';
import { api } from '../api/client';
import ConnectBankButton from './ConnectBankButton';
import SpendingChart from './SpendingChart';
import TransactionList from './TransactionList';
import BudgetCard from './BudgetCard';

export default function Dashboard() {
  const [accounts, setAccounts] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [summary, setSummary] = useState([]);
  const [budgets, setBudgets] = useState([]);
  const [loading, setLoading] = useState(false);

  const loadData = async () => {
    try {
      const [acc, tx, sum, bud] = await Promise.all([
        api.getAccounts(),
        api.getTransactions(),
        api.getSummary(),
        api.getBudgetAlerts(),
      ]);
      setAccounts(acc);
      setTransactions(tx);
      setSummary(sum);
      setBudgets(bud);
    } catch (err) {
      console.error('Failed to load data:', err);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleSync = async () => {
    setLoading(true);
    try {
      await api.syncAccounts();
      await api.syncTransactions();
      await api.categorizeTransactions();
      await loadData();
    } catch (err) {
      alert('Sync failed: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const totalBalance = accounts.reduce((sum, a) => sum + a.balance, 0);

  return (
    <div style={styles.container}>
      <header style={styles.header}>
        <h1>Finance Dashboard</h1>
        <ConnectBankButton />
      </header>

      <div style={styles.summaryBar}>
        <div style={styles.balanceCard}>
          <span style={styles.balanceLabel}>Total Balance</span>
          <span style={styles.balanceValue}>£{totalBalance.toFixed(2)}</span>
        </div>
        <button onClick={handleSync} disabled={loading} style={styles.syncButton}>
          {loading ? 'Syncing...' : 'Sync Accounts & Transactions'}
        </button>
      </div>

      <div style={styles.grid}>
        <section style={styles.section}>
          <h2>Spending by Category</h2>
          <SpendingChart data={summary} />
        </section>

        <section style={styles.section}>
          <h2>Budgets</h2>
          {budgets.length === 0 ? <p>No budgets set yet.</p> : budgets.map((b) => <BudgetCard key={b.categoryId} budget={b} />)}
        </section>
      </div>

      <section style={styles.section}>
        <h2>Recent Transactions</h2>
        <TransactionList transactions={transactions.slice(0, 20)} />
      </section>
    </div>
  );
}

const styles = {
  container: { maxWidth: '1000px', margin: '0 auto', padding: '20px', fontFamily: 'system-ui, sans-serif' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' },
  summaryBar: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' },
  balanceCard: { display: 'flex', flexDirection: 'column' },
  balanceLabel: { fontSize: '14px', color: '#6b7280' },
  balanceValue: { fontSize: '32px', fontWeight: 'bold' },
  syncButton: { padding: '10px 16px', backgroundColor: '#16a34a', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer' },
  grid: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '30px' },
  section: { border: '1px solid #e5e7eb', borderRadius: '8px', padding: '16px' },
};
