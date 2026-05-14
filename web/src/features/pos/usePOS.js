import { useState, useEffect } from 'react';
import api from '../../core/api/api';
import toast from 'react-hot-toast';

export const usePOS = (user) => {
  const [products, setProducts] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [cart, setCart] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const [rates, setRates] = useState({ PHP: 1.0, USD: 0.018, EUR: 0.016, JPY: 2.65 });
  const [selectedCurrency, setSelectedCurrency] = useState('PHP');

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

  const addToCart = (product) => {
    const existingItem = cart.find(item => item.id === product.id);
    if (existingItem) {
      if (existingItem.quantity < product.stockQuantity) {
        setCart(cart.map(item => item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item));
      } else {
        toast.error(`Only ${product.stockQuantity} units available.`);
      }
    } else if (product.stockQuantity > 0) {
      setCart([...cart, { ...product, quantity: 1 }]);
    } else {
      toast.error("Out of stock!");
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
          toast.error(`Only ${item.stockQuantity} units available.`);
          return { ...item, quantity: item.stockQuantity };
        }
        return { ...item, quantity: val };
      }
      return item;
    }));
  };

  const removeFromCart = (id) => setCart(cart.filter(i => i.id !== id));

  const resetSale = () => {
    setCart([]);
    fetchData();
  };

  const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);

  return {
    products, customers, cart, loading, 
    rates, selectedCurrency, setSelectedCurrency,
    addToCart, updateQuantity, handleQuantityInput, removeFromCart,
    resetSale, total, fetchData
  };
};
