import axios from 'axios';
import { STORAGE_KEYS, PATHS } from '../utils/constants';

const api = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(STORAGE_KEYS.TOKEN);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response && error.response.status === 401
      && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('REFRESH_TOKEN');

        if (!refreshToken) {
          throw new Error('No refresh token available');
        }

        const res = await axios.post('/api/auth/refresh', refreshToken, {
          headers: { 'Content-Type': 'text/plain' }

        });
        if (res.status === 200) {
          const { accessToken } = res.data;

          localStorage.setItem(STORAGE_KEYS.TOKEN, accessToken);

          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          return api(originalRequest);
        }

      } catch (refreshError) {
        logOutAndRedirect();
      }

    }
    return Promise.reject(error);
  }
);

const logOutAndRedirect = () => {
  localStorage.removeItem(STORAGE_KEYS.TOKEN);
  localStorage.removeItem('REFRESH_TOKEN');
  localStorage.removeItem(STORAGE_KEYS.ROLE);
  window.location.href = PATHS.LOGIN;
};


export default api;
