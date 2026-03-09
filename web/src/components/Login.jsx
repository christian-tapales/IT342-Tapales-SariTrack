import { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import { Mail, Lock, ShoppingCart, Eye, EyeOff } from 'lucide-react';
import bgImage from '../assets/Typical_sari-sari_store.jpg';

const Login = ({ onLoginSuccess }) => {
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', credentials);
      if (response.data.includes("successful")) {
        onLoginSuccess({ email: credentials.email, name: response.data.split("Welcome ")[1] });
        navigate('/dashboard');
      } else {
        alert(response.data);
      }
    } catch (error) {
      alert("Login failed. Check backend connection.");
    }
  };

  return (
    <div className="min-h-screen w-full flex items-center justify-center bg-cover bg-center bg-no-repeat relative" 
         style={{ backgroundImage: `url(${bgImage})` }}>
      
      {/* Dark Overlay for better text contrast */}
      <div className="absolute inset-0 bg-black/50 backdrop-blur-[1px]"></div>

      {/* Glassmorphism Login Card */}
      <div className="relative z-10 w-full max-w-md bg-white/95 backdrop-blur-md p-10 rounded-[2.5rem] shadow-2xl m-4 border border-white/20">
        
        <div className="flex flex-col items-center mb-8">
          <div className="flex items-center gap-2">
             <ShoppingCart className="w-10 h-10 text-emerald-600" />
             <h1 className="text-3xl font-bold text-slate-800 tracking-tight">
               Sari<span className="text-emerald-600">Track</span>
             </h1>
          </div>
          <p className="text-slate-500 text-sm mt-1">Sign in to manage your store</p>
        </div>

        <form onSubmit={handleLogin} className="space-y-5">
          {/* Email Field */}
          <div className="relative">
            <Mail className="absolute left-4 top-3.5 w-5 h-5 text-slate-400" />
            <input 
              type="email"
              placeholder="Email Address"
              onChange={e => setCredentials({...credentials, email: e.target.value})}
              className="w-full pl-12 pr-4 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl outline-none focus:ring-2 focus:ring-emerald-500 transition-all"
              required 
            />
          </div>

          {/* Password Field */}
          <div className="relative">
            <Lock className="absolute left-4 top-3.5 w-5 h-5 text-slate-400" />
            <input 
              type={showPassword ? "text" : "password"}
              placeholder="Password"
              onChange={e => setCredentials({...credentials, password: e.target.value})}
              className="w-full pl-12 pr-12 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl outline-none focus:ring-2 focus:ring-emerald-500 transition-all"
              required 
            />
            <button 
              type="button" 
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-4 top-3.5 text-slate-400 hover:text-emerald-600"
            >
              {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
            </button>
          </div>

          <button type="submit" className="w-full bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-4 rounded-2xl shadow-lg shadow-emerald-200 transition-transform active:scale-95 mt-2">
            Login
          </button>
        </form>

        <p className="text-center text-sm text-slate-600 mt-8">
          Need an account? <Link to="/register" className="text-emerald-600 font-bold hover:underline">Register here</Link>
        </p>
      </div>
    </div>
  );
};

export default Login;