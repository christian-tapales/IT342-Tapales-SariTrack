import { Search, Wallet, Package, BookOpen, ChevronRight, TrendingUp, Clock, AlertCircle } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

const ActionCard = ({ icon: Icon, label, value, subtext, colorClass, btnLabel, onClick }) => (
  <div
    onClick={onClick}
    className={`${colorClass} p-6 rounded-[2rem] text-white shadow-lg flex flex-col justify-between h-48 transition-all hover:scale-[1.02] hover:shadow-2xl cursor-pointer active:scale-95`}
  >
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

const Dashboard = ({ user }) => {
  const navigate = useNavigate();
  const [stats, setStats] = useState({
    todaySales: 0,
    lowStockCount: 0,
    totalDebt: 0,
    recentTransactions: [],
    topSelling: [],
    weeklySales: []
  });
  const [loading, setLoading] = useState(true);

  const fetchStats = async () => {
    if (!user?.id) return;
    try {
      const response = await api.get(`/vendor/dashboard/stats?vendorId=${user.id}`);
      setStats(response.data);
      setLoading(false);
    } catch (error) {
      console.error("Failed to fetch dashboard stats", error);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStats();
  }, [user]);

  const isAdmin = user?.role === 'ADMIN';

  const getStatusBadge = (status) => {
    switch (status) {
      case 'PAID':
        return <div className="flex items-center gap-1 text-[10px] text-emerald-500 font-bold justify-end"><TrendingUp size={12} /> Paid</div>;
      case 'CANCELLED':
        return <div className="flex items-center gap-1 text-[10px] text-rose-500 font-bold justify-end"><AlertCircle size={12} /> Cancelled</div>;
      default:
        return <div className="flex items-center gap-1 text-[10px] text-amber-500 font-bold justify-end"><Clock size={12} /> Pending</div>;
    }
  };

  return (
    <div className={`max-w-7xl mx-auto space-y-8 pb-10 animate-in fade-in duration-500 ${isAdmin ? 'text-slate-200' : 'text-slate-800'}`}>

      {/* Top Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className={`text-3xl font-black ${isAdmin ? 'text-white' : 'text-slate-800'}`}>
            {isAdmin ? "Platform Control" : "Kumusta"}, <span className="text-[#16A394]">{user?.name?.split(' ')[0] || "Admin"}</span>!
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
          value={isAdmin ? "₱154,200.00" : `₱${(stats.todaySales || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}`}
          subtext={isAdmin ? "Aggregated store revenue" : "Confirmed payments only"}
          colorClass={isAdmin ? "bg-slate-900 border border-white/5" : "bg-[#16A394]"}
          btnLabel={isAdmin ? "Audit" : "POS"}
          onClick={() => navigate('/sales')}
        />
        <ActionCard
          icon={Package}
          label={isAdmin ? "Active Vendors" : "Low Stock Alert"}
          value={isAdmin ? "1,245 Stores" : `${stats.lowStockCount} Items`}
          subtext={isAdmin ? "Current onboarded sellers" : "Requires attention"}
          colorClass={isAdmin ? "bg-slate-900 border border-white/5" : "bg-rose-500"}
          btnLabel={isAdmin ? "Manage" : "Check"}
          onClick={() => navigate('/inventory')}
        />
        <ActionCard
          icon={BookOpen}
          label={isAdmin ? "System Health" : "Listahan Overview"}
          value={isAdmin ? "99.9%" : `₱${(stats.totalDebt || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}`}
          subtext={isAdmin ? "API & Database Uptime" : "Total active credits"}
          colorClass={isAdmin ? "bg-slate-900 border border-white/5" : "bg-amber-400"}
          btnLabel={isAdmin ? "Open" : "Open"}
          onClick={() => navigate('/listahan')}
        />
      </div>

      {/* Sales Analytics Chart */}
      {!isAdmin && (
        <div className="bg-white p-8 rounded-[3rem] border border-slate-100 shadow-xl space-y-6">
          <div className="flex justify-between items-end">
            <div>
              <h3 className="text-xl font-bold text-slate-800">Weekly Revenue</h3>
              <p className="text-sm text-slate-500 font-medium">Sales performance trend</p>
            </div>
            <div className="text-right">
              <p className="text-sm text-slate-400 font-bold uppercase tracking-wider">Estimated Week Total</p>
              <p className="text-2xl font-black text-[#16A394]">
                ₱{(stats.weeklySales || []).reduce((acc, curr) => acc + (curr.sales || 0), 0).toLocaleString()}
              </p>
            </div>
          </div>
          
          <div className="w-full mt-4">
            <ResponsiveContainer width="100%" height={300}>
              <AreaChart data={stats.weeklySales || []}>
                <defs>
                  <linearGradient id="colorSales" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#16A394" stopOpacity={0.3}/>
                    <stop offset="95%" stopColor="#16A394" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                <XAxis 
                  dataKey="day" 
                  axisLine={false} 
                  tickLine={false} 
                  tick={{fill: '#94a3b8', fontSize: 12, fontWeight: 600}}
                  dy={10}
                />
                <YAxis hide={true} />
                <Tooltip 
                  contentStyle={{borderRadius: '16px', border: 'none', boxShadow: '0 10px 15px -3px rgb(0 0 0 / 0.1)'}}
                  formatter={(value) => [`₱${value.toLocaleString()}`, 'Sales']}
                />
                <Area 
                  type="monotone" 
                  dataKey="sales" 
                  stroke="#16A394" 
                  strokeWidth={4}
                  fillOpacity={1} 
                  fill="url(#colorSales)" 
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Recent Activity Section */}
        <div className="lg:col-span-2 space-y-4">
          <div className="flex justify-between items-center px-2">
            <h3 className={`text-xl font-bold ${isAdmin ? 'text-white' : 'text-slate-800'}`}>
              {isAdmin ? "System Activity" : "Recent Transactions"}
            </h3>
            <button 
              onClick={() => navigate('/transactions')}
              className="text-[#16A394] text-sm font-bold hover:underline"
            >
              See all
            </button>
          </div>

          <div className={`${isAdmin ? 'bg-slate-900/50 border border-white/5' : 'bg-white border border-slate-100'} rounded-[3rem] shadow-xl overflow-hidden`}>
            <div className={`divide-y ${isAdmin ? 'divide-white/5' : 'divide-slate-50'}`}>
              {(stats.recentTransactions || []).length === 0 ? (
                <div className="p-10 text-center text-slate-400 font-medium italic">No recent transactions.</div>
              ) : stats.recentTransactions.map((txn) => (
                <div key={txn.id} className={`p-6 flex items-center justify-between transition-colors cursor-pointer group ${isAdmin ? 'hover:bg-white/5' : 'hover:bg-slate-50'}`}>
                  <div className="flex items-center gap-4">
                    <div className={`h-12 w-12 rounded-2xl flex items-center justify-center font-bold transition-colors ${isAdmin ? 'bg-slate-800 text-teal-400' : 'bg-slate-50 text-[#16A394]'}`}>
                      #{txn.id}
                    </div>
                    <div>
                      <p className={`font-bold ${isAdmin ? 'text-slate-200' : 'text-slate-800'}`}>Transaction #{txn.id}</p>
                      <p className="text-xs text-slate-500 font-medium">
                        {new Date(txn.timestamp).toLocaleString([], { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })}
                      </p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className={`font-black ${isAdmin ? 'text-white' : 'text-slate-800'}`}>₱{(txn.totalAmount || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</p>
                    {getStatusBadge(txn.status)}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Top Selling Section */}
        <div className="space-y-4">
          <h3 className={`text-xl font-bold px-2 ${isAdmin ? 'text-white' : 'text-slate-800'}`}>
            {isAdmin ? "Node Performance" : "Top Selling Items"}
          </h3>
          <div className={`${isAdmin ? 'bg-slate-900/50 border border-white/5' : 'bg-white border border-slate-100'} p-8 rounded-[3rem] shadow-xl space-y-6`}>
            {isAdmin ? (
              [
                { name: 'API Server', sold: 94, color: 'bg-teal-400' },
                { name: 'Database', sold: 88, color: 'bg-blue-400' },
                { name: 'Auth Node', sold: 99, color: 'bg-emerald-400' },
              ].map((item, index) => (
                <div key={index} className="space-y-2">
                  <div className="flex justify-between text-sm">
                    <span className={`font-bold ${isAdmin ? 'text-slate-300' : 'text-slate-700'}`}>{item.name}</span>
                    <span className="font-black text-slate-500">{item.sold}%</span>
                  </div>
                  <div className={`h-2 w-full rounded-full overflow-hidden ${isAdmin ? 'bg-slate-800' : 'bg-slate-100'}`}>
                    <div className={`h-full ${item.color} rounded-full transition-all duration-1000`} style={{ width: `${item.sold}%` }}></div>
                  </div>
                </div>
              ))
            ) : (
              (stats.topSelling || []).length === 0 ? (
                <div className="text-center text-slate-400 py-10 italic">No sales data yet.</div>
              ) : (
                stats.topSelling.map((item, index) => {
                  const colors = ['bg-[#16A394]', 'bg-amber-400', 'bg-rose-500'];
                  const maxSold = stats.topSelling[0].sold;
                  const percentage = (item.sold / maxSold) * 100;
                  return (
                    <div key={index} className="space-y-2">
                      <div className="flex justify-between text-sm">
                        <span className="font-bold text-slate-700">{item.name}</span>
                        <span className="font-black text-slate-500">{item.sold} sold</span>
                      </div>
                      <div className="h-2 w-full rounded-full overflow-hidden bg-slate-50">
                        <div
                          className={`h-full ${colors[index % colors.length]} rounded-full transition-all duration-1000`}
                          style={{ width: `${percentage}%` }}
                        ></div>
                      </div>
                    </div>
                  );
                })
              )
            )}
            <button
              onClick={() => navigate('/inventory')}
              className={`w-full py-4 mt-4 border-2 border-dashed rounded-2xl font-bold text-sm transition-all flex items-center justify-center gap-2 ${isAdmin ? 'border-white/10 text-slate-500 hover:border-teal-400 hover:text-teal-400' : 'border-slate-200 text-slate-400 hover:border-[#16A394] hover:text-[#16A394]'
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