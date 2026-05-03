import { Search, Wallet, Package, BookOpen, ChevronRight, TrendingUp } from 'lucide-react';
import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

const ActionCard = ({ icon: Icon, label, value, subtext, colorClass, btnLabel }) => (
  <div className={`${colorClass} p-6 rounded-[2rem] text-white shadow-lg flex flex-col justify-between h-48 transition-transform hover:scale-[1.02]`}>
    <div className="flex justify-between items-start">
      <div className="bg-white/20 p-3 rounded-2xl">
        <Icon size={24} />
      </div>
      <button className="bg-white/20 hover:bg-white/30 px-4 py-1.5 rounded-full text-xs font-bold transition-colors">
        {btnLabel}
      </button>
    </div>
    <div>
      <p className="text-white/80 text-xs font-bold uppercase tracking-wider">{label}</p>
      <p className="text-3xl font-black mt-1">{value}</p>
      {subtext && <p className="text-white/60 text-[10px] mt-1 font-medium">{subtext}</p>}
    </div>
  </div>
);

const Dashboard = ({ user, onLoginSuccess }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const isAdmin = user?.role === 'ADMIN';

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    if (params.get('loginSuccess') === 'true' && !user) {
      onLoginSuccess({ 
        name: params.get('name') || "Google User", 
        email: params.get('email') || "",
        role: params.get('role') || "VENDOR"
      });
      navigate('/dashboard', { replace: true });
    }
  }, [location, user, onLoginSuccess, navigate]);

  const transactions = isAdmin ? [
    { id: 1, name: 'SariTrack Subscription', time: '11:00 AM', price: '₱499.00', category: 'Revenue' },
    { id: 2, name: "Maria's Store Signup", time: '10:45 AM', price: '₱0.00', category: 'System' },
    { id: 3, name: 'Cloud Storage Upgrade', time: '09:15 AM', price: '₱250.00', category: 'Add-on' },
  ] : [
    { id: 1, name: 'Red Horse (500ml)', time: '10:30 AM', price: '₱120.00', category: 'Beverages' },
    { id: 2, name: 'Lucky Me! Beef', time: '10:25 AM', price: '₱15.00', category: 'Noodles' },
    { id: 3, name: 'Coke 1.5L', time: '10:10 AM', price: '₱75.00', category: 'Beverages' },
  ];

  return (
    <div className={`max-w-7xl mx-auto space-y-8 pb-10 animate-in fade-in duration-500 ${isAdmin ? 'text-slate-200' : 'text-slate-800'}`}>
      
      {/* Top Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className={`text-3xl font-black ${isAdmin ? 'text-white' : 'text-slate-800'}`}>
            {isAdmin ? "Platform Control" : "Kumusta"}, <span className="text-teal-400">{user?.name?.split(' ')[0] || "Admin"}</span>!
          </h1>
          <p className={isAdmin ? 'text-slate-400 font-medium' : 'text-slate-500 font-medium'}>
            {isAdmin ? "Global system status and platform health." : "Here is what's happening today."}
          </p>
        </div>
        
        {isAdmin ? (
          <div className="flex items-center gap-2 bg-teal-500/10 border border-teal-500/20 px-4 py-2 rounded-2xl">
            <div className="h-2 w-2 rounded-full bg-teal-400 animate-pulse"></div>
            <span className="text-xs font-bold text-teal-400 uppercase tracking-widest">Mainframe Stable</span>
          </div>
        ) : (
          <div className="relative group max-w-md w-full">
            <Search className="absolute left-4 top-3.5 text-slate-400 group-focus-within:text-[#16A394] transition-colors" size={20} />
            <input 
              type="text" 
              placeholder="Search transactions, products..." 
              className="w-full pl-12 pr-4 py-3 bg-white border border-slate-200 rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394] shadow-sm transition-all"
            />
          </div>
        )}
      </div>

      {/* Main Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <ActionCard 
          icon={Wallet}
          label={isAdmin ? "Total Platform Sales" : "Today's Sales"}
          value={isAdmin ? "₱154,200.00" : "₱2,450.00"}
          subtext={isAdmin ? "Aggregated store revenue" : "+15% from yesterday"}
          colorClass={isAdmin ? "bg-slate-900 border border-white/5" : "bg-[#16A394]"}
          btnLabel={isAdmin ? "Audit" : "View"}
        />
        <ActionCard 
          icon={Package}
          label={isAdmin ? "Active Vendors" : "Low Stock Alert"}
          value={isAdmin ? "1,245 Stores" : "12 Items"}
          subtext={isAdmin ? "Current onboarded sellers" : "Requires immediate restock"}
          colorClass={isAdmin ? "bg-slate-900 border border-white/5" : "bg-rose-500"}
          btnLabel={isAdmin ? "Manage" : "Check"}
        />
        <ActionCard 
          icon={BookOpen}
          label={isAdmin ? "System Health" : "Listahan Overview"}
          value={isAdmin ? "99.9%" : "₱1,120.00"}
          subtext={isAdmin ? "API & Database Uptime" : "Total active credits"}
          colorClass={isAdmin ? "bg-slate-900 border border-white/5" : "bg-amber-400"}
          btnLabel={isAdmin ? "Logs" : "Open"}
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Recent Activity Section */}
        <div className="lg:col-span-2 space-y-4">
          <div className="flex justify-between items-center px-2">
            <h3 className={`text-xl font-bold ${isAdmin ? 'text-white' : 'text-slate-800'}`}>
              {isAdmin ? "System Activity" : "Recent Transactions"}
            </h3>
            <button className="text-teal-400 text-sm font-bold hover:underline">See all</button>
          </div>
          
          <div className={`${isAdmin ? 'bg-slate-900/50 border border-white/5' : 'bg-white border border-slate-100'} rounded-[2rem] shadow-xl overflow-hidden`}>
            <div className={`divide-y ${isAdmin ? 'divide-white/5' : 'divide-slate-50'}`}>
              {transactions.map((txn) => (
                <div key={txn.id} className={`p-6 flex items-center justify-between transition-colors cursor-pointer group ${isAdmin ? 'hover:bg-white/5' : 'hover:bg-slate-50'}`}>
                  <div className="flex items-center gap-4">
                    <div className={`h-12 w-12 rounded-2xl flex items-center justify-center font-bold transition-colors ${isAdmin ? 'bg-slate-800 text-teal-400' : 'bg-slate-100 text-[#16A394]'}`}>
                      {txn.name.charAt(0)}
                    </div>
                    <div>
                      <p className={`font-bold ${isAdmin ? 'text-slate-200' : 'text-slate-800'}`}>{txn.name}</p>
                      <p className="text-xs text-slate-500 font-medium">{txn.time} • {txn.category}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className={`font-black ${isAdmin ? 'text-white' : 'text-slate-800'}`}>{txn.price}</p>
                    <div className="flex items-center gap-1 text-[10px] text-emerald-400 font-bold justify-end">
                      <TrendingUp size={12} /> Success
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Status Section */}
        <div className="space-y-4">
          <h3 className={`text-xl font-bold px-2 ${isAdmin ? 'text-white' : 'text-slate-800'}`}>
            {isAdmin ? "Node Performance" : "Top Selling"}
          </h3>
          <div className={`${isAdmin ? 'bg-slate-900/50 border border-white/5' : 'bg-white border border-slate-100'} p-8 rounded-[2rem] shadow-xl space-y-6`}>
            {(isAdmin ? [
              { name: 'API Server', sold: 94, color: 'bg-teal-400' },
              { name: 'Database', sold: 88, color: 'bg-blue-400' },
              { name: 'Auth Node', sold: 99, color: 'bg-emerald-400' },
            ] : [
              { name: 'Red Horse (500ml)', sold: 45, color: 'bg-[#16A394]' },
              { name: 'Lucky Me! Canton', sold: 38, color: 'bg-amber-400' },
              { name: 'Coke 1.5L', sold: 25, color: 'bg-rose-500' },
            ]).map((item, index) => (
              <div key={index} className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span className={`font-bold ${isAdmin ? 'text-slate-300' : 'text-slate-700'}`}>{item.name}</span>
                  <span className="font-black text-slate-500">{item.sold}%</span>
                </div>
                <div className={`h-2 w-full rounded-full overflow-hidden ${isAdmin ? 'bg-slate-800' : 'bg-slate-100'}`}>
                  <div 
                    className={`h-full ${item.color} rounded-full transition-all duration-1000 shadow-[0_0_10px_rgba(45,212,191,0.3)]`} 
                    style={{ width: `${item.sold}%` }}
                  ></div>
                </div>
              </div>
            ))}
            <button className={`w-full py-4 mt-4 border-2 border-dashed rounded-2xl font-bold text-sm transition-all flex items-center justify-center gap-2 ${
              isAdmin ? 'border-white/10 text-slate-500 hover:border-teal-400 hover:text-teal-400' : 'border-slate-200 text-slate-400 hover:border-[#16A394] hover:text-[#16A394]'
            }`}>
              {isAdmin ? "System Health Report" : "Full Inventory Report"} <ChevronRight size={16} />
            </button>
          </div>
        </div>

      </div>
    </div>
  );
};

export default Dashboard;