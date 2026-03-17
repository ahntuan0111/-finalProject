import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api/documents';

// Hàm hỗ trợ tự động lấy Token từ LocalStorage
const getAuthHeader = () => {
    // Lưu ý: Đảm bảo 'token' là tên biến bạn dùng khi lưu ở hàm Login
    const token = localStorage.getItem('access_token'); 
    if (token) {
        return { 'Authorization': `Bearer ${token}` };
    }
    return {};
};

export const documentApi = {
    // R: Lấy tất cả tài liệu
    getAll: async () => {
        const response = await axios.get(`${BASE_URL}/all`, {
            headers: getAuthHeader() 
        });
        return response.data;
    },

    // C: Upload tài liệu mới
    upload: async (file, titleVi, titleEn, descriptionVi, descriptionEn, subjectCode, grade, examTypeVi, examTypeEn) => {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('titleVi', titleVi);
        if (titleEn) formData.append('titleEn', titleEn);
        if (descriptionVi) formData.append('descriptionVi', descriptionVi);
        if (descriptionEn) formData.append('descriptionEn', descriptionEn);
        formData.append('subjectCode', subjectCode);
        formData.append('grade', grade);
        if (examTypeVi) formData.append('examTypeVi', examTypeVi);
        if (examTypeEn) formData.append('examTypeEn', examTypeEn);

        const response = await axios.post(`${BASE_URL}/upload`, formData, {
            // SỬA LỖI Ở ĐÂY: Xóa bỏ 'Content-Type': 'multipart/form-data'
            // Chỉ cần truyền mỗi Auth Header, Axios sẽ tự động thêm Content-Type + Boundary
            headers: getAuthHeader() 
        });
        return response.data;
    },

    // U: Cập nhật thông tin
    update: async (id, documentData) => {
        const response = await axios.put(`${BASE_URL}/${id}`, documentData, {
            headers: getAuthHeader() 
        });
        return response.data;
    },

    // D: Xóa tài liệu
    delete: async (id) => {
        const response = await axios.delete(`${BASE_URL}/${id}`, {
            headers: getAuthHeader() 
        });
        return response.data;
    }
};