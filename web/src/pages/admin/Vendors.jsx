import { useState, useEffect } from 'react';
import { 
  Users, 
  Store, 
  Search, 
  Filter, 
  MoreVertical, 
  ExternalLink,
  ShieldCheck,
  AlertCircle
} from 'lucide-react';

import axios from 'axios';
import api from '../../api';

const Vendors = () => {
  const [vendors, setVendors] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchVendors = async () => {
      try {
        const response = await api.get('/admin/vendors/analytics');
        setVendors(response.data);
      } catch (error) {
        console.error("Error fetching vendors:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchVendors();
  }, []);

  // Use raw data from backend
  const displayVendors = vendors;

  // Header Stats Calculations
  const totalVendorsCount = vendors.length;
  const activeVendorsCount = vendors.filter(v => v.status !== 'New Member').length;
  const topSellersCount = vendors.filter(v => v.status === 'Top Seller').length;

  return (
    <div className="space-y-8 animate-in fade-in duration-500 text-slate-800 dark:text-slate-200 transition-colors">
      {/* Page Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-slate-800 dark:text-white tracking-tight">Vendor Management</h1>
          <p className="text-slate-500 dark:text-slate-400 mt-1">Control and monitor all registered sari-sari stores on your platform.</p>
        </div>
        <div className="flex items-center gap-3">
          <button className="px-4 py-2.5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-slate-600 dark:text-slate-300 rounded-xl font-semibold shadow-sm hover:bg-slate-50 dark:hover:bg-slate-800 transition-all flex items-center gap-2 border-white/5">
            <Filter size={18} />
            Filter
          </button>
          <button className="px-4 py-2.5 bg-teal-600 text-white rounded-xl font-bold shadow-lg shadow-teal-600/20 hover:bg-teal-700 transition-all active:scale-95 flex items-center gap-2">
            <ShieldCheck size={18} />
            Approve All
          </button>
        </div>
      </div>

      {/* Stats Quick Look */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {[
          { label: 'Total Vendors', value: totalVendorsCount, icon: Store, color: 'bg-blue-500/10 text-blue-500 dark:text-blue-400 border-blue-500/20' },
          { label: 'Active Today', value: activeVendorsCount, icon: Users, color: 'bg-teal-500/10 text-teal-600 dark:text-teal-400 border-teal-500/20' },
          { label: 'Top Sellers', value: topSellersCount, icon: ShieldCheck, color: 'bg-amber-500/10 text-amber-600 dark:text-amber-400 border-amber-500/20' },
        ].map((stat) => (
          <div key={stat.label} className="bg-white dark:bg-slate-900/50 backdrop-blur-md p-6 rounded-[2rem] border border-slate-100 dark:border-white/5 shadow-2xl flex items-center gap-4 transition-colors">
            <div className={`${stat.color} p-3 rounded-2xl border shadow-lg`}>
              <stat.icon size={24} />
            </div>
            <div>
              <p className="text-xs font-bold text-slate-400 dark:text-slate-500 uppercase tracking-widest">{stat.label}</p>
              <p className="text-2xl font-bold text-slate-800 dark:text-white">{stat.value}</p>
            </div>
          </div>
        ))}
      </div>

      {/* Vendors Table */}
      <div className="bg-white dark:bg-slate-900/40 backdrop-blur-xl rounded-[2.5rem] border border-slate-100 dark:border-white/5 shadow-2xl overflow-hidden transition-colors">
        <div className="p-6 border-b border-slate-50 dark:border-white/5 flex items-center justify-between gap-4 bg-slate-50/30 dark:bg-white/5">
          <div className="relative flex-1 max-w-md">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 dark:text-slate-500" size={20} />
            <input 
              type="text" 
              placeholder="Search by store name or owner..." 
              className="w-full pl-12 pr-4 py-3 bg-white dark:bg-slate-950/50 border border-slate-200 dark:border-white/5 rounded-2xl focus:ring-2 focus:ring-teal-500/20 transition-all text-sm text-slate-800 dark:text-slate-200 placeholder:text-slate-400 dark:placeholder:text-slate-600"
            />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-slate-50/50 dark:bg-white/5">
                <th className="px-6 py-4 text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Store Information</th>
                <th className="px-6 py-4 text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Status</th>
                <th className="px-6 py-4 text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Joined Date</th>
                <th className="px-6 py-4 text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Lifetime Sales</th>
                <th className="px-6 py-4 text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50 dark:divide-white/5">
              {loading ? (
                Array(5).fill(0).map((_, idx) => (
                  <tr key={idx}>
                    <td className="px-6 py-5"><div className="flex gap-3 items-center"><Skeleton className="h-10 w-10 rounded-xl" /><div className="space-y-2"><Skeleton className="h-4 w-32" /><Skeleton className="h-3 w-24" /></div></div></td>
                    <td className="px-6 py-5"><Skeleton className="h-6 w-20 rounded-full" /></td>
                    <td className="px-6 py-5"><Skeleton className="h-4 w-24" /></td>
                    <td className="px-6 py-5"><Skeleton className="h-4 w-16" /></td>
                    <td className="px-6 py-5 text-right"><Skeleton className="h-8 w-8 ml-auto rounded-lg" /></td>
                  </tr>
                ))
              ) : displayVendors.map((vendor) => (
                <tr key={vendor.id} className="hover:bg-slate-50 dark:hover:bg-white/5 transition-colors group">
                  <td className="px-6 py-5">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center text-slate-500 dark:text-slate-400 font-bold border border-slate-200 dark:border-white/5 group-hover:border-teal-500/30 transition-all">
                        {vendor.name.charAt(0)}
                      </div>
                      <div>
                        <p className="font-bold text-slate-800 dark:text-slate-200 flex items-center gap-2 group-hover:text-teal-600 dark:group-hover:text-teal-400 transition-colors">
                          {vendor.name}'s Store
                          <ExternalLink size={12} className="text-slate-400 dark:text-slate-600 opacity-0 group-hover:opacity-100 transition-opacity" />
                        </p>
                        <p className="text-xs text-slate-500">{vendor.name} • {vendor.email}</p>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-5">
                    <span className={`px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider ${
                      vendor.status === 'Top Seller' ? 'bg-amber-500/10 text-amber-600 dark:text-amber-400 border border-amber-500/20' : 
                      vendor.status === 'Active' ? 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20' : 
                      'bg-slate-500/10 text-slate-500 dark:text-slate-400 border border-slate-500/20'
                    }`}>
                      {vendor.status}
                    </span>
                  </td>
                  <td className="px-6 py-5 text-sm text-slate-500 dark:text-slate-400 font-medium">
                    {vendor.registrationDate ? new Date(vendor.registrationDate).toLocaleDateString() : 'N/A'}
                  </td>
                  <td className="px-6 py-5 text-sm font-bold text-slate-700 dark:text-slate-300">₱{vendor.totalSales?.toFixed(2)}</td>
                  <td className="px-6 py-5 text-right">
                    <button className="p-2 text-slate-400 hover:text-teal-600 dark:hover:text-teal-400 hover:bg-teal-50 dark:hover:bg-teal-500/10 rounded-lg transition-all">
                      <MoreVertical size={20} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        
        <div className="p-6 bg-slate-50/50 dark:bg-white/5 border-t border-slate-100 dark:border-white/5 text-center transition-colors">
          <button className="text-sm font-bold text-teal-600 dark:text-teal-400 hover:text-teal-700 dark:hover:text-teal-300 transition-colors">
            View All Platform Data
          </button>
        </div>
      </div>
    </div>
  );
};

export default Vendors;
