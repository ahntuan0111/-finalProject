import axiosClient from './axiosClient';

const courseApi = {
  // --- SUBJECT ---
  // Lấy danh sách Môn học (VD: Toán 6, Toán 7...)
  // Cần BE hỗ trợ API lấy all subjects hoặc filter. 
  // Tạm thời giả định có API lấy danh sách Subject theo Grade hoặc All
  getAllSubjects: () => {
    // Nếu chưa có API này, bạn cần thêm ở BE AdminContentServiceImpl.getAllSubjects
    // Tạm thời mình hardcode logic gọi BE, bạn nhớ thêm API này ở BE nhé
    return axiosClient.get('/admin/subjects'); 
  },

  // --- CHAPTER ---
  getChaptersBySubject: (subjectId) => {
    return axiosClient.get(`/admin/subjects/${subjectId}/chapters`);
  },
  createChapter: (data) => {
    return axiosClient.post('/admin/chapters', data);
  },
  updateChapter: (id, data) => {
    return axiosClient.put(`/admin/chapters/${id}`, data);
  },
  deleteChapter: (id) => {
    return axiosClient.delete(`/admin/chapters/${id}`);
  },

  // --- LESSON ---
  getLessonsByChapter: (chapterId) => {
    return axiosClient.get(`/admin/chapters/${chapterId}/lessons`);
  },
  getLessonDetail: (id) => {
    return axiosClient.get(`/admin/lessons/${id}`);
  },
  createLesson: (data) => {
    return axiosClient.post('/admin/lessons', data);
  },
  updateLesson: (id, data) => {
    return axiosClient.put(`/admin/lessons/${id}`, data);
  },
  deleteLesson: (id) => {
    return axiosClient.delete(`/admin/lessons/${id}`);
  },
};

export default courseApi;