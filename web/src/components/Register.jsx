import { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import { Mail, User, Lock, Eye, EyeOff, ShoppingCart } from 'lucide-react'; 
import Input from './Input'; // Ensure the path is correct
import bgImage from '../assets/Typical_sari-sari_store.jpg'; 

const Register = () => {
  const [formData, setFormData] = useState({ name: '', email: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/api/auth/register', formData);
      if (response.data === "User registered successfully!") {
        alert("Registration Successful!");
        navigate('/login');
      } else {
        alert(response.data);
      }
    } catch (error) {
      alert("Registration failed. Check backend connection.");
    }
  };

  return (
    <div className="min-h-screen w-full flex items-center justify-center bg-cover bg-center bg-no-repeat relative" 
         style={{ backgroundImage: `url(${bgImage})` }}>
      
      <div className="absolute inset-0 bg-black/40 backdrop-blur-[2px]"></div>

      <div className="relative z-10 w-full max-w-md bg-white/90 backdrop-blur-md p-10 rounded-[2.5rem] shadow-2xl m-4 border border-white/20">
        
        <div className="flex flex-col items-center mb-8">
          <div className="flex items-center gap-2">
             <ShoppingCart className="w-10 h-10 text-emerald-600" />
             <h1 className="text-3xl font-bold text-slate-800 tracking-tight">
               Sari<span className="text-emerald-600">Track</span>
             </h1>
          </div>
          <p className="text-slate-500 text-sm mt-1">Inventory for your local store</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <Input 
            icon={User}
            type="text"
            placeholder="Full Name"
            value={formData.name}
            onChange={(e) => setFormData({...formData, name: e.target.value})}
          />

          <Input 
            icon={Mail}
            type="email"
            placeholder="Email Address"
            value={formData.email}
            onChange={(e) => setFormData({...formData, email: e.target.value})}
          />

          <Input 
            icon={Lock}
            type={showPassword ? "text" : "password"}
            placeholder="Password"
            value={formData.password}
            onChange={(e) => setFormData({...formData, password: e.target.value})}
            showPasswordButton={showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
            onTogglePassword={() => setShowPassword(!showPassword)}
          />

          <button type="submit" className="w-full bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-4 rounded-2xl shadow-lg shadow-emerald-200 transition-all active:scale-95 mt-4">
            Create Account
          </button>
        </form>

        <p className="text-center text-sm text-slate-600 mt-8">
          Already have an account? <Link to="/login" className="text-emerald-600 font-bold hover:underline">Sign in</Link>
        </p>
      </div>
    </div>
  );
};

export default Register;