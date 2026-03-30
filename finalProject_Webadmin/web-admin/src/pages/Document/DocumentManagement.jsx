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
    
    const [searchText, setSearchText] = useState('');
    const [filterSubject, setFilterSubject] = useState(null);
    const [filterGrade, setFilterGrade] = useState(null);

    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [editId, setEditId] = useState(null);
    const [fileList, setFileList] = useState([]); 
    const [form] = Form.useForm();

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
            message.error("Không thể tải dữ liệu từ server.");
        } finally {
            setLoading(false);
        }
    };

    const handleOpenAdd = () => {
        setIsEditing(false);
        setEditId(null);
        setFileList([]);
        form.resetFields();
        form.setFieldsValue({ 
            subjectCode: 'toan', 
            grade: 6, 
            examTypeCode: 'MID_TERM' 
        });
        setShowModal(true);
    };

    const handleOpenEdit = (doc) => {
        setIsEditing(true);
        setEditId(doc.id);
        setFileList([]); 
        
        let examCode = 'THEORY';
        // SỬA: Tương thích ngược lấy cả examType cũ
        const typeVi = doc.examTypeVi || doc.examType; 
        
        if (typeVi === 'Giữa kỳ' || typeVi === 'MID_TERM') examCode = 'MID_TERM';
        if (typeVi === 'Cuối kỳ' || typeVi === 'FINAL') examCode = 'FINAL';

        form.setFieldsValue({
            titleVi: doc.titleVi || doc.title, // SỬA: Tương thích ngược
            titleEn: doc.titleEn,
            descriptionVi: doc.descriptionVi || doc.description,
            descriptionEn: doc.descriptionEn,
            subjectCode: doc.subjectCode,
            grade: doc.grade,
            examTypeCode: examCode
        });
        setShowModal(true);
    };

    const handleSubmit = async (values) => {
        try {
            let examTypeVi = 'Chuyên đề / Lý thuyết';
            let examTypeEn = 'Theory';
            if (values.examTypeCode === 'MID_TERM') { examTypeVi = 'Giữa kỳ'; examTypeEn = 'Midterm'; }
            if (values.examTypeCode === 'FINAL') { examTypeVi = 'Cuối kỳ'; examTypeEn = 'Final'; }

            const payload = {
                ...values,
                examTypeVi,
                examTypeEn
            };

            if (isEditing) {
                await documentApi.update(editId, payload);
                message.success("Cập nhật tài liệu thành công!");
            } else {
                // CREATE (Gửi FormData)
                if (fileList.length === 0) {
                    message.warning("Vui lòng kéo thả hoặc chọn file PDF!");
                    return;
                }
                
                // SỬA LẠI: Lấy file gốc hoặc lấy chính nó nếu không có bọc ngoài
                const fileToUpload = fileList[0].originFileObj || fileList[0];
                
                await documentApi.upload(
                    fileToUpload, // Truyền biến mới vào đây
                    values.titleVi, values.titleEn, 
                    values.descriptionVi, values.descriptionEn, 
                    values.subjectCode, values.grade, 
                    examTypeVi, examTypeEn
                );
                message.success("Upload tài liệu thành công!");
            }
            setShowModal(false);
            fetchDocuments(); 
        } catch (error) {
            message.error("Có lỗi xảy ra khi lưu dữ liệu.");
        }
    };

    const handleDelete = async (id) => {
        try {
            await documentApi.delete(id);
            message.success("Đã xóa tài liệu thành công!");
            fetchDocuments();
        } catch (error) {
            message.error("Lỗi khi xóa tài liệu.");
        }
    };

    const filteredDocuments = documents.filter(doc => {
        // SỬA: Tương thích ngược cho chức năng tìm kiếm
        const docTitle = doc.titleVi || doc.title || '';
        const matchTitle = docTitle.toLowerCase().includes(searchText.toLowerCase());
        const matchSubject = filterSubject ? doc.subjectCode === filterSubject : true;
        const matchGrade = filterGrade ? doc.grade === filterGrade : true;
        return matchTitle && matchSubject && matchGrade;
    });

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
            setFileList([file]); 
            return false; 
        },
        fileList,
        accept: "application/pdf",
        maxCount: 1
    };

    // --- CẤU HÌNH CỘT BẢNG ---
    const columns = [
        {
            title: 'ID',
            dataIndex: 'id',
            width: 70,
            render: (text) => <Text type="secondary">#{text}</Text>
        },
        {
            title: 'TIÊU ĐỀ (VN)',
            key: 'titleVi', // Dùng key thay vì dataIndex
            render: (_, record) => {
                // SỬA: Lấy titleVi, nếu BE chưa cập nhật thì lấy title cũ
                const text = record.titleVi || record.title; 
                // filePath bây giờ là Firebase URL đầy đủ hoặc đường dẫn cũ
                const href = record.filePath && record.filePath.startsWith('http')
                    ? record.filePath 
                    : `http://localhost:8080${record.filePath}`;
                return (
                    <Space>
                        <FilePdfOutlined style={{ color: '#ff4d4f', fontSize: '18px' }} />
                        <a href={href} target="_blank" rel="noreferrer" style={{ color: '#1f1f1f', fontWeight: 500 }}>
                            {text || "Chưa có tên"}
                        </a>
                    </Space>
                );
            }
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
            key: 'examTypeVi', 
            width: 150,
            render: (_, record) => {
                // SỬA: Tương thích cả chữ "Giữa kỳ" và mã "MID_TERM" cũ
                const type = record.examTypeVi || record.examType; 
                
                let color = 'default';
                let label = type;

                if (type === 'Giữa kỳ' || type === 'MID_TERM') { color = 'cyan'; label = 'GIỮA KỲ'; }
                else if (type === 'Cuối kỳ' || type === 'FINAL') { color = 'volcano'; label = 'CUỐI KỲ'; }
                else if (type === 'Chuyên đề / Lý thuyết' || type === 'THEORY') { color = 'geekblue'; label = 'LÝ THUYẾT'; }
                
                return <Tag color={color} style={{ fontWeight: 'bold', borderRadius: '4px' }}>{label ? label.toUpperCase() : 'KHÔNG XÁC ĐỊNH'}</Tag>;
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
                        description="File PDF sẽ bị xóa vĩnh viễn khỏi Firebase Storage."
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
            
            <div style={{ marginBottom: '24px' }}>
                <Breadcrumb items={[{ title: 'Trang chủ' }, { title: 'Quản lý Tài liệu' }]} style={{ fontSize: '13px' }}/>
            </div>

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
                                <Select.Option value="toan">Toán học</Select.Option>
                                <Select.Option value="khtn">Khoa học Tự nhiên</Select.Option>
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
                                {[6, 7, 8, 9].map(g => (
                                    <Select.Option key={g} value={g}>Lớp {g}</Select.Option>
                                ))}
                            </Select>
                        </Col>
                        <Col xs={24} md={6} style={{ textAlign: 'right' }}>
                            <Button type="text" icon={<FilterOutlined />}>Bộ lọc nâng cao</Button>
                        </Col>
                    </Row>
                </div>

                <Table 
                    columns={columns} 
                    dataSource={filteredDocuments} 
                    rowKey="id" 
                    loading={loading}
                    pagination={{ pageSize: 8, showTotal: (total, range) => `Hiển thị ${range[0]} - ${range[1]} trong tổng số ${total} tài liệu` }}
                    rowClassName={() => 'custom-table-row'}
                />
            </Card>

            <Modal
                title={<Text strong style={{ fontSize: '18px' }}>{isEditing ? "Cập nhật tài liệu" : "Thêm tài liệu mới"}</Text>}
                open={showModal}
                onCancel={() => setShowModal(false)}
                onOk={() => form.submit()}
                okText={isEditing ? "Lưu thay đổi" : "Tải lên"}
                cancelText="Hủy bỏ"
                width={800}
                centered
                destroyOnHidden
            >
                <Form 
                    form={form} 
                    layout="vertical" 
                    onFinish={handleSubmit}
                    style={{ marginTop: 24 }}
                >
                    <Row gutter={16}>
                        <Col span={12}>
                            <Form.Item 
                                name="titleVi" 
                                label={<Text strong>Tên tài liệu (Tiếng Việt) <span style={{color:'red'}}>*</span></Text>} 
                                rules={[{ required: true, message: 'Vui lòng nhập tên tài liệu!' }]}
                            >
                                <Input size="large" placeholder="VD: Đề thi thử giữa kỳ 1 Toán 6" />
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item name="titleEn" label={<Text strong>Tên tài liệu (English)</Text>} >
                                <Input size="large" placeholder="Example: Midterm Mock Test" />
                            </Form.Item>
                        </Col>
                    </Row>

                    <Row gutter={16}>
                        <Col span={8}>
                            <Form.Item name="subjectCode" label={<Text strong>Môn học <span style={{color:'red'}}>*</span></Text>} rules={[{ required: true }]}>
                                <Select size="large" placeholder="Chọn môn học">
                                    <Select.Option value="toan">Toán học</Select.Option>
                                    <Select.Option value="khtn">Khoa học Tự nhiên</Select.Option>
                                </Select>
                            </Form.Item>
                        </Col>
                        <Col span={8}>
                            <Form.Item name="grade" label={<Text strong>Khối lớp <span style={{color:'red'}}>*</span></Text>} rules={[{ required: true }]}>
                                <Select size="large" placeholder="Chọn khối">
                                    {[6, 7, 8, 9].map(g => (
                                        <Select.Option key={g} value={g}>Lớp {g}</Select.Option>
                                    ))}
                                </Select>
                            </Form.Item>
                        </Col>
                        <Col span={8}>
                            <Form.Item name="examTypeCode" label={<Text strong>Loại đề <span style={{color:'red'}}>*</span></Text>} rules={[{ required: true }]}>
                                <Select size="large" placeholder="Chọn loại đề">
                                    <Select.Option value="MID_TERM">Giữa kỳ</Select.Option>
                                    <Select.Option value="FINAL">Cuối kỳ</Select.Option>
                                    <Select.Option value="THEORY">Chuyên đề / Lý thuyết</Select.Option>
                                </Select>
                            </Form.Item>
                        </Col>
                    </Row>

                    <Row gutter={16}>
                        <Col span={12}>
                            <Form.Item name="descriptionVi" label={<Text strong>Mô tả (Tiếng Việt)</Text>}>
                                <Input.TextArea rows={3} placeholder="Nhập ghi chú ngắn về tài liệu này..." />
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item name="descriptionEn" label={<Text strong>Mô tả (English)</Text>}>
                                <Input.TextArea rows={3} placeholder="Enter a short description..." />
                            </Form.Item>
                        </Col>
                    </Row>

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
                                Đang ở chế độ chỉnh sửa thông tin. Để bảo vệ dữ liệu, hệ thống tạm khóa tính năng cập nhật file PDF đính kèm.
                            </Text>
                        </div>
                    )}
                </Form>
            </Modal>
        </div>
    );
};

export default DocumentManagement;