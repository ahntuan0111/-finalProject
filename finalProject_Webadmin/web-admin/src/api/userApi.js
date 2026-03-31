import axiosClient from './axiosClient';

const userApi = {
  // Lấy danh sách user (Cần bổ sung API này ở Backend nếu chưa có)
  getAll: () => {
    return axiosClient.get('/admin/users');
  },
  
  // Xóa user (Optional)
  delete: (id) => {
    return axiosClient.delete(`/admin/users/${id}`);
  },

  // Lấy hồ sơ chi tiết (Mới thêm cho Dashboard)
  getProfile: (id) => {
    return axiosClient.get(`/admin/dashboard/student/${id}`);
  }
};

export default userApi;