import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import AuthLayout from './layouts/AuthLayout';
import DashboardLayout from './layouts/DashboardLayout';
import Login from './components/Login';
import Register from './components/Register';
import Dashboard from './components/Dashboard';
import Inventory from './pages/Inventory';
import PointOfSale from './pages/PointOfSale';
import Listahan from './pages/Listahan';
import Vendors from './pages/admin/Vendors';
import GlobalProducts from './pages/admin/GlobalProducts';
import Settings from './pages/admin/Settings';
import PaymentSuccess from './pages/PaymentSuccess';
import PaymentCancel from './pages/PaymentCancel';
import Transactions from './pages/Transactions';

function App() {
  // Load user from localStorage on startup
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('sariTrack_user');
    return savedUser ? JSON.parse(savedUser) : null;
  });

  // Save user to localStorage whenever it changes
  useEffect(() => {
    if (user) {
      localStorage.setItem('sariTrack_user', JSON.stringify(user));
    } else {
      localStorage.removeItem('sariTrack_user');
    }
  }, [user]);

  const params = new URLSearchParams(window.location.search);
  const isRedirectingFromGoogle = params.get('loginSuccess') === 'true';

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('sariTrack_user');
  };

  // Google Login Handshake (Global)
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    if (params.get('loginSuccess') === 'true' && !user) {
      const googleUser = {
        id: params.get('id'),
        name: params.get('name') || "Google User",
        token: params.get('token') || "",
        role: params.get('role') || "VENDOR"
      };
      setUser(googleUser);
      // Clean up URL
      window.history.replaceState({}, document.title, window.location.pathname);
    }
  }, [user]);

  return (
    <Router>
      <Routes>
        {/* Auth Group: Centered with sari-sari store background */}
        <Route element={<AuthLayout />}>
          <Route path="/login" element={<Login onLoginSuccess={setUser} />} />
          <Route path="/register" element={<Register />} />
        </Route>

        {/* Allow the route if user exists OR if it's a Google redirect */}
        <Route
          element={(user || isRedirectingFromGoogle) ?
            <DashboardLayout user={user} onLogout={handleLogout} /> :
            <Navigate to="/login" />
          }
        >

          {/*Pass onLoginSuccess={setUser} here */}
          <Route path="/dashboard" element={<Dashboard user={user} onLoginSuccess={setUser} />} />
          <Route path="/inventory" element={<Inventory user={user} />} />
          <Route path="/sales" element={<PointOfSale user={user} />} />
          <Route path="/listahan" element={<Listahan user={user} />} />

          {/* Admin Specific Routes */}
          <Route path="/admin/vendors" element={<Vendors />} />
          <Route path="/admin/products" element={<GlobalProducts />} />
          <Route path="/admin/settings" element={<Settings />} />
          <Route path="/payment-success" element={<PaymentSuccess />} />
          <Route path="/payment-cancel" element={<PaymentCancel />} />
          <Route path="/transactions" element={<Transactions user={user} />} />
        </Route>

        {/* Default redirect to login */}
        <Route path="/" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}

export default App;