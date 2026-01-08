import React, { useEffect } from 'react';
import { Modal, Form, InputNumber, Button, Space, Card, Select, Upload, message, Image, Divider } from 'antd';
import { MinusCircleOutlined, PlusOutlined, UploadOutlined } from '@ant-design/icons';
import MathEditor from '../../components/editor/MathEditor'; 
import uploadApi from '../../api/uploadApi';

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
        // --- CHẾ ĐỘ SỬA ---
        form.setFieldsValue({
          // Map các trường mới từ API
          questionType: initialValues.questionType || 'TEXT', 
          questionValue: initialValues.questionValue,
          orderNo: initialValues.orderNo,
          solutions: initialValues.solutions.map(sol => ({
            type: sol.solutionType, 
            value: sol.solutionValue,
            order: sol.solutionOrder
          }))
        });
      } else {
        // --- CHẾ ĐỘ TẠO MỚI ---
        form.resetFields();
        form.setFieldsValue({
            questionType: 'TEXT', // Mặc định câu hỏi là Text
            orderNo: nextOrderNo || 1, 
            solutions: [{ type: 'TEXT', value: '', order: 1 }]
        });
      }
    }
  }, [initialValues, open, form, nextOrderNo]);

  // Xử lý upload ảnh (Dùng chung cho cả Question và Solution)
  // target: 'question' hoặc 'solution'
  // index: chỉ dùng cho solution
  const customRequest = async ({ file, onSuccess, onError }, target, index = null) => {
    try {
      const res = await uploadApi.uploadImage(file);
      if (res.success) {
        onSuccess("ok");
        
        if (target === 'question') {
            // Update cho câu hỏi
            form.setFieldsValue({ questionValue: res.data });
        } else {
            // Update cho solution tại dòng thứ index
            const solutions = form.getFieldValue('solutions');
            solutions[index].value = res.data;
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
      const processedSolutions = values.solutions.map((sol, idx) => ({
          ...sol,
          order: idx + 1
      }));
      onSubmit({ ...values, solutions: processedSolutions });
    });
  };

  return (
    <Modal
      title={initialValues ? "Cập nhật Bài tập" : "Tạo Bài tập mới"}
      open={open}
      onOk={handleOk}
      onCancel={onClose}
      confirmLoading={loading}
      width={900}
      maskClosable={false}
      style={{ top: 20 }}
    >
      <Form form={form} layout="vertical">
        
        {/* === PHẦN 1: CÂU HỎI (QUESTION) === */}
        <Card size="small" style={{ marginBottom: 20, background: '#f0f5ff', borderColor: '#adc6ff' }}>
            <div style={{ display: 'flex', gap: 16 }}>
                <Form.Item
                    name="orderNo"
                    label="Thứ tự hiển thị"
                    rules={[{ required: true }]}
                    style={{ width: 150 }}
                >
                    <InputNumber min={1} style={{ width: '100%' }} />
                </Form.Item>

                {/* Chọn loại câu hỏi */}
                <Form.Item
                    name="questionType"
                    label="Loại câu hỏi"
                    rules={[{ required: true }]}
                    style={{ width: 200 }}
                >
                    <Select onChange={() => form.setFieldsValue({ questionValue: '' })}>
                        <Select.Option value="TEXT">📝 Văn bản & Toán</Select.Option>
                        <Select.Option value="IMAGE">🖼️ Hình ảnh</Select.Option>
                    </Select>
                </Form.Item>
            </div>

            {/* Render nội dung câu hỏi dựa trên loại (Text/Image) */}
            <Form.Item
                noStyle
                shouldUpdate={(prev, curr) => prev.questionType !== curr.questionType}
            >
                {({ getFieldValue }) => {
                    const type = getFieldValue('questionType');
                    
                    if (type === 'IMAGE') {
                        // --- GIAO DIỆN UPLOAD ẢNH CHO CÂU HỎI ---
                        return (
                            <Form.Item
                                name="questionValue"
                                label="Ảnh đề bài"
                                rules={[{ required: true, message: 'Vui lòng upload ảnh' }]}
                            >
                                <div style={{ border: '1px dashed #d9d9d9', padding: 20, textAlign: 'center', background: '#fff', borderRadius: 6 }}>
                                    {getFieldValue('questionValue') ? (
                                        <div>
                                            <Image 
                                                src={getFullImageUrl(getFieldValue('questionValue'))} 
                                                height={150} 
                                                style={{objectFit: 'contain'}} 
                                            />
                                            <br/>
                                            <Button size="small" danger style={{marginTop: 10}} onClick={() => {
                                                form.setFieldsValue({ questionValue: '' });
                                            }}>Xóa ảnh</Button>
                                        </div>
                                    ) : (
                                        <Upload 
                                            customRequest={(options) => customRequest(options, 'question')} 
                                            showUploadList={false} 
                                            accept="image/*"
                                        >
                                            <Button icon={<UploadOutlined />}>Tải ảnh lên</Button>
                                        </Upload>
                                    )}
                                </div>
                            </Form.Item>
                        );
                    }

                    // --- GIAO DIỆN TEXT CHO CÂU HỎI ---
                    return (
                        <Form.Item
                            name="questionValue"
                            label="Nội dung câu hỏi"
                            rules={[{ required: true, message: 'Vui lòng nhập câu hỏi' }]}
                        >
                            <MathEditor placeholder="Nhập đề bài tại đây..." />
                        </Form.Item>
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
                    label="Loại nội dung"
                    rules={[{ required: true }]}
                    initialValue="TEXT"
                    style={{ width: 200 }}
                  >
                    <Select onChange={() => {
                        const solutions = form.getFieldValue('solutions');
                        solutions[name].value = '';
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
                        prev.solutions?.[name]?.value !== curr.solutions?.[name]?.value
                    }
                  >
                    {({ getFieldValue }) => {
                        const type = getFieldValue(['solutions', name, 'type']);
                        const value = getFieldValue(['solutions', name, 'value']);

                        if (type === 'IMAGE') {
                            return (
                                <Form.Item
                                    {...restField}
                                    name={[name, 'value']}
                                    rules={[{ required: true, message: 'Vui lòng upload ảnh' }]}
                                >
                                    <div style={{ border: '1px dashed #d9d9d9', padding: 10, textAlign: 'center', background: '#fff' }}>
                                        {value ? (
                                            <div>
                                                <Image src={getFullImageUrl(value)} height={100} />
                                                <br/>
                                                <Button size="small" danger style={{marginTop: 5}} onClick={() => {
                                                    const solutions = form.getFieldValue('solutions');
                                                    solutions[name].value = '';
                                                    form.setFieldsValue({ solutions });
                                                }}>Xóa ảnh</Button>
                                            </div>
                                        ) : (
                                            <Upload customRequest={(options) => customRequest(options, 'solution', name)} showUploadList={false} accept="image/*">
                                                <Button icon={<UploadOutlined />}>Tải ảnh lên</Button>
                                            </Upload>
                                        )}
                                    </div>
                                </Form.Item>
                            );
                        }

                        return (
                            <Form.Item
                                {...restField}
                                name={[name, 'value']}
                                rules={[{ required: true, message: 'Nhập nội dung lời giải' }]}
                            >
                                <MathEditor placeholder="Nhập hướng dẫn giải..." />
                            </Form.Item>
                        );
                    }}
                  </Form.Item>
                </Card>
              ))}

              <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>Thêm bước giải mới</Button>
            </div>
          )}
        </Form.List>
      </Form>
    </Modal>
  );
};

export default ExerciseModal;