import { Outlet } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import Topbar from '../components/Topbar';
import Navbar from '../components/Navbar';

const DashboardLayout = ({ user, onLogout }) => {
  // Determine if user is a vendor or admin
  const isVendor = user?.role === 'VENDOR' || !user?.role; // Defaulting to vendor if no role for safety

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

  // Original Sidebar layout for Admins
  return (
    <div className="flex h-screen bg-gray-100 overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0">
        <Topbar user={user} onLogout={onLogout} />
        <main className="flex-1 overflow-x-hidden overflow-y-auto p-6 bg-slate-50">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default DashboardLayout;