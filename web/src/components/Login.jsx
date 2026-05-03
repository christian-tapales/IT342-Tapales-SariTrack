import axios from 'axios';
import { Mail, Lock, ShoppingCart, Eye, EyeOff } from 'lucide-react';
import Input from './Input';
import { useState, useEffect } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';

const Login = ({ onLoginSuccess }) => {
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  // --- CATCH GOOGLE REDIRECT HERE ---
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    if (params.get('loginSuccess') === 'true') {
      const userData = { 
        name: params.get('name') || "Google User", 
        email: params.get('email') || "Synchronized",
        role: params.get('role') || "VENDOR"
      };
      
      onLoginSuccess(userData);
      navigate('/dashboard');
    }
  }, [location, onLoginSuccess, navigate]);

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', credentials);
      
      // Now handling JSON object instead of String
      if (response.data && typeof response.data === 'object') {
        onLoginSuccess(response.data);
        navigate('/dashboard');
      } else {
        alert(response.data || "Invalid response from server");
      }
    } catch (error) {
      alert("Login failed. Make sure your Spring Boot backend is running.");
    }
  };

  // --- ADDED GOOGLE OAUTH LOGIC ---
  const handleGoogleLogin = () => {
    // This directs the browser to the Spring Boot OAuth entry point we configured
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  return (
    <div className="bg-white/90 backdrop-blur-xl p-10 rounded-[2.5rem] shadow-2xl border border-white/20 text-slate-800 animate-in fade-in zoom-in duration-300">
      <div className="flex flex-col items-center mb-8 text-center">
        <div className="flex items-center gap-2 mb-2">
           <ShoppingCart className="w-10 h-10 text-[#16A394]" />
           <h1 className="text-3xl font-bold tracking-tight">
             Sari<span className="text-[#16A394]">Track</span>
           </h1>
        </div>
        <p className="text-slate-500 text-sm">Sign in to manage your store</p>
      </div>

      <form onSubmit={handleLogin} className="space-y-5">
        <Input 
          icon={Mail}
          type="email"
          placeholder="Email Address"
          value={credentials.email}
          onChange={e => setCredentials({...credentials, email: e.target.value})}
        />
        <Input 
          icon={Lock}
          type={showPassword ? "text" : "password"}
          placeholder="Password"
          value={credentials.password}
          onChange={e => setCredentials({...credentials, password: e.target.value})}
          showPasswordButton={showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
          onTogglePassword={() => setShowPassword(!showPassword)}
        />
        <button type="submit" className="w-full bg-[#16A394] hover:bg-[#0D7A6F] text-white font-bold py-4 rounded-2xl shadow-lg transition-all active:scale-95">
          Login
        </button>
      </form>

      {/* --- ADDED GOOGLE DIVIDER AND BUTTON --- */}
      <div className="relative my-6">
        <div className="absolute inset-0 flex items-center"><span className="w-full border-t border-slate-300"></span></div>
        <div className="relative flex justify-center text-xs uppercase"><span className="bg-white px-2 text-slate-500">Or continue with</span></div>
      </div>

      <button 
        onClick={handleGoogleLogin}
        type="button"
        className="w-full flex items-center justify-center gap-3 bg-white border border-slate-300 hover:bg-slate-50 text-slate-700 font-semibold py-3 rounded-2xl shadow-sm transition-all active:scale-95"
      >
        <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" className="w-5 h-5" alt="Google" />
        Google Account
      </button>

      <p className="text-center text-sm text-slate-600 mt-8">
        Need an account? <Link to="/register" className="text-[#16A394] font-bold hover:underline">Register</Link>
      </p>
    </div>
  );
};

export default Login;