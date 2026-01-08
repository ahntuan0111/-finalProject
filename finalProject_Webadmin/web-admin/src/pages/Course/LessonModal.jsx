import React, { useEffect } from 'react';
import { Modal, Form, Input, InputNumber, Button, Space, Card, Select, Upload, message, Image } from 'antd';
import { MinusCircleOutlined, PlusOutlined, UploadOutlined } from '@ant-design/icons';
import MathEditor from '../../components/editor/MathEditor';
import uploadApi from '../../api/uploadApi';

const getFullImageUrl = (path) => {
  if (!path) return '';
  if (path.startsWith('http')) return path;
  return `${import.meta.env.VITE_API_URL.replace('/api', '')}${path}`; 
};

const LessonModal = ({ open, onClose, onSubmit, initialValues, loading, nextOrderNo }) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (open) {
      if (initialValues) {
        // --- CHẾ ĐỘ SỬA ---
        form.setFieldsValue({
          title: initialValues.title,
          videoUrl: initialValues.videoUrl,
          orderNo: initialValues.orderNo,
          contents: initialValues.contents.map(c => ({
            type: c.contentType === 'IMAGE' ? 'IMAGE' : 'TEXT',
            value: c.contentValue,
            order: c.contentOrder
          }))
        });
      } else {
        // --- CHẾ ĐỘ TẠO MỚI ---
        form.resetFields();
        form.setFieldsValue({
            orderNo: nextOrderNo || 1, 
            contents: [{ type: 'TEXT', value: '', order: 1 }] 
        });
      }
    }
  }, [initialValues, open, form, nextOrderNo]);

  const customRequest = async ({ file, onSuccess, onError }, add, name) => {
    try {
      const res = await uploadApi.uploadImage(file);
      if (res.success) {
        onSuccess("ok");
        // Update form value
        const contents = form.getFieldValue('contents');
        contents[name].value = res.data;
        form.setFieldsValue({ contents });
        message.success("Upload ảnh thành công!");
      }
    } catch (err) {
      onError(err);
      message.error("Upload thất bại");
    }
  };

  const handleOk = () => {
    form.validateFields().then((values) => {
      // Đảm bảo thứ tự order
      const processedContents = values.contents.map((c, idx) => ({
          ...c,
          order: idx + 1
      }));
      onSubmit({ ...values, contents: processedContents });
    });
  };

  return (
    <Modal
      title={initialValues ? "Cập nhật Bài học" : "Tạo Bài học mới"}
      open={open}
      onOk={handleOk}
      onCancel={onClose}
      confirmLoading={loading}
      width={900}
      maskClosable={false}
      style={{ top: 20 }}
    >
      <Form form={form} layout="vertical">
        <div style={{ display: 'flex', gap: 16 }}>
            <Form.Item
                name="title"
                label="Tên bài học"
                style={{ flex: 1 }}
                rules={[{ required: true, message: 'Vui lòng nhập tên bài học' }]}
            >
                <Input placeholder="Ví dụ: Bài 1: Số nguyên tố" />
            </Form.Item>

            <Form.Item
                name="orderNo"
                label="Thứ tự"
                style={{ width: 100 }}
                rules={[{ required: true }]}
            >
                <InputNumber min={1} style={{ width: '100%' }} />
            </Form.Item>
        </div>

        <Form.Item name="videoUrl" label="Link Video (Youtube)">
            <Input placeholder="https://youtube.com/..." prefix={<UploadOutlined />} />
        </Form.Item>

        <h4 style={{ marginTop: 10, marginBottom: 10, borderBottom: '1px solid #eee', paddingBottom: 5 }}>
            Nội dung chi tiết
        </h4>
        
        <Form.List name="contents">
          {(fields, { add, remove }) => (
            <div style={{ maxHeight: '500px', overflowY: 'auto', paddingRight: 5 }}>
              {fields.map(({ key, name, ...restField }, index) => (
                <Card 
                  key={key} 
                  size="small" 
                  style={{ marginBottom: 15, background: '#fafafa', borderColor: '#d9d9d9' }}
                  title={`Khối nội dung #${index + 1}`}
                  extra={
                    <Button type="text" danger icon={<MinusCircleOutlined />} onClick={() => remove(name)}>Xóa</Button>
                  }
                >
                  <Space style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                    <Form.Item
                      {...restField}
                      name={[name, 'type']}
                      label="Loại"
                      rules={[{ required: true }]}
                      style={{ width: 180 }}
                    >
                      <Select onChange={() => {
                          const contents = form.getFieldValue('contents');
                          contents[name].value = '';
                          form.setFieldsValue({ contents });
                      }}>
                        <Select.Option value="TEXT">📝 Văn bản & Toán</Select.Option>
                        <Select.Option value="IMAGE">🖼️ Hình ảnh</Select.Option>
                      </Select>
                    </Form.Item>
                  </Space>

                  <Form.Item
                    noStyle
                    shouldUpdate={(prev, curr) => 
                        prev.contents?.[name]?.type !== curr.contents?.[name]?.type ||
                        prev.contents?.[name]?.value !== curr.contents?.[name]?.value
                    }
                  >
                    {({ getFieldValue }) => {
                        const type = getFieldValue(['contents', name, 'type']);
                        const value = getFieldValue(['contents', name, 'value']);

                        // 1. TRƯỜNG HỢP HÌNH ẢNH
                        if (type === 'IMAGE') {
                            return (
                                <Form.Item
                                    {...restField}
                                    name={[name, 'value']}
                                    rules={[{ required: true, message: 'Vui lòng upload ảnh' }]}
                                >
                                    <div style={{ border: '1px dashed #d9d9d9', padding: 20, textAlign: 'center', borderRadius: 6, background: '#fff' }}>
                                        {value ? (
                                            <div>
                                                <Image src={getFullImageUrl(value)} height={150} style={{ objectFit: 'contain' }} />
                                                <div style={{ marginTop: 8 }}>
                                                    <Button size="small" danger onClick={() => {
                                                        const contents = form.getFieldValue('contents');
                                                        contents[name].value = '';
                                                        form.setFieldsValue({ contents });
                                                    }}>Gỡ ảnh</Button>
                                                </div>
                                            </div>
                                        ) : (
                                            <Upload customRequest={(options) => customRequest(options, add, name)} showUploadList={false} accept="image/*">
                                                <Button icon={<UploadOutlined />}>Tải ảnh lên</Button>
                                            </Upload>
                                        )}
                                    </div>
                                </Form.Item>
                            );
                        }

                        // 2. TRƯỜNG HỢP VĂN BẢN (Sử dụng MathEditor mới)
                        return (
                            <Form.Item
                                {...restField}
                                name={[name, 'value']}
                                rules={[{ required: true, message: 'Nhập nội dung' }]}
                            >
                                <MathEditor 
                                    placeholder="Gõ nội dung bài học. Bấm 'Chèn công thức Toán' để thêm LaTeX..." 
                                />
                            </Form.Item>
                        );
                    }}
                  </Form.Item>
                </Card>
              ))}

              <Form.Item>
                <Button 
                    type="dashed" 
                    onClick={() => add({ type: 'TEXT', value: '', order: fields.length + 1 })} 
                    block 
                    icon={<PlusOutlined />}
                    style={{ height: 40 }}
                >
                  Thêm nội dung
                </Button>
              </Form.Item>
            </div>
          )}
        </Form.List>
      </Form>
    </Modal>
  );
};

export default LessonModal;