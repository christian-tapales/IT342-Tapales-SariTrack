import { useState } from 'react';
import { LogOut, Settings, UserCog, ChevronDown } from 'lucide-react';

const Topbar = ({ user, onLogout }) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const isAdmin = user?.role === 'ADMIN';
  const initial = user?.name ? user.name.charAt(0).toUpperCase() : (isAdmin ? 'A' : 'V');

  return (
    <header className={`${isAdmin ? 'bg-slate-950 border-b border-white/5' : 'bg-white shadow-sm'} h-16 flex items-center justify-between px-8 relative z-50`}>
      <div className="flex items-center gap-2">
        <div className={`h-2 w-2 rounded-full animate-pulse ${isAdmin ? 'bg-teal-400' : 'bg-teal-500'}`}></div>
        <span className={`text-[10px] font-bold uppercase tracking-widest ${isAdmin ? 'text-teal-400' : 'text-teal-600'}`}>
          {isAdmin ? 'Platform Control' : 'System Online'}
        </span>
      </div>
      
      <div className="flex items-center space-x-4">
        <div className="text-right hidden sm:block">
          <p className={`text-sm font-bold ${isAdmin ? 'text-slate-200' : 'text-slate-800'}`}>
            {user?.name || (isAdmin ? "Super Admin" : "User")}
          </p>
          <p className={`text-[10px] font-medium uppercase tracking-tighter ${isAdmin ? 'text-slate-500' : 'text-slate-400'}`}>
            {isAdmin ? 'Root Administrator' : 'Store Owner'}
          </p>
        </div>
        
        {/* Profile Interaction Area */}
        <div className="relative">
          <button 
            onClick={() => setIsMenuOpen(!isMenuOpen)}
            className={`flex items-center gap-2 p-1 rounded-full transition-all active:scale-95 ${isAdmin ? 'hover:bg-white/5' : 'hover:bg-gray-50'}`}
          >
            <div className={`h-10 w-10 rounded-xl flex items-center justify-center font-bold shadow-lg border ${
              isAdmin 
                ? 'bg-teal-500/10 text-teal-400 border-teal-500/20' 
                : 'bg-[#E8F6F5] text-[#16A394] border-[#16A394]/10'
            }`}>
              {initial}
            </div>
            <ChevronDown size={16} className={`transition-transform ${isMenuOpen ? 'rotate-180' : ''} ${isAdmin ? 'text-slate-500' : 'text-gray-400'}`} />
          </button>

          {/* Dropdown Menu */}
          {isMenuOpen && (
            <>
              <div 
                className="fixed inset-0 z-[-1]" 
                onClick={() => setIsMenuOpen(false)}
              ></div>
              
              <div className={`absolute right-0 mt-2 w-48 rounded-2xl shadow-2xl border py-2 animate-in fade-in slide-in-from-top-2 duration-200 ${
                isAdmin 
                  ? 'bg-slate-900 border-white/10 text-slate-200' 
                  : 'bg-white border-slate-100 text-slate-700'
              }`}>
                <div className={`px-4 py-2 border-b mb-1 ${isAdmin ? 'border-white/5' : 'border-slate-50'}`}>
                  <p className="text-xs font-bold text-slate-500 uppercase tracking-wider">Account</p>
                </div>
                
                <button 
                  className={`w-full flex items-center gap-3 px-4 py-2.5 text-sm transition-colors ${
                    isAdmin ? 'hover:bg-white/5 hover:text-teal-400' : 'hover:bg-slate-50 hover:text-[#16A394]'
                  }`}
                  onClick={() => setIsMenuOpen(false)}
                >
                  <Settings size={18} />
                  <span>Settings</span>
                </button>

                <button 
                  className={`w-full flex items-center gap-3 px-4 py-2.5 text-sm transition-colors ${
                    isAdmin ? 'hover:bg-rose-500/10 text-rose-400' : 'hover:bg-rose-50 text-rose-600'
                  }`}
                  onClick={() => {
                    setIsMenuOpen(false);
                    onLogout();
                  }}
                >
                  <LogOut size={18} />
                  <span>Log Out</span>
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </header>
  );
};

export default Topbar;