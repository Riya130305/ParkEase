import React, { useEffect, useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import axios from 'axios';
import { LogOut, ParkingCircle, RefreshCcw } from 'lucide-react';
import './styles.css';

const api = axios.create({
  baseURL: 'http://localhost:8080',
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

function toInputDate(date) {
  const offset = date.getTimezoneOffset() * 60000;
  return new Date(date.getTime() - offset).toISOString().slice(0, 16);
}

function withSeconds(value) {
  return value.length === 16 ? `${value}:00` : value;
}

function Login({ onLogin }) {
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({ name: '', email: '', password: '' });
  const [error, setError] = useState('');

  async function submit(event) {
    event.preventDefault();
    setError('');
    try {
      const endpoint = mode === 'login' ? '/auth/login' : '/auth/register';
      const payload = mode === 'login'
        ? { email: form.email, password: form.password }
        : form;
      const { data } = await api.post(endpoint, payload);
      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify(data));
      onLogin(data);
    } catch (err) {
      setError(err.response?.data?.message || 'Authentication failed');
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-panel">
        <div className="brand-row">
          <ParkingCircle size={32} />
          <h1>Parking System</h1>
        </div>

        <div className="mode-switch">
          <button className={mode === 'login' ? 'active' : ''} onClick={() => setMode('login')}>
            Login
          </button>
          <button className={mode === 'register' ? 'active' : ''} onClick={() => setMode('register')}>
            Register
          </button>
        </div>

        <form onSubmit={submit}>
          {mode === 'register' && (
            <label>
              Name
              <input
                value={form.name}
                onChange={(event) => setForm({ ...form, name: event.target.value })}
                required
              />
            </label>
          )}
          <label>
            Email
            <input
              type="email"
              value={form.email}
              onChange={(event) => setForm({ ...form, email: event.target.value })}
              required
            />
          </label>
          <label>
            Password
            <input
              type="password"
              value={form.password}
              onChange={(event) => setForm({ ...form, password: event.target.value })}
              minLength={6}
              required
            />
          </label>
          {error && <p className="error">{error}</p>}
          <button className="primary" type="submit">
            {mode === 'login' ? 'Login' : 'Create account'}
          </button>
        </form>
      </section>
    </main>
  );
}

function Dashboard({ user, onLogout }) {
  const now = useMemo(() => new Date(), []);
  const [startTime, setStartTime] = useState(toInputDate(new Date(now.getTime() + 60 * 60 * 1000)));
  const [endTime, setEndTime] = useState(toInputDate(new Date(now.getTime() + 3 * 60 * 60 * 1000)));
  const [availableSlots, setAvailableSlots] = useState([]);
  const [allSlots, setAllSlots] = useState([]);
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [bookings, setBookings] = useState([]);
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);

  async function loadData() {
    setLoading(true);
    setMessage('');
    try {
      const [slotsResponse, availableResponse, bookingsResponse] = await Promise.all([
        api.get('/slots'),
        api.get('/slots/available', { params: { start: withSeconds(startTime), end: withSeconds(endTime) } }),
        api.get('/booking/my'),
      ]);
      setAllSlots(slotsResponse.data);
      setAvailableSlots(availableResponse.data);
      setBookings(bookingsResponse.data);
      setSelectedSlot(null);
    } catch (err) {
      setMessage(err.response?.data?.message || 'Could not fetch slots');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadData();
  }, []);

  async function bookSlot() {
    if (!selectedSlot) {
      setMessage('Select a slot first');
      return;
    }

    try {
      const { data } = await api.post('/booking', {
        slotId: selectedSlot.id,
        startTime: withSeconds(startTime),
        endTime: withSeconds(endTime),
      });
      setMessage(`Booked ${data.slotNumber}. Cost: ₹${data.cost}`);
      await loadData();
    } catch (err) {
      setMessage(err.response?.data?.message || 'Booking failed');
    }
  }

  async function cancelBooking(id) {
    await api.delete(`/booking/${id}`);
    await loadData();
  }

  const availableIds = new Set(availableSlots.map((slot) => slot.id));

  return (
    <main className="dashboard">
      <header>
        <div>
          <p>Welcome, {user?.name || 'User'}</p>
          <h1>Book a Parking Slot</h1>
        </div>
        <button className="icon-button" onClick={onLogout} title="Logout">
          <LogOut size={18} />
        </button>
      </header>

      <section className="controls">
        <label>
          Start
          <input type="datetime-local" value={startTime} onChange={(event) => setStartTime(event.target.value)} />
        </label>
        <label>
          End
          <input type="datetime-local" value={endTime} onChange={(event) => setEndTime(event.target.value)} />
        </label>
        <button onClick={loadData}>
          <RefreshCcw size={16} />
          Check slots
        </button>
      </section>

      <section className="slot-section">
        <div className="section-title">
          <h2>Slots</h2>
          <span>{availableSlots.length} available</span>
        </div>

        <div className="slot-grid">
          {allSlots.map((slot) => {
            const isAvailable = availableIds.has(slot.id);
            const isSelected = selectedSlot?.id === slot.id;
            return (
              <button
                key={slot.id}
                disabled={!isAvailable}
                className={`slot ${isAvailable ? 'available' : 'booked'} ${isSelected ? 'selected' : ''}`}
                onClick={() => setSelectedSlot(slot)}
              >
                <strong>{slot.slotNumber}</strong>
                <small>{slot.type}</small>
              </button>
            );
          })}
        </div>
      </section>

      {message && <p className="message">{message}</p>}

      <div className="action-row">
        <button className="primary" onClick={bookSlot} disabled={loading || !selectedSlot}>
          Book selected slot
        </button>
      </div>

      <section className="bookings">
        <h2>My Bookings</h2>
        {bookings.length === 0 ? (
          <p className="muted">No bookings yet.</p>
        ) : (
          bookings.map((booking) => (
            <div className="booking-row" key={booking.id}>
              <div>
                <strong>{booking.slotNumber}</strong>
                <span>{booking.startTime} to {booking.endTime}</span>
                <small>{booking.status} · ₹{booking.cost}</small>
              </div>
              {booking.status === 'BOOKED' && (
                <button onClick={() => cancelBooking(booking.id)}>Cancel</button>
              )}
            </div>
          ))
        )}
      </section>
    </main>
  );
}

function App() {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user');
    return saved ? JSON.parse(saved) : null;
  });

  function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  }

  return user ? <Dashboard user={user} onLogout={logout} /> : <Login onLogin={setUser} />;
}

createRoot(document.getElementById('root')).render(<App />);
