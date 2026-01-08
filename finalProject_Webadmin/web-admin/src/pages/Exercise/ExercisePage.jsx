import React, { useState, useEffect } from 'react';
import { Card, Table, Button, Space, message, Select, Popconfirm, Tag, Breadcrumb } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';

// Import các API
import exerciseApi from "../../api/exerciseApi"; 
import chapterApi from "../../api/chapterApi";
import lessonApi from "../../api/lessonApi";
import subjectApi from "../../api/subjectApi";

// Import Modal
import ExerciseModal from "./ExerciseModal";

// Helper lấy URL ảnh
const getFullImageUrl = (path) => {
  if (!path) return '';
  if (path.startsWith('http')) return path;
  return `${import.meta.env.VITE_API_URL.replace('/api', '')}${path}`; 
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
      
      // Reset cấp con
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

  // --- Columns Table (ĐÃ CẬP NHẬT RENDER CÂU HỎI) ---
  const columns = [
    {
      title: 'STT',
      dataIndex: 'orderNo',
      width: 70,
      align: 'center',
      render: (text) => <Tag color="blue">#{text}</Tag>
    },
    {
      title: 'Câu hỏi',
      key: 'question',
      width: '40%',
      render: (_, record) => {
        // 1. Nếu là ẢNH
        if (record.questionType === 'IMAGE') {
            return (
                <div style={{ textAlign: 'left' }}>
                    <img 
                        src={getFullImageUrl(record.questionValue)} 
                        alt="Question" 
                        style={{ maxHeight: 80, maxWidth: '100%', objectFit: 'contain', borderRadius: 4, border: '1px solid #eee' }} 
                    />
                    <div style={{ fontSize: 11, color: '#888', marginTop: 4 }}>
                        <Tag color="cyan">IMAGE</Tag>
                    </div>
                </div>
            );
        }
        // 2. Nếu là TEXT
        return (
            <div>
               <div 
                  style={{ maxHeight: 80, overflow: 'hidden', textOverflow: 'ellipsis' }}
                  dangerouslySetInnerHTML={{ __html: record.questionValue }} 
               />
               <div style={{ fontSize: 11, marginTop: 4 }}>
                   <Tag color="blue">TEXT</Tag>
               </div>
            </div>
        );
      }
    },
    {
      title: 'Số bước giải',
      dataIndex: 'solutions',
      align: 'center',
      width: 120,
      render: (solutions) => <Tag color="green">{solutions?.length || 0} bước</Tag>
    },
    {
      title: 'Hành động',
      key: 'action',
      width: 120,
      align: 'center',
      render: (_, record) => (
        <Space>
          <Button icon={<EditOutlined />} size="small" onClick={() => handleEdit(record)} />
          <Popconfirm title="Xóa bài tập này?" onConfirm={() => handleDelete(record.id)}>
            <Button icon={<DeleteOutlined />} size="small" danger />
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: 24 }}>
      <Breadcrumb items={[{ title: 'Trang chủ' }, { title: 'Ngân hàng bài tập' }]} style={{ marginBottom: 16 }} />
      
      <Card title="Quản lý Ngân hàng Bài tập" bordered={false}>
        
        {/* --- BỘ LỌC 3 CẤP --- */}
        <div style={{ marginBottom: 20, display: 'flex', gap: 16, background: '#f9f9f9', padding: 20, borderRadius: 8, border: '1px solid #eee', flexWrap: 'wrap' }}>
            <div style={{ flex: 1, minWidth: 200 }}>
                <div style={{ marginBottom: 6, fontWeight: 600, color: '#555' }}>1. Chọn Môn học:</div>
                <Select 
                    style={{ width: '100%' }} 
                    placeholder="-- Chọn môn --"
                    onChange={setSelectedSubjectId}
                    value={selectedSubjectId}
                    options={subjects.map(s => ({ label: s.name, value: s.id }))}
                    showSearch
                    filterOption={(input, option) => (option?.label ?? '').toLowerCase().includes(input.toLowerCase())}
                />
            </div>

            <div style={{ flex: 1, minWidth: 200 }}>
                <div style={{ marginBottom: 6, fontWeight: 600, color: '#555' }}>2. Chọn Chương:</div>
                <Select 
                    style={{ width: '100%' }} 
                    placeholder="-- Chọn chương --"
                    onChange={setSelectedChapterId}
                    value={selectedChapterId}
                    disabled={!selectedSubjectId}
                    options={chapters.map(c => ({ label: c.title, value: c.id }))}
                    showSearch
                    filterOption={(input, option) => (option?.label ?? '').toLowerCase().includes(input.toLowerCase())}
                />
            </div>

            <div style={{ flex: 1, minWidth: 200 }}>
                <div style={{ marginBottom: 6, fontWeight: 600, color: '#555' }}>3. Chọn Bài học:</div>
                <Select 
                    style={{ width: '100%' }} 
                    placeholder="-- Chọn bài học --"
                    onChange={setSelectedLessonId}
                    value={selectedLessonId}
                    disabled={!selectedChapterId}
                    options={lessons.map(l => ({ label: l.title, value: l.id }))}
                    showSearch
                    filterOption={(input, option) => (option?.label ?? '').toLowerCase().includes(input.toLowerCase())}
                />
            </div>

            <div style={{ display: 'flex', alignItems: 'flex-end' }}>
                <Button 
                    type="primary" 
                    icon={<PlusOutlined />} 
                    onClick={handleCreate}
                    disabled={!selectedLessonId}
                    size="large"
                    style={{ minWidth: 140 }}
                >
                    Thêm Bài Tập
                </Button>
            </div>
        </div>

        {/* --- TABLE --- */}
        <Table 
            columns={columns} 
            dataSource={exercises} 
            rowKey="id" 
            loading={loading}
            locale={{ emptyText: !selectedLessonId ? 'Vui lòng chọn đầy đủ Môn -> Chương -> Bài học' : 'Chưa có dữ liệu bài tập' }}
            pagination={{ pageSize: 10 }}
            bordered
        />
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