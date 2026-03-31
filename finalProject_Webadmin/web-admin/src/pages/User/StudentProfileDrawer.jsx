import React, { useState, useEffect } from 'react';
import { 
  Drawer, Tabs, Row, Col, Card, Statistic, Avatar, 
  Tag, Descriptions, Timeline, Table, Space, Typography, Spin, Empty 
} from 'antd';
import { 
  UserOutlined, FireOutlined, ClockCircleOutlined, 
  CheckCircleOutlined, TrophyOutlined, BarChartOutlined,
  CalendarOutlined, HistoryOutlined
} from '@ant-design/icons';
import { Radar, Column } from '@ant-design/plots';
import userApi from '../../api/userApi';

const { Title, Text } = Typography;
const { TabPane } = Tabs;

const StudentProfileDrawer = ({ visible, userId, onClose }) => {
  const [loading, setLoading] = useState(false);
  const [profileData, setProfileData] = useState(null);

  useEffect(() => {
    if (visible && userId) {
      fetchProfile();
    }
  }, [visible, userId]);

  const fetchProfile = async () => {
    setLoading(true);
    try {
      const response = await userApi.getProfile(userId);
      if (response.success) {
        setProfileData(response.data);
      }
    } catch (error) {
      console.error("Lỗi khi tải hồ sơ học sinh:", error);
    } finally {
      setLoading(false);
    }
  };

  // Cấu hình Biểu đồ Radar (Kỹ năng)
  const radarConfig = {
    data: profileData?.charts?.radarSkills || [],
    xField: 'subjectName',
    yField: 'score',
    meta: {
      score: {
        alias: 'Điểm trung bình',
        min: 0,
        max: 10,
      },
    },
    xAxis: {
      line: null,
      tickLine: null,
      grid: {
        line: {
          style: {
            lineDash: [4, 4],
          },
        },
      },
    },
    yAxis: {
      grid: {
        line: {
          type: 'circle',
        },
      },
    },
    point: {
      size: 2,
    },
    area: {},
  };

  // Cấu hình Biểu đồ Cột (Thời gian học hàng tuần)
  const columnConfig = {
    data: profileData?.charts?.weeklyActivity || [],
    xField: 'day',
    yField: 'minutes',
    label: {
      position: 'middle',
      style: {
        fill: '#FFFFFF',
        opacity: 0.6,
      },
    },
    meta: {
      minutes: {
        alias: 'Số phút học',
      },
    },
    color: '#1890ff',
  };

  const assessmentColumns = [
    { title: 'Tên bài kiểm tra', dataIndex: 'title', key: 'title', render: text => <strong>{text}</strong> },
    { title: 'Loại', dataIndex: 'type', key: 'type', render: type => <Tag>{type}</Tag> },
    { title: 'Thời gian nộp', dataIndex: 'submittedAt', key: 'submittedAt' },
    { 
      title: 'Điểm số', 
      dataIndex: 'score', 
      key: 'score',
      render: score => (
        <Text strong style={{ color: score >= 5 ? '#52c41a' : '#f5222d' }}>
          {score}/10
        </Text>
      )
    },
    { 
      title: 'Trạng thái', 
      dataIndex: 'status', 
      key: 'status',
      render: status => <Tag color={status === 'ĐẠT' ? 'success' : 'error'}>{status}</Tag>
    },
  ];

  if (!userId) return null;

  return (
    <Drawer
      title="Chi tiết Hồ sơ học tập"
      placement="right"
      width={1000}
      onClose={onClose}
      open={visible}
      destroyOnClose
    >
      {loading ? (
        <div style={{ textAlign: 'center', padding: '100px' }}><Spin size="large" /></div>
      ) : profileData ? (
        <div style={{ padding: '0 10px' }}>
          {/* PHẦN A: HEADER */}
          <Card variant="borderless" style={{ backgroundColor: '#fafafa', marginBottom: 24 }}>
            <Row align="middle" gutter={24}>
              <Col>
                <Avatar size={80} icon={<UserOutlined />} style={{ backgroundColor: '#1890ff' }} />
              </Col>
              <Col flex="auto">
                <Title level={4} style={{ margin: 0 }}>{profileData.header.username}</Title>
                <Space direction="vertical" style={{ marginTop: 8 }}>
                  <Text type="secondary">{profileData.header.email}</Text>
                  <Space>
                    <Tag color="blue">Học sinh</Tag>
                    <Tag color={profileData.header.status === 'Hoạt động' ? 'green' : 'default'}>
                      {profileData.header.status}
                    </Tag>
                    <Text type="secondary" size="small">
                      <CalendarOutlined /> Tham gia: {profileData.header.joinDate}
                    </Text>
                  </Space>
                </Space>
              </Col>
            </Row>
          </Card>

          {/* PHẦN B: METRICS */}
          <Row gutter={16} style={{ marginBottom: 24 }}>
            <Col span={6}>
              <Card>
                <Statistic 
                  title="Chuỗi học tập" 
                  value={profileData.metrics.currentStreak} 
                  suffix="ngày"
                  prefix={<FireOutlined style={{ color: '#fa8c16' }} />}
                  valueStyle={{ color: '#fa8c16' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card>
                <Statistic 
                  title="Tổng thời gian" 
                  value={profileData.metrics.totalMinutes} 
                  suffix="phút"
                  prefix={<ClockCircleOutlined style={{ color: '#1890ff' }} />}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card>
                <Statistic 
                  title="Điểm chuyên cần" 
                  value={profileData.metrics.completedLessons} 
                  suffix="bài"
                  prefix={<CheckCircleOutlined style={{ color: '#52c41a' }} />}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card>
                <Statistic 
                  title="Quiz Trung bình" 
                  value={profileData.metrics.averageQuizScore} 
                  suffix="/10"
                  prefix={<TrophyOutlined style={{ color: '#fadb14' }} />}
                />
              </Card>
            </Col>
          </Row>

          {/* PHẦN C: TABS NỘI DUNG */}
          <Tabs defaultActiveKey="1" type="card">
            <TabPane 
              tab={<span><BarChartOutlined />Tổng quan</span>} 
              key="1"
            >
              <Row gutter={24}>
                <Col span={12}>
                  <Card title="Năng lực theo môn học" variant="borderless">
                    {profileData.charts.radarSkills.length > 0 ? (
                      <Radar {...radarConfig} height={300} />
                    ) : (
                      <Empty description="Chưa có dữ liệu bài làm" />
                    )}
                  </Card>
                </Col>
                <Col span={12}>
                  <Card title="Tần suất học (7 ngày qua)" variant="borderless">
                    <Column {...columnConfig} height={300} />
                  </Card>
                </Col>
              </Row>
            </TabPane>
            
            <TabPane 
              tab={<span><HistoryOutlined />Hoạt động</span>} 
              key="2"
            >
              <Card variant="borderless">
                <Timeline mode="left">
                  {profileData.activities.map((act, index) => (
                    <Timeline.Item key={index} color={act.status} label={act.time}>
                      <Text strong>{act.action}: </Text>
                      <Text>{act.title}</Text>
                    </Timeline.Item>
                  ))}
                </Timeline>
                {profileData.activities.length === 0 && <Empty />}
              </Card>
            </TabPane>

            <TabPane 
              tab={<span><TrophyOutlined />Lịch sử Làm bài</span>} 
              key="3"
            >
              <Table 
                columns={assessmentColumns} 
                dataSource={profileData.assessments} 
                pagination={{ pageSize: 5 }}
                rowKey="id"
              />
            </TabPane>
          </Tabs>
        </div>
      ) : (
        <Empty />
      )}
    </Drawer>
  );
};

export default StudentProfileDrawer;
