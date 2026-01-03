import React from 'react';
import { Layout, theme } from 'antd';
import { Outlet } from 'react-router-dom';
import SidebarMenu from './SidebarMenu';

const { Header, Sider, Content } = Layout;

const MainLayout = () => {
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider collapsible breakpoint="lg">
        <div className="demo-logo-vertical" style={{ height: 64, margin: 16, background: 'rgba(255,255,255,.2)', borderRadius: 6 }} />
        <SidebarMenu />
      </Sider>
      <Layout>
        <Header style={{ padding: 0, background: colorBgContainer, textAlign: 'center', fontWeight: 'bold', fontSize: '18px' }}>
          HỆ THỐNG QUẢN TRỊ E-LEARNING
        </Header>
        <Content style={{ margin: '16px' }}>
          <div
            style={{
              padding: 24,
              minHeight: 360,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
            }}
          >
            {/* Đây là nơi các trang con (UserList, Dashboard...) sẽ hiển thị */}
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  );
};

export default MainLayout;