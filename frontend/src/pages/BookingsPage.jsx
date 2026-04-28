import React from 'react';
import { useEffect, useState } from 'react';
import { api } from '../api';
import { BookingList } from '../components/BookingList';

export function BookingsPage() {
  const [bookings, setBookings] = useState([]);
  const [statusFilter, setStatusFilter] = useState('');
  const [message, setMessage] = useState('');

  async function loadBookings() {
    setMessage('');
    try {
      const params = statusFilter ? { status: statusFilter } : {};
      const { data } = await api.get('/booking/my', { params });
      setBookings(data);
    } catch (err) {
      setMessage(err.response?.data?.message || 'Could not load bookings');
    }
  }

  useEffect(() => {
    loadBookings();
  }, [statusFilter]);

  async function cancelBooking(id) {
    await api.delete(`/booking/${id}`);
    await loadBookings();
  }

  return (
    <>
      {message && <p className="message">{message}</p>}
      <BookingList
        bookings={bookings}
        statusFilter={statusFilter}
        onStatusFilterChange={setStatusFilter}
        onCancelBooking={cancelBooking}
      />
    </>
  );
}
