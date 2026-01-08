import React from 'react';
import { Menu } from 'antd';
import { 
  DashboardOutlined, 
  UserOutlined, 
  BookOutlined, 
  FormOutlined, // Icon cho bài tập
  LogoutOutlined 
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';

const SidebarMenu = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const items = [
    { key: '/dashboard', icon: <DashboardOutlined />, label: 'Dashboard' },
    { key: '/users', icon: <UserOutlined />, label: 'Quản lý Người dùng' },
    { key: '/courses', icon: <BookOutlined />, label: 'Quản lý Bài học' },
    // --- MỤC BÀI TẬP ---
    { key: '/exercises', icon: <FormOutlined />, label: 'Quản lý Bài tập' },
    // -------------------
    { type: 'divider' },
    { key: 'logout', icon: <LogoutOutlined />, label: 'Đăng xuất', danger: true },
  ];

  const handleMenuClick = ({ key }) => {
    if (key === 'logout') {
      localStorage.removeItem('token');
      window.location.href = '/login';
    } else {
      navigate(key);
    }
  };

  return (
    <Menu
      theme="dark"
      mode="inline"
      selectedKeys={[location.pathname]}
      items={items}
      onClick={handleMenuClick}
    />
  );
};

export default SidebarMenu;