import React from 'react';
import { Menu } from 'antd';
import { 
  DashboardOutlined, 
  UserOutlined, 
  BookOutlined, 
  FormOutlined, 
  FilePdfOutlined,
  CheckSquareOutlined, // <-- Import thêm icon cho Quiz
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
    { key: '/exercises', icon: <FormOutlined />, label: 'Quản lý Bài tập' },
    { key: '/documents', icon: <FilePdfOutlined />, label: 'Quản lý Tài liệu' },
    
    // --- THÊM MỤC QUẢN LÝ QUIZ ---
    { key: '/quizzes', icon: <CheckSquareOutlined />, label: 'Quản lý Quiz' },
    // -----------------------------

    { type: 'divider' },
    { key: 'logout', icon: <LogoutOutlined />, label: 'Đăng xuất', danger: true },
  ];

  const handleMenuClick = ({ key }) => {
    if (key === 'logout') {
      // Đã sửa lại thành 'access_token' cho khớp với tên biến bạn đang lưu
      localStorage.removeItem('access_token'); 
      localStorage.removeItem('user_info'); // Nên xóa thêm thông tin user nếu có lưu
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