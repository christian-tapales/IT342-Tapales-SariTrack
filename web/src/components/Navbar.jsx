import { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import api from '../api';
import {
  LayoutDashboard,
  Package,
  ShoppingCart,
  BookOpen,
  Settings,
  Bell,
  UserCircle,
  LogOut,
  ChevronDown,
  X,
  Info,
  AlertTriangle,
  CheckCircle2,
  History,
  Sun,
  Moon
} from 'lucide-react';

const Navbar = ({ user, onLogout }) => {
  const location = useLocation();
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [isDark, setIsDark] = useState(() => {
    return document.documentElement.classList.contains('dark') || 
           localStorage.getItem('sariTrack_theme') === 'dark';
  });

  useEffect(() => {
    if (isDark) {
      document.documentElement.classList.add('dark');
      localStorage.setItem('sariTrack_theme', 'dark');
    } else {
      document.documentElement.classList.remove('dark');
      localStorage.setItem('sariTrack_theme', 'light');
    }
  }, [isDark]);

  const fetchNotifications = async () => {
    if (!user?.id) return;
    try {
      const response = await api.get(`/notifications?vendorId=${user.id}`);
      setNotifications(response.data);
    } catch (error) {
      console.error("Error fetching notifications", error);
    }
  };

  useEffect(() => {
    const syncAndFetch = async () => {
      if (!user?.id) return;
      try {
        await api.post(`/notifications/sync?vendorId=${user.id}`);
        fetchNotifications();
      } catch (error) {
        console.error("Error syncing notifications", error);
      }
    };

    syncAndFetch();
    const interval = setInterval(fetchNotifications, 10000); // Check every 10s
    return () => clearInterval(interval);
  }, [user]);

  const markAsRead = async (id) => {
    try {
      await api.post(`/notifications/${id}/read`);
      fetchNotifications();
    } catch (error) {
      console.error("Error marking as read", error);
    }
  };

  const markAllAsRead = async () => {
    try {
      await api.post(`/notifications/read-all?vendorId=${user.id}`);
      fetchNotifications();
    } catch (error) {
      console.error("Error marking all as read", error);
    }
  };

  const menuItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'Products', path: '/inventory', icon: Package },
    { name: 'Sales', path: '/sales', icon: ShoppingCart },
    { name: 'Listahan', path: '/listahan', icon: BookOpen },
    { name: 'History', path: '/transactions', icon: History }
  ];

  const unreadCount = notifications.filter(n => !n.isRead).length;

  const getTypeStyle = (type) => {
    switch (type) {
      case 'WARNING': return { bg: 'bg-amber-50', text: 'text-amber-600', icon: AlertTriangle };
      case 'SUCCESS': return { bg: 'bg-emerald-50', text: 'text-emerald-600', icon: CheckCircle2 };
      default: return { bg: 'bg-teal-50', text: 'text-teal-600', icon: Info };
    }
  };

  return (
    <nav className="h-20 bg-white dark:bg-slate-900 border-b border-gray-100 dark:border-slate-800 flex items-center justify-between px-8 sticky top-0 z-50 shadow-sm transition-colors duration-300">
      {/* Left: Branding */}
      <div className="flex items-center gap-3">
        <div className="bg-[#16A394] p-2 rounded-lg shadow-sm">
          <ShoppingCart className="text-white w-6 h-6" />
        </div>
        <span className="text-2xl font-bold tracking-tight text-gray-800 dark:text-white">
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
              key={item.name} to={item.path}
              className={`flex items-center gap-2 px-5 py-2.5 rounded-xl transition-all duration-300 font-medium ${isActive ? 'bg-teal-50 dark:bg-teal-900/20 text-[#16A394]' : 'text-gray-500 dark:text-slate-400 hover:bg-gray-50 dark:hover:bg-slate-800 hover:text-[#16A394]'}`}
            >
              <Icon size={20} strokeWidth={isActive ? 2.5 : 2} />
              <span className="hidden lg:block">{item.name}</span>
            </Link>
          );
        })}
      </div>

      {/* Right: Actions */}
      <div className="flex items-center gap-4">
        {/* Theme Toggle */}
        <button 
          onClick={() => setIsDark(!isDark)}
          className="p-2.5 rounded-full transition-all duration-300 text-gray-400 dark:text-slate-500 hover:text-[#16A394] hover:bg-gray-50 dark:hover:bg-slate-800"
          title={isDark ? "Switch to Light Mode" : "Switch to Dark Mode"}
        >
          {isDark ? <Sun size={22} className="text-amber-400" /> : <Moon size={22} />}
        </button>

        {/* Notifications Dropdown */}
        <div className="relative">
          <button 
            onClick={() => { setIsNotificationsOpen(!isNotificationsOpen); setIsProfileOpen(false); }}
            className={`p-2.5 rounded-full transition-all duration-300 relative ${isNotificationsOpen ? 'bg-teal-50 dark:bg-teal-900/20 text-[#16A394]' : 'text-gray-400 dark:text-slate-500 hover:text-[#16A394] hover:bg-gray-50 dark:hover:bg-slate-800'}`}
          >
            <Bell size={22} />
            {unreadCount > 0 && (
              <span className="absolute top-1.5 right-1.5 w-4 h-4 bg-rose-500 text-white text-[10px] font-black rounded-full border-2 border-white dark:border-slate-900 flex items-center justify-center animate-pulse">
                {unreadCount}
              </span>
            )}
          </button>
...
      </div>
    </nav>

          {isNotificationsOpen && (
            <>
              <div className="fixed inset-0 z-[-1]" onClick={() => setIsNotificationsOpen(false)}></div>
              <div className="absolute right-0 mt-3 w-80 bg-white dark:bg-slate-800 rounded-3xl shadow-2xl border border-gray-100 dark:border-slate-700 overflow-hidden animate-in fade-in slide-in-from-top-2 duration-200 z-[100]">
                <div className="p-4 bg-slate-50 dark:bg-slate-900/50 border-b border-gray-100 dark:border-slate-700 flex justify-between items-center">
                  <p className="text-xs font-black text-slate-800 dark:text-slate-200 uppercase tracking-widest">Alerts Center</p>
                  {unreadCount > 0 && (
                    <button onClick={markAllAsRead} className="text-[10px] font-bold text-[#16A394] hover:underline">Mark all read</button>
                  )}
                </div>
                
                <div className="max-h-96 overflow-y-auto">
                  {notifications.length === 0 ? (
                    <div className="p-8 text-center text-gray-400 italic text-sm">No notifications yet.</div>
                  ) : (
                    notifications.map((n) => {
                      const style = getTypeStyle(n.type);
                      const Icon = style.icon;
                      return (
                        <div 
                          key={n.id} 
                          onClick={() => { if(!n.isRead) markAsRead(n.id); }}
                          className={`p-4 border-b border-gray-50 dark:border-slate-700/50 hover:bg-gray-50 dark:hover:bg-slate-700/50 transition-colors cursor-pointer relative ${!n.isRead ? 'bg-teal-50/20 dark:bg-teal-500/10' : 'opacity-60'}`}
                        >
                          {!n.isRead && <div className="absolute left-1 top-1/2 -translate-y-1/2 w-1 h-8 bg-[#16A394] rounded-full"></div>}
                          <div className="flex gap-3">
                            <div className={`h-8 w-8 rounded-lg flex items-center justify-center shrink-0 ${style.bg} ${style.text}`}>
                              <Icon size={16} />
                            </div>
                            <div className="flex-1">
                              <p className={`text-xs font-bold ${!n.isRead ? 'text-slate-800 dark:text-slate-200' : 'text-slate-500'}`}>{n.title}</p>
                              <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5 line-clamp-2">{n.message}</p>
                              <p className="text-[9px] text-gray-400 mt-1 font-medium">{new Date(n.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</p>
                            </div>
                          </div>
                        </div>
                      );
                    })
                  )}
                </div>
              </div>
            </>
          )}
        </div>
        
        <div className="h-8 w-px bg-gray-200 dark:bg-slate-700 mx-2"></div>

        {/* Profile Dropdown */}
        <div className="relative">
          <button onClick={() => { setIsProfileOpen(!isProfileOpen); setIsNotificationsOpen(false); }} className="flex items-center gap-3 pl-2 pr-2 py-1.5 rounded-full hover:bg-gray-50 dark:hover:bg-slate-800 transition-all duration-300 group">
            <div className="text-right hidden sm:block">
              <p className="text-xs font-bold text-gray-700 dark:text-slate-200">{user?.name || "Vendor"}</p>
              <p className="text-[10px] text-gray-400 uppercase tracking-tighter">Store Owner</p>
            </div>
            <div className="bg-gray-100 dark:bg-slate-800 p-1 rounded-full group-hover:bg-teal-100 dark:group-hover:bg-teal-900/50 transition-colors duration-300">
              <UserCircle size={28} className="text-gray-500 dark:text-slate-400 group-hover:text-[#16A394]" />
            </div>
            <ChevronDown size={14} className={`text-gray-400 transition-transform duration-300 ${isProfileOpen ? 'rotate-180' : ''}`} />
          </button>

          {isProfileOpen && (
            <>
              <div className="fixed inset-0 z-[-1]" onClick={() => setIsProfileOpen(false)}></div>
              <div className="absolute right-0 mt-3 w-56 bg-white dark:bg-slate-800 rounded-2xl shadow-xl border border-gray-100 dark:border-slate-700 py-2 animate-in fade-in slide-in-from-top-2 duration-200 z-[100]">
                <div className="px-4 py-3 border-b border-gray-50 dark:border-slate-700 mb-1"><p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">Account Menu</p></div>
                <button className="w-full flex items-center gap-3 px-4 py-3 text-sm text-gray-600 dark:text-slate-300 hover:bg-teal-50 dark:hover:bg-teal-900/20 hover:text-[#16A394] transition-all"><Settings size={18} /><span>Store Settings</span></button>
                <button onClick={() => { setIsProfileOpen(false); onLogout(); }} className="w-full flex items-center gap-3 px-4 py-3 text-sm text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-900/20 transition-all"><LogOut size={18} /><span>Log Out</span></button>
              </div>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
