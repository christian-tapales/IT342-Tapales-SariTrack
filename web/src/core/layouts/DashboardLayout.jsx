import { Outlet } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import Topbar from '../components/Topbar';
import Navbar from '../components/Navbar';

const DashboardLayout = ({ user, onLogout }) => {
  // Determine if user is a vendor or admin
  const isVendor = user?.role === 'VENDOR' || !user?.role; 

  if (isVendor) {
    return (
      <div className="flex flex-col h-screen bg-[#F8FAFB] dark:bg-slate-900 overflow-hidden transition-colors duration-300">
        {/* Unified Top Navigation for Vendors */}
        <Navbar user={user} onLogout={onLogout} />
        
        <main className="flex-1 overflow-x-hidden overflow-y-auto p-8">
          <Outlet />
        </main>
      </div>
    );
  }

  // Admin Control Center
  return (
    <div className="flex h-screen bg-slate-50 dark:bg-slate-950 text-slate-800 dark:text-slate-200 overflow-hidden font-sans transition-colors duration-300">
      <Sidebar onLogout={onLogout} />
      <div className="flex-1 flex flex-col min-w-0 bg-slate-50 dark:bg-slate-950">
        <Topbar user={user} onLogout={onLogout} />
        <main className="flex-1 overflow-x-hidden overflow-y-auto p-8 bg-slate-50 dark:bg-[#020617]">
          <Outlet /> 
        </main>
      </div>
    </div>
  );
};

export default DashboardLayout;