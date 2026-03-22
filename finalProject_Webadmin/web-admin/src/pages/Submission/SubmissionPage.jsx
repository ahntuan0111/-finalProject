import React, { useState, useEffect } from 'react';
import { Card, Table, Typography, Breadcrumb, Tag, Image, Tooltip, Space } from 'antd';
import { CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';
import submissionApi from '../../api/submissionApi';

const { Title, Text } = Typography;

const SubmissionPage = () => {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchSubmissions();
  }, []);

  const fetchSubmissions = async () => {
    setLoading(true);
    try {
      const res = await submissionApi.getAll();
      if (res.success) {
        setSubmissions(res.data);
      }
    } catch (error) {
      console.error("Lỗi tải danh sách bài nộp:", error);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    {
      title: 'ID Học Sinh',
      dataIndex: 'userId',
      width: 100,
      align: 'center',
      render: (text) => <Text strong>#{text}</Text>
    },
    {
      title: 'ID Bài Tập',
      dataIndex: 'exerciseId',
      width: 100,
      align: 'center',
      render: (text) => <Text type="secondary" strong>#{text}</Text>
    },
    {
      title: 'Ảnh Nộp (Firebase)',
      dataIndex: 'imageUrl',
      width: 150,
      align: 'center',
      render: (url) => (
        <Image 
            src={url} 
            alt="Submission" 
            style={{ maxHeight: 80, maxWidth: 120, objectFit: 'contain', borderRadius: 6, border: '1px solid #f0f0f0' }} 
            fallback="https://via.placeholder.com/120x80?text=L%E1%BB%97i+%E1%BA%A3nh"
        />
      )
    },
    {
      title: 'Kết quả AI',
      dataIndex: 'isValid',
      width: 150,
      align: 'center',
      render: (isValid) => (
        isValid 
          ? <Tag icon={<CheckCircleOutlined />} color="success">Hợp lệ</Tag>
          : <Tag icon={<CloseCircleOutlined />} color="error">Không hợp lệ</Tag>
      )
    },
    {
      title: 'Lý do (AI Reason)',
      dataIndex: 'aiReason',
      render: (reason) => <Text type="secondary">{reason || '-'}</Text>
    },
    {
      title: 'Thời gian nộp',
      dataIndex: 'submittedAt',
      width: 180,
      align: 'right',
      render: (dateString) => {
          if (!dateString) return '-';
          return new Date(dateString).toLocaleString('vi-VN');
      }
    }
  ];

  return (
    <div style={{ padding: '24px', backgroundColor: '#f5f7fa', minHeight: '100vh' }}>
      <div style={{ marginBottom: '24px' }}>
        <Breadcrumb items={[{ title: 'Dashboard' }, { title: 'Lịch sử nộp bài AI' }]} style={{ fontSize: '13px' }}/>
        <Title level={3} style={{ margin: '8px 0 0 0' }}>Quản lý Bài Nộp Viết Tay</Title>
      </div>
      
      <Card 
        variant="borderless" 
        style={{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}
        styles={{ body: { padding: '24px' } }}
      >
        <Table 
            columns={columns} 
            dataSource={submissions} 
            rowKey="id" 
            loading={loading}
            pagination={{ pageSize: 15 }}
            rowClassName={() => 'custom-table-row'}
        />
      </Card>
    </div>
  );
};

export default SubmissionPage;
