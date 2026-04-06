import { Outlet } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import Topbar from '../components/Topbar';

const DashboardLayout = ({ user, onLogout }) => {
  return (
    <div className="flex h-screen bg-gray-100 overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0">
        {/* Pass the props to Topbar */}
        <Topbar user={user} onLogout={onLogout} />
        <main className="flex-1 overflow-x-hidden overflow-y-auto p-6 bg-slate-50">
          <Outlet /> 
        </main>
      </div>
    </div>
  );
};

export default DashboardLayout;