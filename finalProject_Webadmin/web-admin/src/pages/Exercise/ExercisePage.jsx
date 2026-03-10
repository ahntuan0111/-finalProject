import React, { useState, useEffect, useRef } from 'react';
// ĐÃ BỔ SUNG Row, Col, Image VÀO DÒNG DƯỚI ĐÂY
import { Card, Table, Button, Space, message, Select, Popconfirm, Tag, Breadcrumb, Empty, Typography, Tooltip, Row, Col, Image } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, BookOutlined, FolderOpenOutlined, FileTextOutlined } from '@ant-design/icons';

// Import các API
import exerciseApi from "../../api/exerciseApi"; 
import chapterApi from "../../api/chapterApi";
import lessonApi from "../../api/lessonApi";
import subjectApi from "../../api/subjectApi";

// Import Modal
import ExerciseModal from "./ExerciseModal";

const { Title, Text } = Typography;

// Helper lấy URL ảnh
const getFullImageUrl = (path) => {
  if (!path) return '';
  if (path.startsWith('http')) return path;
  return `${import.meta.env.VITE_API_URL.replace('/api', '')}${path}`; 
};

// Component con để Render LaTeX an toàn trong Table
const MathRenderer = ({ htmlContent }) => {
  const containerRef = useRef(null);

  useEffect(() => {
    // Gọi MathJax để render lại toán học mỗi khi content thay đổi
    if (window.MathJax && containerRef.current) {
      window.MathJax.typesetPromise([containerRef.current]).catch((err) => console.log(err));
    }
  }, [htmlContent]);

  return (
    <div 
      ref={containerRef}
      style={{ 
        display: '-webkit-box', 
        WebkitLineClamp: 2, 
        WebkitBoxOrient: 'vertical', 
        overflow: 'hidden', 
        textOverflow: 'ellipsis',
        lineHeight: '1.6'
      }}
      dangerouslySetInnerHTML={{ __html: htmlContent }} 
    />
  );
};

const ExercisePage = () => {
  // --- State dữ liệu ---
  const [subjects, setSubjects] = useState([]);
  const [chapters, setChapters] = useState([]);
  const [lessons, setLessons] = useState([]);
  const [exercises, setExercises] = useState([]);

  // --- State bộ lọc (Filter) ---
  const [selectedSubjectId, setSelectedSubjectId] = useState(null);
  const [selectedChapterId, setSelectedChapterId] = useState(null);
  const [selectedLessonId, setSelectedLessonId] = useState(null);

  // --- State UI ---
  const [loading, setLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingExercise, setEditingExercise] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  // 1. Load danh sách Môn học
  useEffect(() => {
    const fetchSubjects = async () => {
      try {
        const res = await subjectApi.getAll();
        if (res.success) setSubjects(res.data);
      } catch (error) {
        console.error("Lỗi tải môn học:", error);
      }
    };
    fetchSubjects();
    
    // Đảm bảo MathJax script được tải vào
    if (!document.getElementById('mathjax-script')) {
        const script = document.createElement('script');
        script.id = 'mathjax-script';
        script.src = 'https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js';
        script.async = true;
        document.head.appendChild(script);
    }
  }, []);

  // 2. Load Chương khi chọn Môn học
  useEffect(() => {
    if (selectedSubjectId) {
      const fetchChapters = async () => {
        try {
          const res = await chapterApi.getBySubjectId(selectedSubjectId);
          if (res.success) setChapters(res.data);
        } catch (error) {
          console.error("Lỗi tải chương:", error);
        }
      };
      fetchChapters();
      
      setChapters([]);
      setSelectedChapterId(null);
      setLessons([]);
      setSelectedLessonId(null);
      setExercises([]);
    }
  }, [selectedSubjectId]);

  // 3. Load Bài học khi chọn Chương
  useEffect(() => {
    if (selectedChapterId) {
      const fetchLessons = async () => {
        try {
          const res = await lessonApi.getByChapterId(selectedChapterId);
          if (res.success) setLessons(res.data);
        } catch (error) {
          console.error("Lỗi tải bài học:", error);
        }
      };
      fetchLessons();
      
      setSelectedLessonId(null);
      setExercises([]);
    } else {
        setLessons([]);
    }
  }, [selectedChapterId]);

  // 4. Load Bài tập khi chọn Bài học
  useEffect(() => {
    if (selectedLessonId) {
      fetchExercises(selectedLessonId);
    } else {
        setExercises([]);
    }
  }, [selectedLessonId]);

  const fetchExercises = async (lessonId) => {
    setLoading(true);
    try {
      const res = await exerciseApi.getByLessonId(lessonId);
      if (res.success) {
        setExercises(res.data);
      }
    } catch (error) {
      message.error("Lỗi tải danh sách bài tập");
    } finally {
      setLoading(false);
    }
  };

  // --- CRUD Logic ---
  const handleCreate = () => {
    if (!selectedLessonId) return;
    setEditingExercise(null);
    setIsModalOpen(true);
  };

  const handleEdit = (record) => {
    setEditingExercise(record);
    setIsModalOpen(true);
  };

  const handleDelete = async (id) => {
    try {
      await exerciseApi.delete(id);
      message.success("Đã xóa bài tập");
      fetchExercises(selectedLessonId);
    } catch (error) {
      message.error("Xóa thất bại");
    }
  };

  const handleSubmit = async (values) => {
    setSubmitting(true);
    try {
      const payload = {
        ...values,
        lessonId: selectedLessonId
      };

      if (editingExercise) {
        await exerciseApi.update(editingExercise.id, payload);
        message.success("Cập nhật thành công");
      } else {
        await exerciseApi.create(payload);
        message.success("Tạo mới thành công");
      }
      setIsModalOpen(false);
      fetchExercises(selectedLessonId);
    } catch (error) {
      message.error("Có lỗi xảy ra");
    } finally {
      setSubmitting(false);
    }
  };

  const currentLessonName = lessons.find(l => l.id === selectedLessonId)?.title || "";

  // --- Columns Table ---
  const columns = [
    {
      title: 'STT',
      dataIndex: 'orderNo',
      width: 80,
      align: 'center',
      render: (text) => <Text type="secondary" strong>#{text}</Text>
    },
    {
      title: 'Nội dung câu hỏi',
      key: 'question',
      width: '55%',
      render: (_, record) => {
        if (record.questionType === 'IMAGE') {
            return (
                <div style={{ display: 'flex', alignItems: 'flex-start', gap: 12 }}>
                    <div style={{ padding: '4px 8px', background: '#e6f4ff', borderRadius: 4 }}>
                        <Text type="secondary" style={{ fontSize: 10, fontWeight: 'bold', color: '#1677ff' }}>ẢNH</Text>
                    </div>
                    <Image 
                        src={getFullImageUrl(record.questionValue)} 
                        alt="Question" 
                        style={{ maxHeight: 60, maxWidth: 200, objectFit: 'contain', borderRadius: 4, border: '1px solid #f0f0f0' }} 
                        fallback="https://via.placeholder.com/200x60?text=L%E1%BB%97i+%E1%BA%A3nh"
                    />
                </div>
            );
        }
        
        return (
            <div style={{ display: 'flex', alignItems: 'flex-start', gap: 12 }}>
               <div style={{ padding: '4px 8px', background: '#f6ffed', borderRadius: 4, marginTop: 4 }}>
                    <Text type="secondary" style={{ fontSize: 10, fontWeight: 'bold', color: '#52c41a' }}>VĂN BẢN</Text>
               </div>
               <MathRenderer htmlContent={record.questionValue} />
            </div>
        );
      }
    },
    {
      title: 'Các bước giải',
      dataIndex: 'solutions',
      align: 'center',
      width: 150,
      render: (solutions) => (
        <Tag color="processing" style={{ borderRadius: 12, padding: '2px 10px' }}>
            {solutions?.length || 0} bước
        </Tag>
      )
    },
    {
      title: 'Thao tác',
      key: 'action',
      width: 120,
      align: 'right',
      render: (_, record) => (
        <Space size="middle">
          <Tooltip title="Chỉnh sửa bài tập">
              <Button type="text" icon={<EditOutlined style={{ color: '#1677ff' }}/>} onClick={() => handleEdit(record)} />
          </Tooltip>
          <Tooltip title="Xóa bài tập">
              <Popconfirm title="Bạn có chắc chắn muốn xóa bài tập này?" onConfirm={() => handleDelete(record.id)}>
                <Button type="text" danger icon={<DeleteOutlined />} />
              </Popconfirm>
          </Tooltip>
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: '24px', backgroundColor: '#f5f7fa', minHeight: '100vh' }}>
      
      {/* --- BREADCRUMB & HEADER --- */}
      <div style={{ marginBottom: '24px' }}>
        <Breadcrumb items={[{ title: 'Dashboard' }, { title: 'Ngân hàng bài tập' }]} style={{ fontSize: '13px' }}/>
        <Title level={3} style={{ margin: '8px 0 0 0' }}>Quản lý Ngân hàng Bài tập</Title>
      </div>
      
      {/* --- BỘ LỌC (TOOLBAR DẢI BĂNG) --- */}
      <Card 
        variant="borderless" 
        style={{ marginBottom: 24, borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}
        styles={{ body: { padding: '16px 24px' } }}
      >
        <Row gutter={[16, 16]} align="middle">
            <Col xs={24} md={8}>
                <Select 
                    size="large"
                    style={{ width: '100%' }} 
                    placeholder={<span><BookOutlined /> Chọn Môn học</span>}
                    onChange={setSelectedSubjectId}
                    value={selectedSubjectId}
                    options={subjects.map(s => ({ label: s.name, value: s.id }))}
                    showSearch
                    filterOption={(input, option) => (option?.label ?? '').toLowerCase().includes(input.toLowerCase())}
                />
            </Col>

            <Col xs={24} md={8}>
                <Select 
                    size="large"
                    style={{ width: '100%' }} 
                    placeholder={<span><FolderOpenOutlined /> Chọn Chương</span>}
                    onChange={setSelectedChapterId}
                    value={selectedChapterId}
                    disabled={!selectedSubjectId}
                    options={chapters.map(c => ({ label: c.title, value: c.id }))}
                    showSearch
                    filterOption={(input, option) => (option?.label ?? '').toLowerCase().includes(input.toLowerCase())}
                />
            </Col>

            <Col xs={24} md={8}>
                <Select 
                    size="large"
                    style={{ width: '100%' }} 
                    placeholder={<span><FileTextOutlined /> Chọn Bài học</span>}
                    onChange={setSelectedLessonId}
                    value={selectedLessonId}
                    disabled={!selectedChapterId}
                    options={lessons.map(l => ({ label: l.title, value: l.id }))}
                    showSearch
                    filterOption={(input, option) => (option?.label ?? '').toLowerCase().includes(input.toLowerCase())}
                />
            </Col>
        </Row>
      </Card>

      {/* --- KHU VỰC DỮ LIỆU --- */}
      <Card 
        variant="borderless" 
        style={{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.04)', minHeight: '400px' }}
        styles={{ body: { padding: selectedLessonId ? '0' : '24px' } }}
      >
        {!selectedLessonId ? (
            <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', height: '100%', paddingTop: '60px', paddingBottom: '60px' }}>
                <FolderOpenOutlined style={{ fontSize: '64px', color: '#e0e0e0', marginBottom: '16px' }} />
                <Title level={4} style={{ color: '#8c8c8c' }}>Chưa có dữ liệu hiển thị</Title>
                <Text type="secondary">Vui lòng chọn lần lượt Môn học, Chương và Bài học ở thanh công cụ phía trên để xem danh sách bài tập.</Text>
            </div>
        ) : (
            <>
                <div style={{ padding: '20px 24px', borderBottom: '1px solid #f0f0f0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                        <Title level={5} style={{ margin: 0, color: '#1f1f1f' }}>Danh sách Bài tập</Title>
                        <Text type="secondary" style={{ fontSize: 13 }}>Thuộc bài: <span style={{ color: '#1677ff', fontWeight: 500 }}>{currentLessonName}</span></Text>
                    </div>
                    <Button 
                        type="primary" 
                        icon={<PlusOutlined />} 
                        onClick={handleCreate}
                        style={{ borderRadius: '6px' }}
                    >
                        Thêm Bài Tập Mới
                    </Button>
                </div>

                <Table 
                    columns={columns} 
                    dataSource={exercises} 
                    rowKey="id" 
                    loading={loading}
                    locale={{ emptyText: <Empty description="Bài học này chưa có bài tập nào" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
                    pagination={{ pageSize: 10 }}
                    rowClassName={() => 'custom-table-row'}
                />
            </>
        )}
      </Card>

      <ExerciseModal 
        open={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleSubmit}
        initialValues={editingExercise}
        loading={submitting}
        nextOrderNo={exercises.length + 1}
      />
    </div>
  );
};

export default ExercisePage;