export default function BudgetCard({ budget }) {
  const percent = Math.min(budget.percentUsed, 100);
  const barColor = budget.overBudget ? '#dc2626' : percent > 80 ? '#ca8a04' : '#16a34a';

  return (
    <div style={styles.card}>
      <div style={styles.header}>
        <strong>{budget.categoryName}</strong>
        <span>£{budget.spentSoFar.toFixed(2)} / £{budget.monthlyLimit.toFixed(2)}</span>
      </div>
      <div style={styles.barBackground}>
        <div style={{ ...styles.barFill, width: `${percent}%`, backgroundColor: barColor }} />
      </div>
      {budget.overBudget && <p style={styles.overText}>Over budget by £{(budget.spentSoFar - budget.monthlyLimit).toFixed(2)}</p>}
    </div>
  );
}

const styles = {
  card: { border: '1px solid #e5e7eb', borderRadius: '8px', padding: '12px', marginBottom: '10px' },
  header: { display: 'flex', justifyContent: 'space-between', marginBottom: '8px' },
  barBackground: { height: '10px', backgroundColor: '#f3f4f6', borderRadius: '5px', overflow: 'hidden' },
  barFill: { height: '100%', transition: 'width 0.3s' },
  overText: { color: '#dc2626', fontSize: '13px', marginTop: '6px' },
};
