const BASE_URL = 'http://localhost:8080/api';

async function request(path, options = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!res.ok) {
    throw new Error(`API error: ${res.status} ${res.statusText}`);
  }
  return res.json();
}

export const api = {
  getAccounts: () => request('/accounts'),
  syncAccounts: () => request('/accounts/sync', { method: 'POST' }),
  getTransactions: () => request('/transactions'),
  syncTransactions: () => request('/transactions/sync', { method: 'POST' }),
  categorizeTransactions: () => request('/transactions/categorize', { method: 'POST' }),
  getSummary: (month) => request(`/transactions/summary${month ? `?month=${month}` : ''}`),
  getBudgets: () => request('/budgets'),
  getBudgetAlerts: () => request('/budgets/alerts'),
  createBudget: (categoryId, monthlyLimit) =>
    request('/budgets', {
      method: 'POST',
      body: JSON.stringify({ categoryId, monthlyLimit }),
    }),
  getCategories: () => request('/categories'),
  getRecurringPayments: () => request('/recurring-payments'),
  connectBank: () => request('/auth/connect'),
};
