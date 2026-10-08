import api, { API_BASE_URL } from '../../core/api/api';
import { Mail, Lock, ShoppingCart, Eye, EyeOff, KeyRound, X } from 'lucide-react';
import Input from '../../core/components/Input';
import { useState, useEffect } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';

const Login = ({ onLoginSuccess }) => {
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  // Forgot / Reset Password state
  const [showForgotModal, setShowForgotModal] = useState(false);
  const [forgotStep, setForgotStep] = useState(1); // 1 = request token, 2 = reset password
  const [forgotEmail, setForgotEmail] = useState('');
  const [resetToken, setResetToken] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmNewPassword, setConfirmNewPassword] = useState('');
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [modalLoading, setModalLoading] = useState(false);
  const [modalFeedback, setModalFeedback] = useState({ type: '', message: '' });
  const [successBanner, setSuccessBanner] = useState('');

  // --- CATCH GOOGLE REDIRECT OR RESET TOKEN QUERY PARAM ---
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    if (params.get('loginSuccess') === 'true') {
      const userData = { 
        name: params.get('name') || "Google User", 
        email: params.get('email') || "Synchronized",
        role: params.get('role') || "VENDOR"
      };
      
      onLoginSuccess(userData);
      navigate('/dashboard');
    }

    const justRegistered = sessionStorage.getItem('registered_success');
    if (justRegistered || params.get('registered') === 'true' || location.state?.registered) {
      setSuccessBanner('Account created successfully! Please sign in with your email and password.');
      sessionStorage.removeItem('registered_success');
    }

    const tokenParam = params.get('token') || params.get('resetToken');
    if (tokenParam) {
      setResetToken(tokenParam);
      setForgotStep(2);
      setShowForgotModal(true);
    }
  }, [location, onLoginSuccess, navigate]);

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await api.post('/auth/login', credentials);
      
      // Now handling JSON object instead of String
      if (response.data && typeof response.data === 'object') {
        onLoginSuccess(response.data);
        navigate('/dashboard');
      } else {
        alert(response.data || "Invalid response from server");
      }
    } catch (error) {
      const errorMsg = error.response?.data?.error || "Login failed. Please verify your credentials or server connection.";
      alert(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  const handleRequestToken = async (e) => {
    e.preventDefault();
    if (!forgotEmail) {
      setModalFeedback({ type: 'error', message: 'Please enter your email address.' });
      return;
    }
    setModalLoading(true);
    setModalFeedback({ type: '', message: '' });
    try {
      const res = await api.post('/auth/forgot-password', { email: forgotEmail });
      setModalFeedback({
        type: 'success',
        message: res.data?.message || 'Password reset instructions have been sent to your email.'
      });
      setTimeout(() => {
        setForgotStep(2);
      }, 1500);
    } catch (error) {
      const msg = error.response?.data?.error || error.response?.data?.message || 'Failed to send reset instructions.';
      setModalFeedback({ type: 'error', message: msg });
    } finally {
      setModalLoading(false);
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    if (!resetToken.trim() || !newPassword.trim()) {
      setModalFeedback({ type: 'error', message: 'Please fill in both the reset token and new password.' });
      return;
    }
    if (newPassword !== confirmNewPassword) {
      setModalFeedback({ type: 'error', message: 'Passwords do not match.' });
      return;
    }
    setModalLoading(true);
    setModalFeedback({ type: '', message: '' });
    try {
      const res = await api.post('/auth/reset-password', {
        token: resetToken.trim(),
        newPassword: newPassword.trim()
      });
      setModalFeedback({
        type: 'success',
        message: res.data?.message || 'Password reset successfully!'
      });
      setTimeout(() => {
        setShowForgotModal(false);
        setForgotStep(1);
        setResetToken('');
        setNewPassword('');
        setConfirmNewPassword('');
        setModalFeedback({ type: '', message: '' });
        alert('Password has been reset successfully! You can now log in.');
      }, 1500);
    } catch (error) {
      const msg = error.response?.data?.error || error.response?.data?.message || 'Failed to reset password.';
      setModalFeedback({ type: 'error', message: msg });
    } finally {
      setModalLoading(false);
    }
  };

  // --- ADDED GOOGLE OAUTH LOGIC ---
  const handleGoogleLogin = () => {
    // Directs the browser to the Spring Boot OAuth entry point
    window.location.href = `${API_BASE_URL}/oauth2/authorization/google`;
  };

  return (
    <div className="bg-white/90 dark:bg-slate-900/90 backdrop-blur-xl p-10 rounded-[2.5rem] shadow-2xl border border-white/20 dark:border-slate-800 text-slate-800 dark:text-slate-100 animate-in fade-in zoom-in duration-300 transition-colors relative">
      <div className="flex flex-col items-center mb-8 text-center">
        <div className="flex items-center gap-2 mb-2">
           <ShoppingCart className="w-10 h-10 text-[#16A394]" />
           <h1 className="text-3xl font-bold tracking-tight dark:text-white">
             Sari<span className="text-[#16A394]">Track</span>
           </h1>
        </div>
        <p className="text-slate-500 dark:text-slate-400 text-sm">Sign in to manage your store</p>
      </div>

      {/* Registration Success Banner */}
      {successBanner && (
        <div className="p-4 mb-6 rounded-2xl bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 text-emerald-800 dark:text-emerald-300 text-sm font-medium flex items-center justify-between gap-3 animate-in fade-in duration-300 shadow-sm">
          <span>{successBanner}</span>
          <button 
            type="button" 
            onClick={() => setSuccessBanner('')}
            className="text-emerald-600 hover:text-emerald-800 dark:text-emerald-400 dark:hover:text-emerald-200 transition-colors p-1"
            title="Dismiss"
          >
            <X size={18} />
          </button>
        </div>
      )}

      <form onSubmit={handleLogin} className="space-y-5">
        <Input 
          icon={Mail}
          type="email"
          placeholder="Email Address"
          value={credentials.email}
          onChange={e => setCredentials({...credentials, email: e.target.value})}
        />
        <Input 
          icon={Lock}
          type={showPassword ? "text" : "password"}
          placeholder="Password"
          value={credentials.password}
          onChange={e => setCredentials({...credentials, password: e.target.value})}
          showPasswordButton={showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
          onTogglePassword={() => setShowPassword(!showPassword)}
        />
        <div className="flex justify-end">
          <button
            type="button"
            onClick={() => {
              setShowForgotModal(true);
              setForgotStep(1);
              setModalFeedback({ type: '', message: '' });
            }}
            className="text-xs font-semibold text-[#16A394] hover:underline transition-colors"
          >
            Forgot password?
          </button>
        </div>
        <button type="submit" className="w-full bg-[#16A394] hover:bg-[#0D7A6F] text-white font-bold py-4 rounded-2xl shadow-lg shadow-teal-600/20 transition-all active:scale-95">
          Login
        </button>
      </form>

      {/* --- GOOGLE DIVIDER AND BUTTON --- */}
      <div className="relative my-6">
        <div className="absolute inset-0 flex items-center"><span className="w-full border-t border-slate-200 dark:border-slate-800"></span></div>
        <div className="relative flex justify-center text-xs uppercase"><span className="bg-white/90 dark:bg-slate-900/90 px-2 text-slate-500 dark:text-slate-400">Or continue with</span></div>
      </div>

      <button 
        onClick={handleGoogleLogin}
        type="button"
        className="w-full flex items-center justify-center gap-3 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-200 font-semibold py-3 rounded-2xl shadow-sm transition-all active:scale-95"
      >
        <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" className="w-5 h-5" alt="Google" />
        Google Account
      </button>

      <p className="text-center text-sm text-slate-600 dark:text-slate-400 mt-8">
        Need an account? <Link to="/register" className="text-[#16A394] font-bold hover:underline">Register</Link>
      </p>

      {/* --- FORGOT / RESET PASSWORD MODAL --- */}
      {showForgotModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 animate-in fade-in duration-200">
          <div className="bg-white dark:bg-slate-900 rounded-3xl shadow-2xl border border-slate-200 dark:border-slate-800 w-full max-w-md p-6 relative">
            <button
              onClick={() => {
                setShowForgotModal(false);
                setModalFeedback({ type: '', message: '' });
              }}
              className="absolute top-5 right-5 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200"
              aria-label="Close modal"
            >
              <X size={20} />
            </button>

            <div className="text-center mb-6">
              <div className="w-12 h-12 bg-teal-50 dark:bg-teal-950/50 rounded-2xl flex items-center justify-center mx-auto mb-3 text-[#16A394]">
                <KeyRound size={24} />
              </div>
              <h2 className="text-xl font-bold dark:text-white">
                {forgotStep === 1 ? 'Forgot Password?' : 'Reset Your Password'}
              </h2>
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
                {forgotStep === 1
                  ? 'Enter your account email to receive a 15-minute reset token.'
                  : 'Enter the reset token sent to your email and your new password.'}
              </p>
            </div>

            {modalFeedback.message && (
              <div
                className={`mb-4 p-3 rounded-xl text-xs font-medium text-center ${
                  modalFeedback.type === 'error'
                    ? 'bg-rose-50 text-rose-600 dark:bg-rose-950/40 dark:text-rose-300'
                    : 'bg-emerald-50 text-emerald-600 dark:bg-emerald-950/40 dark:text-emerald-300'
                }`}
              >
                {modalFeedback.message}
              </div>
            )}

            {forgotStep === 1 ? (
              <form onSubmit={handleRequestToken} className="space-y-4">
                <Input
                  icon={Mail}
                  type="email"
                  placeholder="Your Account Email"
                  value={forgotEmail}
                  onChange={(e) => setForgotEmail(e.target.value)}
                />
                <button
                  type="submit"
                  disabled={modalLoading}
                  className="w-full bg-[#16A394] hover:bg-[#0D7A6F] text-white font-bold py-3.5 rounded-xl shadow-md transition-all active:scale-95 disabled:opacity-50"
                >
                  {modalLoading ? 'Sending...' : 'Send Reset Token'}
                </button>
                <div className="text-center pt-2">
                  <button
                    type="button"
                    onClick={() => {
                      setForgotStep(2);
                      setModalFeedback({ type: '', message: '' });
                    }}
                    className="text-xs text-[#16A394] hover:underline font-semibold"
                  >
                    Already have a reset token? Click here
                  </button>
                </div>
              </form>
            ) : (
              <form onSubmit={handleResetPassword} className="space-y-4">
                <Input
                  icon={KeyRound}
                  type="text"
                  placeholder="Reset Token"
                  value={resetToken}
                  onChange={(e) => setResetToken(e.target.value)}
                />
                <Input
                  icon={Lock}
                  type={showNewPassword ? 'text' : 'password'}
                  placeholder="New Password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  showPasswordButton={showNewPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                  onTogglePassword={() => setShowNewPassword(!showNewPassword)}
                />
                <Input
                  icon={Lock}
                  type={showNewPassword ? 'text' : 'password'}
                  placeholder="Confirm New Password"
                  value={confirmNewPassword}
                  onChange={(e) => setConfirmNewPassword(e.target.value)}
                />
                <button
                  type="submit"
                  disabled={modalLoading}
                  className="w-full bg-[#16A394] hover:bg-[#0D7A6F] text-white font-bold py-3.5 rounded-xl shadow-md transition-all active:scale-95 disabled:opacity-50"
                >
                  {modalLoading ? 'Resetting...' : 'Set New Password'}
                </button>
                <div className="text-center pt-2">
                  <button
                    type="button"
                    onClick={() => {
                      setForgotStep(1);
                      setModalFeedback({ type: '', message: '' });
                    }}
                    className="text-xs text-[#16A394] hover:underline font-semibold"
                  >
                    Back to Request Token
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default Login;