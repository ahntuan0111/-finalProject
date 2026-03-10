import React from 'react';
import { ConfigProvider } from 'antd';
import AppRouter from './router/AppRouter';

function App() {
  return (
    // Bọc toàn bộ App bằng ConfigProvider để đồng bộ UI/UX cho tất cả Component của Ant Design
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#1677ff', // Màu xanh chủ đạo của hệ thống
          borderRadius: 8,         // Bo góc mượt mà cho toàn bộ button, input, card...
          fontFamily: '"Inter", "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif',
          colorBgLayout: '#f5f7fa',// Màu nền tổng thể của toàn bộ trang web (Xám siêu nhạt)
        },
        components: {
          Menu: {
            darkItemBg: '#001529', // Màu nền sidebar xanh đen sâu
            darkItemSelectedBg: '#1677ff', // Màu nền khi click chọn menu
          },
          Card: {
            headerFontSize: 16,
          }
        }
      }}
    >
      <div className="app-container">
         <AppRouter />
      </div>
    </ConfigProvider>
  );
}

export default App;