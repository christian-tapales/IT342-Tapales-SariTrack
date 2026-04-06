import { useState, useEffect } from 'react';
import axios from 'axios';
import { Search, UserPlus, BookOpen, Clock, AlertCircle, CheckCircle, ChevronRight, Filter, X } from 'lucide-react';

const Listahan = ({ user }) => {
  const [customers, setCustomers] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(true);

  // 1. Form State for new Customer (matches Java Entity)
  const [formData, setFormData] = useState({
    fullName: '',
    currentDebt: '',
    vendorId: user?.id || 1
  });

  // 2. Fetch customers from backend
  const fetchCustomers = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/customers');
      setCustomers(response.data);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching customers:", error);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, []);

  // 3. Add new Customer logic
  const handleAddCustomer = async (e) => {
    e.preventDefault();
    try {
      await axios.post('http://localhost:8080/api/customers', formData);
      alert("Customer added to listahan!");
      setIsModalOpen(false);
      setFormData({ fullName: '', currentDebt: '', vendorId: user?.id || 1 });
      fetchCustomers(); // Refresh list
    } catch (error) {
      alert("Failed to add customer.");
    }
  };

  const filteredCustomers = customers.filter(c => 
    c.fullName?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // 4. Calculate Summary Stats
  const totalOutstanding = customers.reduce((sum, c) => sum + (c.currentDebt || 0), 0);
  const activeAccounts = customers.length;

  return (
    <div className="max-w-7xl mx-auto space-y-8 pb-10 animate-in fade-in duration-500">
      
      {/* Header & New Entry Button */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-black text-slate-800">Listahan Overview</h1>
          <p className="text-slate-500 font-medium">Manage and track customer credits (utang).</p>
        </div>
        <button 
          onClick={() => setIsModalOpen(true)}
          className="bg-[#16A394] hover:bg-[#0D7A6F] text-white px-6 py-3 rounded-2xl font-bold shadow-lg shadow-[#16A394]/20 transition-all flex items-center gap-2 active:scale-95"
        >
          <UserPlus size={20} /> Add New Debtor
        </button>
      </div>

      {/* Credit Stats Summary (Dynamic) */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-rose-500 p-6 rounded-[2rem] text-white shadow-xl flex items-center gap-4">
          <div className="bg-white/20 p-4 rounded-2xl"><AlertCircle size={28} /></div>
          <div>
            <p className="text-white/70 text-xs font-bold uppercase tracking-widest">Total Outstanding</p>
            <p className="text-3xl font-black">₱{totalOutstanding.toLocaleString()}</p>
          </div>
        </div>
        <div className="bg-amber-400 p-6 rounded-[2rem] text-white shadow-xl flex items-center gap-4">
          <div className="bg-white/20 p-4 rounded-2xl"><Clock size={28} /></div>
          <div>
            <p className="text-white/70 text-xs font-bold uppercase tracking-widest">Active Accounts</p>
            <p className="text-3xl font-black">{activeAccounts} People</p>
          </div>
        </div>
        <div className="bg-[#16A394] p-6 rounded-[2rem] text-white shadow-xl flex items-center gap-4">
          <div className="bg-white/20 p-4 rounded-2xl"><CheckCircle size={28} /></div>
          <div>
            <p className="text-white/70 text-xs font-bold uppercase tracking-widest">Status</p>
            <p className="text-3xl font-black">Balanced</p>
          </div>
        </div>
      </div>

      {/* Search Bar */}
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
      </div>

      {/* Customer Debt List */}
      <div className="bg-white rounded-[2.5rem] shadow-2xl border border-slate-100 overflow-hidden">
        <div className="p-8 border-b border-slate-50">
          <h3 className="text-xl font-bold text-slate-800">Debtor Records</h3>
        </div>
        
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead className="bg-slate-50/50 text-slate-400 text-[10px] font-black uppercase tracking-[0.2em]">
              <tr>
                <th className="px-8 py-5">Customer Name</th>
                <th className="px-8 py-5">Total Outstanding</th>
                <th className="px-8 py-5">Status</th>
                <th className="px-8 py-5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50">
              {loading ? (
                <tr><td colSpan="4" className="p-10 text-center text-slate-400">Loading Listahan...</td></tr>
              ) : filteredCustomers.map((customer) => (
                <tr key={customer.id} className="hover:bg-slate-50 transition-colors group">
                  <td className="px-8 py-6 font-bold text-slate-800">
                    <div className="flex items-center gap-4">
                       <div className="h-10 w-10 rounded-full bg-slate-100 flex items-center justify-center text-[#16A394]">{customer.fullName?.charAt(0)}</div>
                       {customer.fullName}
                    </div>
                  </td>
                  <td className="px-8 py-6 font-black text-slate-800">
                    ₱{customer.currentDebt?.toLocaleString()}
                  </td>
                  <td className="px-8 py-6">
                    <span className="px-4 py-1.5 rounded-full text-[10px] font-black bg-rose-100 text-rose-600 uppercase tracking-widest">
                      Unpaid
                    </span>
                  </td>
                  <td className="px-8 py-6 text-right">
                    <button className="text-[#16A394] p-3 hover:bg-[#E8F6F5] rounded-xl"><ChevronRight size={20} /></button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {!loading && filteredCustomers.length === 0 && (
          <div className="p-20 text-center opacity-40">
            <BookOpen size={64} className="mx-auto text-slate-300 mb-4" />
            <p className="font-bold text-slate-400">No records found.</p>
          </div>
        )}
      </div>

      {/* Add Debtor Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-sm">
          <div className="bg-white w-full max-w-md rounded-[2.5rem] shadow-2xl p-8 animate-in zoom-in duration-200">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-xl font-black text-slate-800">New Debtor</h2>
              <button onClick={() => setIsModalOpen(false)}><X className="text-slate-400" /></button>
            </div>
            <form onSubmit={handleAddCustomer} className="space-y-4">
              <input 
                type="text" placeholder="Full Name" required
                className="w-full p-4 bg-slate-50 text-slate-800 rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394]"
                value={formData.fullName} onChange={e => setFormData({...formData, fullName: e.target.value})}
              />
              <input 
                type="number" placeholder="Initial Debt (₱)" required
                className="w-full p-4 bg-slate-50 text-slate-800 rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394]"
                value={formData.currentDebt} onChange={e => setFormData({...formData, currentDebt: e.target.value})}
              />
              <button type="submit" className="w-full bg-[#16A394] text-white py-4 rounded-2xl font-black shadow-lg hover:bg-[#0D7A6F] transition-all">
                Add to Listahan
              </button>
            </form>
          </div>
        </div>
      )}

    </div>
  );
};

export default Listahan;