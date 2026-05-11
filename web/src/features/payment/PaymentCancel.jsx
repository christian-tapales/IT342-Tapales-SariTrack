import { XCircle, ArrowLeft } from 'lucide-react';
import { Link } from 'react-router-dom';

const PaymentCancel = () => {
  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-6">
      <div className="max-w-md w-full bg-white rounded-[3rem] shadow-2xl p-10 text-center">
        <div className="flex justify-center mb-6">
          <div className="p-4 bg-rose-50 rounded-full text-rose-500">
            <XCircle size={64} />
          </div>
        </div>
        <h1 className="text-3xl font-black text-slate-800 mb-2">Payment Cancelled</h1>
        <p className="text-slate-500 mb-8">The transaction was not completed. No funds were deducted.</p>
        
        <Link 
          to="/sales" 
          className="inline-flex items-center gap-2 bg-slate-800 hover:bg-slate-900 text-white px-8 py-4 rounded-2xl font-bold transition-all active:scale-95"
        >
          <ArrowLeft size={20} />
          Return to Cart
        </Link>
      </div>
    </div>
  );
};

export default PaymentCancel;
