import { useState } from 'react';
import { Search, UserPlus, BookOpen, Clock, AlertCircle, CheckCircle, ChevronRight, Filter } from 'lucide-react';

const Listahan = () => {
  // 1. Mock Data for Customers with Debts
  const [customers] = useState([
    { id: 1, name: 'Mang Juan', totalDebt: 1250.50, lastUpdate: '2 hours ago', status: 'Unpaid' },
    { id: 2, name: 'Aling Nena', totalDebt: 420.00, lastUpdate: '1 day ago', status: 'Partial' },
    { id: 3, name: 'Kardo Dalisay', totalDebt: 2500.00, lastUpdate: '3 days ago', status: 'Unpaid' },
    { id: 4, name: 'Tiya Pusit', totalDebt: 85.00, lastUpdate: '5 hours ago', status: 'Partial' },
  ]);

  const [searchTerm, setSearchTerm] = useState('');

  const filteredCustomers = customers.filter(c => 
    c.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="max-w-7xl mx-auto space-y-8 pb-10 animate-in fade-in duration-500">
      
      {/* Header & New Entry Button */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-black text-slate-800">Listahan Overview</h1>
          <p className="text-slate-500 font-medium">Manage and track customer credits (utang).</p>
        </div>
        <button className="bg-[#16A394] hover:bg-[#0D7A6F] text-white px-6 py-3 rounded-2xl font-bold shadow-lg shadow-[#16A394]/20 transition-all flex items-center gap-2 active:scale-95">
          <UserPlus size={20} />
          Add New Debtor
        </button>
      </div>

      {/* Credit Stats Summary */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-rose-500 p-6 rounded-[2rem] text-white shadow-xl flex items-center gap-4">
          <div className="bg-white/20 p-4 rounded-2xl"><AlertCircle size={28} /></div>
          <div>
            <p className="text-white/70 text-xs font-bold uppercase tracking-widest">Total Outstanding</p>
            <p className="text-3xl font-black">₱4,255.50</p>
          </div>
        </div>
        <div className="bg-amber-400 p-6 rounded-[2rem] text-white shadow-xl flex items-center gap-4">
          <div className="bg-white/20 p-4 rounded-2xl"><Clock size={28} /></div>
          <div>
            <p className="text-white/70 text-xs font-bold uppercase tracking-widest">Active Accounts</p>
            <p className="text-3xl font-black">14 People</p>
          </div>
        </div>
        <div className="bg-[#16A394] p-6 rounded-[2rem] text-white shadow-xl flex items-center gap-4">
          <div className="bg-white/20 p-4 rounded-2xl"><CheckCircle size={28} /></div>
          <div>
            <p className="text-white/70 text-xs font-bold uppercase tracking-widest">Collected Today</p>
            <p className="text-3xl font-black">₱850.00</p>
          </div>
        </div>
      </div>

      {/* Search and Filters */}
      <div className="bg-white p-6 rounded-[2rem] shadow-xl border border-slate-100 flex flex-col md:flex-row gap-4 items-center">
        <div className="relative flex-1 group">
          <Search className="absolute left-4 top-3.5 text-slate-400 group-focus-within:text-[#16A394] transition-colors" size={20} />
          <input 
            type="text" 
            placeholder="Search by customer name..." 
            className="w-full pl-12 pr-4 py-3 bg-slate-50 border-none rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394] transition-all"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <button className="flex items-center gap-2 px-6 py-3 bg-slate-50 text-slate-500 font-bold rounded-2xl hover:bg-slate-100 transition-colors">
          <Filter size={18} />
          Filters
        </button>
      </div>

      {/* Customer Debt List */}
      <div className="bg-white rounded-[2.5rem] shadow-2xl border border-slate-100 overflow-hidden">
        <div className="p-8 border-b border-slate-50 flex justify-between items-center">
          <h3 className="text-xl font-bold text-slate-800">Debtor Records</h3>
          <span className="text-xs font-black text-[#16A394] bg-[#E8F6F5] px-3 py-1 rounded-full uppercase">
            Sort by: Newest
          </span>
        </div>
        
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead className="bg-slate-50/50 text-slate-400 text-[10px] font-black uppercase tracking-[0.2em]">
              <tr>
                <th className="px-8 py-5">Customer Name</th>
                <th className="px-8 py-5">Total Outstanding</th>
                <th className="px-8 py-5">Last Transaction</th>
                <th className="px-8 py-5">Status</th>
                <th className="px-8 py-5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50">
              {filteredCustomers.map((customer) => (
                <tr key={customer.id} className="hover:bg-slate-50 transition-colors group">
                  <td className="px-8 py-6">
                    <div className="flex items-center gap-4">
                      <div className="h-12 w-12 rounded-2xl bg-slate-100 flex items-center justify-center text-[#16A394] font-black group-hover:bg-white transition-colors">
                        {customer.name.charAt(0)}
                      </div>
                      <p className="font-bold text-slate-800">{customer.name}</p>
                    </div>
                  </td>
                  <td className="px-8 py-6">
                    <p className="text-lg font-black text-slate-800">₱{customer.totalDebt.toLocaleString()}</p>
                  </td>
                  <td className="px-8 py-6">
                    <p className="text-sm text-slate-500 font-medium">{customer.lastUpdate}</p>
                  </td>
                  <td className="px-8 py-6">
                    <span className={`px-4 py-1.5 rounded-full text-[10px] font-black tracking-widest uppercase ${
                      customer.status === 'Unpaid' 
                        ? 'bg-rose-100 text-rose-600' 
                        : 'bg-amber-100 text-amber-600'
                    }`}>
                      {customer.status}
                    </span>
                  </td>
                  <td className="px-8 py-6 text-right">
                    <button className="text-[#16A394] p-3 hover:bg-[#E8F6F5] rounded-xl transition-all active:scale-90">
                      <ChevronRight size={24} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Empty State */}
        {filteredCustomers.length === 0 && (
          <div className="p-20 text-center space-y-4 opacity-40">
            <BookOpen size={64} className="mx-auto text-slate-300" />
            <p className="font-bold text-slate-400">No records found matching your search.</p>
          </div>
        )}
      </div>

    </div>
  );
};

export default Listahan;