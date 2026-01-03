import React from 'react';
import { Menu } from 'antd';
import { 
  DashboardOutlined, 
  UserOutlined, 
  BookOutlined, 
  LogoutOutlined 
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const SidebarMenu = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { logout } = useAuth(); // Lấy hàm logout từ Context

  const menuItems = [
    {
      key: '/dashboard',
      icon: <DashboardOutlined />,
      label: 'Dashboard',
    },
    {
      key: '/users',
      icon: <UserOutlined />,
      label: 'Quản lý Người dùng',
    },
    {
      key: '/courses', // Sẽ dẫn đến trang danh sách môn học/chapter
      icon: <BookOutlined />,
      label: 'Quản lý Bài học',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: 'Đăng xuất',
      danger: true,
    },
  ];

  const handleMenuClick = (e) => {
    if (e.key === 'logout') {
      logout();
    } else {
      navigate(e.key);
    }
  };

  return (
    <Menu
      theme="dark"
      mode="inline"
      selectedKeys={[location.pathname]}
      items={menuItems}
      onClick={handleMenuClick}
    />
  );
};

export default SidebarMenu;