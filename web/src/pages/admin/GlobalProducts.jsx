import React, { useState } from 'react';
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

const GlobalProducts = () => {
  const [searchTerm, setSearchTerm] = useState('');

  // Mock data for global products
  const products = [
    { id: 1, name: "Red Horse (500ml)", vendor: "Maria's Sari-Sari", stock: 12, price: "₱120.00", category: "Beverages", status: "Low Stock" },
    { id: 2, name: "Lucky Me! Beef", vendor: "Junior's Store", stock: 154, price: "₱15.00", category: "Noodles", status: "Healthy" },
    { id: 3, name: "Coke 1.5L", vendor: "Aling Nena's Hub", stock: 5, price: "₱75.00", category: "Beverages", status: "Critical" },
    { id: 4, name: "Safeguard White", vendor: "Maria's Sari-Sari", stock: 45, price: "₱42.00", category: "Personal Care", status: "Healthy" },
    { id: 5, name: "Bear Brand 320g", vendor: "Junior's Store", stock: 22, price: "₱165.00", category: "Milk", status: "Healthy" },
  ];

  return (
    <div className="space-y-8 animate-in fade-in duration-500 text-slate-200">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-black text-white tracking-tight">Global Inventory</h1>
          <p className="text-slate-400 mt-1">Auditing all products and stock levels across the entire platform.</p>
        </div>
        <div className="flex items-center gap-3">
          <button className="px-4 py-2.5 bg-slate-900 border border-white/5 text-slate-300 rounded-xl font-semibold hover:bg-slate-800 transition-all flex items-center gap-2">
            <Filter size={18} />
            Export CSV
          </button>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        {[
          { label: 'Total SKUs', value: '1,420', icon: Box, color: 'text-blue-400' },
          { label: 'Total Stock', value: '45,200', icon: Tag, color: 'text-teal-400' },
          { label: 'Low Stock', value: '24', icon: AlertTriangle, color: 'text-amber-400' },
          { label: 'Out of Stock', value: '8', icon: Box, color: 'text-rose-400' },
        ].map((stat) => (
          <div key={stat.label} className="bg-slate-900/50 backdrop-blur-md p-6 rounded-3xl border border-white/5 shadow-2xl">
            <div className="flex items-center justify-between mb-4">
              <div className={`p-3 rounded-2xl bg-white/5 border border-white/5 ${stat.color}`}>
                <stat.icon size={20} />
              </div>
              <ArrowUpRight size={16} className="text-slate-600" />
            </div>
            <p className="text-xs font-bold text-slate-500 uppercase tracking-widest">{stat.label}</p>
            <p className="text-2xl font-black text-white mt-1">{stat.value}</p>
          </div>
        ))}
      </div>

      {/* Product Table */}
      <div className="bg-slate-900/40 backdrop-blur-xl rounded-[2.5rem] border border-white/5 shadow-2xl overflow-hidden">
        <div className="p-6 border-b border-white/5 bg-white/5">
          <div className="relative max-w-md">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500" size={20} />
            <input 
              type="text" 
              placeholder="Search by product name or vendor..." 
              className="w-full pl-12 pr-4 py-3 bg-slate-950/50 border border-white/5 rounded-2xl focus:ring-2 focus:ring-teal-500/20 transition-all text-sm text-slate-200"
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead>
              <tr className="bg-white/5">
                <th className="px-6 py-4 text-[10px] font-black text-slate-500 uppercase tracking-widest">Product Info</th>
                <th className="px-6 py-4 text-[10px] font-black text-slate-500 uppercase tracking-widest">Vendor</th>
                <th className="px-6 py-4 text-[10px] font-black text-slate-500 uppercase tracking-widest">Stock Level</th>
                <th className="px-6 py-4 text-[10px] font-black text-slate-500 uppercase tracking-widest">Price</th>
                <th className="px-6 py-4 text-[10px] font-black text-slate-500 uppercase tracking-widest">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-white/5">
              {products.map((item) => (
                <tr key={item.id} className="hover:bg-white/5 transition-colors group">
                  <td className="px-6 py-5">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-xl bg-slate-800 flex items-center justify-center text-teal-400 font-bold border border-white/5">
                        {item.name.charAt(0)}
                      </div>
                      <div>
                        <p className="font-bold text-slate-200 group-hover:text-teal-400 transition-colors">{item.name}</p>
                        <p className="text-xs text-slate-500">{item.category}</p>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-5">
                    <div className="flex items-center gap-2 text-slate-400">
                      <Store size={14} className="text-slate-600" />
                      <span className="text-sm font-medium">{item.vendor}</span>
                    </div>
                  </td>
                  <td className="px-6 py-5">
                    <div className="space-y-1">
                      <p className="text-sm font-black text-slate-300">{item.stock} Units</p>
                      <div className="h-1 w-24 bg-slate-800 rounded-full overflow-hidden">
                        <div 
                          className={`h-full ${item.stock < 10 ? 'bg-rose-500' : 'bg-teal-500'} rounded-full`}
                          style={{ width: `${Math.min(item.stock, 100)}%` }}
                        ></div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-5 font-black text-white">{item.price}</td>
                  <td className="px-6 py-5">
                    <span className={`px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider ${
                      item.status === 'Healthy' 
                        ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' 
                        : item.status === 'Low Stock'
                        ? 'bg-amber-500/10 text-amber-400 border border-amber-500/20'
                        : 'bg-rose-500/10 text-rose-400 border border-rose-500/20'
                    }`}>
                      {item.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default GlobalProducts;
