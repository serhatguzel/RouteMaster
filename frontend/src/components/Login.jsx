import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Lock, User, Eye, EyeOff, Loader2 } from 'lucide-react';
import axios from 'axios';
import loginPhoto from '../assets/login-screen.png';
import logo from '../assets/turkish-airlines-logo.png';
import { PATHS, STORAGE_KEYS, API_ENDPOINTS } from '../utils/constants';
import { getErrorMessage } from '../utils/errorUtils';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  // Hataları obje olarak tutuyoruz: { username: true, password: true }
  const [errors, setErrors] = useState({});
  const [apiError, setApiError] = useState('');

  const navigate = useNavigate();

  const validate = () => {
    let tempErrors = {};
    if (!username.trim()) tempErrors.username = "Username is required";
    if (!password.trim()) tempErrors.password = "Password is required";

    setErrors(tempErrors);
    // Eğer tempErrors objesi boşsa validation geçmiştir
    return Object.keys(tempErrors).length === 0;
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setApiError('');

    // Önce client-side validation yapıyoruz
    if (!validate()) return;

    setLoading(true);
    try {
      const response = await axios.post(API_ENDPOINTS.AUTH.LOGIN, { username, password });
      if (response.data.accessToken) {
        localStorage.setItem(STORAGE_KEYS.TOKEN, response.data.accessToken);
        localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, response.data.refreshToken);
        localStorage.setItem(STORAGE_KEYS.ROLE, response.data.role);
        navigate(PATHS.DASHBOARD);
      }
    } catch (err) {
      setApiError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex h-screen w-screen bg-white overflow-hidden m-0 p-0 absolute inset-0">
      {/* Sol Bölüm*/}
      <div className="flex-shrink-0 w-1/2 h-full relative overflow-hidden bg-no-repeat"
        style={{ backgroundImage: `url(${loginPhoto})`, backgroundSize: 'cover' }}>
        <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent" />
        <div className="relative z-10 p-16 flex flex-col justify-end h-full text-white">
          <h2 className="text-6xl font-black font-outfit uppercase tracking-tighter leading-none mb-4">Route Master</h2>
          <p className="text-xl text-white/80 max-w-md font-light leading-relaxed">Streamlined solutions for location and transportation management.</p>
        </div>
      </div>

      {/* Sağ Bölüm: Form */}
      <div className="w-1/2 flex items-center justify-center p-8 md:p-12 bg-slate-50">
        <div className="w-full max-w-md bg-white p-10 rounded-3xl shadow-2xl shadow-slate-200 border border-slate-100">

          <div className="flex flex-col items-center mb-10 text-center">
            <img src={logo} alt="Logo" className="w-32 h-auto mb-6" />
            <h1 className="text-3xl font-bold text-slate-900">Login to Your Account</h1>
          </div>

          <form onSubmit={handleLogin} className="space-y-5" noValidate>
            {/* API'den gelen genel hata mesajı */}
            {apiError && (
              <div className="bg-red-50 border border-red-100 text-red-600 p-4 rounded-xl text-sm font-medium animate-shake">
                {apiError}
              </div>
            )}

            {/* Username Field */}
            <div className="space-y-1">
              <div className="relative group">
                <div className={`absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none transition-colors ${errors.username ? 'text-red-400' : 'text-slate-400'}`}>
                  <User size={19} />
                </div>
                <input
                  type="text"
                  value={username}
                  onChange={(e) => {
                    setUsername(e.target.value);
                    if (errors.username) setErrors(prev => ({ ...prev, username: null }));
                  }}
                  className={`w-full pl-12 pr-4 py-4 bg-slate-50 border rounded-2xl transition-all outline-none font-bold text-slate-900
                    ${errors.username ? 'border-red-500 focus:ring-red-200' : 'border-slate-100 focus:ring-2 focus:ring-slate-900'}`}
                  placeholder="Username"
                />
              </div>
              {errors.username && <p className="text-red-500 text-[10px] mt-1 ml-1 font-bold uppercase tracking-wider">{errors.username}</p>}
            </div>

            {/* Password Field */}
            <div className="space-y-1">
              <div className="relative group">
                <div className={`absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none transition-colors ${errors.password ? 'text-red-400' : 'text-slate-400'}`}>
                  <Lock size={19} />
                </div>
                <input
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => {
                    setPassword(e.target.value);
                    if (errors.password) setErrors(prev => ({ ...prev, password: null }));
                  }}
                  className={`w-full pl-12 pr-12 py-4 bg-slate-50 border rounded-2xl transition-all outline-none font-bold text-slate-900
                    ${errors.password ? 'border-red-500 focus:ring-red-200' : 'border-slate-100 focus:ring-2 focus:ring-slate-900'}`}
                  placeholder="Password"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-4 flex items-center text-slate-400 hover:text-slate-900 transition-colors"
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
              {errors.password && <p className="text-red-500 text-[10px] mt-1 ml-1 font-bold uppercase tracking-wider">{errors.password}</p>}
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-slate-900 text-white font-bold py-4 rounded-xl shadow-lg hover:bg-slate-800 transition-all flex items-center justify-center gap-2 uppercase tracking-widest text-sm"
            >
              {loading ? <Loader2 className="animate-spin w-5 h-5" /> : "Login"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;