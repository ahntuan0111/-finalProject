import React, { createContext, useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosClient from '../api/axiosClient';
import { storage } from '../utils/storage';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  // Kiểm tra token khi F5 lại trang
  useEffect(() => {
    const checkLogin = () => {
      const token = storage.getToken();
      const userInfo = storage.getUserInfo();
      if (token && userInfo) {
        setUser(userInfo);
      }
      setLoading(false);
    };
    checkLogin();
  }, []);

  // Hàm đăng nhập
  const login = async (username, password) => {
    try {
      // Gọi API: POST /api/auth/login
      const response = await axiosClient.post('/auth/login', { username, password });
      
      // LOG KIỂM TRA (Bạn hãy bật F12 console để xem dòng này in ra gì)
      console.log("Response từ Backend:", response);

      // SỬA LỖI Ở ĐÂY:
      // Backend trả về: { success: true, data: { token: "...", role: "..." } }
      // Nên ta cần check response.data
      if (response && response.success && response.data) {
        
        const { token, role, id, username: resUsername } = response.data;

        // Kiểm tra quyền Admin
        if (role !== 'ADMIN') {
          return { success: false, message: "Tài khoản không có quyền truy cập Admin!" };
        }

        // Lưu token và thông tin user
        storage.setToken(token);
        const userInfo = { 
            username: resUsername, 
            role: role,
            id: id
        };
        storage.setUserInfo(userInfo);
        setUser(userInfo);
        
        // Điều hướng vào Dashboard
        navigate('/dashboard');
        return { success: true };
      } else {
        // Trường hợp API trả về 200 nhưng logic sai (ví dụ thiếu data)
        return { success: false, message: response.message || "Dữ liệu đăng nhập không hợp lệ" };
      }

    } catch (error) {
      console.error("Login Error:", error);
      return { 
        success: false, 
        message: error.response?.data?.message || "Lỗi kết nối đến Server" 
      };
    }
  };

  // Hàm đăng xuất
  const logout = () => {
    storage.clear();
    setUser(null);
    navigate('/login');
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);