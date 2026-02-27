import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api/documents';

// Hàm hỗ trợ tự động lấy Token từ LocalStorage
const getAuthHeader = () => {
    // Lưu ý: Đảm bảo 'token' là tên biến bạn dùng khi lưu ở hàm Login
    // Nếu bạn lưu là 'accessToken', hãy đổi lại cho đúng.
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
            headers: getAuthHeader() // Truyền Token vào header
        });
        return response.data;
    },

    // C: Upload tài liệu mới
    upload: async (file, title, description, subjectCode, grade, examType) => {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('title', title);
        formData.append('description', description || '');
        formData.append('subjectCode', subjectCode);
        formData.append('grade', grade);
        formData.append('examType', examType);

        const response = await axios.post(`${BASE_URL}/upload`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
                ...getAuthHeader() // Truyền Token kèm với Multipart header
            }
        });
        return response.data;
    },

    // U: Cập nhật thông tin
    update: async (id, documentData) => {
        const response = await axios.put(`${BASE_URL}/${id}`, documentData, {
            headers: getAuthHeader() // Truyền Token vào header
        });
        return response.data;
    },

    // D: Xóa tài liệu
    delete: async (id) => {
        const response = await axios.delete(`${BASE_URL}/${id}`, {
            headers: getAuthHeader() // Truyền Token vào header
        });
        return response.data;
    }
};