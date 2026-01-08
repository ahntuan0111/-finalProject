import axiosClient from "./axiosClient";

const chapterApi = {
  // Lấy tất cả chương (ít dùng)
  getAll: () => {
      return axiosClient.get('/admin/chapters');
  },
  
  // Quan trọng: Lấy chương theo ID môn học
  getBySubjectId: (subjectId) => {
      return axiosClient.get(`/admin/chapters/by-subject/${subjectId}`);
  }
};

export default chapterApi;