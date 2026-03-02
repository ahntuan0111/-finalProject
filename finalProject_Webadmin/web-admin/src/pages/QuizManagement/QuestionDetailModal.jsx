import React from 'react';
import { Modal, Typography, Divider, List, Tag, Card } from 'antd';
import { CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';

const { Title, Text, Paragraph } = Typography;

const QuestionDetailModal = ({ visible, question, onCancel }) => {
    if (!question) return null;

    return (
        <Modal
            title={<span style={{ fontSize: '18px', color: '#1890ff' }}>Chi tiết Câu hỏi số {question.orderNo}</span>}
            open={visible}
            onCancel={onCancel}
            footer={null} // Ẩn nút OK/Cancel vì chỉ để xem
            width={800}
        >
            {/* LỜI GIẢI THÍCH */}
            <Card size="small" style={{ marginBottom: 16, backgroundColor: '#f6ffed', borderColor: '#b7eb8f' }}>
                <Text strong>Lời giải chi tiết: </Text>
                {question.explanation ? (
                    // Dùng dangerouslySetInnerHTML để render các thẻ HTML/công thức từ MathEditor
                    <Paragraph style={{ marginTop: 8 }} dangerouslySetInnerHTML={{ __html: question.explanation }} />
                ) : (
                    <Text italic type="secondary"> Không có lời giải thích.</Text>
                )}
            </Card>

            {/* NỘI DUNG ĐỀ BÀI */}
            <Title level={5}>Nội dung đề bài:</Title>
            <div style={{ padding: '10px 16px', backgroundColor: '#f0f2f5', borderRadius: '8px', marginBottom: '24px' }}>
                {question.contents?.map((content, index) => (
                    <div key={index} style={{ marginBottom: 8 }}>
                        {content.contentType === 'IMAGE' ? (
                            <img src={content.contentValue} alt={`content-${index}`} style={{ maxWidth: '100%', borderRadius: '4px' }} />
                        ) : (
                            <div dangerouslySetInnerHTML={{ __html: content.contentValue }} />
                        )}
                    </div>
                ))}
            </div>

            <Divider dashed />

            {/* DANH SÁCH ĐÁP ÁN */}
            <Title level={5}>Các lựa chọn đáp án:</Title>
            <List
                dataSource={question.choices || []}
                renderItem={(choice, index) => {
                    const isCorrect = choice.correct; // Dựa vào boolean từ DB
                    return (
                        <List.Item style={{ padding: '8px 0' }}>
                            <Card 
                                size="small" 
                                style={{ 
                                    width: '100%', 
                                    borderColor: isCorrect ? '#52c41a' : '#ff4d4f',
                                    backgroundColor: isCorrect ? '#f6ffed' : '#fff1f0'
                                }}
                            >
                                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                                    <div>
                                        <Text strong style={{ marginRight: 10 }}>Đáp án {String.fromCharCode(65 + index)}:</Text>
                                        {choice.contentType === 'IMAGE' ? (
                                            <img src={choice.contentValue} alt={`choice-${index}`} style={{ maxHeight: '100px' }} />
                                        ) : (
                                            // Render HTML từ MathEditor cho đáp án
                                            <span dangerouslySetInnerHTML={{ __html: choice.contentValue }} />
                                        )}
                                    </div>
                                    
                                    {isCorrect ? (
                                        <Tag icon={<CheckCircleOutlined />} color="success">Đáp án đúng</Tag>
                                    ) : (
                                        <Tag icon={<CloseCircleOutlined />} color="error">Sai</Tag>
                                    )}
                                </div>
                            </Card>
                        </List.Item>
                    );
                }}
            />
        </Modal>
    );
};

export default QuestionDetailModal;