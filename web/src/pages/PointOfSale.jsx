import { useState, useEffect } from 'react';
import api from '../api';
import { Search, ShoppingCart, Plus, Minus, Trash2, CheckCircle, Package, BookOpen, X, UserPlus } from 'lucide-react';

const PointOfSale = ({ user }) => {
  const [products, setProducts] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [cart, setCart] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showUtangModal, setShowUtangModal] = useState(false);
  const [showCashModal, setShowCashModal] = useState(false);
  const [selectedCustomerId, setSelectedCustomerId] = useState('');
  const [customerSearch, setCustomerSearch] = useState('');
  
  // Currency State
  const [rates, setRates] = useState({ PHP: 1.0, USD: 0.018, EUR: 0.016, JPY: 2.65 });
  const [selectedCurrency, setSelectedCurrency] = useState('PHP');
  const [showCurrencyDropdown, setShowCurrencyDropdown] = useState(false);

  // 1. Fetch real products and customers from the backend
  const fetchData = async () => {
    if (!user?.id) return;
    try {
      const [prodRes, custRes, rateRes] = await Promise.all([
        api.get(`/products?vendorId=${user.id}`),
        api.get(`/customers?vendorId=${user.id}`),
        api.get(`/currency/rates`)
      ]);
      setProducts(prodRes.data);
      setCustomers(custRes.data);
      if (rateRes.data) setRates(rateRes.data);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching data:", error);
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user?.id) fetchData();
  }, [user]);

  // 2. Complete Sale (CASH)
  const handleCompleteSale = async () => {
    if (cart.length === 0) return;
    const orderData = {
      vendorId: user?.id,
      customerId: selectedCustomerId || null, // Optional for cash
      totalAmount: total,
      status: 'PAID',
      items: cart.map(item => ({
        productId: item.id,
        quantity: item.quantity,
        priceAtSale: item.price
      }))
    };
    try {
      const response = await api.post('/orders', orderData);
      if (response.data.id) {
        alert(selectedCustomerId ? "Sale Complete & Receipt Sent!" : "Sale Completed Successfully!");
        setShowCashModal(false);
        resetSale();
      }
    } catch (error) {
      alert(error.response?.data || "Error completing sale.");
    }
  };

  // 3. Record as UTANG
  const handleUtangSale = async () => {
    if (!selectedCustomerId) {
      alert("Please select a customer first.");
      return;
    }
    const orderData = {
      vendorId: user?.id,
      customerId: selectedCustomerId,
      totalAmount: total,
      status: 'DEBT',
      items: cart.map(item => ({
        productId: item.id,
        quantity: item.quantity,
        priceAtSale: item.price
      }))
    };
    try {
      const response = await api.post('/orders', orderData);
      if (response.data.id) {
        alert("Utang Recorded Successfully!");
        setShowUtangModal(false);
        resetSale();
      }
    } catch (error) {
      alert(error.response?.data || "Error recording debt.");
    }
  };

  // 4. PayMongo Digital Payment
  const handleDigitalPayment = async () => {
    if (cart.length === 0) return;
    const orderData = {
      vendorId: user?.id,
      totalAmount: total,
      status: 'PENDING',
      items: cart.map(item => ({
        productId: item.id,
        quantity: item.quantity,
        priceAtSale: item.price
      }))
    };
    try {
      const orderResponse = await api.post('/orders', orderData);
      const savedOrder = orderResponse.data;
      const paymentResponse = await api.post('/payments/create-session', {
        amount: total,
        orderId: savedOrder.id,
        description: `Order #${savedOrder.id} from SariTrack`
      });
      if (paymentResponse.data.checkout_url) {
        window.location.href = paymentResponse.data.checkout_url;
      }
    } catch (error) {
      alert(error.response?.data || "Error initiating payment.");
    }
  };

  const resetSale = () => {
    setCart([]);
    setSelectedCustomerId('');
    setCustomerSearch('');
    setShowUtangModal(false);
    setShowCashModal(false);
    fetchData();
  };

  const addToCart = (product) => {
    const existingItem = cart.find(item => item.id === product.id);
    if (existingItem) {
      if (existingItem.quantity < product.stockQuantity) {
        setCart(cart.map(item => item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item));
      } else {
        alert(`Only ${product.stockQuantity} units available.`);
      }
    } else if (product.stockQuantity > 0) {
      setCart([...cart, { ...product, quantity: 1 }]);
    } else {
      alert("Out of stock!");
    }
  };

  const updateQuantity = (id, delta) => {
    setCart(cart.map(item => {
      if (item.id === id) {
        const newQty = item.quantity + delta;
        return (newQty > 0 && newQty <= item.stockQuantity) ? { ...item, quantity: newQty } : item;
      }
      return item;
    }));
  };

  const handleQuantityInput = (id, value) => {
    if (value === "") {
      setCart(cart.map(item => item.id === id ? { ...item, quantity: "" } : item));
      return;
    }
    const val = parseInt(value);
    if (isNaN(val) || val < 0) return;
    
    setCart(cart.map(item => {
      if (item.id === id) {
        if (val > item.stockQuantity) {
          alert(`Only ${item.stockQuantity} units available.`);
          return { ...item, quantity: item.stockQuantity };
        }
        return { ...item, quantity: val };
      }
      return item;
    }));
  };

  const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  const convertedTotal = total * (rates[selectedCurrency] || 1);
  const currencySymbols = { PHP: '₱', USD: '$', EUR: '€', JPY: '¥' };
  
  const filteredProducts = products.filter(p => p.name.toLowerCase().includes(searchTerm.toLowerCase()));
  const filteredCustomers = customers.filter(c => c.fullName.toLowerCase().includes(customerSearch.toLowerCase()));

  return (
    <div className="max-w-7xl mx-auto h-[calc(100vh-120px)] flex flex-col lg:flex-row gap-6 pb-6 animate-in fade-in duration-500">
      
      {/* Product List Section */}
      <div className="lg:w-2/3 flex flex-col gap-6">
        <div className="bg-white dark:bg-slate-900 p-6 rounded-[2rem] shadow-xl border border-slate-100 dark:border-slate-800 transition-colors">
          <div className="relative group">
            <Search className="absolute left-4 top-3.5 text-slate-400 dark:text-slate-500 group-focus-within:text-[#16A394]" size={20} />
            <input 
              type="text" placeholder="Search products..." 
              className="w-full pl-12 pr-4 py-3 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-white border-none rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394] transition-all"
              value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>

        <div className="flex-1 overflow-y-auto grid grid-cols-2 md:grid-cols-3 gap-4 pr-2 custom-scrollbar content-start">
          {loading ? (
             Array(9).fill(0).map((_, idx) => (
               <div key={idx} className="bg-white dark:bg-slate-900 p-4 rounded-3xl border border-slate-100 dark:border-slate-800 shadow-sm">
                 <Skeleton className="h-24 w-full mb-3 dark:bg-slate-800" />
                 <Skeleton className="h-4 w-3/4 mb-2 dark:bg-slate-800" />
                 <div className="flex justify-between items-center mt-2">
                   <Skeleton className="h-5 w-16 dark:bg-slate-800" />
                   <Skeleton className="h-4 w-12 rounded-full dark:bg-slate-800" />
                 </div>
               </div>
             ))
          ) : filteredProducts.length === 0 ? (
             <div className="col-span-full py-20 text-center bg-white dark:bg-slate-900 rounded-[3rem] border border-dashed border-slate-200 dark:border-slate-800">
               <Package className="mx-auto text-slate-200 dark:text-slate-700 mb-4" size={64} />
               <p className="text-slate-400 font-bold italic">No matching products found.</p>
             </div>
          ) : filteredProducts.map((product) => (
            <div key={product.id} onClick={() => addToCart(product)} className="bg-white dark:bg-slate-900 p-4 rounded-3xl border border-slate-100 dark:border-slate-800 shadow-sm hover:shadow-xl hover:border-teal-500/30 dark:hover:border-teal-500/30 transition-all cursor-pointer group active:scale-95 h-fit">
              <div className="h-24 w-full bg-slate-50 dark:bg-slate-950 rounded-2xl mb-3 flex items-center justify-center text-teal-600 overflow-hidden">
                {product.imageUrl ? <img src={product.imageUrl} className="h-full w-full object-cover transition-transform group-hover:scale-110" /> : <Package size={32} className="opacity-20" />}
              </div>
              <h3 className="font-bold text-slate-800 dark:text-slate-100 text-sm truncate">{product.name}</h3>
              <div className="flex justify-between items-center mt-2">
                <p className="text-teal-600 dark:text-teal-400 font-black text-sm">₱{(product.price || 0).toFixed(2)}</p>
                <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${product.stockQuantity < 5 ? 'bg-rose-50 dark:bg-rose-900/20 text-rose-500' : 'bg-slate-50 dark:bg-slate-950 text-slate-400 dark:text-slate-500'}`}>qty: {product.stockQuantity}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Cart Section */}
      <div className="lg:w-1/3 bg-white dark:bg-slate-900 rounded-[2.5rem] shadow-2xl border border-slate-100 dark:border-slate-800 flex flex-col overflow-hidden transition-colors">
        <div className="p-8 border-b border-slate-50 dark:border-slate-800 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-[#E8F6F5] dark:bg-teal-900/20 rounded-xl text-[#16A394]"><ShoppingCart size={24} /></div>
            <h2 className="text-xl font-black text-slate-800 dark:text-white">New Transaction</h2>
          </div>

          {/* Currency Switcher */}
          <div className="relative">
            <button 
              onClick={() => setShowCurrencyDropdown(!showCurrencyDropdown)}
              className="flex items-center gap-2 px-3 py-1.5 bg-slate-50 dark:bg-slate-800 border border-slate-100 dark:border-slate-700 rounded-xl text-xs font-black text-slate-600 dark:text-slate-300 hover:border-teal-500 transition-all"
            >
              <Package size={14} className="text-teal-600" />
              {selectedCurrency}
            </button>
            
            {showCurrencyDropdown && (
              <div className="absolute right-0 mt-2 w-32 bg-white dark:bg-slate-800 rounded-2xl shadow-2xl border border-slate-50 dark:border-slate-700 z-50 overflow-hidden animate-in fade-in zoom-in-95 duration-200">
                {Object.keys(rates).map(curr => (
                  <button 
                    key={curr}
                    onClick={() => { setSelectedCurrency(curr); setShowCurrencyDropdown(false); }}
                    className={`w-full px-4 py-2 text-left text-xs font-bold hover:bg-teal-50 dark:hover:bg-teal-900/20 transition-colors ${selectedCurrency === curr ? 'text-teal-600 bg-teal-50/50 dark:bg-teal-900/40' : 'text-slate-600 dark:text-slate-300'}`}
                  >
                    {curr} ({currencySymbols[curr]})
                  </button>
                ))}
              </div>
            )}
          </div>
        </div>

        <div className="flex-1 overflow-y-auto p-6 space-y-4">
          {cart.length === 0 ? (
            <div className="h-full flex flex-col items-center justify-center text-slate-400 dark:text-slate-600 space-y-2 opacity-60">
              <Package size={48} /><p className="font-medium">Cart is empty</p>
            </div>
          ) : cart.map((item) => (
            <div key={item.id} className="flex items-center justify-between group animate-in slide-in-from-right-4">
              <div className="flex-1">
                <p className="font-bold text-slate-800 dark:text-slate-200 text-sm">{item.name}</p>
                <p className="text-xs text-[#16A394] font-bold">₱{(item.price * item.quantity).toFixed(2)}</p>
              </div>
              <div className="flex items-center gap-3 bg-slate-50 dark:bg-slate-800 px-3 py-1.5 rounded-xl border border-slate-100 dark:border-slate-700 group-focus-within:border-teal-500 transition-all">
                <button onClick={() => updateQuantity(item.id, -1)} className="text-slate-400 hover:text-[#16A394] transition-colors"><Minus size={14}/></button>
                <input 
                  type="number" 
                  value={item.quantity}
                  onChange={(e) => handleQuantityInput(item.id, e.target.value)}
                  className="text-sm font-black text-slate-700 dark:text-slate-200 w-10 text-center bg-transparent border-none outline-none [appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none"
                />
                <button onClick={() => updateQuantity(item.id, 1)} disabled={item.quantity >= item.stockQuantity} className="text-slate-400 hover:text-[#16A394] transition-colors disabled:opacity-10"><Plus size={14}/></button>
              </div>
              <button onClick={() => setCart(cart.filter(i => i.id !== item.id))} className="ml-3 p-2 text-slate-300 dark:text-slate-600 hover:text-rose-500"><Trash2 size={18} /></button>
            </div>
          ))}
        </div>

        <div className="p-8 bg-slate-50/50 dark:bg-slate-800/50 border-t border-slate-100 dark:border-slate-800 space-y-4">
          <div className="flex flex-col mb-2">
            <div className="flex justify-between items-center">
              <span className="text-sm font-bold text-slate-400">Total Bill</span>
              <span className="text-3xl font-black text-[#16A394]">₱{total.toFixed(2)}</span>
            </div>
          </div>
          
          <button onClick={() => setShowCashModal(true)} disabled={cart.length === 0} className="w-full bg-[#16A394] hover:bg-[#0D7A6F] disabled:bg-slate-200 dark:disabled:bg-slate-800 text-white py-4 rounded-2xl font-black shadow-lg shadow-[#16A394]/10 transition-all active:scale-95 flex items-center justify-center gap-2">
            <CheckCircle size={20} /> Complete Sale (Cash)
          </button>

          <button onClick={() => setShowUtangModal(true)} disabled={cart.length === 0} className="w-full bg-amber-400 hover:bg-amber-500 disabled:bg-slate-200 dark:disabled:bg-slate-800 text-white py-4 rounded-2xl font-black shadow-lg transition-all active:scale-95 flex items-center justify-center gap-2">
            <BookOpen size={20} /> Record as Listahan (Utang)
          </button>

          <button onClick={handleDigitalPayment} disabled={cart.length === 0} className="w-full bg-slate-800 hover:bg-slate-900 disabled:bg-slate-200 text-white py-3 rounded-2xl font-bold transition-all active:scale-95 flex items-center justify-center gap-2 text-sm">
            <ShoppingCart size={18} /> Pay via PayMongo
          </button>
        </div>
      </div>

      {/* Cash Checkout Modal (Optional Receipt) */}
      {showCashModal && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-md z-50 flex items-center justify-center p-4 animate-in fade-in duration-300">
          <div className="bg-white dark:bg-slate-900 w-full max-w-md rounded-[3rem] shadow-2xl overflow-hidden animate-in zoom-in-95 duration-300 border border-transparent dark:border-slate-800">
            <div className="p-8 bg-[#16A394] text-white flex justify-between items-center">
              <div>
                <h2 className="text-2xl font-black italic">Checkout</h2>
                <p className="text-white/80 text-xs font-bold uppercase tracking-wider">Total Amount: ₱{total.toFixed(2)}</p>
              </div>
              <button onClick={() => { setShowCashModal(false); setSelectedCustomerId(''); }} className="p-2 bg-white/20 rounded-xl hover:bg-white/30"><X size={20} /></button>
            </div>
            
            <div className="p-8 space-y-4">
              <p className="text-sm text-slate-500 dark:text-slate-400 font-medium">Select a customer to send a digital receipt (Optional):</p>
              
              <div className="relative">
                <Search className="absolute left-4 top-3.5 text-slate-400 dark:text-slate-500" size={18} />
                <input 
                  type="text" placeholder="Search customer name..." 
                  className="w-full pl-12 pr-4 py-3 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-white border-none rounded-2xl outline-none focus:ring-2 focus:ring-teal-500 border border-transparent dark:border-slate-800"
                  value={customerSearch} onChange={(e) => setCustomerSearch(e.target.value)}
                />
              </div>

              <div className="max-h-48 overflow-y-auto space-y-2 pr-2 custom-scrollbar">
                {filteredCustomers.length === 0 ? (
                  <div className="text-center py-4 text-slate-400 dark:text-slate-600 italic text-sm">No customers found.</div>
                ) : filteredCustomers.map(customer => (
                  <button 
                    key={customer.id} 
                    onClick={() => setSelectedCustomerId(customer.id === selectedCustomerId ? '' : customer.id)}
                    className={`w-full p-4 rounded-2xl flex items-center justify-between transition-all border-2 ${selectedCustomerId === customer.id ? 'border-teal-500 bg-teal-50 dark:bg-teal-900/20 shadow-md' : 'border-transparent bg-slate-50 dark:bg-slate-800 hover:bg-slate-100 dark:hover:bg-slate-700'}`}
                  >
                    <span className="font-bold text-slate-700 dark:text-slate-200">{customer.fullName}</span>
                    {selectedCustomerId === customer.id ? <CheckCircle size={18} className="text-teal-500" /> : <span className="text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase">Select</span>}
                  </button>
                ))}
              </div>

              <div className="grid grid-cols-1 gap-3 pt-4">
                <button onClick={handleCompleteSale} className={`py-4 rounded-2xl font-black transition-all active:scale-95 shadow-xl ${selectedCustomerId ? 'bg-teal-600 text-white' : 'bg-slate-800 dark:bg-slate-700 text-white'}`}>
                  {selectedCustomerId ? "Complete & Send Receipt" : "Complete Anonymous Sale"}
                </button>
                <button onClick={() => { setShowCashModal(false); setSelectedCustomerId(''); }} className="py-3 text-slate-400 dark:text-slate-500 font-bold text-sm">
                  Go Back
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Utang Modal */}
      {showUtangModal && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-md z-50 flex items-center justify-center p-4 animate-in fade-in duration-300">
          <div className="bg-white dark:bg-slate-900 w-full max-w-md rounded-[3rem] shadow-2xl overflow-hidden animate-in zoom-in-95 duration-300 border border-transparent dark:border-slate-800">
            <div className="p-8 bg-amber-400 text-white flex justify-between items-center">
              <div>
                <h2 className="text-2xl font-black italic">Listahan Selection</h2>
                <p className="text-white/80 text-xs font-bold uppercase tracking-wider">Select borrower for ₱{total.toFixed(2)}</p>
              </div>
              <button onClick={() => { setShowUtangModal(false); setSelectedCustomerId(''); }} className="p-2 bg-white/20 rounded-xl hover:bg-white/30"><X size={20} /></button>
            </div>
            
            <div className="p-8 space-y-4">
              <div className="relative">
                <Search className="absolute left-4 top-3.5 text-slate-400 dark:text-slate-500" size={18} />
                <input 
                  type="text" placeholder="Search customer name..." 
                  className="w-full pl-12 pr-4 py-3 bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-white border-none rounded-2xl outline-none focus:ring-2 focus:ring-amber-400 border border-transparent dark:border-slate-800"
                  value={customerSearch} onChange={(e) => setCustomerSearch(e.target.value)}
                />
              </div>

              <div className="max-h-60 overflow-y-auto space-y-2 pr-2 custom-scrollbar">
                {filteredCustomers.length === 0 ? (
                  <div className="text-center py-6 text-slate-400 dark:text-slate-600 italic text-sm">No customers found.</div>
                ) : filteredCustomers.map(customer => (
                  <button 
                    key={customer.id} 
                    onClick={() => setSelectedCustomerId(customer.id)}
                    className={`w-full p-4 rounded-2xl flex items-center justify-between transition-all border-2 ${selectedCustomerId === customer.id ? 'border-amber-400 bg-amber-50 dark:bg-amber-900/20 shadow-md' : 'border-transparent bg-slate-50 dark:bg-slate-800 hover:bg-slate-100 dark:hover:bg-slate-700'}`}
                  >
                    <span className="font-bold text-slate-700 dark:text-slate-200">{customer.fullName}</span>
                    <span className="text-xs font-black text-amber-500 uppercase">Select</span>
                  </button>
                ))}
              </div>

              <button onClick={handleUtangSale} disabled={!selectedCustomerId} className="w-full py-4 bg-amber-400 hover:bg-amber-500 disabled:bg-slate-100 dark:disabled:bg-slate-800 text-white rounded-2xl font-black shadow-xl transition-all active:scale-95 mt-4">
                Confirm Listahan Entry
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PointOfSale;