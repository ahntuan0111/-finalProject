import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Select, Button, Table, message, Popconfirm, Space, Tag, Empty } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, BookOutlined, FileTextOutlined } from '@ant-design/icons';
import courseApi from '../../api/courseApi';
import LessonModal from './LessonModal';

const CoursePage = () => {
  // --- STATE ---
  const [subjects, setSubjects] = useState([]);
  const [selectedSubjectId, setSelectedSubjectId] = useState(null);
  
  const [chapters, setChapters] = useState([]);
  const [selectedChapterId, setSelectedChapterId] = useState(null);
  
  const [lessons, setLessons] = useState([]);
  const [nextLessonOrder, setNextLessonOrder] = useState(1); // State lưu thứ tự bài học tiếp theo

  // Modal State
  const [isLessonModalOpen, setIsLessonModalOpen] = useState(false);
  const [editingLesson, setEditingLesson] = useState(null); // null = create mode
  const [loading, setLoading] = useState(false);

  // --- FETCH DATA ---

  // 1. Load Subjects khi vào trang
  useEffect(() => {
    const fetchSubjects = async () => {
      try {
        const res = await courseApi.getAllSubjects(); 
        if (res.success) {
          setSubjects(res.data);
          // Mặc định chọn môn đầu tiên nếu có
          if (res.data.length > 0) setSelectedSubjectId(res.data[0].id);
        }
      } catch (error) {
        message.error("Lỗi tải danh sách môn học");
      }
    };
    fetchSubjects();
  }, []);

  // 2. Load Chapters khi chọn Subject
  useEffect(() => {
    if (selectedSubjectId) {
      fetchChapters(selectedSubjectId);
      setLessons([]); // Reset lessons
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

  // 3. Load Lessons khi chọn Chapter
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
  const handleAddChapter = async () => {
    const title = window.prompt("Nhập tên chương mới:");
    if (!title) return;
    try {
      await courseApi.createChapter({
        subjectId: selectedSubjectId,
        title: title,
        orderNo: chapters.length + 1
      });
      message.success("Tạo chương thành công");
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
    
    // Logic tự động tính số thứ tự tiếp theo
    if (!lesson) {
        const maxOrder = lessons.length > 0 
            ? Math.max(...lessons.map(l => l.orderNo || 0)) 
            : 0;
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
        contents: values.contents // Array từ form
      };

      if (editingLesson) {
        // Update
        await courseApi.updateLesson(editingLesson.id, payload);
        message.success("Cập nhật bài học thành công");
      } else {
        // Create
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

  // --- COLUMNS TABLE ---
  const chapterColumns = [
    { title: 'STT', dataIndex: 'orderNo', width: 60 },
    { title: 'Tên Chương', dataIndex: 'title', render: (text) => <b>{text}</b> },
    {
      title: '',
      key: 'action',
      width: 80,
      render: (_, record) => (
        <Popconfirm title="Xóa chương này?" onConfirm={(e) => { e.stopPropagation(); handleDeleteChapter(record.id); }}>
           <Button type="text" danger icon={<DeleteOutlined />} onClick={(e) => e.stopPropagation()}/>
        </Popconfirm>
      ),
    },
  ];

  const lessonColumns = [
    { title: 'STT', dataIndex: 'orderNo', width: 60 },
    { title: 'Tên Bài Học', dataIndex: 'title', render: (text) => <span style={{color: '#1677ff', fontWeight: 500}}>{text}</span> },
    { 
        title: 'Video', 
        dataIndex: 'videoUrl', 
        render: (url) => url ? <Tag color="red">Youtube</Tag> : <Tag>None</Tag> 
    },
    {
      title: 'Hành động',
      key: 'action',
      render: (_, record) => (
        <Space>
          <Button size="small" icon={<EditOutlined />} onClick={() => handleOpenLessonModal(record)}>Sửa</Button>
          <Popconfirm title="Xóa bài học?" onConfirm={() => handleDeleteLesson(record.id)}>
            <Button size="small" danger icon={<DeleteOutlined />}>Xóa</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 0 }}>
      {/* HEADER SELECTOR */}
      <Card style={{ marginBottom: 16 }}>
        <Space>
            <span style={{ fontWeight: 'bold' }}>Chọn Môn Học:</span>
            <Select 
                style={{ width: 250 }} 
                value={selectedSubjectId}
                onChange={setSelectedSubjectId}
                placeholder="Chọn môn học..."
            >
                {subjects.map(sub => (
                    <Select.Option key={sub.id} value={sub.id}>
                        {sub.name} ({sub.code.toUpperCase()})
                    </Select.Option>
                ))}
            </Select>
        </Space>
      </Card>

      <Row gutter={16}>
        {/* LEFT: CHAPTER LIST */}
        <Col span={8}>
          <Card 
            title={<><BookOutlined /> Danh sách Chương</>} 
            extra={<Button type="primary" size="small" icon={<PlusOutlined />} onClick={handleAddChapter}>Thêm</Button>}
            bodyStyle={{ padding: 0 }}
          >
            <Table
              dataSource={chapters}
              columns={chapterColumns}
              rowKey="id"
              pagination={false}
              onRow={(record) => ({
                onClick: () => setSelectedChapterId(record.id),
                style: { cursor: 'pointer', background: record.id === selectedChapterId ? '#e6f7ff' : 'white' }
              })}
            />
             {!selectedSubjectId && <Empty description="Vui lòng chọn môn học" image={Empty.PRESENTED_IMAGE_SIMPLE} />}
          </Card>
        </Col>

        {/* RIGHT: LESSON LIST */}
        <Col span={16}>
            {selectedChapterId ? (
                <Card 
                    title={<><FileTextOutlined /> Bài học trong chương</>} 
                    extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => handleOpenLessonModal(null)}>Thêm Bài học</Button>}
                >
                    <Table 
                        dataSource={lessons} 
                        columns={lessonColumns} 
                        rowKey="id"
                        loading={loading}
                    />
                </Card>
            ) : (
                <Card>
                    <Empty description="Chọn một chương bên trái để xem bài học" />
                </Card>
            )}
        </Col>
      </Row>

      {/* MODAL EDIT LESSON */}
      <LessonModal
        open={isLessonModalOpen}
        onClose={() => setIsLessonModalOpen(false)}
        onSubmit={handleLessonSubmit}
        initialValues={editingLesson}
        loading={loading}
        nextOrderNo={nextLessonOrder} // Truyền prop mới vào
      />
    </div>
  );
};

export default CoursePage;