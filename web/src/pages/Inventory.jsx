import { useState, useEffect } from 'react';
import axios from 'axios';
import { Plus, Package, Trash2, Barcode, X } from 'lucide-react';

const Inventory = ({ user }) => {
  const [products, setProducts] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    name: '', barcode: '', price: '', stockQuantity: '', vendorId: user?.id || 1
  });

  // Fetch from your Spring Boot ProductController
  const fetchProducts = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/products');
      setProducts(response.data);
    } catch (error) {
      console.error("Fetch failed:", error);
    }
  };

  useEffect(() => { fetchProducts(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post('http://localhost:8080/api/products', formData);
      setIsModalOpen(false);
      fetchProducts();
      setFormData({ name: '', barcode: '', price: '', stockQuantity: '', vendorId: user?.id || 1 });
    } catch (error) {
      alert("Error saving product");
    }
  };

  return (
    <div className="space-y-8 animate-in fade-in duration-500">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-black text-slate-800">Inventory</h1>
          <p className="text-slate-500 font-medium">Manage your items and stock levels.</p>
        </div>
        <button 
          onClick={() => setIsModalOpen(true)}
          className="bg-[#16A394] text-white px-6 py-3 rounded-2xl font-bold shadow-lg shadow-[#16A394]/20 hover:bg-[#0D7A6F] transition-all active:scale-95 flex items-center gap-2"
        >
          <Plus size={20} /> Add Product
        </button>
      </div>

      {/* Product Grid (More modern than a table) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {products.map((product) => (
          <div key={product.id} className="bg-white p-6 rounded-[2.5rem] shadow-xl border border-slate-100 group relative">
             <div className="h-32 w-full bg-slate-50 rounded-3xl mb-4 flex items-center justify-center text-[#16A394]">
               <Package size={40} />
             </div>
             <h3 className="font-bold text-slate-800 text-lg truncate">{product.name}</h3>
             <p className="text-xs text-slate-400 font-bold mb-3 flex items-center gap-1">
               <Barcode size={12} /> {product.barcode || 'N/A'}
             </p>
             <div className="flex justify-between items-end border-t border-slate-50 pt-3">
               <div>
                 <p className="text-[10px] text-slate-400 font-bold uppercase tracking-widest">Price</p>
                 <p className="text-xl font-black text-[#16A394]">₱{product.price.toFixed(2)}</p>
               </div>
               <div className="text-right">
                 <p className="text-[10px] text-slate-400 font-bold uppercase tracking-widest">Stock</p>
                 <p className={`font-bold ${product.stockQuantity < 5 ? 'text-rose-500' : 'text-slate-700'}`}>
                   {product.stockQuantity}
                 </p>
               </div>
             </div>
          </div>
        ))}
      </div>

      {/* Add Product Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-sm">
          <div className="bg-white w-full max-w-md rounded-[2.5rem] shadow-2xl p-8 animate-in zoom-in duration-200">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-xl font-black text-slate-800">New Product</h2>
              <button onClick={() => setIsModalOpen(false)}><X className="text-slate-400" /></button>
            </div>
            <form onSubmit={handleSubmit} className="space-y-4">
              <input 
                type="text" placeholder="Product Name" required
                className="w-full p-4 bg-slate-50 text-slate-900 rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394]"
                value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})}
              />
              <input 
                type="text" placeholder="Barcode"
                className="w-full p-4 bg-slate-50 text-slate-900  rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394]"
                value={formData.barcode} onChange={e => setFormData({...formData, barcode: e.target.value})}
              />
              <div className="grid grid-cols-2 gap-4">
                <input 
                  type="number" placeholder="Price (₱)" required
                  className="w-full p-4 bg-slate-50 text-slate-900  rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394]"
                  value={formData.price} onChange={e => setFormData({...formData, price: e.target.value})}
                />
                <input 
                  type="number" placeholder="Stock Qty" required
                  className="w-full p-4 bg-slate-50 text-slate-900  rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394]"
                  value={formData.stockQuantity} onChange={e => setFormData({...formData, stockQuantity: e.target.value})}
                />
              </div>
              <button type="submit" className="w-full bg-[#16A394] text-white py-4 rounded-2xl font-black shadow-lg">
                Save Product
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Inventory;