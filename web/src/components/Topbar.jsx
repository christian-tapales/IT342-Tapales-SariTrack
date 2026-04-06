import { useState } from 'react';
import { LogOut, Settings, UserCog, ChevronDown } from 'lucide-react';

const Topbar = ({ user, onLogout }) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const initial = user?.name ? user.name.charAt(0).toUpperCase() : 'V';

  return (
    <header className="bg-white shadow-sm h-16 flex items-center justify-between px-8 relative z-50">
      <div className="text-gray-500 text-sm">
         <span className="text-[#16A394] font-medium"></span>
      </div>
      
      <div className="flex items-center space-x-4">
        <div className="text-right hidden sm:block">
          <p className="text-sm font-semibold text-gray-700">{user?.name || "Vendor Name"}</p>
          <p className="text-xs text-gray-500">Store Owner</p>
        </div>
        
        {/* Profile Interaction Area */}
        <div className="relative">
          <button 
            onClick={() => setIsMenuOpen(!isMenuOpen)}
            className="flex items-center gap-2 p-1 rounded-full hover:bg-gray-50 transition-all active:scale-95"
          >
            <div className="h-10 w-10 rounded-full bg-[#E8F6F5] flex items-center justify-center text-[#16A394] font-bold shadow-sm border border-[#16A394]/10">
              {initial}
            </div>
            <ChevronDown size={16} className={`text-gray-400 transition-transform ${isMenuOpen ? 'rotate-180' : ''}`} />
          </button>

          {/* Dropdown Menu */}
          {isMenuOpen && (
            <>
              {/* Invisible backdrop to close menu when clicking outside */}
              <div 
                className="fixed inset-0 z-[-1]" 
                onClick={() => setIsMenuOpen(false)}
              ></div>
              
              <div className="absolute right-0 mt-2 w-48 bg-white rounded-2xl shadow-xl border border-slate-100 py-2 animate-in fade-in slide-in-from-top-2 duration-200">
                <div className="px-4 py-2 border-b border-slate-50 mb-1">
                  <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">Account</p>
                </div>
                
                <button 
                  className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50 hover:text-[#16A394] transition-colors"
                  onClick={() => {
                    console.log("Navigate to settings");
                    setIsMenuOpen(false);
                  }}
                >
                  <Settings size={18} />
                  <span>Settings</span>
                </button>

                <button 
                  className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-rose-600 hover:bg-rose-50 transition-colors"
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