import React from 'react';
import { Modal, Typography, Divider, List, Tag, Card, Row, Col } from 'antd';
import { CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';

const { Title, Text, Paragraph } = Typography;

const QuestionDetailModal = ({ visible, question, onCancel }) => {
    if (!question) return null;

    return (
        <Modal
            title={<span style={{ fontSize: '18px', color: '#1890ff' }}>Chi tiết Câu hỏi số {question.orderNo}</span>}
            open={visible}
            onCancel={onCancel}
            footer={null} 
            width={1000} // Nới rộng Modal
        >
            {/* LỜI GIẢI THÍCH */}
            <Card size="small" style={{ marginBottom: 16, backgroundColor: '#f6ffed', borderColor: '#b7eb8f' }}>
                <Row gutter={16}>
                    <Col span={12}>
                        <Text strong>Lời giải chi tiết (VN): </Text>
                        {question.explanationVi ? (
                            <Paragraph style={{ marginTop: 8 }} dangerouslySetInnerHTML={{ __html: question.explanationVi }} />
                        ) : <Text italic type="secondary"> Không có</Text>}
                    </Col>
                    <Col span={12}>
                        <Text strong>Lời giải chi tiết (EN): </Text>
                        {question.explanationEn ? (
                            <Paragraph style={{ marginTop: 8 }} dangerouslySetInnerHTML={{ __html: question.explanationEn }} />
                        ) : <Text italic type="secondary"> Không có</Text>}
                    </Col>
                </Row>
            </Card>

            {/* NỘI DUNG ĐỀ BÀI */}
            <Title level={5}>Nội dung đề bài:</Title>
            <div style={{ padding: '10px 16px', backgroundColor: '#f0f2f5', borderRadius: '8px', marginBottom: '24px' }}>
                {question.contents?.map((content, index) => (
                    <Row gutter={16} key={index} style={{ marginBottom: 16 }}>
                        <Col span={12}>
                            <Text type="secondary" style={{fontSize: 12}}>Tiếng Việt:</Text>
                            <div style={{marginTop: 4}}>
                                {content.contentType === 'IMAGE' && content.contentValueVi ? (
                                    <img src={content.contentValueVi} alt={`vn-${index}`} style={{ maxWidth: '100%', borderRadius: '4px' }} />
                                ) : (
                                    <div dangerouslySetInnerHTML={{ __html: content.contentValueVi || '(Trống)' }} />
                                )}
                            </div>
                        </Col>
                        <Col span={12}>
                            <Text type="secondary" style={{fontSize: 12}}>English:</Text>
                            <div style={{marginTop: 4}}>
                                {content.contentType === 'IMAGE' && content.contentValueEn ? (
                                    <img src={content.contentValueEn} alt={`en-${index}`} style={{ maxWidth: '100%', borderRadius: '4px' }} />
                                ) : (
                                    <div dangerouslySetInnerHTML={{ __html: content.contentValueEn || '(Trống)' }} />
                                )}
                            </div>
                        </Col>
                    </Row>
                ))}
            </div>

            <Divider dashed />

            {/* DANH SÁCH ĐÁP ÁN */}
            <Title level={5}>Các lựa chọn đáp án:</Title>
            <List
                dataSource={question.choices || []}
                renderItem={(choice, index) => {
                    const isCorrect = choice.correct; 
                    return (
                        <List.Item style={{ padding: '8px 0' }}>
                            <Card 
                                size="small" 
                                style={{ width: '100%', borderColor: isCorrect ? '#52c41a' : '#d9d9d9', backgroundColor: isCorrect ? '#f6ffed' : '#fff' }}
                            >
                                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8 }}>
                                    <Text strong>Đáp án {String.fromCharCode(65 + index)}:</Text>
                                    {isCorrect ? <Tag icon={<CheckCircleOutlined />} color="success">Đáp án đúng</Tag> : <Tag color="default">Sai</Tag>}
                                </div>
                                <Row gutter={16}>
                                    <Col span={12}>
                                        <Text type="secondary" style={{fontSize: 12}}>VN:</Text>
                                        <div style={{marginTop: 4}}>
                                            {choice.contentType === 'IMAGE' && choice.contentValueVi ? (
                                                <img src={choice.contentValueVi} style={{ maxHeight: '80px' }} />
                                            ) : <span dangerouslySetInnerHTML={{ __html: choice.contentValueVi || '(Trống)' }} />}
                                        </div>
                                    </Col>
                                    <Col span={12}>
                                        <Text type="secondary" style={{fontSize: 12}}>EN:</Text>
                                        <div style={{marginTop: 4}}>
                                            {choice.contentType === 'IMAGE' && choice.contentValueEn ? (
                                                <img src={choice.contentValueEn} style={{ maxHeight: '80px' }} />
                                            ) : <span dangerouslySetInnerHTML={{ __html: choice.contentValueEn || '(Trống)' }} />}
                                        </div>
                                    </Col>
                                </Row>
                            </Card>
                        </List.Item>
                    );
                }}
            />
        </Modal>
    );
};

export default QuestionDetailModal;