import React, { useState, useEffect } from 'react';
import { Modal, Form, Input, Button, Space, Card, Select, Switch, message, Divider } from 'antd';
import { PlusOutlined, MinusCircleOutlined } from '@ant-design/icons';
import { quizApi } from '../../api/quizApi';

const { Option } = Select;
const { TextArea } = Input;

const QuizCreateModal = ({ visible, onCancel, onSuccess }) => {
    const [form] = Form.useForm();
    const [loading, setLoading] = useState(false);
    
    // State lưu dữ liệu cho 2 Dropdown
    const [quizTypes, setQuizTypes] = useState([]);
    const [chapters, setChapters] = useState([]);

    // Tự động load danh sách Chương và Loại Quiz khi mở Modal
    useEffect(() => {
        if (visible) {
            fetchQuizTypes();
            fetchChapters();
        } else {
            form.resetFields(); // Làm sạch form khi đóng Modal
        }
    }, [visible, form]);

    const fetchQuizTypes = async () => {
        try {
            const res = await quizApi.getTypes();
            if (res.success) setQuizTypes(res.data);
        } catch (error) {
            message.error('Không thể tải danh sách Loại bài kiểm tra!');
        }
    };

    const fetchChapters = async () => {
        try {
            const res = await quizApi.getChapters();
            if (res.success) setChapters(res.data);
        } catch (error) {
            message.error('Không thể tải danh sách Chương học!');
        }
    };

    const handleOk = async () => {
        try {
            // Validate tất cả các field yêu cầu nhập
            const values = await form.validateFields();
            setLoading(true);

            // XỬ LÝ DỮ LIỆU: Tự động gán orderNo và contentOrder trước khi gửi lên BE
            const formattedValues = {
                ...values,
                questions: values.questions?.map((q, qIndex) => ({
                    ...q,
                    orderNo: qIndex + 1, // Tự sinh thứ tự câu hỏi: 1, 2, 3...
                    contents: q.contents?.map((c, cIndex) => ({
                        ...c,
                        contentOrder: cIndex + 1 // Tự sinh thứ tự nội dung: 1, 2, 3...
                    })),
                    choices: q.choices?.map(ch => ({
                        ...ch,
                        contentType: ch.contentType || 'TEXT', // Mặc định là TEXT nếu chưa chọn
                    }))
                }))
            };

            // Gọi API tạo mới Quiz
            const res = await quizApi.create(formattedValues);
            if (res.success) {
                message.success('Tạo bài trắc nghiệm thành công!');
                form.resetFields();
                onSuccess(); // Đóng modal và refresh lại bảng
            }
        } catch (error) {
            if (error.response) {
                message.error('Lỗi từ server: ' + error.response.data.message);
            } else if (error.errorFields) {
                message.error('Vui lòng điền đầy đủ các thông tin bắt buộc (màu đỏ)!');
            } else {
                message.error('Có lỗi xảy ra: ' + error.message);
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal
            title={<span style={{ fontSize: '18px', fontWeight: 'bold' }}>Tạo Bài Trắc Nghiệm Mới</span>}
            open={visible}
            onOk={handleOk}
            onCancel={onCancel}
            confirmLoading={loading}
            width={1000} // Cố định chiều rộng để form dễ nhìn
            okText="Lưu bài Quiz"
            cancelText="Hủy bỏ"
            maskClosable={false} // Tránh click ra ngoài làm mất dữ liệu
            style={{ top: 20 }} // Đẩy modal lên cao một chút
        >
            <Form form={form} layout="vertical" name="quiz_create_form">
                
                {/* 1. THÔNG TIN CHUNG CỦA BÀI KIỂM TRA */}
                <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1.5fr 1.5fr', gap: '16px' }}>
                    <Form.Item name="title" label="Tiêu đề bài kiểm tra" rules={[{ required: true, message: 'Nhập tiêu đề!' }]}>
                        <Input placeholder="VD: Bài kiểm tra 15 phút Chương 1" />
                    </Form.Item>
                    
                    <Form.Item name="code" label="Mã Quiz (Duy nhất)" rules={[{ required: true, message: 'Nhập mã quiz!' }]}>
                        <Input placeholder="VD: TOAN_10_01" />
                    </Form.Item>
                    
                    {/* DROP-DOWN CHỌN CHƯƠNG HỌC */}
                    <Form.Item name="chapterId" label="Thuộc Chương học" rules={[{ required: true, message: 'Chọn Chương học!' }]}>
                        <Select 
                            placeholder="Chọn chương học..."
                            showSearch
                            optionFilterProp="children"
                        >
                            {chapters.map(chapter => (
                                // Ghi chú: Nếu Entity Chapter lấy tên là 'name' thì sửa chapter.title thành chapter.name
                                <Option key={chapter.id} value={chapter.id}>
                                    {chapter.title || chapter.name} 
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>

                    {/* DROP-DOWN CHỌN LOẠI QUIZ */}
                    <Form.Item name="quizTypeId" label="Loại Bài Kiểm Tra" rules={[{ required: true, message: 'Chọn loại Quiz!' }]}>
                        <Select placeholder="Chọn loại bài...">
                            {quizTypes.map(type => (
                                <Option key={type.id} value={type.id}>
                                    {type.name}
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>
                </div>

                <Divider style={{ borderColor: '#1890ff' }}>XÂY DỰNG DANH SÁCH CÂU HỎI</Divider>

                {/* 2. DANH SÁCH CÂU HỎI (DYNAMIC FORM LỒNG NHAU) */}
                <Form.List name="questions">
                    {(questionFields, { add: addQuestion, remove: removeQuestion }) => (
                        <div style={{ display: 'flex', flexDirection: 'column', rowGap: 24 }}>
                            
                            {/* MAP TỪNG CÂU HỎI */}
                            {questionFields.map((qField, index) => (
                                <Card
                                    size="small"
                                    key={qField.key}
                                    title={<span style={{ fontSize: '16px', fontWeight: 'bold', color: '#1890ff' }}>Câu hỏi {index + 1}</span>}
                                    extra={<Button danger type="dashed" onClick={() => removeQuestion(qField.name)} icon={<MinusCircleOutlined />}>Xóa câu hỏi</Button>}
                                    style={{ backgroundColor: '#f9f9f9', border: '1px solid #d9d9d9' }}
                                >
                                    {/* LỜI GIẢI THÍCH CHO CÂU HỎI NÀY */}
                                    <Form.Item name={[qField.name, 'explanation']} label="Lời giải chi tiết (Hiển thị sau khi nộp bài)">
                                        <TextArea rows={2} placeholder="Nhập lời giải thích (Tùy chọn)..." />
                                    </Form.Item>

                                    {/* --- 2.1 NỘI DUNG ĐỀ BÀI (Văn bản hoặc Hình ảnh) --- */}
                                    <Card size="small" title="Đề bài" style={{ marginBottom: 16, border: '1px dashed #d9d9d9' }}>
                                        <Form.List name={[qField.name, 'contents']}>
                                            {(contentFields, { add: addContent, remove: removeContent }) => (
                                                <>
                                                    {contentFields.map(cField => (
                                                        <Space key={cField.key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                                                            <Form.Item name={[cField.name, 'contentType']} rules={[{ required: true }]} initialValue="TEXT">
                                                                <Select style={{ width: 110 }}>
                                                                    <Option value="TEXT">Văn bản</Option>
                                                                    <Option value="IMAGE">Hình ảnh</Option>
                                                                </Select>
                                                            </Form.Item>
                                                            
                                                            <Form.Item name={[cField.name, 'contentValue']} rules={[{ required: true, message: 'Không được để trống!' }]}>
                                                                <Input style={{ width: 600 }} placeholder="Nhập đề bài hoặc dán URL hình ảnh..." />
                                                            </Form.Item>
                                                            
                                                            <MinusCircleOutlined onClick={() => removeContent(cField.name)} style={{ color: 'red', cursor: 'pointer', fontSize: '16px' }} />
                                                        </Space>
                                                    ))}
                                                    <Button type="dashed" onClick={() => addContent()} icon={<PlusOutlined />}>
                                                        Thêm dòng nội dung đề bài
                                                    </Button>
                                                </>
                                            )}
                                        </Form.List>
                                    </Card>

                                    {/* --- 2.2 CÁC LỰA CHỌN ĐÁP ÁN --- */}
                                    <Card size="small" title="Các đáp án" style={{ border: '1px dashed #d9d9d9' }}>
                                        <Form.List name={[qField.name, 'choices']}>
                                            {(choiceFields, { add: addChoice, remove: removeChoice }) => (
                                                <>
                                                    {choiceFields.map(chField => (
                                                        <Space key={chField.key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                                                            <Form.Item name={[chField.name, 'contentType']} rules={[{ required: true }]} initialValue="TEXT">
                                                                <Select style={{ width: 110 }}>
                                                                    <Option value="TEXT">Văn bản</Option>
                                                                    <Option value="IMAGE">Hình ảnh</Option>
                                                                </Select>
                                                            </Form.Item>
                                                            
                                                            <Form.Item name={[chField.name, 'contentValue']} rules={[{ required: true, message: 'Nhập đáp án!' }]}>
                                                                <Input style={{ width: 500 }} placeholder="Nhập nội dung đáp án (VD: 15 cm) hoặc dán URL ảnh..." />
                                                            </Form.Item>
                                                            
                                                            <Form.Item name={[chField.name, 'correct']} valuePropName="checked" initialValue={false}>
                                                                <Switch checkedChildren="Đúng" unCheckedChildren="Sai" />
                                                            </Form.Item>
                                                            
                                                            <MinusCircleOutlined onClick={() => removeChoice(chField.name)} style={{ color: 'red', cursor: 'pointer', fontSize: '16px' }} />
                                                        </Space>
                                                    ))}
                                                    <Button type="dashed" onClick={() => addChoice()} icon={<PlusOutlined />}>
                                                        Thêm đáp án
                                                    </Button>
                                                </>
                                            )}
                                        </Form.List>
                                    </Card>

                                </Card>
                            ))}

                            {/* NÚT THÊM CÂU HỎI LỚN */}
                            <Button 
                                type="dashed" 
                                onClick={() => addQuestion()} 
                                block 
                                icon={<PlusOutlined />} 
                                style={{ height: 50, borderColor: '#52c41a', color: '#52c41a', fontWeight: 'bold' }}
                            >
                                + THÊM CÂU HỎI MỚI VÀO BÀI KIỂM TRA
                            </Button>
                        </div>
                    )}
                </Form.List>
            </Form>
        </Modal>
    );
};

export default QuizCreateModal;