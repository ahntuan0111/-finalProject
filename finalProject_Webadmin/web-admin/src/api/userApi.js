import axiosClient from './axiosClient';

const userApi = {
  // Lấy danh sách user (Cần bổ sung API này ở Backend nếu chưa có)
  getAll: () => {
    return axiosClient.get('/admin/users');
  },
  
  // Xóa user (Optional)
  delete: (id) => {
    return axiosClient.delete(`/admin/users/${id}`);
  }
};

export default userApi;