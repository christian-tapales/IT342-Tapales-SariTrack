import React from 'react';

const Input = ({ icon: Icon, type, placeholder, value, onChange, showPasswordButton, onTogglePassword }) => {
  return (
    <div className="relative">
      {/* Icon is passed as a component prop from Lucide */}
      {Icon && <Icon className="absolute left-4 top-3.5 w-5 h-5 text-slate-400" />}
      
      <input 
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        // text-slate-900 sets the typing color; placeholder:text-slate-400 sets the hint color
        className="w-full pl-12 pr-12 py-3.5 bg-white/50 border border-slate-200 rounded-2xl outline-none focus:ring-2 focus:ring-emerald-500 focus:bg-white transition-all text-slate-900 placeholder:text-slate-400"
        required 
      />

      {/* Optional eye icon for password fields */}
      {showPasswordButton && (
        <button 
          type="button"
          onClick={onTogglePassword}
          className="absolute right-4 top-3.5 text-slate-400 hover:text-emerald-600"
        >
          {showPasswordButton}
        </button>
      )}
    </div>
  );
};

export default Input;