import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Button, Table, message, Popconfirm, Space, Empty, List, Typography, Segmented, Input, Modal } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, BookOutlined, PlayCircleOutlined, SearchOutlined, UnorderedListOutlined } from '@ant-design/icons';
import courseApi from '../../api/courseApi';
import LessonModal from './LessonModal';

const { Title, Text } = Typography;

const CoursePage = () => {
  // --- STATE ---
  const [subjects, setSubjects] = useState([]);
  const [selectedSubjectId, setSelectedSubjectId] = useState(null);
  
  const [chapters, setChapters] = useState([]);
  const [selectedChapterId, setSelectedChapterId] = useState(null);
  
  const [lessons, setLessons] = useState([]);
  const [searchText, setSearchText] = useState("");
  const [nextLessonOrder, setNextLessonOrder] = useState(1);

  // Lesson Modal State
  const [isLessonModalOpen, setIsLessonModalOpen] = useState(false);
  const [editingLesson, setEditingLesson] = useState(null);
  const [loading, setLoading] = useState(false);

  // Chapter Modal State (MỚI THÊM)
  const [isChapterModalOpen, setIsChapterModalOpen] = useState(false);
  const [newChapterTitle, setNewChapterTitle] = useState("");

  // Hover state cho nút Xóa chương
  const [hoveredChapterId, setHoveredChapterId] = useState(null);

  // --- FETCH DATA ---
  useEffect(() => {
    const fetchSubjects = async () => {
      try {
        const res = await courseApi.getAllSubjects(); 
        if (res.success) {
          setSubjects(res.data);
          if (res.data.length > 0) setSelectedSubjectId(res.data[0].id);
        }
      } catch (error) {
        message.error("Lỗi tải danh sách môn học");
      }
    };
    fetchSubjects();
  }, []);

  useEffect(() => {
    if (selectedSubjectId) {
      fetchChapters(selectedSubjectId);
      setLessons([]);
      setSelectedChapterId(null);
    }
  }, [selectedSubjectId]);

  const fetchChapters = async (subjectId) => {
    try {
      const res = await courseApi.getChaptersBySubject(subjectId);
      if (res.success) setChapters(res.data);
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    if (selectedChapterId) {
      fetchLessons(selectedChapterId);
    }
  }, [selectedChapterId]);

  const fetchLessons = async (chapterId) => {
    setLoading(true);
    try {
      const res = await courseApi.getLessonsByChapter(chapterId);
      if (res.success) setLessons(res.data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  // --- HANDLERS CHO CHAPTER ---
  
  // Mở modal thêm chương thay vì dùng window.prompt
  const showAddChapterModal = () => {
    setNewChapterTitle(""); // Xóa text cũ nếu có
    setIsChapterModalOpen(true);
  };

  // Xử lý khi bấm nút "Lưu" trong Modal Chapter
  const handleChapterSubmit = async () => {
    if (!newChapterTitle.trim()) {
        message.warning("Vui lòng nhập tên chương!");
        return;
    }
    
    try {
      await courseApi.createChapter({
        subjectId: selectedSubjectId,
        title: newChapterTitle,
        orderNo: chapters.length + 1
      });
      message.success("Tạo chương thành công");
      setIsChapterModalOpen(false);
      fetchChapters(selectedSubjectId);
    } catch (e) {
      message.error("Lỗi tạo chương");
    }
  };

  const handleDeleteChapter = async (id) => {
    try {
      await courseApi.deleteChapter(id);
      message.success("Xóa chương thành công");
      fetchChapters(selectedSubjectId);
      if (selectedChapterId === id) {
        setSelectedChapterId(null);
        setLessons([]);
      }
    } catch (e) {
      message.error("Lỗi xóa chương");
    }
  };

  // --- HANDLERS CHO LESSON ---
  const handleOpenLessonModal = (lesson = null) => {
    setEditingLesson(lesson);
    if (!lesson) {
        const maxOrder = lessons.length > 0 ? Math.max(...lessons.map(l => l.orderNo || 0)) : 0;
        setNextLessonOrder(maxOrder + 1);
    }
    setIsLessonModalOpen(true);
  };

  const handleLessonSubmit = async (values) => {
    setLoading(true);
    try {
      const payload = {
        chapterId: selectedChapterId,
        title: values.title,
        videoUrl: values.videoUrl,
        orderNo: values.orderNo,
        contents: values.contents
      };

      if (editingLesson) {
        await courseApi.updateLesson(editingLesson.id, payload);
        message.success("Cập nhật bài học thành công");
      } else {
        await courseApi.createLesson(payload);
        message.success("Tạo bài học thành công");
      }
      setIsLessonModalOpen(false);
      fetchLessons(selectedChapterId);
    } catch (error) {
      message.error("Có lỗi xảy ra");
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteLesson = async (id) => {
    try {
      await courseApi.deleteLesson(id);
      message.success("Xóa bài học thành công");
      fetchLessons(selectedChapterId);
    } catch (error) {
      message.error("Lỗi xóa bài học");
    }
  };

  const filteredLessons = lessons.filter(l => 
    l.title.toLowerCase().includes(searchText.toLowerCase())
  );

  const lessonColumns = [
    { 
      title: 'STT', 
      dataIndex: 'orderNo', 
      width: 60,
      render: (text) => <Text type="secondary">{text}</Text>
    },
    { 
      title: 'TÊN BÀI HỌC', 
      dataIndex: 'title', 
      render: (text) => <Text strong style={{ color: '#1890ff' }}>{text}</Text> 
    },
    { 
        title: 'VIDEO', 
        dataIndex: 'videoUrl', 
        render: (url) => url ? (
          <Space>
            <PlayCircleOutlined style={{ color: '#ff4d4f' }} />
            <Text type="danger">Youtube</Text>
          </Space>
        ) : <Text type="secondary">-</Text> 
    },
    {
      title: 'HÀNH ĐỘNG',
      key: 'action',
      align: 'right',
      render: (_, record) => (
        <Space>
          <Button size="small" type="text" icon={<EditOutlined />} onClick={() => handleOpenLessonModal(record)}>Sửa</Button>
          <Popconfirm title="Xóa bài học này?" onConfirm={() => handleDeleteLesson(record.id)}>
            <Button size="small" type="text" danger icon={<DeleteOutlined />}>Xóa</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ backgroundColor: '#f5f7fa', minHeight: '100vh', padding: '24px' }}>
      
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <div>
          <Text type="secondary" style={{ fontSize: '13px' }}>Dashboard {'>'} Quản lý Bài học</Text>
          <Title level={3} style={{ margin: '4px 0 16px 0' }}>Quản lý Nội dung Học tập</Title>
          
          {subjects.length > 0 && (
            <Segmented 
              size="large"
              options={subjects.map(s => ({ label: s.name, value: s.id }))}
              value={selectedSubjectId}
              onChange={setSelectedSubjectId}
              style={{ padding: '4px', background: '#fff', border: '1px solid #e8e8e8', boxShadow: '0 2px 8px rgba(0,0,0,0.05)' }}
            />
          )}
        </div>
      </div>

      <Row gutter={24}>
        <Col span={7}>
          <Card 
            variant="borderless"
            style={{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.05)' }}
            styles={{ body: { padding: '0' } }}
          >
            <div style={{ padding: '16px 20px', borderBottom: '1px solid #f0f0f0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Space>
                <UnorderedListOutlined style={{ color: '#1890ff' }} />
                <Text strong style={{ fontSize: '16px' }}>Danh sách Chương</Text>
              </Space>
              {/* Đã sửa onClick gọi hàm showAddChapterModal */}
              <Button type="link" size="small" icon={<PlusOutlined />} onClick={showAddChapterModal}>Thêm Chương</Button>
            </div>
            
            <List
              itemLayout="horizontal"
              dataSource={chapters}
              renderItem={(chapter, index) => {
                const isActive = chapter.id === selectedChapterId;
                return (
                  <List.Item
                    onClick={() => setSelectedChapterId(chapter.id)}
                    onMouseEnter={() => setHoveredChapterId(chapter.id)}
                    onMouseLeave={() => setHoveredChapterId(null)}
                    style={{
                      padding: '16px 20px',
                      cursor: 'pointer',
                      borderBottom: '1px solid #f0f0f0',
                      backgroundColor: isActive ? '#e6f4ff' : 'transparent',
                      borderLeft: isActive ? '4px solid #1890ff' : '4px solid transparent',
                      transition: 'all 0.3s'
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', width: '100%', alignItems: 'center' }}>
                      <Space>
                        <Text type="secondary" style={{ fontSize: '12px', fontWeight: 'bold' }}>{String(index + 1).padStart(2, '0')}</Text>
                        <Text strong={isActive} style={{ color: isActive ? '#1890ff' : '#333', marginLeft: '8px' }}>
                          {chapter.title}
                        </Text>
                      </Space>
                      
                      {hoveredChapterId === chapter.id && (
                        <Popconfirm 
                            title="Xóa chương này?" 
                            onConfirm={(e) => { e.stopPropagation(); handleDeleteChapter(chapter.id); }}
                            onCancel={(e) => e.stopPropagation()}
                        >
                            <Button size="small" type="text" danger icon={<DeleteOutlined />} onClick={(e) => e.stopPropagation()}/>
                        </Popconfirm>
                      )}
                    </div>
                  </List.Item>
                )
              }}
            />
            {chapters.length === 0 && <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="Chưa có chương nào" style={{ padding: '20px 0' }}/>}
          </Card>
        </Col>

        <Col span={17}>
            <Card 
                variant="borderless"
                style={{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.05)', minHeight: '500px' }}
                styles={{ body: { padding: '24px' } }}
            >
                {selectedChapterId ? (
                    <>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '24px' }}>
                            <div>
                                <Title level={4} style={{ margin: 0 }}>Bài học trong chương</Title>
                                <Text type="secondary">
                                    Chương: {chapters.find(c => c.id === selectedChapterId)?.title}
                                </Text>
                            </div>
                            <Space>
                                <Input 
                                    placeholder="Tìm kiếm bài học..." 
                                    prefix={<SearchOutlined />} 
                                    style={{ width: 250, borderRadius: '20px' }}
                                    onChange={(e) => setSearchText(e.target.value)}
                                    allowClear
                                />
                                <Button type="primary" style={{ borderRadius: '20px' }} icon={<PlusOutlined />} onClick={() => handleOpenLessonModal(null)}>Thêm Bài học</Button>
                            </Space>
                        </div>
                        
                        <Table 
                            dataSource={filteredLessons} 
                            columns={lessonColumns} 
                            rowKey="id"
                            loading={loading}
                            pagination={{ pageSize: 8 }}
                            rowClassName={() => 'custom-table-row'}
                        />
                    </>
                ) : (
                    <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', height: '100%', paddingTop: '100px' }}>
                        <BookOutlined style={{ fontSize: '64px', color: '#e0e0e0', marginBottom: '16px' }} />
                        <Title level={4} style={{ color: '#8c8c8c' }}>Chưa chọn Chương học</Title>
                        <Text type="secondary">Vui lòng chọn một chương ở menu bên trái để quản lý bài học.</Text>
                    </div>
                )}
            </Card>
        </Col>
      </Row>

      {/* --- MODAL THÊM BÀI HỌC --- */}
      <LessonModal
        open={isLessonModalOpen}
        onClose={() => setIsLessonModalOpen(false)}
        onSubmit={handleLessonSubmit}
        initialValues={editingLesson}
        loading={loading}
        nextOrderNo={nextLessonOrder}
      />

      {/* --- MODAL THÊM CHƯƠNG MỚI (THAY THẾ PROMPT) --- */}
      <Modal
        title="Thêm Chương mới"
        open={isChapterModalOpen}
        onOk={handleChapterSubmit}
        onCancel={() => setIsChapterModalOpen(false)}
        okText="Lưu Chương"
        cancelText="Hủy"
        width={400}
        destroyOnClose // Reset lại data khi đóng
      >
        <div style={{ marginTop: 16, marginBottom: 8 }}>
            <Text strong>Tên chương <span style={{color:'red'}}>*</span></Text>
            <Input 
                style={{ marginTop: 8 }}
                size="large"
                placeholder="Ví dụ: Chương 1: Động học chất điểm" 
                value={newChapterTitle}
                onChange={(e) => setNewChapterTitle(e.target.value)}
                onPressEnter={handleChapterSubmit} // Hỗ trợ nhấn Enter để lưu
                autoFocus // Tự động focus vào ô input khi mở Modal
            />
        </div>
      </Modal>

    </div>
  );
};

export default CoursePage;