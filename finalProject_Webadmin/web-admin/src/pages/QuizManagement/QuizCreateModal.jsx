import React, { useState, useEffect } from 'react';
import { Modal, Form, Input, Button, Card, Select, Switch, message, Divider, Row, Col, Image, Upload } from 'antd';
import { PlusOutlined, MinusCircleOutlined, UploadOutlined } from '@ant-design/icons';
import { quizApi } from '../../api/quizApi';
import uploadApi from '../../api/uploadApi';
import MathEditor from '../../components/editor/MathEditor'; 

const { Option } = Select;

const getFullImageUrl = (path) => {
    if (!path) return '';
    if (path.startsWith('http')) return path;
    return `${import.meta.env.VITE_API_URL.replace('/api', '')}${path}`; 
};

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
        } catch (error) {}
    };

    const fetchChapters = async () => {
        try {
            const res = await quizApi.getChapters();
            if (res.success) setChapters(res.data);
        } catch (error) {}
    };

    const customRequest = async ({ file, onSuccess, onError }, qIndex, fieldName, cIndex, langType) => {
        try {
            const res = await uploadApi.uploadImage(file);
            if (res.success) {
                onSuccess("ok");
                const questions = form.getFieldValue('questions');
                questions[qIndex][fieldName][cIndex][langType] = res.data;
                form.setFieldsValue({ questions });
                message.success("Upload ảnh thành công!");
            }
        } catch (err) {
            onError(err);
            message.error("Upload thất bại");
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
            if (error.response) message.error('Lỗi từ server: ' + error.response.data.message);
            else if (error.errorFields) message.error('Vui lòng điền đầy đủ các thông tin bắt buộc (màu đỏ)!');
            else message.error('Có lỗi xảy ra: ' + error.message);
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
            width={1250} // Nới rộng Modal lên 1250px
            okText="Lưu bài Quiz"
            cancelText="Hủy bỏ"
            maskClosable={false}
            style={{ top: 20 }}
        >
            <Form form={form} layout="vertical" name="quiz_create_form">
                
                {/* THÔNG TIN CHUNG */}
                <Row gutter={16}>
                    <Col span={8}>
                        <Form.Item name="titleVi" label="Tiêu đề (Tiếng Việt)" rules={[{ required: true, message: 'Nhập tiêu đề!' }]}>
                            <Input placeholder="VD: Kiểm tra 15 phút" />
                        </Form.Item>
                    </Col>
                    <Col span={8}>
                        <Form.Item name="titleEn" label="Tiêu đề (English)">
                            <Input placeholder="VD: 15-minute Test" />
                        </Form.Item>
                    </Col>
                    <Col span={8}>
                        <Form.Item name="code" label="Mã Quiz (Duy nhất)" rules={[{ required: true, message: 'Nhập mã quiz!' }]}>
                            <Input placeholder="VD: TOAN_10_01" />
                        </Form.Item>
                    </Col>
                </Row>

                <Row gutter={16}>
                    <Col span={12}>
                        <Form.Item name="chapterId" label="Thuộc Chương học" rules={[{ required: true, message: 'Chọn Chương học!' }]}>
                            <Select placeholder="Chọn chương học..." showSearch optionFilterProp="children">
                                {chapters.map(chapter => (
                                    <Option key={chapter.id} value={chapter.id}>{chapter.titleVi || chapter.name}</Option>
                                ))}
                            </Select>
                        </Form.Item>
                    </Col>
                    <Col span={12}>
                        <Form.Item name="quizTypeId" label="Loại Bài Kiểm Tra" rules={[{ required: true, message: 'Chọn loại Quiz!' }]}>
                            <Select placeholder="Chọn loại bài...">
                                {quizTypes.map(type => (
                                    <Option key={type.id} value={type.id}>{type.name}</Option>
                                ))}
                            </Select>
                        </Form.Item>
                    </Col>
                </Row>

                <Divider style={{ borderColor: '#1890ff' }}>XÂY DỰNG DANH SÁCH CÂU HỎI</Divider>

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
                                    {/* LỜI GIẢI THÍCH 2 NGÔN NGỮ */}
                                    <Row gutter={16}>
                                        <Col span={12}>
                                            <Form.Item name={[qField.name, 'explanationVi']} label="Lời giải (Tiếng Việt)">
                                                <MathEditor placeholder="Nhập lời giải..." />
                                            </Form.Item>
                                        </Col>
                                        <Col span={12}>
                                            <Form.Item name={[qField.name, 'explanationEn']} label="Lời giải (English)">
                                                <MathEditor placeholder="Type explanation..." />
                                            </Form.Item>
                                        </Col>
                                    </Row>

                                    {/* NỘI DUNG ĐỀ BÀI */}
                                    <Card size="small" title="Nội dung Đề bài" style={{ marginBottom: 16, border: '1px dashed #d9d9d9' }}>
                                        <Form.List name={[qField.name, 'contents']}>
                                            {(contentFields, { add: addContent, remove: removeContent }) => (
                                                <>
                                                    {contentFields.map(cField => (
                                                        <div key={cField.key} style={{ display: 'flex', gap: '16px', marginBottom: '16px', alignItems: 'flex-start', width: '100%' }}>
                                                            {/* Dropdown Chọn Loại */}
                                                            <div style={{ width: '110px', flexShrink: 0 }}>
                                                                <Form.Item name={[cField.name, 'contentType']} rules={[{ required: true }]} initialValue="TEXT" style={{ margin: 0 }}>
                                                                    <Select onChange={() => {
                                                                        const questions = form.getFieldValue('questions');
                                                                        questions[qField.name].contents[cField.name].contentValueVi = '';
                                                                        questions[qField.name].contents[cField.name].contentValueEn = '';
                                                                        form.setFieldsValue({ questions });
                                                                    }}>
                                                                        <Option value="TEXT">Văn bản</Option>
                                                                        <Option value="IMAGE">Hình ảnh</Option>
                                                                    </Select>
                                                                </Form.Item>
                                                            </div>
                                                            
                                                            {/* Khu vực Nhập liệu chính tự động co giãn */}
                                                            <div style={{ flex: 1 }}>
                                                                <Form.Item noStyle shouldUpdate={(prev, curr) => prev.questions?.[qField.name]?.contents?.[cField.name]?.contentType !== curr.questions?.[qField.name]?.contents?.[cField.name]?.contentType}>
                                                                    {({ getFieldValue }) => {
                                                                        const type = getFieldValue(['questions', qField.name, 'contents', cField.name, 'contentType']) || 'TEXT';
                                                                        const valVi = getFieldValue(['questions', qField.name, 'contents', cField.name, 'contentValueVi']);
                                                                        const valEn = getFieldValue(['questions', qField.name, 'contents', cField.name, 'contentValueEn']);

                                                                        if (type === 'IMAGE') {
                                                                            return (
                                                                                <Row gutter={16}>
                                                                                    <Col span={12}>
                                                                                        <Form.Item name={[cField.name, 'contentValueVi']} rules={[{ required: true, message: 'Upload ảnh VN!' }]} style={{ margin: 0 }}>
                                                                                            <div style={{ border: '1px dashed #d9d9d9', padding: 10, textAlign: 'center', background: '#fff' }}>
                                                                                                {valVi ? (
                                                                                                    <div><Image src={getFullImageUrl(valVi)} height={80}/><br/><Button size="small" danger onClick={() => { const q = form.getFieldValue('questions'); q[qField.name].contents[cField.name].contentValueVi = ''; form.setFieldsValue({questions: q}); }}>Xóa</Button></div>
                                                                                                ) : (
                                                                                                    <Upload customRequest={(opts) => customRequest(opts, qField.name, 'contents', cField.name, 'contentValueVi')} showUploadList={false}><Button icon={<UploadOutlined/>}>Tải ảnh VN</Button></Upload>
                                                                                                )}
                                                                                            </div>
                                                                                        </Form.Item>
                                                                                    </Col>
                                                                                    <Col span={12}>
                                                                                        <Form.Item name={[cField.name, 'contentValueEn']} style={{ margin: 0 }}>
                                                                                            <div style={{ border: '1px dashed #d9d9d9', padding: 10, textAlign: 'center', background: '#fff' }}>
                                                                                                {valEn ? (
                                                                                                    <div><Image src={getFullImageUrl(valEn)} height={80}/><br/><Button size="small" danger onClick={() => { const q = form.getFieldValue('questions'); q[qField.name].contents[cField.name].contentValueEn = ''; form.setFieldsValue({questions: q}); }}>Xóa</Button></div>
                                                                                                ) : (
                                                                                                    <Upload customRequest={(opts) => customRequest(opts, qField.name, 'contents', cField.name, 'contentValueEn')} showUploadList={false}><Button icon={<UploadOutlined/>}>Tải ảnh EN</Button></Upload>
                                                                                                )}
                                                                                        </div>
                                                                                        </Form.Item>
                                                                                    </Col>
                                                                                </Row>
                                                                            );
                                                                        }
                                                                        return (
                                                                            <Row gutter={16}>
                                                                                <Col span={12}>
                                                                                    <Form.Item name={[cField.name, 'contentValueVi']} rules={[{ required: true, message: 'Nhập đề!' }]} style={{ margin: 0 }}>
                                                                                        <MathEditor placeholder="Đề bài VN..." />
                                                                                    </Form.Item>
                                                                                </Col>
                                                                                <Col span={12}>
                                                                                    <Form.Item name={[cField.name, 'contentValueEn']} style={{ margin: 0 }}>
                                                                                        <MathEditor placeholder="Question EN..." />
                                                                                    </Form.Item>
                                                                                </Col>
                                                                            </Row>
                                                                        );
                                                                    }}
                                                                </Form.Item>
                                                            </div>
                                                            
                                                            {/* Nút Xóa Mảng */}
                                                            <div style={{ width: '30px', flexShrink: 0, display: 'flex', justifyContent: 'center', paddingTop: '8px' }}>
                                                                <MinusCircleOutlined onClick={() => removeContent(cField.name)} style={{ color: 'red', cursor: 'pointer', fontSize: '18px' }} />
                                                            </div>
                                                        </div>
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
                                                        <div key={chField.key} style={{ display: 'flex', gap: '16px', marginBottom: '16px', alignItems: 'flex-start', width: '100%' }}>
                                                            
                                                            {/* Dropdown Chọn Loại */}
                                                            <div style={{ width: '110px', flexShrink: 0 }}>
                                                                <Form.Item name={[chField.name, 'contentType']} rules={[{ required: true }]} initialValue="TEXT" style={{ margin: 0 }}>
                                                                    <Select onChange={() => {
                                                                        const questions = form.getFieldValue('questions');
                                                                        questions[qField.name].choices[chField.name].contentValueVi = '';
                                                                        questions[qField.name].choices[chField.name].contentValueEn = '';
                                                                        form.setFieldsValue({ questions });
                                                                    }}>
                                                                        <Option value="TEXT">Văn bản</Option>
                                                                        <Option value="IMAGE">Hình ảnh</Option>
                                                                    </Select>
                                                                </Form.Item>
                                                            </div>
                                                            
                                                            {/* Khu vực Nhập liệu chính tự động co giãn */}
                                                            <div style={{ flex: 1 }}>
                                                                <Form.Item noStyle shouldUpdate={(prev, curr) => prev.questions?.[qField.name]?.choices?.[chField.name]?.contentType !== curr.questions?.[qField.name]?.choices?.[chField.name]?.contentType}>
                                                                    {({ getFieldValue }) => {
                                                                        const type = getFieldValue(['questions', qField.name, 'choices', chField.name, 'contentType']) || 'TEXT';
                                                                        const valVi = getFieldValue(['questions', qField.name, 'choices', chField.name, 'contentValueVi']);
                                                                        const valEn = getFieldValue(['questions', qField.name, 'choices', chField.name, 'contentValueEn']);

                                                                        if (type === 'IMAGE') {
                                                                            return (
                                                                                <Row gutter={16}>
                                                                                    <Col span={12}>
                                                                                        <Form.Item name={[chField.name, 'contentValueVi']} rules={[{ required: true, message: 'Upload!' }]} style={{ margin: 0 }}>
                                                                                            <div style={{ border: '1px dashed #d9d9d9', padding: 10, textAlign: 'center', background: '#fff' }}>
                                                                                                {valVi ? <div><Image src={getFullImageUrl(valVi)} height={60}/><br/><Button size="small" danger onClick={() => { const q = form.getFieldValue('questions'); q[qField.name].choices[chField.name].contentValueVi = ''; form.setFieldsValue({questions: q}); }}>Xóa</Button></div> : <Upload customRequest={(opts) => customRequest(opts, qField.name, 'choices', chField.name, 'contentValueVi')} showUploadList={false}><Button icon={<UploadOutlined/>}>Tải ảnh VN</Button></Upload>}
                                                                                            </div>
                                                                                        </Form.Item>
                                                                                    </Col>
                                                                                    <Col span={12}>
                                                                                        <Form.Item name={[chField.name, 'contentValueEn']} style={{ margin: 0 }}>
                                                                                            <div style={{ border: '1px dashed #d9d9d9', padding: 10, textAlign: 'center', background: '#fff' }}>
                                                                                                {valEn ? <div><Image src={getFullImageUrl(valEn)} height={60}/><br/><Button size="small" danger onClick={() => { const q = form.getFieldValue('questions'); q[qField.name].choices[chField.name].contentValueEn = ''; form.setFieldsValue({questions: q}); }}>Xóa</Button></div> : <Upload customRequest={(opts) => customRequest(opts, qField.name, 'choices', chField.name, 'contentValueEn')} showUploadList={false}><Button icon={<UploadOutlined/>}>Tải ảnh EN</Button></Upload>}
                                                                                            </div>
                                                                                        </Form.Item>
                                                                                    </Col>
                                                                                </Row>
                                                                            );
                                                                        }
                                                                        return (
                                                                            <Row gutter={16}>
                                                                                <Col span={12}>
                                                                                    <Form.Item name={[chField.name, 'contentValueVi']} rules={[{ required: true, message: 'Nhập đáp án!' }]} style={{ margin: 0 }}>
                                                                                        <MathEditor placeholder="Đáp án VN..." />
                                                                                    </Form.Item>
                                                                                </Col>
                                                                                <Col span={12}>
                                                                                    <Form.Item name={[chField.name, 'contentValueEn']} style={{ margin: 0 }}>
                                                                                        <MathEditor placeholder="Answer EN..." />
                                                                                    </Form.Item>
                                                                                </Col>
                                                                            </Row>
                                                                        );
                                                                    }}
                                                                </Form.Item>
                                                            </div>
                                                            
                                                            {/* Nút Switch Đúng Sai */}
                                                            <div style={{ width: '80px', flexShrink: 0 }}>
                                                                <Form.Item name={[chField.name, 'correct']} valuePropName="checked" initialValue={false} style={{ margin: 0 }}>
                                                                    <Switch checkedChildren="Đúng" unCheckedChildren="Sai" />
                                                                </Form.Item>
                                                            </div>

                                                            {/* Nút Xóa Mảng */}
                                                            <div style={{ width: '30px', flexShrink: 0, display: 'flex', justifyContent: 'center', paddingTop: '4px' }}>
                                                                <MinusCircleOutlined onClick={() => removeChoice(chField.name)} style={{ color: 'red', cursor: 'pointer', fontSize: '18px' }} />
                                                            </div>
                                                        </div>
                                                    ))}
                                                    <Button type="dashed" onClick={() => addChoice()} icon={<PlusOutlined />}>Thêm đáp án</Button>
                                                </>
                                            )}
                                        </Form.List>
                                    </Card>
                                </Card>
                            ))}
                            <Button type="dashed" onClick={() => addQuestion()} block icon={<PlusOutlined />} style={{ height: 50, borderColor: '#52c41a', color: '#52c41a', fontWeight: 'bold' }}>
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