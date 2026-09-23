import axios from 'axios';

export const API_BASE_URL = import.meta.env.VITE_API_URL 
  ? import.meta.env.VITE_API_URL.replace(/\/+$/, '') 
  : 'http://localhost:8080';

const api = axios.create({
  baseURL: `${API_BASE_URL}/api`
});

// Automatically add JWT token to every request
api.interceptors.request.use((config) => {
  const savedUser = localStorage.getItem('sariTrack_user');
  if (savedUser) {
    const user = JSON.parse(savedUser);
    if (user.token) {
      config.headers.Authorization = `Bearer ${user.token}`;
    }
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

export default api;
