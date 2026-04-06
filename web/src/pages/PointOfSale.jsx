import { useState } from 'react';
// Added 'Package' to the import list below
import { Search, ShoppingCart, Plus, Minus, Trash2, CheckCircle, Package } from 'lucide-react';

const PointOfSale = () => {
  const [products] = useState([
    { id: 1, name: 'Coke 1.5L', price: 75.00, stock: 12, category: 'Beverages' },
    { id: 2, name: 'Lucky Me! Canton', price: 15.00, stock: 45, category: 'Noodles' },
    { id: 3, name: 'Red Horse 500ml', price: 120.00, stock: 24, category: 'Beverages' },
    { id: 4, name: 'Egg (Medium)', price: 8.00, stock: 30, category: 'Fresh' },
    { id: 5, name: 'Magic Sarap 8g', price: 5.00, stock: 100, category: 'Seasoning' },
  ]);

  const [searchTerm, setSearchTerm] = useState('');
  const [cart, setCart] = useState([]);

  const addToCart = (product) => {
    const existingItem = cart.find(item => item.id === product.id);
    if (existingItem) {
      setCart(cart.map(item => 
        item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item
      ));
    } else {
      setCart([...cart, { ...product, quantity: 1 }]);
    }
  };

  const updateQuantity = (id, delta) => {
    setCart(cart.map(item => {
      if (item.id === id) {
        const newQty = item.quantity + delta;
        return newQty > 0 ? { ...item, quantity: newQty } : item;
      }
      return item;
    }));
  };

  const removeFromCart = (id) => {
    setCart(cart.filter(item => item.id !== id));
  };

  const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);

  const filteredProducts = products.filter(p => 
    p.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="max-w-7xl mx-auto h-[calc(100vh-120px)] flex flex-col lg:flex-row gap-6 pb-6 animate-in fade-in duration-500">
      
      {/* Left Side: Product Selection */}
      <div className="lg:w-2/3 flex flex-col gap-6">
        <div className="bg-white p-6 rounded-[2rem] shadow-xl border border-slate-100">
          <div className="relative group">
            <Search className="absolute left-4 top-3.5 text-slate-400 group-focus-within:text-[#16A394] transition-colors" size={20} />
            <input 
              type="text" 
              placeholder="Search products to add..." 
              className="w-full pl-12 pr-4 py-3 bg-slate-50 border-none rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394] transition-all"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>

        <div className="flex-1 overflow-y-auto grid grid-cols-2 md:grid-cols-3 gap-4 pr-2 custom-scrollbar">
          {filteredProducts.map((product) => (
            <button 
                key={product.id}
                onClick={() => addToCart(product)}
                className="bg-white p-5 rounded-[2rem] ... h-40"
            >
                <div>
                {/* CATEGORY SPAN REMOVED */}
                <p className="font-bold text-slate-800 mt-2 line-clamp-2">{product.name}</p>
                </div>
                <div className="flex justify-between items-end">
                <p className="text-xl font-black text-slate-800">₱{product.price.toFixed(2)}</p>
                <p className="text-[10px] text-slate-400 font-medium">Stock: {product.stockQuantity}</p>
                </div>
            </button>
          ))}
        </div>
      </div>

      {/* Right Side: Cart Summary */}
      <div className="lg:w-1/3 bg-white rounded-[2.5rem] shadow-2xl border border-slate-100 flex flex-col overflow-hidden">
        <div className="p-8 border-b border-slate-50">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-[#E8F6F5] rounded-xl text-[#16A394]">
              <ShoppingCart size={24} />
            </div>
            <h2 className="text-xl font-black text-slate-800">New Transaction</h2>
          </div>
        </div>

        <div className="flex-1 overflow-y-auto p-6 space-y-4">
          {cart.length === 0 ? (
            <div className="h-full flex flex-col items-center justify-center text-slate-400 space-y-2 opacity-60">
              <Package size={48} />
              <p className="font-medium">Cart is currently empty</p>
            </div>
          ) : (
            cart.map((item) => (
              <div key={item.id} className="flex items-center justify-between group">
                <div className="flex-1">
                  <p className="font-bold text-slate-800 text-sm">{item.name}</p>
                  <p className="text-xs text-[#16A394] font-bold">₱{(item.price * item.quantity).toFixed(2)}</p>
                </div>
                
                <div className="flex items-center gap-3 bg-slate-50 px-3 py-1.5 rounded-xl">
                  <button onClick={() => updateQuantity(item.id, -1)} className="text-slate-400 hover:text-[#16A394]"><Minus size={14}/></button>
                  <span className="text-sm font-black text-slate-700 w-4 text-center">{item.quantity}</span>
                  <button onClick={() => updateQuantity(item.id, 1)} className="text-slate-400 hover:text-[#16A394]"><Plus size={14}/></button>
                </div>

                <button onClick={() => removeFromCart(item.id)} className="ml-3 p-2 text-slate-300 hover:text-rose-500 transition-colors">
                  <Trash2 size={18} />
                </button>
              </div>
            ))
          )}
        </div>

        <div className="p-8 bg-slate-50/50 border-t border-slate-100 space-y-6">
          <div className="flex justify-between items-center">
            <span className="text-xl font-black text-slate-800">Total Bill</span>
            <span className="text-3xl font-black text-[#16A394]">₱{total.toFixed(2)}</span>
          </div>
          
          <button 
            disabled={cart.length === 0}
            className="w-full bg-[#16A394] hover:bg-[#0D7A6F] disabled:bg-slate-200 text-white py-4 rounded-2xl font-black shadow-lg shadow-[#16A394]/20 transition-all active:scale-95 flex items-center justify-center gap-2"
          >
            <CheckCircle size={20} />
            Complete Sale
          </button>
        </div>
      </div>
    </div>
  );
};

export default PointOfSale;