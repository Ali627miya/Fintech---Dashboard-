export default function TransactionList({ transactions }) {
  if (!transactions || transactions.length === 0) {
    return <p>No transactions yet. Sync your accounts to get started.</p>;
  }

  return (
    <table style={styles.table}>
      <thead>
        <tr>
          <th style={styles.th}>Date</th>
          <th style={styles.th}>Merchant</th>
          <th style={styles.th}>Description</th>
          <th style={styles.th}>Amount</th>
        </tr>
      </thead>
      <tbody>
        {transactions.map((t) => (
          <tr key={t.id}>
            <td style={styles.td}>{t.transactionDate}</td>
            <td style={styles.td}>{t.merchantName || '—'}</td>
            <td style={styles.td}>{t.description}</td>
            <td style={styles.td}>£{Math.abs(t.amount).toFixed(2)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

const styles = {
  table: { width: '100%', borderCollapse: 'collapse', marginTop: '10px' },
  th: { textAlign: 'left', borderBottom: '2px solid #e5e7eb', padding: '8px' },
  td: { borderBottom: '1px solid #f3f4f6', padding: '8px' },
};
