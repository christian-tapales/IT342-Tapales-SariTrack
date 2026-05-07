import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api'
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
