import React, { useEffect } from 'react';
import { Modal, Form, InputNumber, Button, Space, Card, Select, Upload, message, Image, Divider, Row, Col, Typography } from 'antd';
import { MinusCircleOutlined, PlusOutlined, UploadOutlined } from '@ant-design/icons';
import MathEditor from '../../components/editor/MathEditor'; 
import uploadApi from '../../api/uploadApi';

const { Text } = Typography;

const getFullImageUrl = (path) => {
  if (!path) return '';
  if (path.startsWith('http')) return path;
  return `${import.meta.env.VITE_API_URL.replace('/api', '')}${path}`; 
};

const ExerciseModal = ({ open, onClose, onSubmit, initialValues, loading, nextOrderNo }) => {
  const [form] = Form.useForm();

  // Load dữ liệu khi mở Modal
  useEffect(() => {
    if (open) {
      if (initialValues) {
        // --- CHẾ ĐỘ SỬA (Map 2 ngôn ngữ) ---
        const safeSolutions = initialValues.solutions || [];
        form.setFieldsValue({
          questionType: initialValues.questionType || 'TEXT', 
          questionValueVi: initialValues.questionValueVi || '',
          questionValueEn: initialValues.questionValueEn || '',
          orderNo: initialValues.orderNo || 1,
          solutions: safeSolutions.map(sol => ({
            type: sol.solutionType || 'TEXT', 
            valueVi: sol.solutionValueVi || '',
            valueEn: sol.solutionValueEn || '',
            order: sol.solutionOrder
          }))
        });
      } else {
        // --- CHẾ ĐỘ TẠO MỚI ---
        form.resetFields();
        form.setFieldsValue({
            questionType: 'TEXT', 
            orderNo: nextOrderNo || 1, 
            solutions: [{ type: 'TEXT', valueVi: '', valueEn: '', order: 1 }]
        });
      }
    }
  }, [initialValues, open, form, nextOrderNo]);

  // Xử lý upload ảnh
  const customRequest = async ({ file, onSuccess, onError }, target, langType, index = null) => {
    try {
      const res = await uploadApi.uploadImage(file);
      if (res.success) {
        onSuccess("ok");
        
        if (target === 'question') {
            form.setFieldsValue({ [langType]: res.data }); // [langType] sẽ là questionValueVi hoặc questionValueEn
        } else {
            const solutions = form.getFieldValue('solutions');
            solutions[index][langType] = res.data; // [langType] sẽ là valueVi hoặc valueEn
            form.setFieldsValue({ solutions });
        }
        message.success("Upload ảnh thành công!");
      }
    } catch (err) {
      onError(err);
      message.error("Upload thất bại");
    }
  };

  const handleOk = () => {
    form.validateFields().then((values) => {
      // Chuẩn hóa data trước khi submit
      const processedSolutions = (values.solutions || []).map((sol, idx) => ({
          ...sol,
          order: idx + 1
      }));
      onSubmit({ ...values, solutions: processedSolutions });
    });
  };

  return (
    <Modal
      title={<Text strong style={{ fontSize: '18px' }}>{initialValues ? "Cập nhật Bài tập" : "Tạo Bài tập mới"}</Text>}
      open={open}
      onOk={handleOk}
      onCancel={onClose}
      confirmLoading={loading}
      width={1000} // Nới rộng Modal
      maskClosable={false}
      style={{ top: 20 }}
      okText="Lưu Bài tập"
      cancelText="Hủy bỏ"
    >
      <Form form={form} layout="vertical" style={{ marginTop: '20px' }}>
        
        {/* === PHẦN 1: CÂU HỎI (QUESTION) === */}
        <Card size="small" style={{ marginBottom: 20, background: '#f0f5ff', borderColor: '#adc6ff' }}>
            <div style={{ display: 'flex', gap: 16 }}>
                <Form.Item
                    name="orderNo"
                    label={<Text strong>Thứ tự hiển thị <span style={{color:'red'}}>*</span></Text>}
                    rules={[{ required: true }]}
                    style={{ width: 150 }}
                >
                    <InputNumber min={1} size="large" style={{ width: '100%' }} />
                </Form.Item>

                <Form.Item
                    name="questionType"
                    label={<Text strong>Loại câu hỏi <span style={{color:'red'}}>*</span></Text>}
                    rules={[{ required: true }]}
                    style={{ width: 200 }}
                >
                    <Select size="large" onChange={() => form.setFieldsValue({ questionValueVi: '', questionValueEn: '' })}>
                        <Select.Option value="TEXT">📝 Văn bản & Toán</Select.Option>
                        <Select.Option value="IMAGE">🖼️ Hình ảnh</Select.Option>
                    </Select>
                </Form.Item>
            </div>

            <Form.Item
                noStyle
                shouldUpdate={(prev, curr) => 
                    prev.questionType !== curr.questionType ||
                    prev.questionValueVi !== curr.questionValueVi ||
                    prev.questionValueEn !== curr.questionValueEn
                }
            >
                {({ getFieldValue }) => {
                    const type = getFieldValue('questionType');
                    const valVi = getFieldValue('questionValueVi');
                    const valEn = getFieldValue('questionValueEn');
                    
                    if (type === 'IMAGE') {
                        return (
                            <Row gutter={16}>
                                <Col span={12}>
                                    <Form.Item
                                        name="questionValueVi"
                                        label={<Text strong>Ảnh đề bài (Tiếng Việt) <span style={{color:'red'}}>*</span></Text>}
                                        rules={[{ required: true, message: 'Vui lòng upload ảnh VN' }]}
                                    >
                                        <div style={{ border: '1px dashed #d9d9d9', padding: 20, textAlign: 'center', background: '#fff', borderRadius: 6 }}>
                                            {valVi ? (
                                                <div>
                                                    <Image src={getFullImageUrl(valVi)} height={120} style={{objectFit: 'contain'}} />
                                                    <br/>
                                                    <Button size="small" danger style={{marginTop: 10}} onClick={() => form.setFieldsValue({ questionValueVi: '' })}>Xóa ảnh VN</Button>
                                                </div>
                                            ) : (
                                                <Upload customRequest={(opts) => customRequest(opts, 'question', 'questionValueVi')} showUploadList={false} accept="image/*">
                                                    <Button icon={<UploadOutlined />}>Tải ảnh VN</Button>
                                                </Upload>
                                            )}
                                        </div>
                                    </Form.Item>
                                </Col>
                                <Col span={12}>
                                    <Form.Item name="questionValueEn" label={<Text strong>Ảnh đề bài (English)</Text>}>
                                        <div style={{ border: '1px dashed #d9d9d9', padding: 20, textAlign: 'center', background: '#fff', borderRadius: 6 }}>
                                            {valEn ? (
                                                <div>
                                                    <Image src={getFullImageUrl(valEn)} height={120} style={{objectFit: 'contain'}} />
                                                    <br/>
                                                    <Button size="small" danger style={{marginTop: 10}} onClick={() => form.setFieldsValue({ questionValueEn: '' })}>Xóa ảnh EN</Button>
                                                </div>
                                            ) : (
                                                <Upload customRequest={(opts) => customRequest(opts, 'question', 'questionValueEn')} showUploadList={false} accept="image/*">
                                                    <Button icon={<UploadOutlined />}>Tải ảnh EN</Button>
                                                </Upload>
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
                                <Form.Item
                                    name="questionValueVi"
                                    label={<Text strong>Nội dung câu hỏi (Tiếng Việt) <span style={{color:'red'}}>*</span></Text>}
                                    rules={[{ required: true, message: 'Vui lòng nhập câu hỏi VN' }]}
                                >
                                    <MathEditor placeholder="Nhập đề bài VN tại đây..." />
                                </Form.Item>
                            </Col>
                            <Col span={12}>
                                <Form.Item name="questionValueEn" label={<Text strong>Nội dung câu hỏi (English)</Text>}>
                                    <MathEditor placeholder="Type English question here..." />
                                </Form.Item>
                            </Col>
                        </Row>
                    );
                }}
            </Form.Item>
        </Card>

        <Divider orientation="left" style={{ borderColor: '#999' }}>Lời giải chi tiết</Divider>
        
        {/* === PHẦN 2: DANH SÁCH LỜI GIẢI === */}
        <Form.List name="solutions">
          {(fields, { add, remove }) => (
            <div style={{ maxHeight: '400px', overflowY: 'auto', paddingRight: 5 }}>
              {fields.map(({ key, name, ...restField }, index) => (
                <Card 
                  key={key} 
                  size="small" 
                  style={{ marginBottom: 15, background: '#fafafa', borderColor: '#d9d9d9' }}
                  title={`Bước giải #${index + 1}`}
                  extra={<Button type="text" danger icon={<MinusCircleOutlined />} onClick={() => remove(name)}>Xóa</Button>}
                >
                  <Form.Item
                    {...restField}
                    name={[name, 'type']}
                    label={<Text strong>Loại nội dung</Text>}
                    rules={[{ required: true }]}
                    style={{ width: 200 }}
                  >
                    <Select onChange={() => {
                        const solutions = form.getFieldValue('solutions');
                        solutions[name].valueVi = '';
                        solutions[name].valueEn = '';
                        form.setFieldsValue({ solutions });
                    }}>
                      <Select.Option value="TEXT">📝 Văn bản & Toán</Select.Option>
                      <Select.Option value="IMAGE">🖼️ Hình ảnh</Select.Option>
                    </Select>
                  </Form.Item>

                  <Form.Item
                    noStyle
                    shouldUpdate={(prev, curr) => 
                        prev.solutions?.[name]?.type !== curr.solutions?.[name]?.type ||
                        prev.solutions?.[name]?.valueVi !== curr.solutions?.[name]?.valueVi ||
                        prev.solutions?.[name]?.valueEn !== curr.solutions?.[name]?.valueEn
                    }
                  >
                    {({ getFieldValue }) => {
                        const type = getFieldValue(['solutions', name, 'type']);
                        const valVi = getFieldValue(['solutions', name, 'valueVi']);
                        const valEn = getFieldValue(['solutions', name, 'valueEn']);

                        if (type === 'IMAGE') {
                            return (
                                <Row gutter={16}>
                                    <Col span={12}>
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'valueVi']}
                                            rules={[{ required: true, message: 'Vui lòng upload ảnh' }]}
                                        >
                                            <div style={{ border: '1px dashed #d9d9d9', padding: 10, textAlign: 'center', background: '#fff' }}>
                                                {valVi ? (
                                                    <div>
                                                        <Image src={getFullImageUrl(valVi)} height={100} />
                                                        <br/>
                                                        <Button size="small" danger style={{marginTop: 5}} onClick={() => {
                                                            const solutions = form.getFieldValue('solutions');
                                                            solutions[name].valueVi = '';
                                                            form.setFieldsValue({ solutions });
                                                        }}>Xóa ảnh VN</Button>
                                                    </div>
                                                ) : (
                                                    <Upload customRequest={(opts) => customRequest(opts, 'solution', 'valueVi', name)} showUploadList={false} accept="image/*">
                                                        <Button icon={<UploadOutlined />}>Tải ảnh VN</Button>
                                                    </Upload>
                                                )}
                                            </div>
                                        </Form.Item>
                                    </Col>
                                    <Col span={12}>
                                        <Form.Item {...restField} name={[name, 'valueEn']}>
                                            <div style={{ border: '1px dashed #d9d9d9', padding: 10, textAlign: 'center', background: '#fff' }}>
                                                {valEn ? (
                                                    <div>
                                                        <Image src={getFullImageUrl(valEn)} height={100} />
                                                        <br/>
                                                        <Button size="small" danger style={{marginTop: 5}} onClick={() => {
                                                            const solutions = form.getFieldValue('solutions');
                                                            solutions[name].valueEn = '';
                                                            form.setFieldsValue({ solutions });
                                                        }}>Xóa ảnh EN</Button>
                                                    </div>
                                                ) : (
                                                    <Upload customRequest={(opts) => customRequest(opts, 'solution', 'valueEn', name)} showUploadList={false} accept="image/*">
                                                        <Button icon={<UploadOutlined />}>Tải ảnh EN</Button>
                                                    </Upload>
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
                                    <Form.Item
                                        {...restField}
                                        name={[name, 'valueVi']}
                                        rules={[{ required: true, message: 'Nhập nội dung lời giải VN' }]}
                                    >
                                        <MathEditor placeholder="Nhập hướng dẫn giải VN..." />
                                    </Form.Item>
                                </Col>
                                <Col span={12}>
                                    <Form.Item {...restField} name={[name, 'valueEn']}>
                                        <MathEditor placeholder="Type English solution here..." />
                                    </Form.Item>
                                </Col>
                            </Row>
                        );
                    }}
                  </Form.Item>
                </Card>
              ))}

              <Button type="dashed" onClick={() => add({ type: 'TEXT', valueVi: '', valueEn: '', order: fields.length + 1 })} block icon={<PlusOutlined />}>Thêm bước giải mới</Button>
            </div>
          )}
        </Form.List>
      </Form>
    </Modal>
  );
};

export default ExerciseModal;