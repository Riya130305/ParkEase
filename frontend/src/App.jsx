import React from 'react';
import { useState } from 'react';
import { Navigate, Route, Routes, useNavigate } from 'react-router-dom';
import { Dashboard } from './components/Dashboard';
import { AdminPage } from './pages/AdminPage';
import { BookingsPage } from './pages/BookingsPage';
import { Login } from './components/Login';
import { AppLayout } from './components/AppLayout';

function ProtectedRoute({ user, children }) {
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

function LoginPage({ user, onLogin }) {
  const navigate = useNavigate();

  if (user) {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <Login
      onLogin={(data) => {
        onLogin(data);
        navigate('/dashboard');
      }}
    />
  );
}

export function App() {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user');
    return saved ? JSON.parse(saved) : null;
  });

  function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  }

  return (
    <Routes>
      <Route path="/login" element={<LoginPage user={user} onLogin={setUser} />} />
      <Route
        path="/"
        element={
          <ProtectedRoute user={user}>
            <AppLayout user={user} onLogout={logout} />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="bookings" element={<BookingsPage />} />
        <Route
          path="admin"
          element={user?.role === 'ADMIN' ? <AdminPage /> : <Navigate to="/dashboard" replace />}
        />
      </Route>
      <Route path="*" element={<Navigate to={user ? '/dashboard' : '/login'} replace />} />
    </Routes>
  );
}
