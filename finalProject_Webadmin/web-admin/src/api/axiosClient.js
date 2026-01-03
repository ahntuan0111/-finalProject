import axios from 'axios';
import { storage } from '../utils/storage';

const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// REQUEST INTERCEPTOR (Gửi đi)
axiosClient.interceptors.request.use((config) => {
  const token = storage.getToken();
  
  // LOG: Xem request gửi đi đâu, mang theo Token gì
  console.group(`🚀 [API REQUEST] ${config.method?.toUpperCase()} ${config.url}`);
  console.log('Token:', token ? `Available (${token.substring(0, 10)}...)` : 'No Token');
  console.groupEnd();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

// RESPONSE INTERCEPTOR (Nhận về)
axiosClient.interceptors.response.use(
  (response) => {
    // LOG: Xem kết quả trả về có đúng format không
    console.group(`✅ [API RESPONSE] ${response.config.url}`);
    console.log('Status:', response.status);
    console.log('Data:', response.data);
    console.groupEnd();

    if (response && response.data) {
      return response.data;
    }
    return response;
  },
  (error) => {
    // LOG: Xem lỗi chi tiết là gì
    console.group(`jh [API ERROR] ${error.config?.url}`);
    if (error.response) {
      // Server trả về lỗi (4xx, 5xx)
      console.error('Status:', error.response.status);
      console.error('Data:', error.response.data);
    } else if (error.request) {
      // Không nhận được phản hồi (CORS, Mất mạng, Server chết)
      console.error('No response received (CORS or Network Error)');
    } else {
      console.error('Error Message:', error.message);
    }
    console.groupEnd();

    if (error.response && error.response.status === 401) {
      console.warn("Token hết hạn -> Auto Logout");
      storage.clear();
      window.location.href = '/login';
    }
    throw error;
  }
);

export default axiosClient;