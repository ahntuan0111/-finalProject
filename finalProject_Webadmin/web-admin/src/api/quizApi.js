import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api/admin/quizzes';

// Tự động lấy Token từ LocalStorage
const getAuthHeader = () => {
    const token = localStorage.getItem('access_token'); 
    if (token) {
        return { 'Authorization': `Bearer ${token}` };
    }
    return {};
};

export const quizApi = {
    // R: Lấy danh sách bài Quiz
    getAll: async () => {
        const response = await axios.get(BASE_URL, {
            headers: getAuthHeader()
        });
        return response.data;
    },

    // C: Tạo bài Quiz mới (kèm câu hỏi, nội dung, đáp án)
    create: async (quizData) => {
        const response = await axios.post(BASE_URL, quizData, {
            headers: {
                'Content-Type': 'application/json',
                ...getAuthHeader()
            }
        });
        return response.data;
    },

    // D: Xóa bài Quiz
    delete: async (id) => {
        const response = await axios.delete(`${BASE_URL}/${id}`, {
            headers: getAuthHeader()
        });
        return response.data;
    },

    getTypes: async () => {
        const response = await axios.get(`${BASE_URL}/types`, {
            headers: getAuthHeader()
        });
        return response.data;
    },

    getChapters: async () => {
        const response = await axios.get(`${BASE_URL}/chapters`, {
            headers: getAuthHeader()
        });
        return response.data;
    },

    // U: Cập nhật Quiz
    update: async (id, quizData) => {
        const response = await axios.put(`${BASE_URL}/${id}`, quizData, {
            headers: {
                'Content-Type': 'application/json',
                ...getAuthHeader()
            }
        });
        return response.data;
    },
};