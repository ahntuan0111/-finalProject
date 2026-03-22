import React from 'react';
import { Menu } from 'antd';
import { 
  DashboardOutlined, 
  TeamOutlined, 
  BookOutlined, 
  EditOutlined, 
  FilePdfOutlined,
  CheckSquareOutlined,
  LogoutOutlined,
  SettingOutlined
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';

const SidebarMenu = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const items = [
    { 
        key: 'group1', 
        type: 'group', 
        label: 'CHÍNH',
        children: [
            { key: '/dashboard', icon: <DashboardOutlined />, label: 'Bảng điều khiển' },
        ]
    },
    { 
        key: 'group2', 
        type: 'group', 
        label: 'HỌC TẬP & NỘI DUNG',
        children: [
            { key: '/courses', icon: <BookOutlined />, label: 'Quản lý Bài học' },
            { key: '/exercises', icon: <EditOutlined />, label: 'Ngân hàng Bài tập' },
            { key: '/submissions', icon: <CheckSquareOutlined />, label: 'Lịch sử Nộp bài AI' },
            { key: '/quizzes', icon: <CheckSquareOutlined />, label: 'Quản lý Đề thi' },
            { key: '/documents', icon: <FilePdfOutlined />, label: 'Quản lý Tài liệu' },
        ]
    },
    { 
        key: 'group3', 
        type: 'group', 
        label: 'TÀI KHOẢN',
        children: [
            { key: '/users', icon: <TeamOutlined />, label: 'Quản lý Học sinh' },
        ]
    },
    { type: 'divider' },
    { key: '/settings', icon: <SettingOutlined />, label: 'Cài đặt hệ thống' },
    { key: 'logout', icon: <LogoutOutlined />, label: 'Đăng xuất', danger: true },
  ];

  const handleMenuClick = ({ key }) => {
    if (key === 'logout') {
      localStorage.removeItem('access_token'); 
      localStorage.removeItem('user_info'); 
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
      style={{ borderRight: 0, padding: '0 8px' }} // Cách lề một chút cho các item menu khỏi chạm viền
    />
  );
};

export default SidebarMenu;