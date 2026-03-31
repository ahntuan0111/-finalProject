import React, { useEffect, useState } from 'react';
import { Table, Tag, Space, Button, Popconfirm, message, Tooltip } from 'antd';
import { DeleteOutlined, EyeOutlined } from '@ant-design/icons';
import userApi from '../../api/userApi';
import StudentProfileDrawer from './StudentProfileDrawer';

const UserList = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  
  // State cho Drawer hồ sơ
  const [isDrawerVisible, setIsDrawerVisible] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState(null);

  // Hàm gọi API lấy danh sách
  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await userApi.getAll();
      if (response.success) {
        setUsers(response.data);
      }
    } catch (error) {
      message.error("Không thể tải danh sách người dùng");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleViewProfile = (id) => {
    setSelectedUserId(id);
    setIsDrawerVisible(true);
  };

  const closeDrawer = () => {
    setIsDrawerVisible(false);
    setSelectedUserId(null);
  };

  // Hàm xử lý xóa
  const handleDelete = async (id) => {
    try {
      await userApi.delete(id);
      message.success("Xóa người dùng thành công");
      fetchUsers();
    } catch (error) {
      message.error("Xóa thất bại");
    }
  };

  // Cấu hình cột cho bảng Ant Design
  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 70,
    },
    {
      title: 'Username',
      dataIndex: 'username',
      key: 'username',
      render: (text, record) => (
        <a onClick={() => handleViewProfile(record.id)}>
          <strong>{text}</strong>
        </a>
      ),
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: 'Vai trò',
      dataIndex: 'role',
      key: 'role',
      render: (role) => {
        let color = role === 'ADMIN' ? 'volcano' : 'geekblue';
        return (
          <Tag color={color} key={role}>
            {role.toUpperCase()}
          </Tag>
        );
      },
    },
    {
      title: 'Ngày tạo',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date) => new Date(date).toLocaleDateString('vi-VN'),
    },
    {
      title: 'Hành động',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <Tooltip title="Xem chi tiết hồ sơ">
            <Button 
              type="primary" 
              ghost 
              icon={<EyeOutlined />} 
              size="small"
              onClick={() => handleViewProfile(record.id)}
            >
              Chi tiết
            </Button>
          </Tooltip>

          <Popconfirm
            title="Xóa người dùng?"
            description="Hành động này không thể hoàn tác."
            onConfirm={() => handleDelete(record.id)}
            okText="Xóa"
            cancelText="Hủy"
            disabled={record.role === 'ADMIN'}
          >
            <Button 
              danger 
              icon={<DeleteOutlined />} 
              size="small"
              disabled={record.role === 'ADMIN'}
            >
              Xóa
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px', backgroundColor: '#fff', borderRadius: '8px' }}>
      <h2 style={{ marginBottom: 24 }}>Danh sách người dùng</h2>
      <Table 
        columns={columns} 
        dataSource={users} 
        rowKey="id"
        loading={loading}
        pagination={{ pageSize: 10 }}
      />

      {/* Drawer hiển thị chi tiết */}
      <StudentProfileDrawer 
        visible={isDrawerVisible}
        userId={selectedUserId}
        onClose={closeDrawer}
      />
    </div>
  );
};

export default UserList;