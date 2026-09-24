import { useState } from 'react';
import { api } from '../api/client';

export default function ConnectBankButton() {
  const [loading, setLoading] = useState(false);

  const handleConnect = async () => {
    setLoading(true);
    try {
      const authUrl = await api.connectBank();
      window.location.href = authUrl;
    } catch (err) {
      alert('Failed to start connection: ' + err.message);
      setLoading(false);
    }
  };

  return (
    <button onClick={handleConnect} disabled={loading} style={styles.button}>
      {loading ? 'Connecting...' : 'Connect Bank Account'}
    </button>
  );
}

const styles = {
  button: {
    padding: '10px 20px',
    fontSize: '16px',
    backgroundColor: '#2563eb',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
  },
};
