import React, { useState, useEffect } from 'react';
import { Table, Button, Popconfirm, message, notification, Card, Space, Tag, Badge } from 'antd';
import { PlusOutlined, DeleteOutlined, EditOutlined, EyeOutlined } from '@ant-design/icons';
import { quizApi } from '../../api/quizApi';
import QuizCreateModal from './QuizCreateModal';
import QuestionDetailModal from './QuestionDetailModal';
import QuizEditModal from './QuizEditModal'; // IMPORT MODAL SỬA MỚI TẠO

const QuizManagement = () => {
    const [quizzes, setQuizzes] = useState([]);
    const [loading, setLoading] = useState(false);
    
    // States cho Modal tạo mới
    const [isModalVisible, setIsModalVisible] = useState(false);
    
    // States cho Modal xem chi tiết câu hỏi
    const [viewQuestionVisible, setViewQuestionVisible] = useState(false);
    const [selectedQuestion, setSelectedQuestion] = useState(null);

    // States cho Modal sửa Quiz
    const [isEditModalVisible, setIsEditModalVisible] = useState(false);
    const [editingQuiz, setEditingQuiz] = useState(null);

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
                fetchQuizzes();
            }
        } catch (error) {
            message.error('Lỗi khi xóa: ' + (error.response?.data?.message || error.message));
        }
    };

    // Hàm xử lý mở Modal xem câu hỏi
    const handleViewQuestion = (question) => {
        setSelectedQuestion(question);
        setViewQuestionVisible(true);
    };

    // Hàm xử lý mở Modal sửa bài Quiz
    const handleEditQuiz = (quiz) => {
        setEditingQuiz(quiz);
        setIsEditModalVisible(true);
    };

    // --- BẢNG CHÍNH (QUẢN LÝ QUIZ) ---
    const columns = [
        { title: 'ID', dataIndex: 'id', key: 'id', width: 80, align: 'center' },
        { title: 'Mã Quiz', dataIndex: 'code', key: 'code', render: text => <Tag color="blue">{text}</Tag> },
        { title: 'Tiêu đề bài kiểm tra', dataIndex: 'title', key: 'title', render: text => <strong style={{ color: '#1890ff' }}>{text}</strong> },
        { title: 'Chương học', dataIndex: 'chapterTitle', key: 'chapterTitle' },
        { title: 'Loại bài', dataIndex: 'quizTypeName', key: 'quizTypeName' },
        {
            title: 'Hành động',
            key: 'action',
            align: 'center',
            render: (_, record) => (
                <Space>
                    {/* GẮN SỰ KIỆN MỞ MODAL SỬA */}
                    <Button type="primary" ghost icon={<EditOutlined />} onClick={() => handleEditQuiz(record)}>
                        Sửa
                    </Button>
                    <Popconfirm
                        title="Bạn có chắc chắn muốn xóa bài Quiz này không?"
                        onConfirm={() => handleDelete(record.id)}
                        okText="Xóa ngay"
                        cancelText="Hủy"
                        okButtonProps={{ danger: true }}
                    >
                        <Button danger icon={<DeleteOutlined />}>Xóa</Button>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    // --- BẢNG CON (CÂU HỎI TRONG ĐỀ) ---
    const expandedRowRender = (record) => {
        const questionColumns = [
            { 
                title: 'Câu số', 
                dataIndex: 'orderNo', 
                key: 'orderNo', 
                width: 100, 
                align: 'center',
                render: text => <Badge count={text} style={{ backgroundColor: '#52c41a' }} /> 
            },
            {
                title: 'Nội dung (Preview)',
                key: 'contentPreview',
                render: (_, question) => {
                    const firstTextContent = question.contents?.find(c => c.contentType === 'TEXT');
                    if (firstTextContent) {
                        const rawText = firstTextContent.contentValue.replace(/<[^>]+>/g, '');
                        return rawText.length > 80 ? rawText.substring(0, 80) + '...' : rawText;
                    }
                    return <span style={{ fontStyle: 'italic', color: 'gray' }}>[Chứa hình ảnh / Công thức]</span>;
                }
            },
            {
                title: 'Số đáp án',
                key: 'choicesCount',
                align: 'center',
                render: (_, question) => <Tag color="orange">{question.choices?.length || 0} đáp án</Tag>
            },
            {
                title: 'Hành động (Câu hỏi)',
                key: 'action',
                align: 'center',
                render: (_, question) => (
                    <Space size="middle">
                        <Button size="small" type="link" icon={<EyeOutlined />} onClick={() => handleViewQuestion(question)}>
                            Xem chi tiết
                        </Button>
                        {/* Hướng dẫn người dùng dùng nút Sửa của bài Quiz chính */}
                        <Button size="small" type="link" icon={<EditOutlined />} onClick={() => message.info('Vui lòng bấm Sửa ở dòng Bài kiểm tra phía trên để chỉnh sửa câu hỏi này!')}>
                            Sửa câu
                        </Button>
                        <Button size="small" type="link" danger icon={<DeleteOutlined />} onClick={() => message.error('Vui lòng bấm Sửa ở dòng Bài kiểm tra phía trên để xóa câu hỏi này!')}>
                            Xóa
                        </Button>
                    </Space>
                ),
            },
        ];

        return (
            <Card size="small" title={<span style={{fontWeight: 'bold', color: '#595959'}}>Danh sách câu hỏi của đề: {record.code}</span>} style={{ margin: '10px 30px', backgroundColor: '#f0f2f5' }}>
                <Table 
                    columns={questionColumns} 
                    dataSource={record.questions || []} 
                    pagination={false} 
                    rowKey="id" 
                    size="small"
                />
            </Card>
        );
    };

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
                expandable={{
                    expandedRowRender,
                    rowExpandable: record => record.questions && record.questions.length > 0,
                }}
            />

            {/* MODAL TẠO MỚI QUIZ */}
            <QuizCreateModal 
                visible={isModalVisible} 
                onCancel={() => setIsModalVisible(false)}
                onSuccess={() => {
                    setIsModalVisible(false);
                    notification.success({
                        message: 'Tạo Quiz Thành Công',
                        description: 'Bài kiểm tra và toàn bộ câu hỏi đã được lưu vào cơ sở dữ liệu.',
                        placement: 'topRight',
                    });
                    fetchQuizzes();
                }}
            />

            {/* MODAL CẬP NHẬT QUIZ */}
            <QuizEditModal
                visible={isEditModalVisible}
                quizData={editingQuiz}
                onCancel={() => {
                    setIsEditModalVisible(false);
                    setEditingQuiz(null);
                }}
                onSuccess={() => {
                    setIsEditModalVisible(false);
                    setEditingQuiz(null);
                    notification.success({
                        message: 'Cập Nhật Thành Công',
                        description: 'Bài kiểm tra đã được cập nhật lưu lại cơ sở dữ liệu.',
                        placement: 'topRight',
                    });
                    fetchQuizzes(); 
                }}
            />

            {/* MODAL XEM CHI TIẾT CÂU HỎI */}
            <QuestionDetailModal 
                visible={viewQuestionVisible}
                question={selectedQuestion}
                onCancel={() => setViewQuestionVisible(false)}
            />
        </Card>
    );
};

export default QuizManagement;