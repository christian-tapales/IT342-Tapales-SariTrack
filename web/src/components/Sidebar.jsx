import { Link, useLocation } from 'react-router-dom';
import {
  LayoutDashboard,
  Users,
  Store,
  PackageSearch,
  Settings,
  ShieldCheck,
  Database,
  ChevronRight,
  LogOut
} from 'lucide-react';

const Sidebar = ({ onLogout }) => {
  const location = useLocation();

  const sections = [
    {
      title: 'Management',
      items: [
        { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
        { name: 'Vendors', path: '/admin/vendors', icon: Store },
        { name: 'Global Products', path: '/admin/products', icon: PackageSearch },
      ]
    },
    {
      title: 'System',
      items: [
        { name: 'Settings', path: '/admin/settings', icon: Settings },
      ]
    }
  ];

  return (
    <div className="w-72 bg-slate-950 text-slate-300 h-screen flex flex-col border-r border-slate-800 shadow-2xl">
      {/* Admin Branding */}
      <div className="p-8 flex items-center gap-3 border-b border-slate-900 bg-slate-950/50 backdrop-blur-xl">
        <div className="bg-teal-500/10 p-2 rounded-xl border border-teal-500/20">
          <ShieldCheck className="text-teal-400 w-6 h-6" />
        </div>
        <div className="flex flex-col">
          <span className="text-xl font-bold tracking-tight text-white leading-none">
            Sari<span className="text-teal-400">Admin</span>
          </span>
          <span className="text-[10px] text-slate-500 font-bold uppercase tracking-widest mt-1">
            System Control
          </span>
        </div>
      </div>

      {/* Navigation Groups */}
      <nav className="flex-1 px-4 py-6 overflow-y-auto space-y-8 custom-scrollbar">
        {sections.map((section) => (
          <div key={section.title} className="space-y-2">
            <h3 className="px-4 text-[10px] font-black text-slate-500 uppercase tracking-[0.2em]">
              {section.title}
            </h3>
            <div className="space-y-1">
              {section.items.map((item) => {
                const Icon = item.icon;
                const isActive = location.pathname === item.path;
                return (
                  <Link
                    key={item.name}
                    to={item.path}
                    className={`group flex items-center justify-between px-4 py-3 rounded-xl transition-all duration-300 ${isActive
                        ? 'bg-teal-500/10 text-teal-400 shadow-[inset_0_0_20px_rgba(20,184,166,0.05)]'
                        : 'hover:bg-slate-900 hover:text-white'
                      }`}
                  >
                    <div className="flex items-center gap-3">
                      <Icon size={20} className={isActive ? 'text-teal-400' : 'text-slate-500 group-hover:text-teal-400'} />
                      <span className="text-sm font-medium">{item.name}</span>
                    </div>
                    {isActive && (
                      <div className="w-1.5 h-1.5 rounded-full bg-teal-400 shadow-[0_0_8px_rgba(20,184,166,0.8)]"></div>
                    )}
                  </Link>
                );
              })}
            </div>
          </div>
        ))}
      </nav>

      {/* Admin Footer Profile - Simplified */}
      <div className="p-4 border-t border-slate-900 bg-slate-950/80">
        <div className="bg-slate-900/50 p-4 rounded-2xl border border-slate-800 flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-teal-400 to-emerald-600 flex items-center justify-center text-white font-bold shadow-lg">
            A
          </div>
          <div className="flex flex-col">
            <span className="text-xs font-bold text-white leading-none">Super Admin</span>
            <span className="text-[10px] text-slate-500 mt-1">In Session</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Sidebar;