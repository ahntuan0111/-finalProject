import axiosClient from "./axiosClient";

const submissionApi = {
  // Lấy tất cả bài nộp (dành cho Admin)
  getAll: () => {
    return axiosClient.get(`/admin/submissions`);
  }
};

export default submissionApi;
