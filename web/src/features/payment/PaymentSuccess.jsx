import { CheckCircle, Home } from 'lucide-react';
import { Link } from 'react-router-dom';

const PaymentSuccess = () => {
  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-6">
      <div className="max-w-md w-full bg-white rounded-[3rem] shadow-2xl p-10 text-center animate-in zoom-in duration-500">
        <div className="flex justify-center mb-6">
          <div className="p-4 bg-teal-50 rounded-full text-teal-600 animate-bounce">
            <CheckCircle size={64} />
          </div>
        </div>
        <h1 className="text-3xl font-black text-slate-800 mb-2">Payment Received!</h1>
        <p className="text-slate-500 mb-8">The transaction was successful and the inventory has been updated.</p>
        
        <Link 
          to="/sales" 
          className="inline-flex items-center gap-2 bg-teal-600 hover:bg-teal-700 text-white px-8 py-4 rounded-2xl font-bold transition-all active:scale-95"
        >
          <Home size={20} />
          Back to POS
        </Link>
      </div>
    </div>
  );
};

export default PaymentSuccess;
