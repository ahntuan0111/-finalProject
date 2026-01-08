import axiosClient from "./axiosClient";

const subjectApi = {
  // Lấy danh sách tất cả môn học
  getAll: () => {
    return axiosClient.get('/admin/subjects');
  },
  
  // (Optional) Lấy chi tiết môn học nếu cần
  getById: (id) => {
    return axiosClient.get(`/admin/subjects/${id}`);
  }
};

export default subjectApi;