import React, { useState } from 'react';
import { Form, Input, Button, Checkbox, Typography, message, Row, Col } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useAuth } from '../../context/AuthContext';

// Trong Vite, bạn có thể import ảnh từ thư mục src/assets
// Hãy đảm bảo bạn đã chèn đúng đường dẫn ảnh logo và background của bạn
// import logo from '../../assets/masci-logo.png'; 
// import loginBg from '../../assets/login-bg.png';

const { Title, Text } = Typography;

const LoginPage = () => {
  const { login } = useAuth();
  const [loading, setLoading] = useState(false);

  const onFinish = async (values) => {
    setLoading(true);
    const result = await login(values.username, values.password);
    if (!result.success) {
      message.error(result.message);
    } else {
      message.success("Chào mừng quay trở lại hệ thống!");
    }
    setLoading(false);
  };

  return (
    <Row style={{ minHeight: '100vh', backgroundColor: '#fff' }}>
      {/* CỘT TRÁI: Branding & Background */}
      {/* Hiển thị trên màn hình lớn, ẩn trên màn hình di động (xs=0) */}
      <Col xs={0} md={12} lg={14} style={{
        background: 'linear-gradient(135deg, #1890ff 0%, #722ed1 100%)', // Chuyển sắc Xanh - Tím
        // backgroundImage: `url(${loginBg})`, // Mở comment này nếu có ảnh nền
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        padding: '40px',
        color: '#fff'
      }}>
        {/* Có thể thay bằng thẻ <img> chứa Logo */}
        <Title level={1} style={{ color: '#fff', fontSize: '48px', margin: 0 }}>
          MaSci Admin
        </Title>
        <Text style={{ color: 'rgba(255, 255, 255, 0.8)', fontSize: '18px', marginTop: '16px', textAlign: 'center', maxWidth: '80%' }}>
          Hệ thống quản trị nội dung học tập thông minh. Vui lòng đăng nhập để tiếp tục.
        </Text>
      </Col>

      {/* CỘT PHẢI: Form Đăng nhập */}
      <Col xs={24} md={12} lg={10} style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F8F9FA' // Xám cực nhạt để nổi bật Form
      }}>
        <div style={{
          width: '100%',
          maxWidth: '420px',
          padding: '40px',
          backgroundColor: '#fff',
          borderRadius: '16px', // Bo góc mềm mại
          boxShadow: '0 8px 24px rgba(0,0,0,0.05)' // Bóng đổ nhẹ, sang trọng
        }}>
          
          <div style={{ textAlign: 'center', marginBottom: '32px' }}>
             {/* Logo dùng cho màn hình nhỏ khi cột trái bị ẩn */}
            <Title level={2} style={{ color: '#1890ff', marginBottom: '8px' }}>Đăng nhập</Title>
            <Text type="secondary">Nhập thông tin tài khoản quản trị của bạn</Text>
          </div>

          <Form
            name="admin_login"
            onFinish={onFinish}
            layout="vertical"
            size="large"
            initialValues={{ remember: true }}
          >
            <Form.Item
              name="username"
              rules={[{ required: true, message: 'Vui lòng nhập Tên đăng nhập!' }]}
            >
              <Input 
                prefix={<UserOutlined style={{ color: '#bfbfbf' }} />} 
                placeholder="Tên đăng nhập" 
                style={{ borderRadius: '8px' }}
              />
            </Form.Item>

            <Form.Item
              name="password"
              rules={[{ required: true, message: 'Vui lòng nhập Mật khẩu!' }]}
            >
              <Input.Password 
                prefix={<LockOutlined style={{ color: '#bfbfbf' }} />} 
                placeholder="Mật khẩu" 
                style={{ borderRadius: '8px' }}
              />
            </Form.Item>

            {/* Chức năng mở rộng: Ghi nhớ & Quên mật khẩu */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
              <Form.Item name="remember" valuePropName="checked" noStyle>
                <Checkbox>Ghi nhớ tôi</Checkbox>
              </Form.Item>
              <a href="#" style={{ color: '#1890ff', fontWeight: 500 }}>Quên mật khẩu?</a>
            </div>

            <Form.Item style={{ marginBottom: 0 }}>
              <Button 
                type="primary" 
                htmlType="submit" 
                block 
                loading={loading}
                style={{ height: '48px', borderRadius: '8px', fontSize: '16px', fontWeight: 600 }}
              >
                ĐĂNG NHẬP
              </Button>
            </Form.Item>
          </Form>

        </div>
      </Col>
    </Row>
  );
};

export default LoginPage;