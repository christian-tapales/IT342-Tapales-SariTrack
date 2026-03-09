import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useState } from 'react';
import Register from './components/Register';
import Login from './components/Login';
import Dashboard from './components/Dashboard';

// Import your asset here
import bgImage from './assets/Typical_sari-sari_store.jpg';

function App() {
  const [user, setUser] = useState(null);

  return (
    <Router>
      {/* GLOBAL LAYOUT WRAPPER */}
      <div 
        className="min-h-screen w-full flex items-center justify-center bg-cover bg-center bg-no-repeat relative overflow-hidden"
        style={{ backgroundImage: `url(${bgImage})` }}
      >
        
        {/* GLOBAL DARK OVERLAY (Ensures readability across all pages) */}
        <div className="absolute inset-0 bg-black/50 backdrop-blur-[2px]"></div>

        {/* PAGE CONTENT CONTAINER */}
        <div className="relative z-10 w-full max-w-md mx-4">
          <Routes>
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login onLoginSuccess={(data) => setUser(data)} />} />
            
            {/* Protected Route */}
            <Route 
              path="/dashboard" 
              element={user ? <Dashboard user={user} /> : <Navigate to="/login" />} 
            />
            
            {/* Default path */}
            <Route path="/" element={<Navigate to="/register" />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;