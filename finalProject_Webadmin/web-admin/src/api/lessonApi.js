import axiosClient from "./axiosClient";

const lessonApi = {
  // Lấy bài học theo chương
  getByChapterId: (chapterId) => {
      return axiosClient.get(`/admin/lessons/by-chapter/${chapterId}`);
  }
};

export default lessonApi;