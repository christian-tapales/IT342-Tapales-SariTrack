import React from 'react';

const Input = ({ icon: Icon, type, placeholder, value, onChange, showPasswordButton, onTogglePassword }) => {
  return (
    <div className="relative">
      {Icon && <Icon className="absolute left-4 top-3.5 w-5 h-5 text-slate-400" />}
      <input 
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        // ALIGNED: focus:ring-[#16A394]
        className="w-full pl-12 pr-12 py-3.5 bg-white/50 border border-slate-200 rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394] focus:bg-white transition-all text-slate-900 placeholder:text-slate-400"
        required 
      />
      {showPasswordButton && (
        <button type="button" onClick={onTogglePassword} className="absolute right-4 top-3.5 text-slate-400 hover:text-[#16A394]">
          {showPasswordButton}
        </button>
      )}
    </div>
  );
};

export default Input;