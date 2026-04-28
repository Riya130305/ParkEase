import React from 'react';
import { useEffect, useState } from 'react';
import { api } from '../api';
import { AdminPanel } from '../components/AdminPanel';

export function AdminPage() {
  const [slots, setSlots] = useState([]);
  const [message, setMessage] = useState('');

  async function loadSlots() {
    setMessage('');
    try {
      const { data } = await api.get('/slots');
      setSlots(data);
    } catch (err) {
      setMessage(err.response?.data?.message || 'Could not load slots');
    }
  }

  useEffect(() => {
    loadSlots();
  }, []);

  return (
    <>
      {message && <p className="message">{message}</p>}
      <AdminPanel slots={slots} onSlotsChanged={loadSlots} />
    </>
  );
}
