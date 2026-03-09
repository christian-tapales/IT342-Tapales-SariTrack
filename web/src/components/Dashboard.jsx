import { ShoppingCart, User, Mail, ShieldCheck, LogOut } from 'lucide-react';
import bgImage from '../assets/Typical_sari-sari_store.jpg';

// Reusable sub-component for Profile Info Rows
const InfoRow = ({ icon: Icon, label, value, colorClass }) => (
  <div className="flex items-center gap-4">
    <div className={`p-3 ${colorClass} rounded-2xl`}>
      <Icon size={24} />
    </div>
    <div>
      <p className="text-xs text-slate-400 font-medium uppercase tracking-wide">{label}</p>
      <p className="text-lg font-bold text-slate-700 leading-tight">{value}</p>
    </div>
  </div>
);

const Dashboard = ({ user }) => {
  return (
    <div className="min-h-screen w-full flex items-center justify-center bg-cover bg-center bg-no-repeat relative" 
         style={{ backgroundImage: `url(${bgImage})` }}>
      
      {/* Dark Overlay for Readability */}
      <div className="absolute inset-0 bg-black/50 backdrop-blur-[2px]"></div>

      {/* Glassmorphism Dashboard Card */}
      <div className="relative z-10 w-full max-w-lg bg-white/95 backdrop-blur-md p-10 rounded-[2.5rem] shadow-2xl m-4 border border-white/20 text-slate-800">
        
        {/* Header */}
        <div className="flex flex-col items-center mb-8 text-center">
          <div className="flex items-center gap-2 mb-2">
             <ShoppingCart className="w-10 h-10 text-emerald-600" />
             <h1 className="text-3xl font-bold tracking-tight">
               Sari<span className="text-emerald-600">Track</span>
             </h1>
          </div>
          <h2 className="text-xl font-semibold text-slate-700">Welcome to your Dashboard</h2>
          <p className="text-slate-500 text-sm">Managing your store's inventory starts here.</p>
        </div>

        {/* User Profile Section using Reusable InfoRows */}
        <div className="bg-slate-50/50 rounded-3xl p-6 border border-slate-100 space-y-4">
          <h3 className="text-sm font-bold uppercase tracking-wider text-slate-400 mb-2">Vendor Profile</h3>
          
          <InfoRow 
            icon={User} 
            label="Full Name" 
            value={user?.name || "Guest User"} 
            colorClass="bg-emerald-100 text-emerald-600" 
          />
          
          <InfoRow 
            icon={Mail} 
            label="Email Address" 
            value={user?.email || "No email provided"} 
            colorClass="bg-blue-100 text-blue-600" 
          />
          
          <InfoRow 
            icon={ShieldCheck} 
            label="Account Role" 
            value="Vendor (Admin)" 
            colorClass="bg-amber-100 text-amber-600" 
          />
        </div>

        {/* Action Buttons */}
        <div className="mt-8 grid grid-cols-1 gap-3">
          <button 
            onClick={() => window.location.reload()} 
            className="flex items-center justify-center gap-2 w-full bg-slate-800 hover:bg-slate-900 text-white font-bold py-4 rounded-2xl shadow-lg transition-all active:scale-95"
          >
            <LogOut size={20} />
            Logout from Session
          </button>
        </div>

      </div>
    </div>
  );
};

export default Dashboard;