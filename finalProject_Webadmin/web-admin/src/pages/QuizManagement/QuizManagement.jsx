import React, { useState, useEffect } from 'react';
import { Table, Button, Popconfirm, message, Card, Space, Tag } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import { quizApi } from '../../api/quizApi';
import QuizCreateModal from './QuizCreateModal';

const QuizManagement = () => {
    const [quizzes, setQuizzes] = useState([]);
    const [loading, setLoading] = useState(false);
    const [isModalVisible, setIsModalVisible] = useState(false);

    // Load danh sách khi mở trang
    useEffect(() => {
        fetchQuizzes();
    }, []);

    const fetchQuizzes = async () => {
        setLoading(true);
        try {
            const res = await quizApi.getAll();
            if (res.success) {
                setQuizzes(res.data);
            }
        } catch (error) {
            message.error('Lỗi khi tải danh sách Quiz: ' + (error.response?.data?.message || error.message));
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        try {
            const res = await quizApi.delete(id);
            if (res.success) {
                message.success('Xóa bài Quiz thành công!');
                fetchQuizzes(); // Cập nhật lại bảng sau khi xóa
            }
        } catch (error) {
            message.error('Lỗi khi xóa: ' + (error.response?.data?.message || error.message));
        }
    };

    // Định nghĩa các cột cho Bảng
    const columns = [
        { title: 'ID', dataIndex: 'id', key: 'id', width: 80, align: 'center' },
        { title: 'Mã Quiz', dataIndex: 'code', key: 'code', render: text => <Tag color="blue">{text}</Tag> },
        { title: 'Tiêu đề bài kiểm tra', dataIndex: 'title', key: 'title', render: text => <strong style={{ color: '#1890ff' }}>{text}</strong> },
        
        // Cột Chương: Trỏ vào thuộc tính 'title' (hoặc 'name') của đối tượng chapter trả về từ BE
        { title: 'Chương học', dataIndex: ['chapter', 'title'], key: 'chapterTitle' },
        
        // Cột Loại Quiz: Trỏ vào thuộc tính 'name' của đối tượng quizType trả về từ BE
        { title: 'Loại bài', dataIndex: ['quizType', 'name'], key: 'quizTypeName' },
        
        {
            title: 'Hành động',
            key: 'action',
            align: 'center',
            render: (_, record) => (
                <Popconfirm
                    title="Bạn có chắc chắn muốn xóa bài Quiz này không?"
                    description="Toàn bộ câu hỏi và đáp án bên trong cũng sẽ bị xóa vĩnh viễn."
                    onConfirm={() => handleDelete(record.id)}
                    okText="Xóa ngay"
                    cancelText="Hủy"
                    okButtonProps={{ danger: true }}
                >
                    <Button danger icon={<DeleteOutlined />}>Xóa</Button>
                </Popconfirm>
            ),
        },
    ];

    return (
        <Card title="Quản lý Bài trắc nghiệm (Quiz)" bordered={false} style={{ margin: '16px' }}>
            <Space style={{ marginBottom: 16 }}>
                <Button 
                    type="primary" 
                    icon={<PlusOutlined />} 
                    onClick={() => setIsModalVisible(true)}
                    style={{ backgroundColor: '#52c41a' }}
                >
                    Thêm Quiz mới
                </Button>
            </Space>

            <Table 
                columns={columns} 
                dataSource={quizzes} 
                rowKey="id" 
                loading={loading}
                bordered
                pagination={{ pageSize: 10 }}
            />

            {/* Modal Form Thêm Mới */}
            <QuizCreateModal 
                visible={isModalVisible} 
                onCancel={() => setIsModalVisible(false)}
                onSuccess={() => {
                    setIsModalVisible(false);
                    fetchQuizzes();
                }}
            />
        </Card>
    );
};

export default QuizManagement;