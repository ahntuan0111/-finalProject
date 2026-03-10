import React, { useState, useEffect } from 'react';
import { 
    Card, Table, Button, Input, Select, Tag, Popconfirm, 
    Modal, Form, Upload, Space, Breadcrumb, Row, Col, message, Typography 
} from 'antd';
import { 
    PlusOutlined, EditOutlined, DeleteOutlined, 
    FilePdfOutlined, InboxOutlined, SearchOutlined, FilterOutlined 
} from '@ant-design/icons';
import { documentApi } from '../../api/documentApi';

const { Title, Text } = Typography;
const { Dragger } = Upload;

const DocumentManagement = () => {
    const [documents, setDocuments] = useState([]);
    const [loading, setLoading] = useState(false);
    
    // --- State cho Bộ lọc (Filter) ---
    const [searchText, setSearchText] = useState('');
    const [filterSubject, setFilterSubject] = useState(null);
    const [filterGrade, setFilterGrade] = useState(null);

    // --- State cho Form (Modal) ---
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [editId, setEditId] = useState(null);
    const [fileList, setFileList] = useState([]); // Quản lý file của Antd Upload
    const [form] = Form.useForm();

    // Load dữ liệu khi vào trang
    useEffect(() => {
        fetchDocuments();
    }, []);

    const fetchDocuments = async () => {
        try {
            setLoading(true);
            const res = await documentApi.getAll();
            if (res.success) {
                setDocuments(res.data);
            }
        } catch (error) {
            console.error("Lỗi khi tải dữ liệu:", error);
            message.error("Không thể tải dữ liệu từ server.");
        } finally {
            setLoading(false);
        }
    };

    // --- Xử lý Mở Modal ---
    const handleOpenAdd = () => {
        setIsEditing(false);
        setEditId(null);
        setFileList([]);
        form.resetFields();
        form.setFieldsValue({ 
            subjectCode: 'Toán học', 
            grade: 6, 
            examType: 'MID_TERM' 
        });
        setShowModal(true);
    };

    const handleOpenEdit = (doc) => {
        setIsEditing(true);
        setEditId(doc.id);
        setFileList([]); // Sửa thì không bắt buộc upload lại file
        form.setFieldsValue({
            title: doc.title,
            description: doc.description || '',
            subjectCode: doc.subjectCode,
            grade: doc.grade,
            examType: doc.examType
        });
        setShowModal(true);
    };

    // --- Xử lý Submit Form ---
    const handleSubmit = async (values) => {
        try {
            if (isEditing) {
                // UPDATE
                await documentApi.update(editId, values);
                message.success("Cập nhật tài liệu thành công!");
            } else {
                // CREATE
                if (fileList.length === 0) {
                    message.warning("Vui lòng kéo thả hoặc chọn file PDF!");
                    return;
                }
                const file = fileList[0].originFileObj;
                await documentApi.upload(
                    file, values.title, values.description, 
                    values.subjectCode, values.grade, values.examType
                );
                message.success("Upload tài liệu thành công!");
            }
            setShowModal(false);
            fetchDocuments(); 
        } catch (error) {
            console.error("Lỗi lưu dữ liệu:", error);
            message.error("Có lỗi xảy ra khi lưu dữ liệu.");
        }
    };

    // --- Xử lý Xóa ---
    const handleDelete = async (id) => {
        try {
            await documentApi.delete(id);
            message.success("Đã xóa tài liệu thành công!");
            fetchDocuments();
        } catch (error) {
            console.error("Lỗi xóa:", error);
            message.error("Lỗi khi xóa tài liệu.");
        }
    };

    // --- Lọc Dữ liệu (Filter) ---
    const filteredDocuments = documents.filter(doc => {
        const matchTitle = doc.title.toLowerCase().includes(searchText.toLowerCase());
        const matchSubject = filterSubject ? doc.subjectCode === filterSubject : true;
        const matchGrade = filterGrade ? doc.grade === filterGrade : true;
        return matchTitle && matchSubject && matchGrade;
    });

    // --- Cấu hình Upload File ---
    const uploadProps = {
        onRemove: (file) => {
            setFileList((prev) => prev.filter((item) => item.uid !== file.uid));
        },
        beforeUpload: (file) => {
            const isPDF = file.type === 'application/pdf';
            if (!isPDF) {
                message.error('Bạn chỉ có thể upload file định dạng PDF!');
                return Upload.LIST_IGNORE;
            }
            setFileList([file]); // Chỉ cho phép 1 file
            return false; // Ngăn Antd tự động upload (để submit chung với form)
        },
        fileList,
        accept: "application/pdf",
        maxCount: 1
    };

    // --- Cấu hình Cột Bảng ---
    const columns = [
        {
            title: 'ID',
            dataIndex: 'id',
            width: 70,
            render: (text) => <Text type="secondary">#{text}</Text>
        },
        {
            title: 'TIÊU ĐỀ',
            dataIndex: 'title',
            render: (text, record) => (
                <Space>
                    <FilePdfOutlined style={{ color: '#ff4d4f', fontSize: '18px' }} />
                    <a href={`http://localhost:8080${record.filePath}`} target="_blank" rel="noreferrer" style={{ color: '#1f1f1f', fontWeight: 500 }}>
                        {text}
                    </a>
                </Space>
            )
        },
        {
            title: 'MÔN HỌC',
            dataIndex: 'subjectCode',
            width: 150,
        },
        {
            title: 'KHỐI',
            dataIndex: 'grade',
            width: 100,
            render: (grade) => `Lớp ${grade}`
        },
        {
            title: 'LOẠI ĐỀ',
            dataIndex: 'examType',
            width: 130,
            render: (type) => {
                let color = 'default';
                let label = type;
                if (type === 'MID_TERM') { color = 'cyan'; label = 'GIỮA KỲ'; }
                else if (type === 'FINAL') { color = 'volcano'; label = 'CUỐI KỲ'; }
                else if (type === 'THEORY') { color = 'geekblue'; label = 'LÝ THUYẾT'; }
                
                return <Tag color={color} style={{ fontWeight: 'bold', borderRadius: '4px' }}>{label}</Tag>;
            }
        },
        {
            title: 'DUNG LƯỢNG',
            dataIndex: 'fileSize',
            width: 130,
            render: (size) => size ? `${(size / 1024 / 1024).toFixed(2)} MB` : 'N/A'
        },
        {
            title: 'THAO TÁC',
            key: 'action',
            width: 120,
            align: 'center',
            render: (_, record) => (
                <Space size="small">
                    <Button type="text" icon={<EditOutlined style={{ color: '#1677ff' }} />} onClick={() => handleOpenEdit(record)} />
                    <Popconfirm 
                        title="Xóa tài liệu này?" 
                        description="File PDF vật lý cũng sẽ bị xóa vĩnh viễn."
                        onConfirm={() => handleDelete(record.id)}
                        okText="Xóa"
                        cancelText="Hủy"
                        okButtonProps={{ danger: true }}
                    >
                        <Button type="text" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                </Space>
            )
        }
    ];

    return (
        <div style={{ padding: '24px', backgroundColor: '#f5f7fa', minHeight: '100vh' }}>
            
            {/* Header & Breadcrumb */}
            <div style={{ marginBottom: '24px' }}>
                <Breadcrumb items={[{ title: 'Trang chủ' }, { title: 'Quản lý Tài liệu' }]} style={{ fontSize: '13px' }}/>
            </div>

            {/* Bọc toàn bộ bằng Card */}
            <Card 
                title={<Title level={3} style={{ margin: 0 }}>Quản lý Tài liệu (PDF)</Title>}
                variant="borderless"
                style={{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}
                extra={
                    <Button type="primary" icon={<PlusOutlined />} onClick={handleOpenAdd} size="large" style={{ borderRadius: '6px' }}>
                        Thêm tài liệu mới
                    </Button>
                }
            >
                {/* --- Khu vực Toolbar & Filter --- */}
                <div style={{ marginBottom: 20, padding: '16px', background: '#fafafa', borderRadius: '8px', border: '1px solid #f0f0f0' }}>
                    <Row gutter={[16, 16]} align="middle">
                        <Col xs={24} md={8}>
                            <Input 
                                placeholder="Tìm kiếm theo tên tài liệu..." 
                                prefix={<SearchOutlined />} 
                                value={searchText}
                                onChange={(e) => setSearchText(e.target.value)}
                                size="large"
                                allowClear
                            />
                        </Col>
                        <Col xs={12} md={5}>
                            <Select 
                                style={{ width: '100%' }} 
                                placeholder="Chọn Môn học"
                                size="large"
                                allowClear
                                value={filterSubject}
                                onChange={setFilterSubject}
                            >
                                <Select.Option value="Toán học">Toán học</Select.Option>
                                <Select.Option value="Khoa học Tự nhiên">Khoa học Tự nhiên</Select.Option>
                            </Select>
                        </Col>
                        <Col xs={12} md={5}>
                            <Select 
                                style={{ width: '100%' }} 
                                placeholder="Chọn Khối lớp"
                                size="large"
                                allowClear
                                value={filterGrade}
                                onChange={setFilterGrade}
                            >
                                {[6, 7, 8, 9, 10, 11, 12].map(g => (
                                    <Select.Option key={g} value={g}>Lớp {g}</Select.Option>
                                ))}
                            </Select>
                        </Col>
                        <Col xs={24} md={6} style={{ textAlign: 'right' }}>
                            <Button type="text" icon={<FilterOutlined />}>Bộ lọc nâng cao</Button>
                        </Col>
                    </Row>
                </div>

                {/* --- Bảng Dữ liệu --- */}
                <Table 
                    columns={columns} 
                    dataSource={filteredDocuments} 
                    rowKey="id" 
                    loading={loading}
                    pagination={{ pageSize: 8, showTotal: (total, range) => `Hiển thị ${range[0]} - ${range[1]} trong tổng số ${total} tài liệu` }}
                    rowClassName={() => 'custom-table-row'}
                />
            </Card>

            {/* --- Modal Thêm / Sửa Tài liệu --- */}
            <Modal
                title={<Text strong style={{ fontSize: '18px' }}>{isEditing ? "Cập nhật tài liệu" : "Thêm tài liệu mới"}</Text>}
                open={showModal}
                onCancel={() => setShowModal(false)}
                onOk={() => form.submit()}
                okText={isEditing ? "Lưu thay đổi" : "Tải lên"}
                cancelText="Hủy bỏ"
                width={600}
                centered
                destroyOnClose
            >
                <Form 
                    form={form} 
                    layout="vertical" 
                    onFinish={handleSubmit}
                    style={{ marginTop: 24 }}
                >
                    <Form.Item 
                        name="title" 
                        label={<Text strong>Tên tài liệu</Text>} 
                        rules={[{ required: true, message: 'Vui lòng nhập tên tài liệu!' }]}
                    >
                        <Input size="large" placeholder="VD: Đề thi thử giữa kỳ 1 Toán 6" />
                    </Form.Item>

                    <Row gutter={16}>
                        <Col span={12}>
                            <Form.Item 
                                name="subjectCode" 
                                label={<Text strong>Môn học</Text>} 
                                rules={[{ required: true }]}
                            >
                                <Select size="large" placeholder="Chọn môn học">
                                    <Select.Option value="Toán học">Toán học</Select.Option>
                                    <Select.Option value="Khoa học Tự nhiên">Khoa học Tự nhiên</Select.Option>
                                </Select>
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item 
                                name="grade" 
                                label={<Text strong>Khối lớp</Text>} 
                                rules={[{ required: true }]}
                            >
                                <Select size="large" placeholder="Chọn khối">
                                    {[6, 7, 8, 9, 10, 11, 12].map(g => (
                                        <Select.Option key={g} value={g}>Lớp {g}</Select.Option>
                                    ))}
                                </Select>
                            </Form.Item>
                        </Col>
                    </Row>

                    <Form.Item 
                        name="examType" 
                        label={<Text strong>Loại đề</Text>} 
                        rules={[{ required: true }]}
                    >
                        <Select size="large" placeholder="Chọn loại đề">
                            <Select.Option value="MID_TERM">Giữa kỳ</Select.Option>
                            <Select.Option value="FINAL">Cuối kỳ</Select.Option>
                            <Select.Option value="THEORY">Chuyên đề / Lý thuyết</Select.Option>
                        </Select>
                    </Form.Item>

                    <Form.Item name="description" label={<Text strong>Mô tả (Không bắt buộc)</Text>}>
                        <Input.TextArea rows={2} placeholder="Nhập ghi chú ngắn về tài liệu này..." />
                    </Form.Item>

                    {/* Chỉ bắt buộc upload nếu là Thêm mới. Nếu Sửa thì hiện thông báo */}
                    {!isEditing ? (
                        <Form.Item label={<Text strong>File tài liệu</Text>} required>
                            <Dragger {...uploadProps}>
                                <p className="ant-upload-drag-icon">
                                    <InboxOutlined style={{ color: '#1677ff' }} />
                                </p>
                                <p className="ant-upload-text" style={{ fontWeight: 600 }}>Nhấp hoặc kéo thả file PDF vào đây để tải lên</p>
                                <p className="ant-upload-hint">
                                    Hỗ trợ duy nhất định dạng .pdf. Dung lượng tối đa 20MB.
                                </p>
                            </Dragger>
                        </Form.Item>
                    ) : (
                        <div style={{ padding: '12px', background: '#e6f4ff', borderRadius: '6px', border: '1px solid #91caff' }}>
                            <Text type="secondary">
                                Đang ở chế độ chỉnh sửa thông tin. Bạn không cần tải lại file trừ khi muốn thay thế file hiện tại. (Tính năng cập nhật file đang phát triển).
                            </Text>
                        </div>
                    )}
                </Form>
            </Modal>

        </div>
    );
};

export default DocumentManagement;