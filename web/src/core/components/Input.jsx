import React from 'react';

const Input = ({ icon: Icon, type, placeholder, value, onChange, showPasswordButton, onTogglePassword }) => {
  return (
    <div className="relative group transition-all">
      {Icon && <Icon className="absolute left-4 top-3.5 w-5 h-5 text-slate-400 group-focus-within:text-teal-500 transition-colors" />}
      <input 
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        // ALIGNED: focus:ring-[#16A394]
        className="w-full pl-12 pr-12 py-3.5 bg-slate-50/50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-800 rounded-2xl outline-none focus:ring-2 focus:ring-[#16A394] focus:bg-white dark:focus:bg-slate-900 transition-all text-slate-900 dark:text-slate-100 placeholder:text-slate-400 dark:placeholder:text-slate-600"
        required 
      />
      {showPasswordButton && (
        <button type="button" onClick={onTogglePassword} className="absolute right-4 top-3.5 text-slate-400 hover:text-[#16A394] transition-colors">
          {showPasswordButton}
        </button>
      )}
    </div>
  );
};

export default Input;