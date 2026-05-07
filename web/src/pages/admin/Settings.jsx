import React from 'react';
import { 
  Settings as SettingsIcon, 
  Shield, 
  Globe, 
  CreditCard, 
  Bell, 
  Cloud,
  ChevronRight,
  Save
} from 'lucide-react';

const Settings = () => {
  const sections = [
    {
      title: 'Platform Branding',
      icon: Globe,
      description: 'Manage how SariTrack appears to vendors and customers.',
      fields: [
        { label: 'Platform Name', value: 'SariTrack', type: 'text' },
        { label: 'Support Email', value: 'admin@saritrack.com', type: 'email' },
      ]
    },
    {
      title: 'API & Integrations',
      icon: Cloud,
      description: 'Configure third-party payment and auth providers.',
      fields: [
        { label: 'PayMongo Secret Key', value: 'sk_test_••••••••••••', type: 'password' },
        { label: 'Google Client ID', value: '98472-••••••••••••.google.com', type: 'password' },
      ]
    },
    {
      title: 'Security Policy',
      icon: Shield,
      description: 'System-wide security and access rules.',
      fields: [
        { label: 'Session Timeout', value: '24 Hours', type: 'select', options: ['1 Hour', '12 Hours', '24 Hours', 'Unlimited'] },
        { label: 'Two-Factor Auth', value: 'Required for Admin', type: 'toggle' },
      ]
    }
  ];

  return (
    <div className="max-w-4xl mx-auto space-y-8 animate-in fade-in duration-500 text-slate-800 dark:text-slate-200 pb-10 transition-colors">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-black text-slate-800 dark:text-white tracking-tight">System Settings</h1>
          <p className="text-slate-500 dark:text-slate-400 mt-1">Configure global platform behavior and security protocols.</p>
        </div>
        <button className="px-6 py-2.5 bg-teal-600 text-white rounded-xl font-bold shadow-lg shadow-teal-600/20 hover:bg-teal-700 transition-all active:scale-95 flex items-center gap-2">
          <Save size={18} />
          Save Changes
        </button>
      </div>

      <div className="grid grid-cols-1 gap-6">
        {sections.map((section) => (
          <div key={section.title} className="bg-white dark:bg-slate-900/50 backdrop-blur-md rounded-[2rem] border border-slate-100 dark:border-white/5 overflow-hidden shadow-xl transition-colors">
            <div className="p-8 border-b border-slate-50 dark:border-white/5 flex items-center gap-4 bg-slate-50/50 dark:bg-white/5">
              <div className="p-3 rounded-2xl bg-teal-500/10 border border-teal-500/20 text-teal-600 dark:text-teal-400">
                <section.icon size={24} />
              </div>
              <div>
                <h3 className="text-xl font-bold text-slate-800 dark:text-white">{section.title}</h3>
                <p className="text-sm text-slate-500 dark:text-slate-400">{section.description}</p>
              </div>
            </div>
            
            <div className="p-8 space-y-6">
              {section.fields.map((field) => (
                <div key={field.label} className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                  <div className="space-y-1">
                    <p className="text-sm font-bold text-slate-700 dark:text-slate-300">{field.label}</p>
                    <p className="text-[10px] text-slate-400 dark:text-slate-500 uppercase font-bold tracking-widest">Configuration Key</p>
                  </div>
                  <div className="w-full md:w-2/3">
                    {field.type === 'toggle' ? (
                      <div className="w-12 h-6 bg-slate-200 dark:bg-teal-600/20 border border-slate-300 dark:border-teal-600/30 rounded-full relative cursor-pointer">
                        <div className="absolute right-1 top-1 h-4 w-4 bg-white dark:bg-teal-400 rounded-full shadow-md dark:shadow-[0_0_10px_rgba(45,212,191,0.5)]"></div>
                      </div>
                    ) : (
                      <div className="relative group">
                        <input 
                          type={field.type} 
                          defaultValue={field.value}
                          className="w-full bg-slate-50 dark:bg-slate-950/50 border border-slate-200 dark:border-white/5 rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-teal-500/20 outline-none transition-all text-slate-800 dark:text-slate-200"
                        />
                        <SettingsIcon size={14} className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 dark:text-slate-600 group-focus-within:text-teal-600 dark:group-focus-within:text-teal-400 transition-colors" />
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>

      {/* Danger Zone */}
      <div className="bg-rose-50 dark:bg-rose-500/5 rounded-[2rem] border border-rose-100 dark:border-rose-500/10 p-8 flex items-center justify-between transition-colors">
        <div>
          <h4 className="text-rose-600 dark:text-rose-400 font-bold">Maintenance Mode</h4>
          <p className="text-xs text-rose-500/60 mt-1">Disconnect all vendors and halt platform transactions.</p>
        </div>
        <button className="px-4 py-2 bg-white dark:bg-rose-500/10 border border-rose-200 dark:border-rose-500/20 text-rose-600 dark:text-rose-400 rounded-xl text-xs font-bold hover:bg-rose-50 dark:hover:bg-rose-500/20 transition-all shadow-sm">
          Enable Lockdown
        </button>
      </div>
    </div>
  );
};

export default Settings;
