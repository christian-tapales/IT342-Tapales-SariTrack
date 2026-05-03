import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import {
  LayoutDashboard,
  Package,
  ShoppingCart,
  BookOpen,
  Settings,
  Bell,
  UserCircle,
  LogOut,
  ChevronDown
} from 'lucide-react';

const Navbar = ({ user, onLogout }) => {
  const location = useLocation();
  const [isProfileOpen, setIsProfileOpen] = useState(false);

  const menuItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'Products', path: '/inventory', icon: Package },
    { name: 'Sales', path: '/sales', icon: ShoppingCart },
    { name: 'Listahan', path: '/listahan', icon: BookOpen },
  ];

  return (
    <nav className="h-20 bg-white border-b border-gray-100 flex items-center justify-between px-8 sticky top-0 z-50 shadow-sm">
      {/* Left: Branding */}
      <div className="flex items-center gap-3">
        <div className="bg-[#16A394] p-2 rounded-lg shadow-sm">
          <ShoppingCart className="text-white w-6 h-6" />
        </div>
        <span className="text-2xl font-bold tracking-tight text-gray-800">
          Sari <span className="text-[#16A394]">Track</span>
        </span>
      </div>

      {/* Middle: Navigation Links */}
      <div className="flex items-center gap-2">
        {menuItems.map((item) => {
          const Icon = item.icon;
          const isActive = location.pathname === item.path;
          return (
            <Link
              key={item.name}
              to={item.path}
              className={`flex items-center gap-2 px-5 py-2.5 rounded-xl transition-all duration-300 font-medium ${isActive
                  ? 'bg-teal-50 text-[#16A394]'
                  : 'text-gray-500 hover:bg-gray-50 hover:text-[#16A394]'
                }`}
            >
              <Icon size={20} strokeWidth={isActive ? 2.5 : 2} />
              <span>{item.name}</span>
            </Link>
          );
        })}
      </div>

      {/* Right: Actions */}
      <div className="flex items-center gap-4">
        <button className="p-2.5 text-gray-400 hover:text-[#16A394] hover:bg-teal-50 rounded-full transition-all duration-300 relative">
          <Bell size={22} />
          <span className="absolute top-2 right-2 w-2 h-2 bg-red-500 rounded-full border-2 border-white"></span>
        </button>
        
        <div className="h-8 w-px bg-gray-200 mx-2"></div>

        {/* Profile Dropdown */}
        <div className="relative">
          <button 
            onClick={() => setIsProfileOpen(!isProfileOpen)}
            className="flex items-center gap-3 pl-2 pr-2 py-1.5 rounded-full hover:bg-gray-50 transition-all duration-300 group"
          >
            <div className="text-right hidden sm:block">
              <p className="text-xs font-bold text-gray-700">{user?.name || "Vendor"}</p>
              <p className="text-[10px] text-gray-400 uppercase tracking-tighter">Store Owner</p>
            </div>
            <div className="bg-gray-100 p-1 rounded-full group-hover:bg-teal-100 transition-colors duration-300">
              <UserCircle size={28} className="text-gray-500 group-hover:text-[#16A394]" />
            </div>
            <ChevronDown size={14} className={`text-gray-400 transition-transform duration-300 ${isProfileOpen ? 'rotate-180' : ''}`} />
          </button>

          {/* Dropdown Menu */}
          {isProfileOpen && (
            <>
              <div 
                className="fixed inset-0 z-[-1]" 
                onClick={() => setIsProfileOpen(false)}
              ></div>
              <div className="absolute right-0 mt-2 w-56 bg-white rounded-2xl shadow-xl border border-gray-100 py-2 animate-in fade-in slide-in-from-top-2 duration-200 z-[100]">
                <div className="px-4 py-3 border-b border-gray-50 mb-1">
                  <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">Account Menu</p>
                </div>
                
                <button 
                  className="w-full flex items-center gap-3 px-4 py-3 text-sm text-gray-600 hover:bg-teal-50 hover:text-[#16A394] transition-all"
                  onClick={() => setIsProfileOpen(false)}
                >
                  <Settings size={18} />
                  <span>Store Settings</span>
                </button>

                <button 
                  className="w-full flex items-center gap-3 px-4 py-3 text-sm text-rose-600 hover:bg-rose-50 transition-all"
                  onClick={() => {
                    setIsProfileOpen(false);
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
    </nav>
  );
};

export default Navbar;
