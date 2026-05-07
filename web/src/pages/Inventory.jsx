import { useState, useEffect } from 'react';
import api from '../api';
import { Plus, Package, Trash2, Barcode, X, Pencil, Image as ImageIcon, Loader2, Upload, Wand2, FileText, Download } from 'lucide-react';
import { supabase } from '../supabaseClient';
import Skeleton from '../components/Skeleton';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

const Inventory = ({ user }) => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [formData, setFormData] = useState({
    name: '', barcode: '', price: '', stockQuantity: '', imageUrl: '', vendorId: user?.id
  });

  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    try {
      setUploading(true);
      const fileExt = file.name.split('.').pop();
      const fileName = `${Math.random()}.${fileExt}`;
      const filePath = `${user.id}/${fileName}`;

      const { error: uploadError } = await supabase.storage
        .from('product-images')
        .upload(filePath, file);

      if (uploadError) throw uploadError;

      const { data: { publicUrl } } = supabase.storage
        .from('product-images')
        .getPublicUrl(filePath);

      setFormData({ ...formData, imageUrl: publicUrl });
      alert("Image uploaded successfully!");
    } catch (error) {
      alert("Error uploading image: " + error.message);
    } finally {
      setUploading(false);
    }
  };

  const handleLookup = async () => {
    if (!formData.barcode) return;
    try {
      setUploading(true);
      const response = await api.get(`/products/lookup/${formData.barcode}`);
      if (response.data.productName && response.data.productName !== "Unknown Product") {
        setFormData({ ...formData, name: response.data.productName });
      } else {
        alert("Product not found in international database.");
      }
    } catch (error) {
      console.error("Lookup failed:", error);
    } finally {
      setUploading(false);
    }
  };

  // Fetch only this vendor's products
  const fetchProducts = async () => {
    if (!user?.id) return;
    try {
      setLoading(true);
      const response = await api.get(`/products?vendorId=${user.id}`);
      setProducts(response.data);
    } catch (error) {
      console.error("Fetch failed:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { 
    if (user?.id) {
      fetchProducts(); 
      setFormData(prev => ({ ...prev, vendorId: user.id }));
    }
  }, [user]);

  const openEditModal = (product) => {
    setEditingId(product.id);
    setFormData({
      name: product.name,
      barcode: product.barcode,
      price: product.price,
      stockQuantity: product.stockQuantity,
      imageUrl: product.imageUrl || '',
      vendorId: user.id
    });
    setIsModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!user?.id) {
      alert("Error: User session not found. Please log in again.");
      return;
    }

    try {
      const finalData = { ...formData, vendorId: user.id };
      
      if (editingId) {
        // UPDATE existing product
        await api.put(`/products/${editingId}?vendorId=${user.id}`, finalData);
      } else {
        // CREATE new product
        await api.post('/products', finalData);
      }

      setIsModalOpen(false);
      setEditingId(null);
      fetchProducts();
      setFormData({ name: '', barcode: '', price: '', stockQuantity: '', imageUrl: '', vendorId: user.id });
    } catch (error) {
      alert("Error saving product: " + (error.response?.data || "Unknown error"));
    }
  };

  const [isExportMenuOpen, setIsExportMenuOpen] = useState(false);

  const handleExportCSV = () => {
    if (products.length === 0) {
      alert("No products to export.");
      return;
    }

    const headers = ["ID", "Name", "Barcode", "Price", "Stock", "Category"];
    const csvContent = [
      headers.join(","),
      ...products.map(p => [
        p.id,
        `"${p.name}"`,
        p.barcode || 'N/A',
        p.price,
        p.stockQuantity,
        p.category || 'General'
      ].join(","))
    ].join("\n");

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement("a");
    const url = URL.createObjectURL(blob);
    link.setAttribute("href", url);
    link.setAttribute("download", `SariTrack_Inventory_${new Date().toISOString().split('T')[0]}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    setIsExportMenuOpen(false);
  };

  const handleExportPDF = () => {
    if (products.length === 0) {
      alert("No products to export.");
      return;
    }

    try {
      const doc = new jsPDF();
      
      // Add Header
      doc.setFontSize(20);
      doc.text('SariTrack Inventory Report', 14, 22);
      doc.setFontSize(11);
      doc.setTextColor(100);
      doc.text(`Generated on: ${new Date().toLocaleString()}`, 14, 30);
      doc.text(`Vendor: ${user?.name || 'SariTrack Store'}`, 14, 36);

      const tableColumn = ["ID", "Product Name", "Barcode", "Price", "Stock"];
      const tableRows = products.map(p => [
        p.id,
        p.name,
        p.barcode || 'N/A',
        `PHP ${(p.price || 0).toLocaleString()}`,
        p.stockQuantity || 0
      ]);

      autoTable(doc, {
        head: [tableColumn],
        body: tableRows,
        startY: 45,
        theme: 'grid',
        headStyles: { fillColor: [22, 163, 148] }, // Hex #16A394 in RGB
        styles: { fontSize: 9 }
      });

      doc.save(`SariTrack_Inventory_${new Date().toISOString().split('T')[0]}.pdf`);
    } catch (error) {
      console.error("PDF Export failed:", error);
      alert("Failed to generate PDF. Please try again.");
    } finally {
      setIsExportMenuOpen(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Are you sure you want to delete this product?")) {
      try {
        await api.delete(`/products/${id}?vendorId=${user?.id}`);
        fetchProducts();
      } catch (error) {
        alert("Delete failed: Unauthorized or product not found");
      }
    }
  };

  return (
    <div className="space-y-8 animate-in fade-in duration-500">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-black text-slate-800 dark:text-white">Inventory</h1>
          <p className="text-slate-500 dark:text-slate-400 font-medium">Manage your items and stock levels.</p>
        </div>
        <div className="flex gap-3">
          <div className="relative">
            <button 
              onClick={() => setIsExportMenuOpen(!isExportMenuOpen)}
              className="bg-white dark:bg-slate-800 text-slate-600 dark:text-slate-300 border border-slate-200 dark:border-slate-700 px-6 py-3 rounded-2xl font-bold hover:bg-slate-50 dark:hover:bg-slate-700 transition-all active:scale-95 flex items-center gap-2 shadow-sm"
            >
              <Download size={20} /> Export
            </button>

            {isExportMenuOpen && (
              <>
                <div className="fixed inset-0 z-40" onClick={() => setIsExportMenuOpen(false)}></div>
                <div className="absolute right-0 mt-2 w-48 bg-white dark:bg-slate-800 rounded-2xl shadow-2xl border border-slate-100 dark:border-slate-700 py-2 z-50 animate-in fade-in slide-in-from-top-2 duration-200">
                  <button 
                    onClick={handleExportCSV}
                    className="w-full flex items-center gap-3 px-4 py-2.5 text-sm font-bold text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700/50 hover:text-[#16A394] transition-colors"
                  >
                    <Download size={18} /> Export as CSV
                  </button>
                  <button 
                    onClick={handleExportPDF}
                    className="w-full flex items-center gap-3 px-4 py-2.5 text-sm font-bold text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700/50 hover:text-[#16A394] transition-colors"
                  >
                    <FileText size={18} /> Export as PDF
                  </button>
                </div>
              </>
            )}
          </div>
          
          <button 
            onClick={() => {
              setEditingId(null);
              setFormData({ name: '', barcode: '', price: '', stockQuantity: '', imageUrl: '', vendorId: user.id });
              setIsModalOpen(true);
            }}
            className="bg-[#16A394] text-white px-6 py-3 rounded-2xl font-bold shadow-lg shadow-[#16A394]/20 hover:bg-[#0D7A6F] transition-all active:scale-95 flex items-center gap-2"
          >
            <Plus size={20} /> Add Product
          </button>
        </div>
      </div>

      {/* Product Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {loading ? (
          Array(8).fill(0).map((_, idx) => (
            <div key={idx} className="bg-white dark:bg-slate-800 p-6 rounded-[2.5rem] shadow-xl border border-slate-100 dark:border-slate-700">
              <Skeleton className="h-40 w-full mb-4 dark:bg-slate-700" />
              <Skeleton className="h-6 w-3/4 mb-3 dark:bg-slate-700" />
              <Skeleton className="h-4 w-1/2 mb-4 dark:bg-slate-700" />
              <div className="flex justify-between items-end pt-3 border-t border-slate-50 dark:border-slate-700/50">
                <Skeleton className="h-10 w-20 dark:bg-slate-700" />
                <Skeleton className="h-10 w-16 dark:bg-slate-700" />
              </div>
            </div>
          ))
        ) : products.length === 0 ? (
          <div className="col-span-full py-20 text-center bg-white dark:bg-slate-800 rounded-[3rem] border border-dashed border-slate-200 dark:border-slate-700">
            <Package className="mx-auto text-slate-200 dark:text-slate-700 mb-4" size={64} />
            <p className="text-slate-400 font-bold italic">No products found in your inventory.</p>
          </div>
        ) : (
          products.map((product) => (
            <div key={product.id} className="bg-white dark:bg-slate-800 p-6 rounded-[2.5rem] shadow-xl border border-slate-100 dark:border-slate-700 group relative overflow-hidden">
             {/* Product Image */}
             <div className="h-40 w-full bg-slate-50 dark:bg-slate-900/50 rounded-3xl mb-4 overflow-hidden flex items-center justify-center text-[#16A394]">
               {product.imageUrl ? (
                 <img src={product.imageUrl} alt={product.name} className="h-full w-full object-cover transition-transform group-hover:scale-110" />
               ) : (
                 <div className="flex flex-col items-center gap-2">
                   <Package size={40} />
                   <span className="text-[10px] font-bold uppercase tracking-widest text-slate-300 dark:text-slate-600">No Image</span>
                 </div>
               )}
             </div>

             <h3 className="font-bold text-slate-800 dark:text-slate-100 text-lg truncate">{product.name}</h3>
             <p className="text-xs text-slate-400 font-bold mb-3 flex items-center gap-1">
               <Barcode size={12} /> {product.barcode || 'N/A'}
             </p>

             <div className="flex justify-between items-end border-t border-slate-50 dark:border-slate-700/50 pt-3">
               <div>
                 <p className="text-[10px] text-slate-400 font-bold uppercase tracking-widest">Price</p>
                 <p className="text-xl font-black text-[#16A394]">₱{(product.price || 0).toFixed(2)}</p>
               </div>
               <div className="flex flex-col items-end">
                 <p className="text-[10px] text-slate-400 font-bold uppercase tracking-widest">Stock</p>
                 <p className={`font-bold ${product.stockQuantity < 5 ? 'text-rose-500' : 'text-slate-700 dark:text-slate-300'}`}>
                   {product.stockQuantity}
                 </p>
               </div>
             </div>
             
             {/* Action Buttons */}
             <div className="absolute top-4 right-4 flex flex-col gap-2 opacity-0 group-hover:opacity-100 transition-all transform translate-x-4 group-hover:translate-x-0">
                <button 
                  onClick={() => openEditModal(product)}
                  className="p-2 bg-white text-teal-600 rounded-xl shadow-lg hover:bg-teal-600 hover:text-white transition-all"
                >
                  <Pencil size={16} />
                </button>
                <button 
                  onClick={() => handleDelete(product.id)}
                  className="p-2 bg-white text-rose-500 rounded-xl shadow-lg hover:bg-rose-500 hover:text-white transition-all"
                >
                  <Trash2 size={16} />
                </button>
             </div>
          </div>
        )))}
      </div>

      {/* Product Modal (Handles both Add & Edit) */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-md">
          <div className="bg-white dark:bg-slate-900 w-full max-w-md rounded-[2.5rem] shadow-2xl p-8 animate-in zoom-in duration-200 border border-transparent dark:border-slate-800">
            <div className="flex justify-between items-center mb-6">
              <div>
                <h2 className="text-xl font-black text-slate-800 dark:text-white">{editingId ? "Edit Product" : "New Product"}</h2>
                <p className="text-xs text-slate-500 dark:text-slate-400 font-medium">Update your product details below.</p>
              </div>
              <button onClick={() => { setIsModalOpen(false); setEditingId(null); }} className="p-2 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-xl transition-colors"><X className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200" /></button>
            </div>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-2">
                <label className="text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase ml-2">Product Image</label>
                
                {/* Image Preview & Upload Area */}
                <div className="relative group">
                  <div className={`w-full h-40 rounded-3xl border-2 border-dashed transition-all flex flex-col items-center justify-center overflow-hidden bg-slate-50 dark:bg-slate-950 ${formData.imageUrl ? 'border-teal-500/30' : 'border-slate-200 dark:border-slate-800 hover:border-teal-400'}`}>
                    {formData.imageUrl ? (
                      <div className="relative w-full h-full group">
                        <img src={formData.imageUrl} alt="Preview" className="w-full h-full object-cover" />
                        <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                          <p className="text-white text-xs font-bold">Change Photo</p>
                        </div>
                      </div>
                    ) : (
                      <div className="flex flex-col items-center gap-2 text-slate-400">
                        {uploading ? <Loader2 className="animate-spin text-teal-500" size={32} /> : <Upload size={32} />}
                        <p className="text-xs font-bold">{uploading ? "Uploading..." : "Click to upload"}</p>
                      </div>
                    )}
                    <input 
                      type="file" 
                      accept="image/*"
                      onChange={handleFileUpload}
                      disabled={uploading}
                      className="absolute inset-0 opacity-0 cursor-pointer disabled:cursor-not-allowed"
                    />
                  </div>
                </div>

                {/* Optional Manual URL Fallback */}
                <div className="relative">
                  <ImageIcon className="absolute left-4 top-3 text-slate-300 dark:text-slate-600" size={16} />
                  <input 
                    type="text" placeholder="Or paste image link..."
                    className="w-full pl-10 pr-4 py-3 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-white rounded-xl outline-none focus:ring-2 focus:ring-[#16A394] text-[10px] font-medium border border-transparent dark:border-slate-800"
                    value={formData.imageUrl} onChange={e => setFormData({...formData, imageUrl: e.target.value})}
                  />
                </div>
              </div>

              <input 
                type="text" placeholder="Product Name" required
                className="w-full p-4 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-white rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394] border border-transparent dark:border-slate-800"
                value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})}
              />
              <div className="relative flex gap-2">
                <input 
                  type="text" placeholder="Barcode"
                  className="flex-1 p-4 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-white rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394] border border-transparent dark:border-slate-800"
                  value={formData.barcode} onChange={e => setFormData({...formData, barcode: e.target.value})}
                />
                <button 
                  type="button"
                  onClick={handleLookup}
                  className="bg-amber-400 text-white px-5 rounded-2xl hover:bg-amber-500 transition-all active:scale-95 flex items-center justify-center shadow-lg shadow-amber-400/20"
                  title="Lookup Name via Barcode"
                >
                  <Wand2 size={20} />
                </button>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <input 
                  type="number" placeholder="Price (₱)" required
                  className="w-full p-4 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-white  rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394] border border-transparent dark:border-slate-800"
                  value={formData.price} onChange={e => setFormData({...formData, price: e.target.value})}
                />
                <input 
                  type="number" placeholder="Stock Qty" required
                  className="w-full p-4 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-white  rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394] border border-transparent dark:border-slate-800"
                  value={formData.stockQuantity} onChange={e => setFormData({...formData, stockQuantity: e.target.value})}
                />
              </div>
              <button type="submit" className="w-full bg-[#16A394] text-white py-4 rounded-3xl font-black shadow-lg shadow-[#16A394]/30 hover:bg-[#0D7A6F] transition-all transform hover:-translate-y-1 active:translate-y-0">
                {editingId ? "Update Product" : "Save Product"}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Inventory;