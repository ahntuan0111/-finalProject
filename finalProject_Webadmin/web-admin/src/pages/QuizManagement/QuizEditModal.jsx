import React, { useState, useEffect } from 'react';
import { Modal, Form, Input, Button, Space, Card, Select, Switch, message, Divider } from 'antd';
import { PlusOutlined, MinusCircleOutlined } from '@ant-design/icons';
import { quizApi } from '../../api/quizApi';

import MathEditor from '../../components/editor/MathEditor'; 

const { Option } = Select;
const { TextArea } = Input;

const QuizEditModal = ({ visible, onCancel, onSuccess, quizData }) => {
    const [form] = Form.useForm();
    const [loading, setLoading] = useState(false);
    
    const [quizTypes, setQuizTypes] = useState([]);
    const [chapters, setChapters] = useState([]);

    // Đổ dữ liệu cũ vào Form khi mở Modal
    useEffect(() => {
        if (visible) {
            fetchQuizTypes();
            fetchChapters();
            
            // Nếu có quizData được truyền vào, đổ vào form
            if (quizData) {
                form.setFieldsValue({
                    ...quizData,
                    // Đảm bảo data map đúng với cấu trúc Form
                    questions: quizData.questions || []
                });
            }
        } else {
            form.resetFields();
        }
    }, [visible, quizData, form]);

    const fetchQuizTypes = async () => {
        try {
            const res = await quizApi.getTypes();
            if (res.success) setQuizTypes(res.data);
        } catch (error) {}
    };

    const fetchChapters = async () => {
        try {
            const res = await quizApi.getChapters();
            if (res.success) setChapters(res.data);
        } catch (error) {}
    };

    const handleOk = async () => {
        try {
            const values = await form.validateFields();
            setLoading(true);

            // Giữ nguyên các ID để Backend biết đường update
            const formattedValues = {
                ...values,
                questions: values.questions?.map((q, qIndex) => ({
                    ...q,
                    id: q.id, // QUAN TRỌNG: Giữ ID câu hỏi
                    orderNo: qIndex + 1,
                    contents: q.contents?.map((c, cIndex) => ({
                        ...c,
                        id: c.id, // QUAN TRỌNG: Giữ ID nội dung
                        contentOrder: cIndex + 1
                    })),
                    choices: q.choices?.map(ch => ({
                        ...ch,
                        id: ch.id, // QUAN TRỌNG: Giữ ID đáp án
                        contentType: ch.contentType || 'TEXT',
                    }))
                }))
            };

            // Gọi API Update (truyền ID của Quiz)
            const res = await quizApi.update(quizData.id, formattedValues);
            if (res.success) {
                message.success('Cập nhật bài trắc nghiệm thành công!');
                form.resetFields();
                onSuccess();
            }
        } catch (error) {
            if (error.response) message.error('Lỗi từ server: ' + error.response.data.message);
            else if (error.errorFields) message.error('Vui lòng điền đầy đủ các thông tin bắt buộc!');
            else message.error('Có lỗi xảy ra: ' + error.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal
            title={<span style={{ fontSize: '18px', fontWeight: 'bold', color: '#fa8c16' }}>Sửa Bài Trắc Nghiệm: {quizData?.code}</span>}
            open={visible}
            onOk={handleOk}
            onCancel={onCancel}
            confirmLoading={loading}
            width={1100} 
            okText="Cập nhật"
            cancelText="Hủy bỏ"
            maskClosable={false}
            style={{ top: 20 }}
        >
            <Form form={form} layout="vertical" name="quiz_edit_form">
                <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1.5fr 1.5fr', gap: '16px' }}>
                    <Form.Item name="title" label="Tiêu đề bài kiểm tra" rules={[{ required: true }]}>
                        <Input placeholder="VD: Bài kiểm tra 15 phút Chương 1" />
                    </Form.Item>
                    
                    <Form.Item name="code" label="Mã Quiz (Duy nhất)" rules={[{ required: true }]}>
                        <Input placeholder="VD: TOAN_10_01" />
                    </Form.Item>
                    
                    <Form.Item name="chapterId" label="Thuộc Chương học" rules={[{ required: true }]}>
                        <Select placeholder="Chọn chương học..." showSearch optionFilterProp="children">
                            {chapters.map(chapter => (
                                <Option key={chapter.id} value={chapter.id}>{chapter.title || chapter.name}</Option>
                            ))}
                        </Select>
                    </Form.Item>

                    <Form.Item name="quizTypeId" label="Loại Bài Kiểm Tra" rules={[{ required: true }]}>
                        <Select placeholder="Chọn loại bài...">
                            {quizTypes.map(type => (
                                <Option key={type.id} value={type.id}>{type.name}</Option>
                            ))}
                        </Select>
                    </Form.Item>
                </div>

                <Divider style={{ borderColor: '#fa8c16' }}>CHỈNH SỬA DANH SÁCH CÂU HỎI</Divider>

                <Form.List name="questions">
                    {(questionFields, { add: addQuestion, remove: removeQuestion }) => (
                        <div style={{ display: 'flex', flexDirection: 'column', rowGap: 24 }}>
                            {questionFields.map((qField, index) => (
                                <Card
                                    size="small"
                                    key={qField.key}
                                    title={<span style={{ fontSize: '16px', fontWeight: 'bold', color: '#fa8c16' }}>Câu hỏi {index + 1}</span>}
                                    extra={<Button danger type="dashed" onClick={() => removeQuestion(qField.name)} icon={<MinusCircleOutlined />}>Xóa câu hỏi</Button>}
                                    style={{ backgroundColor: '#fffbe6', border: '1px solid #ffe58f' }}
                                >
                                    {/* TRƯỜNG ẨN: GIỮ ID CÂU HỎI */}
                                    <Form.Item name={[qField.name, 'id']} hidden><Input /></Form.Item>

                                    <Form.Item name={[qField.name, 'explanation']} label="Lời giải chi tiết">
                                        <MathEditor placeholder="Nhập lời giải thích chứa công thức toán học..." />
                                    </Form.Item>

                                    {/* NỘI DUNG ĐỀ BÀI */}
                                    <Card size="small" title="Đề bài" style={{ marginBottom: 16 }}>
                                        <Form.List name={[qField.name, 'contents']}>
                                            {(contentFields, { add: addContent, remove: removeContent }) => (
                                                <>
                                                    {contentFields.map(cField => (
                                                        <Space key={cField.key} style={{ display: 'flex', marginBottom: 8, alignItems: 'flex-start' }} align="baseline">
                                                            {/* TRƯỜNG ẨN: GIỮ ID NỘI DUNG */}
                                                            <Form.Item name={[cField.name, 'id']} hidden><Input /></Form.Item>
                                                            
                                                            <Form.Item name={[cField.name, 'contentType']} rules={[{ required: true }]} initialValue="TEXT">
                                                                <Select style={{ width: 110 }}>
                                                                    <Option value="TEXT">Văn bản</Option>
                                                                    <Option value="IMAGE">Hình ảnh</Option>
                                                                </Select>
                                                            </Form.Item>
                                                            
                                                            <Form.Item noStyle shouldUpdate={(prev, curr) => prev.questions?.[qField.name]?.contents?.[cField.name]?.contentType !== curr.questions?.[qField.name]?.contents?.[cField.name]?.contentType}>
                                                                {({ getFieldValue }) => {
                                                                    const type = getFieldValue(['questions', qField.name, 'contents', cField.name, 'contentType']) || 'TEXT';
                                                                    return type === 'TEXT' ? (
                                                                        <div style={{ width: '600px' }}>
                                                                            <Form.Item name={[cField.name, 'contentValue']} rules={[{ required: true }]}>
                                                                                <MathEditor placeholder="Nhập công thức/đề bài..." />
                                                                            </Form.Item>
                                                                        </div>
                                                                    ) : (
                                                                        <div style={{ width: '600px' }}>
                                                                            <Form.Item name={[cField.name, 'contentValue']} rules={[{ required: true }]}>
                                                                                <Input placeholder="Dán URL hình ảnh..." />
                                                                            </Form.Item>
                                                                        </div>
                                                                    );
                                                                }}
                                                            </Form.Item>
                                                            <MinusCircleOutlined onClick={() => removeContent(cField.name)} style={{ color: 'red', fontSize: '16px', marginTop: '10px' }} />
                                                        </Space>
                                                    ))}
                                                    <Button type="dashed" onClick={() => addContent()} icon={<PlusOutlined />}>Thêm dòng nội dung đề bài</Button>
                                                </>
                                            )}
                                        </Form.List>
                                    </Card>

                                    {/* CÁC ĐÁP ÁN */}
                                    <Card size="small" title="Các đáp án">
                                        <Form.List name={[qField.name, 'choices']}>
                                            {(choiceFields, { add: addChoice, remove: removeChoice }) => (
                                                <>
                                                    {choiceFields.map(chField => (
                                                        <Space key={chField.key} style={{ display: 'flex', marginBottom: 8, alignItems: 'flex-start' }} align="baseline">
                                                            {/* TRƯỜNG ẨN: GIỮ ID ĐÁP ÁN */}
                                                            <Form.Item name={[chField.name, 'id']} hidden><Input /></Form.Item>

                                                            <Form.Item name={[chField.name, 'contentType']} rules={[{ required: true }]} initialValue="TEXT">
                                                                <Select style={{ width: 110 }}>
                                                                    <Option value="TEXT">Văn bản</Option>
                                                                    <Option value="IMAGE">Hình ảnh</Option>
                                                                </Select>
                                                            </Form.Item>
                                                            
                                                            <Form.Item noStyle shouldUpdate={(prev, curr) => prev.questions?.[qField.name]?.choices?.[chField.name]?.contentType !== curr.questions?.[qField.name]?.choices?.[chField.name]?.contentType}>
                                                                {({ getFieldValue }) => {
                                                                    const type = getFieldValue(['questions', qField.name, 'choices', chField.name, 'contentType']) || 'TEXT';
                                                                    return type === 'TEXT' ? (
                                                                        <div style={{ width: '500px' }}>
                                                                            <Form.Item name={[chField.name, 'contentValue']} rules={[{ required: true }]}>
                                                                                <MathEditor placeholder="Nhập đáp án (Hỗ trợ công thức)..." />
                                                                            </Form.Item>
                                                                        </div>
                                                                    ) : (
                                                                        <div style={{ width: '500px' }}>
                                                                            <Form.Item name={[chField.name, 'contentValue']} rules={[{ required: true }]}>
                                                                                <Input placeholder="Dán URL ảnh đáp án..." />
                                                                            </Form.Item>
                                                                        </div>
                                                                    );
                                                                }}
                                                            </Form.Item>
                                                            
                                                            <Form.Item name={[chField.name, 'correct']} valuePropName="checked" initialValue={false}>
                                                                <Switch checkedChildren="Đúng" unCheckedChildren="Sai" />
                                                            </Form.Item>
                                                            
                                                            <MinusCircleOutlined onClick={() => removeChoice(chField.name)} style={{ color: 'red', fontSize: '16px', marginTop: '10px' }} />
                                                        </Space>
                                                    ))}
                                                    <Button type="dashed" onClick={() => addChoice()} icon={<PlusOutlined />}>Thêm đáp án</Button>
                                                </>
                                            )}
                                        </Form.List>
                                    </Card>
                                </Card>
                            ))}
                            <Button type="dashed" onClick={() => addQuestion()} block icon={<PlusOutlined />} style={{ height: 50, borderColor: '#fa8c16', color: '#fa8c16', fontWeight: 'bold' }}>
                                + THÊM CÂU HỎI MỚI
                            </Button>
                        </div>
                    )}
                </Form.List>
            </Form>
        </Modal>
    );
};

export default QuizEditModal;