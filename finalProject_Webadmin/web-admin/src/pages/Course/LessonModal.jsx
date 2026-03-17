import React, { useEffect, useState } from 'react';
import { Modal, Form, Input, InputNumber, Button, Space, Select, Upload, message, Image, Collapse, Typography, Divider, Row, Col } from 'antd';
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
        // --- ĐỔ DỮ LIỆU TỪ BE VÀO FORM SỬA (KÈM BẢO VỆ DỮ LIỆU NULL) ---
        const safeContents = initialValues.contents || [];
        
        form.setFieldsValue({
          titleVi: initialValues.titleVi || '',
          titleEn: initialValues.titleEn || '',
          videoUrl: initialValues.videoUrl || '',
          orderNo: initialValues.orderNo || 1,
          contents: safeContents.map(c => ({
            type: c.contentType === 'IMAGE' ? 'IMAGE' : 'TEXT',
            valueVi: c.contentValueVi || '',
            valueEn: c.contentValueEn || '',
            order: c.contentOrder || 1
          }))
        });
        const keys = safeContents.map((_, i) => i.toString());
        setActiveKey(keys);
      } else {
        // --- RESET FORM KHI TẠO MỚI ---
        form.resetFields();
        form.setFieldsValue({
            orderNo: nextOrderNo || 1, 
            contents: [{ type: 'TEXT', valueVi: '', valueEn: '', order: 1 }] 
        });
        setActiveKey(['0']);
      }
    }
  }, [initialValues, open, form, nextOrderNo]);

  const customRequest = async ({ file, onSuccess, onError }, add, name, langType) => {
    try {
      const res = await uploadApi.uploadImage(file);
      if (res.success) {
        onSuccess("ok");
        const contents = form.getFieldValue('contents');
        // Lưu URL ảnh vào trường valueVi hoặc valueEn tương ứng
        contents[name][langType] = res.data;
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
      // Đánh lại số thứ tự (order) cho mảng contents trước khi gửi
      const processedContents = (values.contents || []).map((c, idx) => ({
          ...c,
          order: idx + 1
      }));
      // Gửi payload chứa ...Vi và ...En lên hàm cha
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
      width={1000}
      maskClosable={false}
      style={{ top: 20 }}
      okText="Lưu Bài học"
      cancelText="Hủy bỏ"
    >
      <Form form={form} layout="vertical" style={{ marginTop: '20px' }}>
        
        {/* --- DÒNG 1: TÊN BÀI HỌC VÀ THỨ TỰ --- */}
        <Row gutter={16}>
            <Col span={10}>
                <Form.Item
                    name="titleVi"
                    label={<Text strong>Tên bài học (Tiếng Việt) <span style={{color:'red'}}>*</span></Text>}
                    rules={[{ required: true, message: 'Vui lòng nhập tên tiếng Việt' }]}
                >
                    <Input size="large" placeholder="Ví dụ: Bài 1: Số nguyên tố" />
                </Form.Item>
            </Col>
            
            <Col span={10}>
                <Form.Item
                    name="titleEn"
                    label={<Text strong>Tên bài học (English)</Text>}
                >
                    <Input size="large" placeholder="Example: Lesson 1: Prime Numbers" />
                </Form.Item>
            </Col>

            <Col span={4}>
                <Form.Item
                    name="orderNo"
                    label={<Text strong>Thứ tự <span style={{color:'red'}}>*</span></Text>}
                    rules={[{ required: true }]}
                >
                    <InputNumber size="large" min={1} style={{ width: '100%' }} />
                </Form.Item>
            </Col>
        </Row>

        <Form.Item name="videoUrl" label={<Text strong>Link Video (Youtube)</Text>}>
            <Input size="large" placeholder="https://youtube.com/..." prefix={<PlayCircleOutlined style={{color: '#bfbfbf'}}/>} />
        </Form.Item>

        <Divider style={{ margin: '16px 0' }} />
        
        <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Text strong style={{ fontSize: '16px' }}>Nội dung chi tiết</Text>
        </div>
        
        <Form.List name="contents">
          {(fields, { add, remove }) => (
            <div style={{ maxHeight: '55vh', overflowY: 'auto', paddingRight: 8 }}>
              
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
                    <Form.Item
                        {...restField}
                        name={[name, 'type']}
                        label="Loại khối"
                        rules={[{ required: true }]}
                        style={{ width: 250, marginBottom: 16 }}
                    >
                        <Select onChange={() => {
                            // Khi đổi loại khối (TEXT <-> IMAGE), reset lại cả 2 cột giá trị
                            const contents = form.getFieldValue('contents');
                            contents[name].valueVi = '';
                            contents[name].valueEn = '';
                            form.setFieldsValue({ contents });
                        }}>
                          <Select.Option value="TEXT">📝 Văn bản & Toán</Select.Option>
                          <Select.Option value="IMAGE">🖼️ Hình ảnh</Select.Option>
                        </Select>
                    </Form.Item>

                    <Form.Item
                      noStyle
                      shouldUpdate={(prev, curr) => 
                          prev.contents?.[name]?.type !== curr.contents?.[name]?.type ||
                          prev.contents?.[name]?.valueVi !== curr.contents?.[name]?.valueVi ||
                          prev.contents?.[name]?.valueEn !== curr.contents?.[name]?.valueEn
                      }
                    >
                      {({ getFieldValue }) => {
                          const type = getFieldValue(['contents', name, 'type']);
                          const valueVi = getFieldValue(['contents', name, 'valueVi']);
                          const valueEn = getFieldValue(['contents', name, 'valueEn']);

                          // NẾU LÀ HÌNH ẢNH (IMAGE)
                          if (type === 'IMAGE') {
                              return (
                                <Row gutter={16}>
                                    {/* CỘT TIẾNG VIỆT */}
                                    <Col span={12}>
                                        <Text strong style={{ display: 'block', marginBottom: 8 }}>Ảnh (Tiếng Việt)</Text>
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'valueVi']}
                                            rules={[{ required: true, message: 'Vui lòng upload ảnh tiếng Việt' }]}
                                        >
                                            <div style={{ border: '1px dashed #d9d9d9', padding: 20, textAlign: 'center', borderRadius: 6, background: '#fafafa' }}>
                                                {valueVi ? (
                                                    <div>
                                                        <Image src={getFullImageUrl(valueVi)} height={150} style={{ objectFit: 'contain', borderRadius: 4 }} />
                                                        <div style={{ marginTop: 12 }}>
                                                            <Button danger onClick={() => {
                                                                const contents = form.getFieldValue('contents');
                                                                contents[name].valueVi = '';
                                                                form.setFieldsValue({ contents });
                                                            }}>Gỡ ảnh VN</Button>
                                                        </div>
                                                    </div>
                                                ) : (
                                                    <Upload customRequest={(options) => customRequest(options, add, name, 'valueVi')} showUploadList={false} accept="image/*">
                                                        <Button icon={<UploadOutlined />}>Tải ảnh lên (VN)</Button>
                                                    </Upload>
                                                )}
                                            </div>
                                        </Form.Item>
                                    </Col>

                                    {/* CỘT TIẾNG ANH */}
                                    <Col span={12}>
                                        <Text strong style={{ display: 'block', marginBottom: 8 }}>Ảnh (English) - Tùy chọn</Text>
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'valueEn']}
                                        >
                                            <div style={{ border: '1px dashed #d9d9d9', padding: 20, textAlign: 'center', borderRadius: 6, background: '#fafafa' }}>
                                                {valueEn ? (
                                                    <div>
                                                        <Image src={getFullImageUrl(valueEn)} height={150} style={{ objectFit: 'contain', borderRadius: 4 }} />
                                                        <div style={{ marginTop: 12 }}>
                                                            <Button danger onClick={() => {
                                                                const contents = form.getFieldValue('contents');
                                                                contents[name].valueEn = '';
                                                                form.setFieldsValue({ contents });
                                                            }}>Gỡ ảnh EN</Button>
                                                        </div>
                                                    </div>
                                                ) : (
                                                    <Upload customRequest={(options) => customRequest(options, add, name, 'valueEn')} showUploadList={false} accept="image/*">
                                                        <Button icon={<UploadOutlined />}>Tải ảnh lên (EN)</Button>
                                                    </Upload>
                                                )}
                                            </div>
                                        </Form.Item>
                                    </Col>
                                </Row>
                              );
                          }

                          // NẾU LÀ VĂN BẢN (TEXT)
                          return (
                            <Row gutter={16}>
                                <Col span={12}>
                                    <Text strong style={{ display: 'block', marginBottom: 8 }}>Nội dung (Tiếng Việt)</Text>
                                    <Form.Item
                                        {...restField}
                                        name={[name, 'valueVi']}
                                        rules={[{ required: true, message: 'Nhập nội dung Tiếng Việt' }]}
                                    >
                                        <MathEditor placeholder="Gõ nội dung tiếng Việt..." />
                                    </Form.Item>
                                </Col>

                                <Col span={12}>
                                    <Text strong style={{ display: 'block', marginBottom: 8 }}>Nội dung (English) - Tùy chọn</Text>
                                    <Form.Item
                                        {...restField}
                                        name={[name, 'valueEn']}
                                    >
                                        <MathEditor placeholder="Type English content here..." />
                                    </Form.Item>
                                </Col>
                            </Row>
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
                        add({ type: 'TEXT', valueVi: '', valueEn: '', order: fields.length + 1 });
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