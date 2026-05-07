import React, { useState, useEffect } from 'react';
import { 
  PackageSearch, 
  Search, 
  Filter, 
  ArrowUpRight, 
  AlertTriangle,
  Tag,
  Store,
  Box
} from 'lucide-react';
import api from '../../api';

const GlobalProducts = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [products, setProducts] = useState([]);
  const [vendors, setVendors] = useState({});
  const [stats, setStats] = useState({
    totalSKUs: 0,
    totalStock: 0,
    lowStock: 0,
    outOfStock: 0
  });
  const [loading, setLoading] = useState(true);

  const fetchData = async () => {
    try {
      const [statsRes, productsRes, vendorsRes] = await Promise.all([
        api.get('/admin/stats'),
        api.get('/products'),
        api.get('/admin/vendors/analytics')
      ]);

      const vendorMap = {};
      vendorsRes.data.forEach(v => vendorMap[v.id] = v.name);
      setVendors(vendorMap);

      const allProducts = productsRes.data;
      setProducts(allProducts);

      const lowStockCount = allProducts.filter(p => p.stockQuantity > 0 && p.stockQuantity < 10).length;
      const outOfStockCount = allProducts.filter(p => p.stockQuantity === 0).length;

      setStats({
        totalSKUs: statsRes.data.totalSKUs,
        totalStock: statsRes.data.totalStock,
        lowStock: lowStockCount,
        outOfStock: outOfStockCount
      });
      setLoading(false);
    } catch (error) {
      console.error("Failed to fetch global products data", error);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const filteredProducts = products.filter(p => 
    p.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (vendors[p.vendorId] || '').toLowerCase().includes(searchTerm.toLowerCase())
  );

  const exportToCSV = () => {
    const headers = ["ID", "Product Name", "Category", "Vendor", "Stock", "Price", "Status"];
    const rows = filteredProducts.map(p => {
      const status = p.stockQuantity === 0 ? 'Out of Stock' : (p.stockQuantity < 10 ? 'Low Stock' : 'Healthy');
      return [
        p.id,
        p.name,
        p.category,
        vendors[p.vendorId] || 'Unknown',
        p.stockQuantity,
        p.price,
        status
      ];
    });

    const csvContent = [headers, ...rows].map(e => e.join(",")).join("\n");
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement("a");
    const url = URL.createObjectURL(blob);
    link.setAttribute("href", url);
    link.setAttribute("download", `SariTrack_Global_Inventory_${new Date().toISOString().split('T')[0]}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="space-y-8 animate-in fade-in duration-500 text-slate-800 dark:text-slate-200 transition-colors">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-black text-slate-800 dark:text-white tracking-tight">Global Inventory</h1>
          <p className="text-slate-500 dark:text-slate-400 mt-1">Auditing all products and stock levels across the entire platform.</p>
        </div>
        <div className="flex items-center gap-3">
          <button 
            onClick={exportToCSV}
            className="px-4 py-2.5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-white/5 text-slate-600 dark:text-slate-300 rounded-xl font-semibold hover:bg-slate-50 dark:hover:bg-slate-800 transition-all flex items-center gap-2 shadow-sm">
            <Filter size={18} />
            Export CSV
          </button>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        {[
          { label: 'Total SKUs', value: stats.totalSKUs.toLocaleString(), icon: Box, color: 'text-blue-500 dark:text-blue-400' },
          { label: 'Total Stock', value: stats.totalStock.toLocaleString(), icon: Tag, color: 'text-teal-600 dark:text-teal-400' },
          { label: 'Low Stock', value: stats.lowStock.toLocaleString(), icon: AlertTriangle, color: 'text-amber-600 dark:text-amber-400' },
          { label: 'Out of Stock', value: stats.outOfStock.toLocaleString(), icon: Box, color: 'text-rose-600 dark:text-rose-400' },
        ].map((stat) => (
          <div key={stat.label} className="bg-white dark:bg-slate-900/50 backdrop-blur-md p-6 rounded-3xl border border-slate-100 dark:border-white/5 shadow-2xl transition-colors">
            <div className="flex items-center justify-between mb-4">
              <div className={`p-3 rounded-2xl bg-slate-50 dark:bg-white/5 border border-slate-100 dark:border-white/5 ${stat.color}`}>
                <stat.icon size={20} />
              </div>
              <ArrowUpRight size={16} className="text-slate-400 dark:text-slate-600" />
            </div>
            <p className="text-xs font-bold text-slate-400 dark:text-slate-500 uppercase tracking-widest">{stat.label}</p>
            <p className="text-2xl font-black text-slate-800 dark:text-white mt-1">{stat.value}</p>
          </div>
        ))}
      </div>

      {/* Product Table */}
      <div className="bg-white dark:bg-slate-900/40 backdrop-blur-xl rounded-[2.5rem] border border-slate-100 dark:border-white/5 shadow-2xl overflow-hidden transition-colors">
        <div className="p-6 border-b border-slate-50 dark:border-white/5 bg-slate-50/30 dark:bg-white/5">
          <div className="relative max-w-md">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 dark:text-slate-500" size={20} />
            <input 
              type="text" 
              placeholder="Search by product name or vendor..." 
              className="w-full pl-12 pr-4 py-3 bg-white dark:bg-slate-950/50 border border-slate-200 dark:border-white/5 rounded-2xl focus:ring-2 focus:ring-teal-500/20 transition-all text-sm text-slate-800 dark:text-slate-200"
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead>
              <tr className="bg-slate-50/50 dark:bg-white/5">
                <th className="px-6 py-4 text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Product Info</th>
                <th className="px-6 py-4 text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Vendor</th>
                <th className="px-6 py-4 text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Stock Level</th>
                <th className="px-6 py-4 text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Price</th>
                <th className="px-6 py-4 text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50 dark:divide-white/5">
              {loading ? (
                Array(5).fill(0).map((_, idx) => (
                  <tr key={idx}>
                    <td className="px-6 py-5"><div className="flex gap-3 items-center"><Skeleton className="h-10 w-10 rounded-xl" /><div className="space-y-2"><Skeleton className="h-4 w-32" /><Skeleton className="h-3 w-24" /></div></div></td>
                    <td className="px-6 py-5"><Skeleton className="h-4 w-24" /></td>
                    <td className="px-6 py-5"><Skeleton className="h-4 w-20" /></td>
                    <td className="px-6 py-5"><Skeleton className="h-4 w-16" /></td>
                    <td className="px-6 py-5"><Skeleton className="h-6 w-20 rounded-full" /></td>
                  </tr>
                ))
              ) : filteredProducts.map((item) => {
                const status = item.stockQuantity === 0 ? 'Out of Stock' : (item.stockQuantity < 10 ? 'Low Stock' : 'Healthy');
                return (
                  <tr key={item.id} className="hover:bg-slate-50 dark:hover:bg-white/5 transition-colors group">
                    <td className="px-6 py-5">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center text-teal-600 dark:text-teal-400 font-bold border border-slate-200 dark:border-white/5">
                          {item.name.charAt(0)}
                        </div>
                        <div>
                          <p className="font-bold text-slate-800 dark:text-slate-200 group-hover:text-teal-600 dark:group-hover:text-teal-400 transition-colors">{item.name}</p>
                          <p className="text-xs text-slate-500">{item.category}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-5">
                      <div className="flex items-center gap-2 text-slate-500 dark:text-slate-400">
                        <Store size={14} className="text-slate-400 dark:text-slate-600" />
                        <span className="text-sm font-medium">{vendors[item.vendorId] || 'Unknown Vendor'}</span>
                      </div>
                    </td>
                    <td className="px-6 py-5">
                      <div className="space-y-1">
                        <p className="text-sm font-black text-slate-700 dark:text-slate-300">{item.stockQuantity} Units</p>
                        <div className="h-1 w-24 bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                          <div 
                            className={`h-full ${item.stockQuantity < 10 ? 'bg-rose-500' : 'bg-teal-500'} rounded-full`}
                            style={{ width: `${Math.min(item.stockQuantity, 100)}%` }}
                          ></div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-5 font-black text-slate-800 dark:text-white">₱{item.price.toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
                    <td className="px-6 py-5">
                      <span className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-wider ${
                        status === 'Healthy' 
                          ? 'bg-emerald-50 dark:bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20' 
                          : status === 'Low Stock'
                          ? 'bg-amber-50 dark:bg-amber-500/10 text-amber-600 dark:text-amber-400 border border-amber-500/20'
                          : 'bg-rose-50 dark:bg-rose-500/10 text-rose-600 dark:text-rose-400 border border-rose-500/20'
                      }`}>
                        {status}
                      </span>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default GlobalProducts;
