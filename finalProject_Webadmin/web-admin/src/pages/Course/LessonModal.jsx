import React, { useEffect, useState } from 'react';
import { Modal, Form, Input, InputNumber, Button, Space, Select, Upload, message, Image, Collapse, Typography, Divider } from 'antd';
// ĐÃ THÊM PlayCircleOutlined VÀO ĐÂY
import { MinusCircleOutlined, PlusOutlined, UploadOutlined, FileTextOutlined, PictureOutlined, PlayCircleOutlined } from '@ant-design/icons';
import MathEditor from '../../components/editor/MathEditor';
import uploadApi from '../../api/uploadApi';

const { Text } = Typography;
const { Panel } = Collapse;

const getFullImageUrl = (path) => {
  if (!path) return '';
  if (path.startsWith('http')) return path;
  return `${import.meta.env.VITE_API_URL.replace('/api', '')}${path}`; 
};

const LessonModal = ({ open, onClose, onSubmit, initialValues, loading, nextOrderNo }) => {
  const [form] = Form.useForm();
  const [activeKey, setActiveKey] = useState(['0']); 

  useEffect(() => {
    if (open) {
      if (initialValues) {
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
        const keys = initialValues.contents.map((_, i) => i.toString());
        setActiveKey(keys);
      } else {
        form.resetFields();
        form.setFieldsValue({
            orderNo: nextOrderNo || 1, 
            contents: [{ type: 'TEXT', value: '', order: 1 }] 
        });
        setActiveKey(['0']);
      }
    }
  }, [initialValues, open, form, nextOrderNo]);

  const customRequest = async ({ file, onSuccess, onError }, add, name) => {
    try {
      const res = await uploadApi.uploadImage(file);
      if (res.success) {
        onSuccess("ok");
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
      const processedContents = values.contents.map((c, idx) => ({
          ...c,
          order: idx + 1
      }));
      onSubmit({ ...values, contents: processedContents });
    });
  };

  return (
    <Modal
      title={
        <Text strong style={{ fontSize: '18px' }}>
          {initialValues ? "Chỉnh sửa Bài học" : "Tạo Bài học mới"}
        </Text>
      }
      open={open}
      onOk={handleOk}
      onCancel={onClose}
      confirmLoading={loading}
      width={900}
      maskClosable={false}
      style={{ top: 20 }}
      okText="Lưu Bài học"
      cancelText="Hủy bỏ"
    >
      <Form form={form} layout="vertical" style={{ marginTop: '20px' }}>
        
        <div style={{ display: 'flex', gap: 16 }}>
            <Form.Item
                name="title"
                label={<Text strong>Tên bài học <span style={{color:'red'}}>*</span></Text>}
                style={{ flex: 1 }}
                rules={[{ required: true, message: 'Vui lòng nhập tên bài học' }]}
            >
                <Input size="large" placeholder="Ví dụ: Bài 1: Số nguyên tố" />
            </Form.Item>

            <Form.Item
                name="orderNo"
                label={<Text strong>Thứ tự <span style={{color:'red'}}>*</span></Text>}
                style={{ width: 120 }}
                rules={[{ required: true }]}
            >
                <InputNumber size="large" min={1} style={{ width: '100%' }} />
            </Form.Item>
        </div>

        <Form.Item name="videoUrl" label={<Text strong>Link Video (Youtube)</Text>}>
            <Input size="large" placeholder="https://youtube.com/..." prefix={<PlayCircleOutlined style={{color: '#bfbfbf'}}/>} />
        </Form.Item>

        <Divider style={{ margin: '16px 0' }} />
        
        <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Text strong style={{ fontSize: '16px' }}>Nội dung chi tiết</Text>
        </div>
        
        <Form.List name="contents">
          {(fields, { add, remove }) => (
            <div style={{ maxHeight: '50vh', overflowY: 'auto', paddingRight: 8 }}>
              
              <Collapse 
                activeKey={activeKey} 
                onChange={(keys) => setActiveKey(keys)}
                style={{ background: '#fff', border: 'none' }}
              >
                {fields.map(({ key, name, ...restField }, index) => (
                  <Panel 
                    key={index.toString()} 
                    style={{ marginBottom: 12, border: '1px solid #d9d9d9', borderRadius: 8, overflow: 'hidden' }}
                    header={
                      <Space>
                        {form.getFieldValue(['contents', name, 'type']) === 'IMAGE' ? <PictureOutlined style={{ color: '#52c41a' }} /> : <FileTextOutlined style={{ color: '#1890ff' }} />}
                        <Text strong>Khối nội dung #{index + 1}</Text>
                      </Space>
                    }
                    extra={
                      <Button 
                        type="text" 
                        danger 
                        icon={<MinusCircleOutlined />} 
                        size="small"
                        onClick={(e) => {
                            e.stopPropagation(); 
                            remove(name);
                        }}
                      >
                        Xóa
                      </Button>
                    }
                  >
                    <Space style={{ display: 'flex', marginBottom: 12 }} align="baseline">
                      <Form.Item
                        {...restField}
                        name={[name, 'type']}
                        label="Loại khối"
                        rules={[{ required: true }]}
                        style={{ width: 200, margin: 0 }}
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

                          if (type === 'IMAGE') {
                              return (
                                  <Form.Item
                                      {...restField}
                                      name={[name, 'value']}
                                      rules={[{ required: true, message: 'Vui lòng upload ảnh' }]}
                                      style={{ margin: 0 }}
                                  >
                                      <div style={{ border: '1px dashed #d9d9d9', padding: 20, textAlign: 'center', borderRadius: 6, background: '#fafafa' }}>
                                          {value ? (
                                              <div>
                                                  <Image src={getFullImageUrl(value)} height={150} style={{ objectFit: 'contain', borderRadius: 4 }} />
                                                  <div style={{ marginTop: 12 }}>
                                                      <Button danger onClick={() => {
                                                          const contents = form.getFieldValue('contents');
                                                          contents[name].value = '';
                                                          form.setFieldsValue({ contents });
                                                      }}>Gỡ ảnh</Button>
                                                  </div>
                                              </div>
                                          ) : (
                                              <Upload customRequest={(options) => customRequest(options, add, name)} showUploadList={false} accept="image/*">
                                                  <Button icon={<UploadOutlined />}>Tải ảnh lên (Max 2MB)</Button>
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
                                  rules={[{ required: true, message: 'Nhập nội dung' }]}
                                  style={{ margin: 0 }}
                              >
                                  <MathEditor 
                                      placeholder="Gõ nội dung bài học. Bấm 'Chèn công thức Toán' để thêm LaTeX..." 
                                  />
                              </Form.Item>
                          );
                      }}
                    </Form.Item>
                  </Panel>
                ))}
              </Collapse>

              <div style={{ padding: '8px 0' }}>
                <Button 
                    type="dashed" 
                    onClick={() => {
                        const newIndex = fields.length.toString();
                        add({ type: 'TEXT', value: '', order: fields.length + 1 });
                        setActiveKey([...activeKey, newIndex]); 
                    }} 
                    block 
                    icon={<PlusOutlined />}
                    style={{ height: 45, borderColor: '#1890ff', color: '#1890ff' }}
                >
                  Thêm khối nội dung mới
                </Button>
              </div>

            </div>
          )}
        </Form.List>
      </Form>
    </Modal>
  );
};

export default LessonModal;