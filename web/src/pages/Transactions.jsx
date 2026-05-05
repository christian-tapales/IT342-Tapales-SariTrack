import { useState, useEffect } from 'react';
import { Search, Filter, ChevronLeft, ChevronRight, Download, Clock, CheckCircle2, XCircle, X, Package } from 'lucide-react';
import api from '../api';

const Transactions = ({ user }) => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [selectedOrder, setSelectedOrder] = useState(null);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const response = await api.get(`/orders/history?vendorId=${user.id}`);
        setOrders(response.data);
        setLoading(false);
      } catch (error) {
        console.error("Error fetching history", error);
        setLoading(false);
      }
    };
    if (user?.id) fetchOrders();
  }, [user]);

  const filteredOrders = orders.filter(order => {
    const matchesSearch = order.id.toString().includes(searchTerm);
    const matchesStatus = statusFilter === 'ALL' || order.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const getStatusStyle = (status) => {
    switch (status) {
      case 'PAID':
        return { bg: 'bg-emerald-50', text: 'text-emerald-600', icon: CheckCircle2 };
      case 'CANCELLED':
        return { bg: 'bg-rose-50', text: 'text-rose-600', icon: XCircle };
      default:
        return { bg: 'bg-amber-50', text: 'text-amber-600', icon: Clock };
    }
  };

  const handleExport = () => {
    const headers = ['Order ID', 'Date', 'Time', 'Total Amount', 'Status', 'Items Count'];
    const csvRows = filteredOrders.map(order => [
      `#${order.id}`,
      new Date(order.timestamp).toLocaleDateString(),
      new Date(order.timestamp).toLocaleTimeString(),
      order.totalAmount,
      order.status,
      order.items?.length || 0
    ]);

    const csvContent = [headers, ...csvRows].map(row => row.join(',')).join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `SariTrack_Report_${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
  };

  return (
    <div className="max-w-7xl mx-auto space-y-6 animate-in fade-in duration-500 relative">
      {/* Header section */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-black text-slate-800">Transaction History</h1>
          <p className="text-slate-500 font-medium">Review and manage all your store sales.</p>
        </div>
        <button 
          onClick={handleExport}
          className="flex items-center gap-2 bg-slate-900 text-white px-6 py-3 rounded-2xl font-bold hover:bg-slate-800 transition-all shadow-lg active:scale-95"
        >
          <Download size={20} /> Export Report
        </button>
      </div>

      {/* Filters bar */}
      <div className="bg-white p-4 rounded-[2rem] border border-slate-100 shadow-xl flex flex-col md:flex-row gap-4 items-center">
        <div className="relative flex-1 w-full">
          <Search className="absolute left-4 top-3.5 text-slate-400" size={20} />
          <input
            type="text"
            placeholder="Search Order ID..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-12 pr-4 py-3 bg-slate-50 border-none rounded-xl focus:ring-2 focus:ring-[#16A394] outline-none transition-all"
          />
        </div>
        <div className="flex gap-2 w-full md:w-auto overflow-x-auto pb-2 md:pb-0">
          {['ALL', 'PAID', 'PENDING', 'CANCELLED'].map((status) => (
            <button
              key={status}
              onClick={() => setStatusFilter(status)}
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all whitespace-nowrap ${
                statusFilter === status 
                ? 'bg-[#16A394] text-white shadow-md' 
                : 'bg-slate-50 text-slate-500 hover:bg-slate-100'
              }`}
            >
              {status}
            </button>
          ))}
        </div>
      </div>

      {/* Data Table */}
      <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-2xl overflow-hidden">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="bg-slate-50/50">
              <th className="p-6 text-xs font-black text-slate-400 uppercase tracking-widest">Order ID</th>
              <th className="p-6 text-xs font-black text-slate-400 uppercase tracking-widest">Date & Time</th>
              <th className="p-6 text-xs font-black text-slate-400 uppercase tracking-widest">Amount</th>
              <th className="p-6 text-xs font-black text-slate-400 uppercase tracking-widest">Items</th>
              <th className="p-6 text-xs font-black text-slate-400 uppercase tracking-widest">Status</th>
              <th className="p-6 text-xs font-black text-slate-400 uppercase tracking-widest text-right">Action</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-50">
            {loading ? (
              <tr><td colSpan="6" className="p-20 text-center text-slate-400 font-bold italic">Loading records...</td></tr>
            ) : filteredOrders.length === 0 ? (
              <tr><td colSpan="6" className="p-20 text-center text-slate-400 font-bold italic">No transactions found.</td></tr>
            ) : filteredOrders.map((order) => {
              const status = getStatusStyle(order.status);
              const StatusIcon = status.icon;
              return (
                <tr key={order.id} className="hover:bg-slate-50/50 transition-colors group">
                  <td className="p-6">
                    <span className="font-black text-slate-800 bg-slate-100 px-3 py-1.5 rounded-lg text-sm">#{order.id}</span>
                  </td>
                  <td className="p-6">
                    <div className="flex flex-col">
                      <span className="font-bold text-slate-700">{new Date(order.timestamp).toLocaleDateString()}</span>
                      <span className="text-xs text-slate-400 font-medium">{new Date(order.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                    </div>
                  </td>
                  <td className="p-6 font-black text-slate-800 text-lg">
                    ₱{(order.totalAmount || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}
                  </td>
                  <td className="p-6 text-sm text-slate-500 font-medium">
                    {order.items?.length || 0} items
                  </td>
                  <td className="p-6">
                    <div className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full font-bold text-[10px] uppercase tracking-wider ${status.bg} ${status.text}`}>
                      <StatusIcon size={12} /> {order.status || 'PENDING'}
                    </div>
                  </td>
                  <td className="p-6 text-right">
                    <button 
                      onClick={() => setSelectedOrder(order)}
                      className="text-slate-400 hover:text-[#16A394] font-bold text-sm transition-colors"
                    >
                      Details
                    </button>
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>

        {/* Pagination placeholder */}
        <div className="p-6 bg-slate-50/30 flex justify-between items-center border-t border-slate-50">
          <p className="text-xs text-slate-400 font-bold">Showing {filteredOrders.length} transactions</p>
          <div className="flex gap-2">
            <button className="p-2 rounded-xl bg-white border border-slate-100 text-slate-400 hover:text-[#16A394] transition-all"><ChevronLeft size={20} /></button>
            <button className="p-2 rounded-xl bg-white border border-slate-100 text-slate-400 hover:text-[#16A394] transition-all"><ChevronRight size={20} /></button>
          </div>
        </div>
      </div>

      {/* Details Modal */}
      {selectedOrder && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-in fade-in duration-300">
          <div className="bg-white w-full max-w-lg rounded-[3rem] shadow-2xl overflow-hidden animate-in slide-in-from-bottom-8 duration-300">
            <div className="p-8 border-b border-slate-50 flex justify-between items-center bg-slate-50/50">
              <div>
                <h2 className="text-2xl font-black text-slate-800">Order #{selectedOrder.id}</h2>
                <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">{new Date(selectedOrder.timestamp).toLocaleString()}</p>
              </div>
              <button onClick={() => setSelectedOrder(null)} className="p-3 bg-white rounded-2xl text-slate-400 hover:text-rose-500 shadow-sm transition-all"><X size={20} /></button>
            </div>
            
            <div className="p-8 space-y-6 max-h-[60vh] overflow-y-auto">
              {selectedOrder.items?.map((item, idx) => (
                <div key={idx} className="flex items-center gap-4 group">
                  <div className="h-12 w-12 bg-slate-50 rounded-2xl flex items-center justify-center text-slate-300">
                    {item.product?.imageUrl ? (
                      <img src={item.product.imageUrl} alt="" className="h-full w-full object-cover rounded-2xl" />
                    ) : (
                      <Package size={20} />
                    )}
                  </div>
                  <div className="flex-1">
                    <p className="font-bold text-slate-800">{item.product?.name || `Product #${item.productId}`}</p>
                    <p className="text-xs text-slate-400 font-bold">{item.quantity} x ₱{(item.priceAtSale || 0).toFixed(2)}</p>
                  </div>
                  <p className="font-black text-slate-800 text-sm">₱{(item.quantity * item.priceAtSale).toFixed(2)}</p>
                </div>
              ))}
            </div>

            <div className="p-8 bg-slate-900 text-white">
              <div className="flex justify-between items-center">
                <span className="font-bold text-white/60">Grand Total</span>
                <span className="text-3xl font-black text-teal-400">₱{(selectedOrder.totalAmount || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</span>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Transactions;
