import React, { useEffect, useState } from 'react';
import { Table, Tag, Space, Button, Popconfirm, message } from 'antd';
import { DeleteOutlined } from '@ant-design/icons';
import userApi from '../../api/userApi';

const UserList = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);

  // Hàm gọi API lấy danh sách
  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await userApi.getAll();
      // Giả sử API trả về: { success: true, data: [...] }
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

  // Hàm xử lý xóa (nếu có chức năng này)
  const handleDelete = async (id) => {
    try {
      await userApi.delete(id);
      message.success("Xóa người dùng thành công");
      fetchUsers(); // Load lại bảng
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
      render: (text) => <strong>{text}</strong>,
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
      dataIndex: 'createdAt', // Đảm bảo BE trả về trường này
      key: 'createdAt',
      render: (date) => new Date(date).toLocaleDateString('vi-VN'),
    },
    {
      title: 'Hành động',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <Popconfirm
            title="Xóa người dùng?"
            description="Hành động này không thể hoàn tác."
            onConfirm={() => handleDelete(record.id)}
            okText="Xóa"
            cancelText="Hủy"
          >
            <Button danger icon={<DeleteOutlined />} size="small">
              Xóa
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 20 }}>Danh sách người dùng</h2>
      <Table 
        columns={columns} 
        dataSource={users} 
        rowKey="id" // Quan trọng: chỉ định khóa chính
        loading={loading}
        pagination={{ pageSize: 10 }}
      />
    </div>
  );
};

export default UserList;