import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Lock, User, Eye, EyeOff, Loader2, Info } from 'lucide-react';
import axios from 'axios';
import loginPhoto from '../assets/login-screen.png';
import logo from '../assets/turkish-airlines-logo.png';
import { PATHS, STORAGE_KEYS } from '../utils/constants';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await axios.post('/api/auth/login', { username, password });
      localStorage.setItem(STORAGE_KEYS.TOKEN, response.data.token);
      localStorage.setItem(STORAGE_KEYS.ROLE, response.data.role);
      navigate(PATHS.DASHBOARD);
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid username or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex h-screen w-screen bg-white overflow-hidden m-0 p-0 absolute inset-0">
      {/* Left Section: Hero (Aviation Image) */}
      <div
        className="flex-shrink-0 w-1/2 h-full relative overflow-hidden bg-no-repeat"
        style={{
          backgroundImage: `url(${loginPhoto})`,
          backgroundSize: 'cover'
        }}
      >
        <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent" />

        <div className="absolute inset-0 pointer-events-none opacity-40">
          <div className="absolute bottom-1/4 left-1/4 w-3 h-3 bg-white/50 rounded-full animate-ping" />
          <div className="absolute top-1/3 right-1/4 w-2 h-2 bg-white/30 rounded-full" />
        </div>

        <div className="relative z-10 p-16 flex flex-col justify-end h-full text-white">
          <h2 className="text-6xl font-black font-outfit uppercase tracking-tighter leading-none mb-4">
            Route Master
          </h2>
          <p className="text-xl text-white/80 max-w-md font-light leading-relaxed">
            Streamlined solutions for location and <br /> transportation management.
          </p>
        </div>
      </div>

      {/* Right Section: Login Form */}
      <div className="w-1/2 flex items-center justify-center p-8 md:p-12 bg-slate-50">
        <div className="w-full max-w-md bg-white p-10 rounded-3xl shadow-2xl shadow-slate-200 border border-slate-100">

          {/* Logo & Header */}
          <div className="flex flex-col items-center mb-10 text-center">
            <div className="mb-6 relative">
              <img src={logo} alt="RouteMaster Logo" className="w-32 h-auto" />
            </div>
            <h1 className="text-3xl font-bold text-slate-900 font-outfit">Login to Your Account</h1>
            <p className="text-slate-500 mt-3 text-sm max-w-xs mx-auto">
              Access the system to manage locations, <br /> transportations, and calculate routes.
            </p>
          </div>

          <form onSubmit={handleLogin} className="space-y-5">
            {error && (
              <div className="bg-red-50 border border-red-100 text-red-600 p-4 rounded-xl text-sm font-medium animate-shake">
                {error}
              </div>
            )}

            {/* Username */}
            <div className="space-y-1">
              <div className="relative group">
                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-slate-400 group-focus-within:text-slate-800 transition-colors">
                  <User size={19} />
                </div>
                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full pl-12 pr-4 py-4 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-slate-800 focus:border-transparent transition-all outline-none"
                  placeholder="Username"
                  required
                />
              </div>
            </div>

            {/* Password */}
            <div className="space-y-1">
              <div className="relative group">
                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-slate-400 group-focus-within:text-slate-800 transition-colors">
                  <Lock size={19} />
                </div>
                <input
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full pl-12 pr-12 py-4 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-slate-800 focus:border-transparent transition-all outline-none"
                  placeholder="Password"
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-4 flex items-center text-slate-400 hover:text-slate-800 transition-colors"
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
              <div className="text-right">
                <button type="button" className="text-xs font-semibold text-slate-500 hover:text-slate-800 underline underline-offset-4">
                  Forgot Password?
                </button>
              </div>
            </div>

            {/* Login Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full bg-slate-900 text-white font-bold py-4 rounded-xl shadow-lg hover:bg-slate-800 active:scale-[0.98] transition-all flex items-center justify-center gap-2 uppercase tracking-widest text-sm"
            >
              {loading ? (
                <Loader2 className="animate-spin w-5 h-5" />
              ) : (
                "Login"
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;
