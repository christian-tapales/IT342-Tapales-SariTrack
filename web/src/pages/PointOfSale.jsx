import { useState, useEffect } from 'react';
import axios from 'axios';
import { Search, ShoppingCart, Plus, Minus, Trash2, CheckCircle, Package } from 'lucide-react';

const PointOfSale = ({ user }) => {
  const [products, setProducts] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [cart, setCart] = useState([]);
  const [loading, setLoading] = useState(true);

  // 1. Fetch real products from the backend (filtered by vendor)
  const fetchProducts = async () => {
    if (!user?.id) return;
    try {
      const response = await axios.get(`http://localhost:8080/api/products?vendorId=${user.id}`);
      setProducts(response.data);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching products:", error);
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user?.id) {
      fetchProducts();
    }
  }, [user]);

  // 2. Logic to send the order to OrderController.java
  const handleCompleteSale = async () => {
    if (cart.length === 0) return;

    // Structure the data to match your Order and OrderItem entities
    const orderData = {
      vendorId: user?.id, // Securely linking the sale to the current vendor
      totalAmount: total,
      items: cart.map(item => ({
        productId: item.id,
        quantity: item.quantity,
        priceAtSale: item.price
      }))
    };

    try {
      const response = await axios.post('http://localhost:8080/api/orders', orderData);
      
      if (response.data.includes("Sale completed")) {
        alert("Transaction Successful!");
        setCart([]); // Clear the cart
        fetchProducts(); // Refresh stock quantities in the UI
      } else {
        alert(response.data); // Shows "Insufficient stock" errors from backend
      }
    } catch (error) {
      const errorMsg = error.response?.data || "Error completing sale. Please check backend.";
      alert(errorMsg);
    }
  };

  const addToCart = (product) => {
    const existingItem = cart.find(item => item.id === product.id);
    if (existingItem) {
      if (existingItem.quantity < product.stockQuantity) {
        setCart(cart.map(item => 
          item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item
        ));
      } else {
        alert(`Sorry, only ${product.stockQuantity} units of ${product.name} available.`);
      }
    } else {
      if (product.stockQuantity > 0) {
        setCart([...cart, { ...product, quantity: 1 }]);
      } else {
        alert("This item is out of stock!");
      }
    }
  };

  const updateQuantity = (id, delta) => {
    setCart(cart.map(item => {
      if (item.id === id) {
        const newQty = item.quantity + delta;
        // Check both min (1) and max (stockQuantity)
        if (newQty > 0 && newQty <= item.stockQuantity) {
          return { ...item, quantity: newQty };
        }
        return item;
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

        <div className="flex-1 overflow-y-auto grid grid-cols-2 md:grid-cols-3 gap-4 pr-2 custom-scrollbar content-start">
          {loading ? (
             <div className="col-span-full text-center py-10 text-slate-400 font-bold">Loading Inventory...</div>
          ) : filteredProducts.length === 0 ? (
             <div className="col-span-full text-center py-10 text-slate-400 font-bold italic">No products found.</div>
          ) : filteredProducts.map((product) => (
            <div 
              key={product.id} 
              onClick={() => addToCart(product)}
              className="bg-white p-4 rounded-3xl border border-slate-100 shadow-sm hover:shadow-xl hover:border-teal-500/30 transition-all cursor-pointer group active:scale-95 overflow-hidden h-fit"
            >
              <div className="h-24 w-full bg-slate-50 rounded-2xl mb-3 flex items-center justify-center text-teal-600 overflow-hidden">
                {product.imageUrl ? (
                  <img src={product.imageUrl} alt={product.name} className="h-full w-full object-cover transition-transform group-hover:scale-110" />
                ) : (
                  <Package size={32} className="opacity-20" />
                )}
              </div>
              <h3 className="font-bold text-slate-800 text-sm truncate">{product.name}</h3>
              <div className="flex justify-between items-center mt-2">
                <p className="text-teal-600 font-black text-sm">₱{(product.price || 0).toFixed(2)}</p>
                <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${product.stockQuantity < 5 ? 'bg-rose-50 text-rose-500' : 'bg-slate-50 text-slate-400'}`}>
                  qty: {product.stockQuantity}
                </span>
              </div>
            </div>
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
                  <button 
                    onClick={() => updateQuantity(item.id, 1)} 
                    disabled={item.quantity >= item.stockQuantity}
                    className={`transition-colors ${item.quantity >= item.stockQuantity ? 'text-slate-200 cursor-not-allowed' : 'text-slate-400 hover:text-[#16A394]'}`}
                  >
                    <Plus size={14}/>
                  </button>
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
            onClick={handleCompleteSale}
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