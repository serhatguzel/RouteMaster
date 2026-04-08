import axios from 'axios';
import { STORAGE_KEYS, PATHS, API_ENDPOINTS } from '../utils/constants';

const api = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// İstek interceptor: Her isteğe Access Token ekle
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(STORAGE_KEYS.TOKEN);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Yanıt interceptor: 401 gelirse Refresh Token ile yenile
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN);

        if (!refreshToken) {
          throw new Error('No refresh token available');
        }

        const res = await axios.post(API_ENDPOINTS.AUTH.REFRESH, refreshToken, {
          headers: { 'Content-Type': 'text/plain' },
        });

        if (res.status === 200) {
          const { accessToken } = res.data;
          localStorage.setItem(STORAGE_KEYS.TOKEN, accessToken);
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          return api(originalRequest);
        }
      } catch (refreshError) {
        clearStorageAndRedirect();
      }
    }

    return Promise.reject(error);
  }
);

// localStorage temizle ve login'e yönlendir
const clearStorageAndRedirect = () => {
  localStorage.removeItem(STORAGE_KEYS.TOKEN);
  localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
  localStorage.removeItem(STORAGE_KEYS.ROLE);
  window.location.href = PATHS.LOGIN;
};

// Backend'e logout isteği gönder, sonra localStorage temizle
export const logout = async () => {
  const refreshToken = localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN);
  try {
    if (refreshToken) {
      await axios.post(API_ENDPOINTS.AUTH.LOGOUT, null, {
        headers: { 'X-Refresh-Token': refreshToken },
      });
    }
  } catch (e) {
    // Sunucu hatası olsa bile temizlik yapılır
  } finally {
    clearStorageAndRedirect();
  }
};

export default api;
