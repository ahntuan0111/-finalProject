import React from 'react';
import { Card, Col, Row, Statistic } from 'antd';
import { UserOutlined, BookOutlined } from '@ant-design/icons';

const DashboardPage = () => {
  return (
    <div>
      <h2 style={{ marginBottom: '20px' }}>Tổng quan hệ thống</h2>
      <Row gutter={16}>
        <Col span={8}>
          <Card>
            <Statistic
              title="Tổng số học sinh"
              value={120} // Sau này gọi API User count
              prefix={<UserOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="Tổng số bài học"
              value={45} // Sau này gọi API Lesson count
              prefix={<BookOutlined />}
              valueStyle={{ color: '#cf1322' }}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default DashboardPage;