import axiosClient from "./axiosClient";

const exerciseApi = {
  // Lấy danh sách bài tập theo Lesson ID
  getByLessonId: (lessonId) => {
    return axiosClient.get(`/admin/exercises/by-lesson/${lessonId}`);
  },

  // Lấy chi tiết
  getById: (id) => {
    return axiosClient.get(`/admin/exercises/${id}`);
  },

  // Tạo mới
  create: (data) => {
    return axiosClient.post("/admin/exercises", data);
  },

  // Cập nhật
  update: (id, data) => {
    return axiosClient.put(`/admin/exercises/${id}`, data);
  },

  // Xóa
  delete: (id) => {
    return axiosClient.delete(`/admin/exercises/${id}`);
  }
};

export default exerciseApi;