import { Outlet } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import Topbar from '../components/Topbar';
import Navbar from '../components/Navbar';

const DashboardLayout = ({ user, onLogout }) => {
  // Determine if user is a vendor or admin
  const isVendor = user?.role === 'VENDOR' || !user?.role; 

  if (isVendor) {
    return (
      <div className="flex flex-col h-screen bg-[#F8FAFB] overflow-hidden">
        {/* Unified Top Navigation for Vendors */}
        <Navbar user={user} onLogout={onLogout} />
        
        <main className="flex-1 overflow-x-hidden overflow-y-auto p-8">
          <Outlet />
        </main>
      </div>
    );
  }

  // Admin Control Center (Full Dark Theme)
  return (
    <div className="flex h-screen bg-slate-950 text-slate-200 overflow-hidden font-sans">
      <Sidebar onLogout={onLogout} />
      <div className="flex-1 flex flex-col min-w-0 bg-slate-950">
        <Topbar user={user} onLogout={onLogout} />
        <main className="flex-1 overflow-x-hidden overflow-y-auto p-8 bg-[#020617]">
          <Outlet /> 
        </main>
      </div>
    </div>
  );
};

export default DashboardLayout;