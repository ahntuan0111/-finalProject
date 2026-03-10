import React, { useState, useEffect } from 'react';
import { Card, Col, Row, Statistic, Table, Tag, Avatar, Typography, Space, Spin } from 'antd';
import { TeamOutlined, GlobalOutlined, BookOutlined, FileTextOutlined, ArrowUpOutlined } from '@ant-design/icons';
import { Line, Pie } from '@ant-design/plots';
import axios from 'axios';

const { Title, Text } = Typography;

const DashboardPage = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  // 1. GỌI API TỪ SPRING BOOT ĐÃ THÊM XÁC THỰC TOKEN
  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const token = localStorage.getItem('access_token'); 
        
        const response = await axios.get('http://localhost:8080/api/admin/dashboard', {
          headers: {
            Authorization: `Bearer ${token}` // Truyền Token vào header để không bị lỗi 403
          }
        });
        
        if (response.data && response.data.success) {
          setData(response.data.data);
        }
      } catch (error) {
        console.error("Lỗi khi tải dữ liệu Dashboard:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  // 2. CẤU HÌNH BIỂU ĐỒ ĐƯỜNG (Line Chart)
  const lineConfig = {
    data: data?.userGrowth || [],
    padding: 'auto',
    xField: 'date',
    yField: 'count',
    smooth: true, 
    color: '#1890ff',
    area: {
      color: () => {
        return 'l(270) 0:#ffffff 0.5:#7ec2f3 1:#1890ff';
      },
    },
  };

  // 3. CẤU HÌNH BIỂU ĐỒ TRÒN (Donut Chart)
  const pieConfig = {
    appendPadding: 10,
    data: data?.contentDistribution || [],
    angleField: 'value',
    colorField: 'type',
    radius: 1,
    innerRadius: 0.6,
    label: {
      type: 'inner',
      offset: '-50%',
      content: '{value}%',
      style: { textAlign: 'center', fontSize: 14, fill: '#fff' },
    },
    statistic: {
      title: false,
      content: {
        style: { whiteSpace: 'pre-wrap', overflow: 'hidden', textOverflow: 'ellipsis', fontSize: '18px' },
        content: '70%\nTOÁN HỌC',
      },
    },
    color: ['#1890ff', '#d9d9d9'],
  };

  // 4. CẤU HÌNH CỘT BẢNG HỌC SINH MỚI
  const studentColumns = [
    {
      title: 'TÊN HỌC SINH',
      dataIndex: 'name',
      key: 'name',
      render: (text, record) => (
        <Space>
          <Avatar style={{ backgroundColor: '#f0f2f5', color: '#1890ff' }}>{record.id}</Avatar>
          <Text strong>{text}</Text>
        </Space>
      ),
    },
    { title: 'KHỐI LỚP', dataIndex: 'grade', key: 'grade', render: text => <Text type="secondary">{text}</Text> },
    { title: 'NGÀY THAM GIA', dataIndex: 'joinDate', key: 'joinDate', render: text => <Text type="secondary">{text}</Text> },
  ];

  // 5. CẤU HÌNH CỘT BẢNG NỘI DUNG MỚI
  const contentColumns = [
    {
      title: 'TIÊU ĐỀ',
      dataIndex: 'title',
      key: 'title',
      render: text => (
        <Space>
          <BookOutlined style={{ color: '#1890ff' }} />
          <Text strong>{text}</Text>
        </Space>
      )
    },
    { title: 'MÔN HỌC', dataIndex: 'subject', key: 'subject', render: text => <Text type="secondary">{text}</Text> },
    {
      title: 'TRẠNG THÁI',
      dataIndex: 'status',
      key: 'status',
      render: status => {
        let color = status === 'PUBLISHED' ? 'success' : 'warning';
        let text = status === 'PUBLISHED' ? 'ĐÃ XUẤT BẢN' : 'BẢN NHÁP';
        return <Tag color={color}>{text}</Tag>;
      },
    },
  ];

  if (loading) {
    return <div style={{ textAlign: 'center', padding: '50px' }}><Spin size="large" /></div>;
  }

  return (
    <div style={{ padding: '24px', backgroundColor: '#f0f2f5', minHeight: '100vh' }}>
      <Row justify="space-between" align="middle" style={{ marginBottom: '24px' }}>
        <Title level={3} style={{ margin: 0 }}>Bảng điều khiển</Title>
      </Row>

      {/* HÀNG 1: 4 THẺ THỐNG KÊ (TOP CARDS) */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} lg={6}>
          <Card variant="borderless" style={{ borderRadius: '12px' }}>
            <Statistic
              title={<Text type="secondary" strong>Tổng học sinh</Text>}
              value={data?.overview?.totalStudents}
              styles={{ content: { fontWeight: 'bold', fontSize: '28px' } }}
              prefix={<TeamOutlined style={{ color: '#d9d9d9' }} />}
            />
            <Text type="success" style={{ fontSize: '12px', fontWeight: 'bold' }}>
              <ArrowUpOutlined /> {data?.overview?.studentGrowth} so với tháng trước
            </Text>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card variant="borderless" style={{ borderRadius: '12px' }}>
            <Statistic
              title={<Text type="secondary" strong>Đang truy cập</Text>}
              value={data?.overview?.onlineUsers}
              styles={{ content: { fontWeight: 'bold', fontSize: '28px' } }}
              prefix={<GlobalOutlined style={{ color: '#1890ff' }} />}
            />
            <Text type="secondary" style={{ fontSize: '12px' }}>Trực tuyến</Text>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card variant="borderless" style={{ borderRadius: '12px' }}>
            <Statistic
              title={<Text type="secondary" strong>Tổng bài học</Text>}
              value={data?.overview?.totalLessons}
              styles={{ content: { fontWeight: 'bold', fontSize: '28px' } }}
              prefix={<BookOutlined style={{ color: '#d9d9d9' }} />}
            />
            <Text type="success" style={{ fontSize: '12px', fontWeight: 'bold' }}>
              <ArrowUpOutlined /> {data?.overview?.lessonGrowth} Hàng tuần
            </Text>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card variant="borderless" style={{ borderRadius: '12px' }}>
            <Statistic
              title={<Text type="secondary" strong>Tổng đề thi</Text>}
              value={data?.overview?.totalExams}
              styles={{ content: { fontWeight: 'bold', fontSize: '28px' } }}
              prefix={<FileTextOutlined style={{ color: '#d9d9d9' }} />}
            />
            <Text type="secondary" style={{ fontSize: '12px' }}>Cập nhật 2 giờ trước</Text>
          </Card>
        </Col>
      </Row>

      {/* HÀNG 2: BIỂU ĐỒ ĐƯỜNG VÀ BIỂU ĐỒ TRÒN */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} lg={16}>
          <Card 
            title={<Title level={5}>Tăng trưởng Người dùng (30 ngày qua)</Title>} 
            variant="borderless" 
            style={{ borderRadius: '12px', height: '100%' }}
            extra={<Space><Tag color="blue">Hàng tuần</Tag><Tag color="processing">Hàng tháng</Tag></Space>}
          >
            <Line {...lineConfig} height={250} />
          </Card>
        </Col>
        <Col xs={24} lg={8}>
          <Card 
            title={<Title level={5}>Phân bổ Nội dung</Title>} 
            variant="borderless" 
            style={{ borderRadius: '12px', height: '100%' }}
          >
            <Pie {...pieConfig} height={250} />
          </Card>
        </Col>
      </Row>

      {/* HÀNG 3: 2 BẢNG DANH SÁCH MỚI NHẤT */}
      <Row gutter={[16, 16]}>
        <Col xs={24} lg={12}>
          <Card 
            title={<Title level={5}>Học sinh mới đăng ký</Title>} 
            extra={<a href="#">Xem tất cả</a>}
            variant="borderless" 
            style={{ borderRadius: '12px', height: '100%' }}
            styles={{ body: { padding: 0 } }} // Bỏ padding để bảng full card
          >
            <Table 
              columns={studentColumns} 
              dataSource={data?.recentStudents} 
              pagination={false} 
              rowKey="id"
            />
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card 
            title={<Title level={5}>Bài học/Đề thi mới nhất</Title>} 
            extra={<a href="#">Quản lý tất cả</a>}
            variant="borderless" 
            style={{ borderRadius: '12px', height: '100%' }}
            styles={{ body: { padding: 0 } }}
          >
            <Table 
              columns={contentColumns} 
              dataSource={data?.recentContents} 
              pagination={false} 
              rowKey="id"
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default DashboardPage;