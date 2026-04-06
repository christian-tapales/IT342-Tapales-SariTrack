import { Search, Wallet, Package, BookOpen, ChevronRight, TrendingUp } from 'lucide-react';

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

const Dashboard = ({ user }) => {
  const transactions = [
    { id: 1, name: 'Red Horse (500ml)', time: '10:30 AM', price: '₱120.00', category: 'Beverages' },
    { id: 2, name: 'Lucky Me! Beef', time: '10:25 AM', price: '₱15.00', category: 'Noodles' },
    { id: 3, name: 'Coke 1.5L', time: '10:10 AM', price: '₱75.00', category: 'Beverages' },
  ];

  const topSelling = [
    { name: 'Red Horse (500ml)', sold: 45, color: 'bg-[#16A394]' },
    { name: 'Lucky Me! Canton', sold: 38, color: 'bg-amber-400' },
    { name: 'Coke 1.5L', sold: 25, color: 'bg-rose-500' },
  ];

  return (
    <div className="max-w-7xl mx-auto space-y-8 pb-10">
      
      {/* Top Search Bar & Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-black text-slate-800">
            Kumusta, <span className="text-[#16A394]">{user?.name?.split(' ')[0] || "Vendor"}</span>!
          </h1>
          <p className="text-slate-500 font-medium">Here is what's happening today.</p>
        </div>
        <div className="relative group max-w-md w-full">
          <Search className="absolute left-4 top-3.5 text-slate-400 group-focus-within:text-[#16A394] transition-colors" size={20} />
          <input 
            type="text" 
            placeholder="Search transactions, products..." 
            className="w-full pl-12 pr-4 py-3 bg-white border border-slate-200 rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394] shadow-sm transition-all"
          />
        </div>
      </div>

      {/* Main Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <ActionCard 
          icon={Wallet}
          label="Today's Sales"
          value="₱2,450.00"
          subtext="+15% from yesterday"
          colorClass="bg-[#16A394]"
          btnLabel="View"
        />
        <ActionCard 
          icon={Package}
          label="Low Stock Alert"
          value="12 Items"
          subtext="Requires immediate restock"
          colorClass="bg-rose-500"
          btnLabel="Check"
        />
        <ActionCard 
          icon={BookOpen}
          label="Listahan Overview"
          value="₱1,120.00"
          subtext="Total active credits"
          colorClass="bg-amber-400"
          btnLabel="Open"
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Recent Transactions Section */}
        <div className="lg:col-span-2 space-y-4">
          <div className="flex justify-between items-center px-2">
            <h3 className="text-xl font-bold text-slate-800">Recent Transactions</h3>
            <button className="text-[#16A394] text-sm font-bold hover:underline">See all</button>
          </div>
          
          <div className="bg-white rounded-[2rem] shadow-xl border border-slate-100 overflow-hidden">
            <div className="divide-y divide-slate-50">
              {transactions.map((txn) => (
                <div key={txn.id} className="p-6 flex items-center justify-between hover:bg-slate-50 transition-colors cursor-pointer group">
                  <div className="flex items-center gap-4">
                    <div className="h-12 w-12 rounded-2xl bg-slate-100 flex items-center justify-center text-[#16A394] font-bold group-hover:bg-white transition-colors">
                      {txn.name.charAt(0)}
                    </div>
                    <div>
                      <p className="font-bold text-slate-800">{txn.name}</p>
                      <p className="text-xs text-slate-400 font-medium">{txn.time} • {txn.category}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="font-black text-slate-800">{txn.price}</p>
                    <div className="flex items-center gap-1 text-[10px] text-emerald-600 font-bold justify-end">
                      <TrendingUp size={12} /> Success
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Top Selling Section */}
        <div className="space-y-4">
          <h3 className="text-xl font-bold text-slate-800 px-2">Top Selling</h3>
          <div className="bg-white p-8 rounded-[2rem] shadow-xl border border-slate-100 space-y-6">
            {topSelling.map((item, index) => (
              <div key={index} className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="font-bold text-slate-700">{item.name}</span>
                  <span className="font-black text-slate-400">{item.sold}%</span>
                </div>
                <div className="h-2 w-full bg-slate-100 rounded-full overflow-hidden">
                  <div 
                    className={`h-full ${item.color} rounded-full transition-all duration-1000`} 
                    style={{ width: `${item.sold}%` }}
                  ></div>
                </div>
              </div>
            ))}
            <button className="w-full py-4 mt-4 border-2 border-dashed border-slate-200 rounded-2xl text-slate-400 font-bold text-sm hover:border-[#16A394] hover:text-[#16A394] transition-all flex items-center justify-center gap-2">
              Full Inventory Report <ChevronRight size={16} />
            </button>
          </div>
        </div>

      </div>
    </div>
  );
};

export default Dashboard;