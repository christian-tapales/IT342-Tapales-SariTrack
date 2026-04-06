import { useState } from 'react';
import axios from 'axios'; // 1. Added axios import
import { useNavigate, Link } from 'react-router-dom'; // 2. Added useNavigate import
import { Mail, Lock, ShoppingCart, Eye, EyeOff } from 'lucide-react';
import Input from './Input';

const Login = ({ onLoginSuccess }) => {
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate(); // 3. Initialized navigate hook

  // 4. Added the logic to connect to your Spring Boot backend
  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      // Send credentials to AuthController.java
      const response = await axios.post('http://localhost:8080/api/auth/login', credentials);
      
      // Based on your backend, a successful login returns a string containing "successful"
      if (response.data.includes("successful")) {
        // Extract the name from "Login successful! Welcome [Name]"
        const userName = response.data.split("Welcome ")[1];
        
        // CRITICAL: This updates the state in App.jsx to unlock the dashboard
        onLoginSuccess({ email: credentials.email, name: userName });
        
        // Redirect to the dashboard
        navigate('/dashboard');
      } else {
        alert(response.data);
      }
    } catch (error) {
      alert("Login failed. Make sure your Spring Boot backend is running on port 8080.");
    }
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

      {/* 5. Added onSubmit={handleLogin} to the form */}
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

      <p className="text-center text-sm text-slate-600 mt-8">
        Need an account? <Link to="/register" className="text-[#16A394] font-bold hover:underline">Register</Link>
      </p>
    </div>
  );
};

export default Login;