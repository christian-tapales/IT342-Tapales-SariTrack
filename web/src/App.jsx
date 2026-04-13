import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useState } from 'react';
import AuthLayout from './layouts/AuthLayout';
import DashboardLayout from './layouts/DashboardLayout';
import Login from './components/Login';
import Register from './components/Register';
import Dashboard from './components/Dashboard';
import Inventory from './pages/Inventory';
import PointOfSale from './pages/PointOfSale'; // Add this import
import Listahan from './pages/Listahan';

function App() {
  const [user, setUser] = useState(null);

  const params = new URLSearchParams(window.location.search);
  const isRedirectingFromGoogle = params.get('loginSuccess') === 'true';

  const handleLogout = () => {
    setUser(null); // This effectively logs the user out
  };

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
          <Route path="/inventory" element={<Inventory />} />
          <Route path="/sales" element={<PointOfSale />} />
          <Route path="/listahan" element={<Listahan />} />
        </Route>

        {/* Default redirect to login */}
        <Route path="/" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}

export default App;