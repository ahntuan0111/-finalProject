import React, { useState, useEffect } from 'react';
import { Modal, Form, Input, Button, Space, Card, Select, Switch, message, Divider } from 'antd';
import { PlusOutlined, MinusCircleOutlined } from '@ant-design/icons';
import { quizApi } from '../../api/quizApi';

// IMPORT MathEditor
import MathEditor from '../../components/editor/MathEditor'; 

const { Option } = Select;
const { TextArea } = Input; // Đã thêm lại TextArea vì đang bị thiếu ở bản của bạn

const QuizCreateModal = ({ visible, onCancel, onSuccess }) => {
    const [form] = Form.useForm();
    const [loading, setLoading] = useState(false);
    
    const [quizTypes, setQuizTypes] = useState([]);
    const [chapters, setChapters] = useState([]);

    useEffect(() => {
        if (visible) {
            fetchQuizTypes();
            fetchChapters();
        } else {
            form.resetFields();
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
            const values = await form.validateFields();
            setLoading(true);

            const formattedValues = {
                ...values,
                questions: values.questions?.map((q, qIndex) => ({
                    ...q,
                    orderNo: qIndex + 1,
                    contents: q.contents?.map((c, cIndex) => ({
                        ...c,
                        contentOrder: cIndex + 1
                    })),
                    choices: q.choices?.map(ch => ({
                        ...ch,
                        contentType: ch.contentType || 'TEXT',
                    }))
                }))
            };

            const res = await quizApi.create(formattedValues);
            if (res.success) {
                message.success('Tạo bài trắc nghiệm thành công!');
                form.resetFields();
                onSuccess();
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
            width={1100} 
            okText="Lưu bài Quiz"
            cancelText="Hủy bỏ"
            maskClosable={false}
            style={{ top: 20 }}
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
                    
                    <Form.Item name="chapterId" label="Thuộc Chương học" rules={[{ required: true, message: 'Chọn Chương học!' }]}>
                        <Select placeholder="Chọn chương học..." showSearch optionFilterProp="children">
                            {chapters.map(chapter => (
                                <Option key={chapter.id} value={chapter.id}>
                                    {chapter.title || chapter.name} 
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>

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

                {/* 2. DANH SÁCH CÂU HỎI */}
                <Form.List name="questions">
                    {(questionFields, { add: addQuestion, remove: removeQuestion }) => (
                        <div style={{ display: 'flex', flexDirection: 'column', rowGap: 24 }}>
                            
                            {questionFields.map((qField, index) => (
                                <Card
                                    size="small"
                                    key={qField.key}
                                    title={<span style={{ fontSize: '16px', fontWeight: 'bold', color: '#1890ff' }}>Câu hỏi {index + 1}</span>}
                                    extra={<Button danger type="dashed" onClick={() => removeQuestion(qField.name)} icon={<MinusCircleOutlined />}>Xóa câu hỏi</Button>}
                                    style={{ backgroundColor: '#f9f9f9', border: '1px solid #d9d9d9' }}
                                >
                                    {/* SỬ DỤNG MATH EDITOR CHO LỜI GIẢI THÍCH */}
                                    <Form.Item name={[qField.name, 'explanation']} label="Lời giải chi tiết (Hiển thị sau khi nộp bài)">
                                        <MathEditor placeholder="Nhập lời giải thích chứa công thức toán học..." />
                                    </Form.Item>

                                    {/* --- 2.1 NỘI DUNG ĐỀ BÀI --- */}
                                    <Card size="small" title="Đề bài" style={{ marginBottom: 16, border: '1px dashed #d9d9d9' }}>
                                        <Form.List name={[qField.name, 'contents']}>
                                            {(contentFields, { add: addContent, remove: removeContent }) => (
                                                <>
                                                    {contentFields.map(cField => (
                                                        <Space key={cField.key} style={{ display: 'flex', marginBottom: 8, alignItems: 'flex-start' }} align="baseline">
                                                            <Form.Item name={[cField.name, 'contentType']} rules={[{ required: true }]} initialValue="TEXT">
                                                                <Select style={{ width: 110 }}>
                                                                    <Option value="TEXT">Văn bản</Option>
                                                                    <Option value="IMAGE">Hình ảnh</Option>
                                                                </Select>
                                                            </Form.Item>
                                                            
                                                            {/* RENDER ĐỘNG: Đã FIX lỗi bọc div bên ngoài Form.Item */}
                                                            <Form.Item
                                                                noStyle
                                                                shouldUpdate={(prevValues, currentValues) =>
                                                                    prevValues.questions?.[qField.name]?.contents?.[cField.name]?.contentType !==
                                                                    currentValues.questions?.[qField.name]?.contents?.[cField.name]?.contentType
                                                                }
                                                            >
                                                                {({ getFieldValue }) => {
                                                                    const type = getFieldValue(['questions', qField.name, 'contents', cField.name, 'contentType']) || 'TEXT';
                                                                    return type === 'TEXT' ? (
                                                                        <div style={{ width: '600px' }}>
                                                                            <Form.Item name={[cField.name, 'contentValue']} rules={[{ required: true, message: 'Nhập nội dung đề bài!' }]}>
                                                                                <MathEditor placeholder="Nhập công thức/đề bài..." />
                                                                            </Form.Item>
                                                                        </div>
                                                                    ) : (
                                                                        <div style={{ width: '600px' }}>
                                                                            <Form.Item name={[cField.name, 'contentValue']} rules={[{ required: true, message: 'Nhập URL ảnh!' }]}>
                                                                                <Input placeholder="Dán URL hình ảnh..." />
                                                                            </Form.Item>
                                                                        </div>
                                                                    );
                                                                }}
                                                            </Form.Item>
                                                            
                                                            <MinusCircleOutlined onClick={() => removeContent(cField.name)} style={{ color: 'red', cursor: 'pointer', fontSize: '16px', marginTop: '10px' }} />
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
                                                        <Space key={chField.key} style={{ display: 'flex', marginBottom: 8, alignItems: 'flex-start' }} align="baseline">
                                                            <Form.Item name={[chField.name, 'contentType']} rules={[{ required: true }]} initialValue="TEXT">
                                                                <Select style={{ width: 110 }}>
                                                                    <Option value="TEXT">Văn bản</Option>
                                                                    <Option value="IMAGE">Hình ảnh</Option>
                                                                </Select>
                                                            </Form.Item>
                                                            
                                                            {/* RENDER ĐỘNG CHO ĐÁP ÁN: Đã FIX lỗi bọc div bên ngoài Form.Item */}
                                                            <Form.Item
                                                                noStyle
                                                                shouldUpdate={(prevValues, currentValues) =>
                                                                    prevValues.questions?.[qField.name]?.choices?.[chField.name]?.contentType !==
                                                                    currentValues.questions?.[qField.name]?.choices?.[chField.name]?.contentType
                                                                }
                                                            >
                                                                {({ getFieldValue }) => {
                                                                    const type = getFieldValue(['questions', qField.name, 'choices', chField.name, 'contentType']) || 'TEXT';
                                                                    return type === 'TEXT' ? (
                                                                        <div style={{ width: '500px' }}>
                                                                            <Form.Item name={[chField.name, 'contentValue']} rules={[{ required: true, message: 'Nhập đáp án!' }]}>
                                                                                <MathEditor placeholder="Nhập đáp án (Hỗ trợ công thức)..." />
                                                                            </Form.Item>
                                                                        </div>
                                                                    ) : (
                                                                        <div style={{ width: '500px' }}>
                                                                            <Form.Item name={[chField.name, 'contentValue']} rules={[{ required: true, message: 'Nhập URL ảnh!' }]}>
                                                                                <Input placeholder="Dán URL ảnh đáp án..." />
                                                                            </Form.Item>
                                                                        </div>
                                                                    );
                                                                }}
                                                            </Form.Item>
                                                            
                                                            <Form.Item name={[chField.name, 'correct']} valuePropName="checked" initialValue={false}>
                                                                <Switch checkedChildren="Đúng" unCheckedChildren="Sai" />
                                                            </Form.Item>
                                                            
                                                            <MinusCircleOutlined onClick={() => removeChoice(chField.name)} style={{ color: 'red', cursor: 'pointer', fontSize: '16px', marginTop: '10px' }} />
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