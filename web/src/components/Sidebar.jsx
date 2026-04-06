import { Link, useLocation } from 'react-router-dom';

const Sidebar = () => {
  const location = useLocation();
  
  const menuItems = [
    { name: 'Dashboard', path: '/dashboard' },
    { name: 'Products', path: '/inventory' },
    { name: 'Sales', path: '/sales' },
    { name: 'Listahan', path: '/listahan' },
  ];

  return (
    <div className="w-64 bg-[#16A394] text-white flex flex-col">
      <div className="p-6 text-2xl font-bold border-b border-[#0D7A6F]">
        SariTrack
      </div>
      <nav className="flex-1 mt-4">
        {menuItems.map((item) => (
          <Link
            key={item.name}
            to={item.path}
            // bg-[#0D7A6F] is a slightly darker teal for the hover/active state
            className={`block px-6 py-3 hover:bg-[#0D7A6F] transition-colors ${
              location.pathname === item.path ? 'bg-[#0D7A6F] border-l-4 border-white' : ''
            }`}
          >
            {item.name}
          </Link>
        ))}
      </nav>
      <div className="p-6 border-t border-[#0D7A6F] text-sm text-teal-100">
        Vendor Portal v1.0
      </div>
    </div>
  );
};

export default Sidebar;