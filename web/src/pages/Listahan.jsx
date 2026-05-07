import { useState, useEffect } from 'react';
import { Search, Plus, UserPlus, CreditCard, Clock, CheckCircle2, ChevronRight, X, Package, Trash2, History, Banknote } from 'lucide-react';
import api from '../api';
import Skeleton from '../components/Skeleton';

const Listahan = ({ user }) => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showAddModal, setShowAddModal] = useState(false);
  const [showPayModal, setShowPayModal] = useState(false);
  const [showHistoryModal, setShowHistoryModal] = useState(false);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [history, setHistory] = useState([]);
  const [paymentHistory, setPaymentHistory] = useState([]);
  const [activeTab, setActiveTab] = useState('DEBT'); // 'DEBT' or 'PAYMENT'
  const [newCustomer, setNewCustomer] = useState({ fullName: '', email: '' });
  const [payAmount, setPayAmount] = useState('');

  const fetchData = async () => {
    try {
      const response = await api.get(`/customers?vendorId=${user.id}`);
      setCustomers(response.data);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching customers", error);
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user?.id) fetchData();
  }, [user]);

  const fetchHistory = async (customerId) => {
    try {
      const [orderRes, payRes] = await Promise.all([
        api.get(`/orders/history?vendorId=${user.id}&customerId=${customerId}`),
        api.get(`/customers/${customerId}/payments`)
      ]);
      setHistory(orderRes.data);
      setPaymentHistory(payRes.data);
      setShowHistoryModal(true);
    } catch (error) {
      console.error("Error fetching history", error);
    }
  };

  const handleAddCustomer = async (e) => {
    e.preventDefault();
    try {
      await api.post('/customers', {
        ...newCustomer,
        vendorId: user.id,
        currentDebt: 0,
        status: 'Paid'
      });
      setShowAddModal(false);
      setNewCustomer({ fullName: '', email: '' });
      fetchData();
    } catch (error) {
      alert("Error adding customer");
    }
  };

  const handlePayment = async () => {
    if (!payAmount || isNaN(payAmount)) return;
    try {
      await api.post(`/customers/${selectedCustomer.id}/pay`, {
        amount: parseFloat(payAmount)
      });
      setShowPayModal(false);
      setPayAmount('');
      fetchData();
    } catch (error) {
      alert("Error recording payment");
    }
  };

  const totalDebt = customers.reduce((sum, c) => sum + (c.currentDebt || 0), 0);
  const filteredCustomers = customers.filter(c => c.fullName.toLowerCase().includes(searchTerm.toLowerCase()));

  return (
    <div className="max-w-7xl mx-auto space-y-8 pb-10 animate-in fade-in duration-500">
      
      {/* Header & Stats */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
        <div>
          <h1 className="text-4xl font-black text-slate-800 dark:text-white tracking-tight">Listahan <span className="text-amber-500 text-transparent bg-clip-text bg-gradient-to-r from-amber-500 to-orange-600">Tracker</span></h1>
          <p className="text-slate-500 dark:text-slate-400 font-medium mt-1">Manage your store's collectibles and customer credit.</p>
        </div>
        <div className="bg-gradient-to-br from-amber-400 to-orange-500 p-6 rounded-[2.5rem] text-white shadow-2xl flex items-center gap-6 min-w-[300px] border-4 border-white dark:border-slate-800">
          <div className="bg-white/20 p-4 rounded-2xl backdrop-blur-sm"><CreditCard size={32} /></div>
          <div>
            <p className="text-white/70 text-xs font-bold uppercase tracking-widest">Total Collectibles</p>
            <p className="text-3xl font-black italic">₱{totalDebt.toLocaleString(undefined, { minimumFractionDigits: 2 })}</p>
          </div>
        </div>
      </div>

      {/* Main Actions Bar */}
      <div className="flex flex-col md:flex-row gap-4">
        <div className="relative flex-1 group">
          <Search className="absolute left-4 top-4 text-slate-400 dark:text-slate-500 group-focus-within:text-amber-500 transition-colors" size={20} />
          <input 
            type="text" placeholder="Search customer name..." 
            className="w-full pl-12 pr-4 py-4 bg-white dark:bg-slate-900 border border-slate-100 dark:border-slate-800 rounded-[2rem] shadow-lg outline-none focus:ring-2 focus:ring-amber-400 transition-all text-slate-800 dark:text-white font-bold"
            value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <button 
          onClick={() => setShowAddModal(true)}
          className="bg-slate-900 dark:bg-amber-500 text-white dark:text-white px-8 py-4 rounded-[2rem] font-black shadow-xl hover:bg-slate-800 dark:hover:bg-amber-600 transition-all flex items-center justify-center gap-2 active:scale-95"
        >
          <UserPlus size={20} /> Add New Borrower
        </button>
      </div>

      {/* Customers List */}
      <div className="bg-white dark:bg-slate-900 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-2xl overflow-hidden transition-colors">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-slate-50/50 dark:bg-slate-800/50">
                <th className="p-8 text-xs font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Customer Name</th>
                <th className="p-8 text-xs font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Current Balance</th>
                <th className="p-8 text-xs font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Last Activity</th>
                <th className="p-8 text-xs font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Status</th>
                <th className="p-8 text-xs font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50 dark:divide-slate-800">
              {loading ? (
                Array(5).fill(0).map((_, idx) => (
                  <tr key={idx}>
                    <td className="p-8">
                      <div className="flex items-center gap-4">
                        <Skeleton className="h-12 w-12 rounded-2xl dark:bg-slate-800" />
                        <Skeleton className="h-6 w-32 dark:bg-slate-800" />
                      </div>
                    </td>
                    <td className="p-8"><Skeleton className="h-8 w-24 dark:bg-slate-800" /></td>
                    <td className="p-8">
                      <Skeleton className="h-5 w-24 mb-1 dark:bg-slate-800" />
                      <Skeleton className="h-4 w-20 dark:bg-slate-800" />
                    </td>
                    <td className="p-8"><Skeleton className="h-8 w-24 rounded-full dark:bg-slate-800" /></td>
                    <td className="p-8 text-right"><Skeleton className="h-10 w-24 ml-auto rounded-2xl dark:bg-slate-800" /></td>
                  </tr>
                ))
              ) : filteredCustomers.length === 0 ? (
                <tr><td colSpan="5" className="p-20 text-center text-slate-300 dark:text-slate-600 font-bold italic text-lg">No borrowers found.</td></tr>
              ) : filteredCustomers.map((customer) => (
                <tr key={customer.id} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/50 transition-colors group">
                  <td className="p-8">
                    <div className="flex items-center gap-4">
                      <div className="h-12 w-12 bg-amber-50 dark:bg-amber-900/20 text-amber-500 rounded-2xl flex items-center justify-center font-black text-lg shadow-sm">
                        {customer.fullName[0]}
                      </div>
                      <p className="font-bold text-slate-800 dark:text-slate-200 text-lg">{customer.fullName}</p>
                    </div>
                  </td>
                  <td className="p-8">
                    <p className={`text-2xl font-black ${customer.currentDebt > 0 ? 'text-slate-800 dark:text-slate-100' : 'text-slate-300 dark:text-slate-700'}`}>
                      ₱{(customer.currentDebt || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}
                    </p>
                  </td>
                  <td className="p-8">
                    <div className="flex flex-col">
                      <span className="font-bold text-slate-500 dark:text-slate-400 text-sm">
                        {customer.lastUpdate ? new Date(customer.lastUpdate).toLocaleDateString() : 'Never'}
                      </span>
                      <span className="text-[10px] text-slate-400 dark:text-slate-500 font-bold uppercase tracking-tighter">
                        {customer.status === 'Paid' ? 'Cleared On' : 'Last Transaction'}
                      </span>
                    </div>
                  </td>
                  <td className="p-8">
                    <div className={`inline-flex items-center gap-1.5 px-4 py-2 rounded-full font-black text-[10px] uppercase tracking-widest ${
                      customer.currentDebt > 0 ? 'bg-amber-50 dark:bg-amber-900/20 text-amber-500' : 'bg-emerald-50 dark:bg-emerald-900/20 text-emerald-500'
                    }`}>
                      {customer.currentDebt > 0 ? <Clock size={12}/> : <CheckCircle2 size={12}/>}
                      {customer.currentDebt > 0 ? 'Outstanding' : 'Cleared'}
                    </div>
                  </td>
                  <td className="p-8">
                    <div className="flex justify-end gap-2">
                      <button 
                        onClick={() => { setSelectedCustomer(customer); fetchHistory(customer.id); setActiveTab('DEBT'); }}
                        className="p-3 bg-slate-100 dark:bg-slate-800 text-slate-400 dark:text-slate-500 hover:bg-slate-900 dark:hover:bg-amber-500 hover:text-white rounded-2xl transition-all"
                      ><History size={20} /></button>
                      <button 
                        onClick={() => { setSelectedCustomer(customer); setShowPayModal(true); }}
                        disabled={customer.currentDebt <= 0}
                        className="px-6 py-3 bg-amber-400 text-white font-black rounded-2xl hover:bg-amber-500 disabled:opacity-20 dark:disabled:opacity-5 shadow-lg shadow-amber-400/20 transition-all active:scale-95"
                      >Bayad</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add Customer Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-md z-50 flex items-center justify-center p-4">
          <form onSubmit={handleAddCustomer} className="bg-white dark:bg-slate-900 w-full max-w-md rounded-[3rem] shadow-2xl overflow-hidden animate-in zoom-in-95 duration-300 border border-transparent dark:border-slate-800">
            <div className="p-8 bg-slate-900 dark:bg-slate-950 text-white flex justify-between items-center">
              <h2 className="text-2xl font-black">Register <span className="text-amber-400">Borrower</span></h2>
              <button type="button" onClick={() => setShowAddModal(false)} className="hover:text-amber-400 transition-colors"><X size={24} /></button>
            </div>
            <div className="p-8 space-y-6">
              <div className="space-y-2">
                <label className="text-xs font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest ml-1">Full Name</label>
                <input 
                  autoFocus required
                  type="text" placeholder="e.g. Mang Jose" 
                  className="w-full p-4 bg-slate-50 dark:bg-slate-950 rounded-2xl outline-none focus:ring-2 focus:ring-amber-400 font-bold text-slate-800 dark:text-white border border-transparent dark:border-slate-800"
                  value={newCustomer.fullName} onChange={(e) => setNewCustomer({...newCustomer, fullName: e.target.value})}
                />
              </div>
              <div className="space-y-2">
                <label className="text-xs font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest ml-1">Email Address</label>
                <input 
                  type="email" placeholder="e.g. mangjose@gmail.com" 
                  className="w-full p-4 bg-slate-50 dark:bg-slate-950 rounded-2xl outline-none focus:ring-2 focus:ring-amber-400 font-bold text-slate-800 dark:text-white border border-transparent dark:border-slate-800"
                  value={newCustomer.email} onChange={(e) => setNewCustomer({...newCustomer, email: e.target.value})}
                />
              </div>
              <button type="submit" className="w-full py-4 bg-amber-400 text-white rounded-2xl font-black shadow-xl hover:bg-amber-500 transition-all transform active:scale-95">Add to Listahan</button>
            </div>
          </form>
        </div>
      )}

      {/* Payment Modal */}
      {showPayModal && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-md z-50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 w-full max-w-md rounded-[3rem] shadow-2xl overflow-hidden animate-in slide-in-from-bottom-8 duration-300 border border-transparent dark:border-slate-800">
            <div className="p-8 bg-emerald-500 text-white flex justify-between items-center">
              <div>
                <h2 className="text-2xl font-black italic">Record Payment</h2>
                <p className="text-white/80 text-xs font-bold uppercase tracking-widest">{selectedCustomer.fullName}</p>
              </div>
              <button onClick={() => setShowPayModal(false)} className="hover:scale-110 transition-transform"><X size={24} /></button>
            </div>
            <div className="p-8 space-y-6 text-center">
              <div>
                <p className="text-xs font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest mb-1">Outstanding Debt</p>
                <p className="text-4xl font-black text-slate-800 dark:text-white">₱{selectedCustomer.currentDebt.toLocaleString()}</p>
              </div>
              <input 
                type="number" placeholder="Enter amount paid" 
                className="w-full p-6 bg-slate-50 dark:bg-slate-950 rounded-3xl outline-none focus:ring-2 focus:ring-emerald-500 text-center text-2xl font-black text-slate-800 dark:text-white border border-transparent dark:border-slate-800"
                value={payAmount} onChange={(e) => setPayAmount(e.target.value)}
              />
              <button onClick={handlePayment} className="w-full py-5 bg-emerald-500 text-white rounded-[2rem] font-black shadow-xl hover:bg-emerald-600 transition-all active:scale-95">Complete Payment</button>
            </div>
          </div>
        </div>
      )}

      {/* History Modal with Tabs */}
      {showHistoryModal && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-md z-50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 w-full max-w-2xl rounded-[3rem] shadow-2xl overflow-hidden animate-in fade-in duration-300 border border-transparent dark:border-slate-800">
            <div className="p-8 border-b border-slate-50 dark:border-slate-800 flex justify-between items-center bg-slate-50/50 dark:bg-slate-800/50">
              <div>
                <h2 className="text-2xl font-black text-slate-800 dark:text-white">{selectedCustomer?.fullName}</h2>
                <p className="text-xs font-bold text-slate-400 dark:text-slate-500 uppercase tracking-widest">Statement of Account</p>
              </div>
              <button onClick={() => setShowHistoryModal(false)} className="p-2 bg-white dark:bg-slate-800 rounded-xl text-slate-400 hover:text-rose-500 transition-all"><X size={24} /></button>
            </div>

            {/* Tabs */}
            <div className="flex border-b border-slate-50 dark:border-slate-800">
              <button 
                onClick={() => setActiveTab('DEBT')}
                className={`flex-1 py-4 text-xs font-black uppercase tracking-widest flex items-center justify-center gap-2 transition-all ${activeTab === 'DEBT' ? 'text-amber-500 border-b-4 border-amber-500 bg-amber-50/50 dark:bg-amber-900/10' : 'text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800/50'}`}
              >
                <Package size={16} /> Utang History
              </button>
              <button 
                onClick={() => setActiveTab('PAYMENT')}
                className={`flex-1 py-4 text-xs font-black uppercase tracking-widest flex items-center justify-center gap-2 transition-all ${activeTab === 'PAYMENT' ? 'text-emerald-500 border-b-4 border-emerald-500 bg-emerald-50/50 dark:bg-emerald-900/10' : 'text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800/50'}`}
              >
                <Banknote size={16} /> Payment Logs
              </button>
            </div>

            <div className="p-8 max-h-[50vh] overflow-y-auto space-y-4 custom-scrollbar">
              {activeTab === 'DEBT' ? (
                history.length === 0 ? (
                  <p className="text-center text-slate-400 dark:text-slate-600 italic py-10">No debt records found.</p>
                ) : history.map((order) => (
                  <div key={order.id} className="p-6 bg-slate-50 dark:bg-slate-950 rounded-3xl border border-slate-100 dark:border-slate-800 animate-in slide-in-from-right-4 transition-colors">
                    <div className="flex justify-between items-center mb-3">
                      <span className="font-black text-slate-800 dark:text-slate-200 text-sm">Order #{order.id}</span>
                      <span className="text-[10px] font-bold text-slate-400 dark:text-slate-500">{new Date(order.timestamp).toLocaleString()}</span>
                    </div>
                    <div className="space-y-2 mb-3">
                      {order.items.map((item, idx) => (
                        <div key={idx} className="flex justify-between text-xs font-medium">
                          <span className="text-slate-600 dark:text-slate-400">{item.product?.name || 'Item'} x {item.quantity}</span>
                          <span className="text-slate-800 dark:text-slate-200">₱{(item.priceAtSale * item.quantity).toFixed(2)}</span>
                        </div>
                      ))}
                    </div>
                    <div className="flex justify-between pt-3 border-t border-slate-200/50 dark:border-slate-800">
                      <span className="font-bold text-slate-400 dark:text-slate-500 uppercase text-[9px] tracking-widest">Total for this day</span>
                      <span className="font-black text-amber-500">₱{order.totalAmount.toFixed(2)}</span>
                    </div>
                  </div>
                ))
              ) : (
                paymentHistory.length === 0 ? (
                  <p className="text-center text-slate-400 dark:text-slate-600 italic py-10">No payment logs found.</p>
                ) : paymentHistory.map((pay) => (
                  <div key={pay.id} className="p-6 bg-emerald-50/30 dark:bg-emerald-900/10 border border-emerald-100 dark:border-emerald-900/30 rounded-3xl flex justify-between items-center animate-in slide-in-from-left-4">
                    <div className="flex items-center gap-4">
                      <div className="h-10 w-10 bg-emerald-500 text-white rounded-xl flex items-center justify-center shadow-lg shadow-emerald-500/20"><CheckCircle2 size={20}/></div>
                      <div>
                        <p className="font-black text-emerald-600 dark:text-emerald-400">Payment Received</p>
                        <p className="text-[10px] text-slate-400 dark:text-slate-500 font-bold uppercase">{new Date(pay.timestamp).toLocaleString()}</p>
                      </div>
                    </div>
                    <p className="text-xl font-black text-slate-800 dark:text-slate-100">₱{pay.amount.toLocaleString(undefined, { minimumFractionDigits: 2 })}</p>
                  </div>
                ))
              )}
            </div>

            <div className="p-8 bg-slate-900 dark:bg-slate-950 text-white transition-colors">
              <div className="flex justify-between items-center">
                <span className="font-bold text-white/50">Current Balance Due</span>
                <span className="text-3xl font-black text-amber-400">₱{selectedCustomer?.currentDebt.toLocaleString(undefined, { minimumFractionDigits: 2 })}</span>
              </div>
            </div>
          </div>
        </div>
      )}

    </div>
  );
};

export default Listahan;