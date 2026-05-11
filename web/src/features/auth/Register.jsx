import { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import { Mail, User, Lock, Eye, EyeOff, ShoppingCart } from 'lucide-react'; 
import Input from '../../core/components/Input'; 

const Register = () => {
  const [formData, setFormData] = useState({
     name: '', 
     email: '', 
     password: '',
     confirmPassword: ''
    });
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    
    e.preventDefault();


    if(formData.password !== formData.confirmPassword){
      alert("Passwords do not match!");
      return;
    }

    try {
      // Destructure to avoid sending confirmPassword to the backend
      const { confirmPassword, ...registerData } = formData;
      const response = await axios.post('http://localhost:8080/api/auth/register', registerData);
      
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
    /* NOTE: We removed the outer <div> that had the background image 
       because AuthLayout.jsx handles that now.
    */
    <div className="bg-white/90 dark:bg-slate-900/90 backdrop-blur-xl p-10 rounded-[2.5rem] shadow-2xl border border-white/20 dark:border-slate-800 text-slate-800 dark:text-slate-100 animate-in fade-in zoom-in duration-300 transition-colors">
      
      {/* Header Section */}
      <div className="flex flex-col items-center mb-8 text-center">
        <div className="flex items-center gap-2 mb-2">
           <ShoppingCart className="w-10 h-10 text-[#16A394]" />
           <h1 className="text-3xl font-bold tracking-tight dark:text-white">
             Sari<span className="text-[#16A394]">Track</span>
           </h1>
        </div>
        <h2 className="text-xl font-semibold text-slate-700 dark:text-slate-200">Create Account</h2>
        <p className="text-slate-500 dark:text-slate-400 text-sm mt-1">Join SariTrack to manage your store today.</p>
      </div>

      {/* Registration Form */}
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

        {/* New Confirm Password Field */}
        <Input 
          icon={Lock}
          type={showPassword ? "text" : "password"}
          placeholder="Confirm Password"
          value={formData.confirmPassword}
          onChange={(e) => setFormData({...formData, confirmPassword: e.target.value})}
          showPasswordButton={showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
          onTogglePassword={() => setShowPassword(!showPassword)}
        />

        <button 
          type="submit" 
          className="w-full bg-[#16A394] hover:bg-[#0D7A6F] text-white font-bold py-4 rounded-2xl shadow-lg shadow-teal-600/20 transition-all active:scale-95 mt-4"
        >
          Create Account
        </button>
      </form>

      {/* Footer Link */}
      <p className="text-center text-sm text-slate-600 dark:text-slate-400 mt-8">
        Already have an account? <Link to="/login" className="text-[#16A394] font-bold hover:underline">Login</Link>
      </p>
    </div>
  );
};

export default Register;