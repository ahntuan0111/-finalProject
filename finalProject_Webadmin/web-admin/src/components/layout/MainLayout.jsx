import React, { useState } from 'react';
import { Layout, theme, Avatar, Space, Typography, Dropdown, Badge } from 'antd';
import { Outlet, useNavigate } from 'react-router-dom';
import SidebarMenu from './SidebarMenu';
import { 
  BellOutlined, 
  UserOutlined, 
  LogoutOutlined, 
  ReadOutlined,
  SettingOutlined
} from '@ant-design/icons';

const { Header, Sider, Content } = Layout;
const { Text } = Typography;

const MainLayout = () => {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  
  const {
    token: { colorBgContainer, colorBgLayout },
  } = theme.useToken();

  // Menu xổ xuống khi click vào Avatar
  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: 'Hồ sơ cá nhân',
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: 'Cài đặt hệ thống',
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: 'Đăng xuất',
      danger: true,
      onClick: () => {
        localStorage.removeItem('access_token');
        localStorage.removeItem('user_info');
        window.location.href = '/login';
      }
    },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      {/* --- SIDEBAR TRÁI --- */}
      <Sider 
        collapsible 
        collapsed={collapsed} 
        onCollapse={(value) => setCollapsed(value)}
        breakpoint="lg"
        width={260} // Tăng chiều rộng sidebar lên một chút cho thoáng
        style={{ boxShadow: '2px 0 8px 0 rgba(29,35,41,.05)', zIndex: 10 }}
      >
        {/* Khu vực Logo */}
        <div style={{ 
            height: 64, 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: collapsed ? 'center' : 'flex-start',
            padding: collapsed ? '0' : '0 24px',
            color: 'white',
            borderBottom: '1px solid rgba(255, 255, 255, 0.1)',
            marginBottom: 16,
            overflow: 'hidden'
        }}>
          <ReadOutlined style={{ fontSize: '28px', color: '#1677ff' }} />
          {!collapsed && (
             <span style={{ fontSize: '18px', fontWeight: 800, marginLeft: 12, letterSpacing: '0.5px' }}>
                EduAdmin
             </span>
          )}
        </div>

        {/* Menu Điều hướng */}
        <SidebarMenu />
      </Sider>

      <Layout style={{ background: colorBgLayout }}>
        {/* --- HEADER TRÊN CÙNG --- */}
        <Header 
            style={{ 
                padding: '0 24px', 
                background: colorBgContainer, 
                display: 'flex', 
                justifyContent: 'flex-end', // Đẩy các icon sang phải
                alignItems: 'center',
                boxShadow: '0 1px 4px rgba(0,21,41,.08)',
                zIndex: 9
            }}
        >
            <Space size="large">
                {/* Icon Thông báo */}
                <Badge count={5} size="small" offset={[-2, 5]}>
                    <Avatar 
                        shape="circle" 
                        icon={<BellOutlined />} 
                        style={{ cursor: 'pointer', backgroundColor: '#f5f5f5', color: '#666' }} 
                    />
                </Badge>

                {/* Khu vực User Profile */}
                <Dropdown menu={{ items: userMenuItems }} placement="bottomRight" trigger={['click']}>
                    <div style={{ display: 'flex', alignItems: 'center', cursor: 'pointer', padding: '0 8px', borderRadius: 6, transition: 'all 0.3s' }} className="user-dropdown-hover">
                        <Avatar style={{ backgroundColor: '#1677ff' }} icon={<UserOutlined />} />
                        <div style={{ display: 'flex', flexDirection: 'column', lineHeight: '1.2', marginLeft: 10 }}>
                            <Text strong style={{ fontSize: 14 }}>Quản trị viên</Text>
                            <Text type="secondary" style={{ fontSize: 12 }}>Admin</Text>
                        </div>
                    </div>
                </Dropdown>
            </Space>
        </Header>

        {/* --- KHU VỰC HIỂN THỊ NỘI DUNG (CÁC TRANG CON) --- */}
        <Content style={{ margin: 0, padding: 0 }}>
          {/* Không dùng thẻ div bọc background trắng ở đây nữa, để các trang tự xử lý Card của mình */}
          <div style={{ minHeight: 360 }}>
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  );
};

export default MainLayout;