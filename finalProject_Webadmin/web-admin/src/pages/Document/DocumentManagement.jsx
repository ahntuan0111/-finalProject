import React, { useState, useEffect } from 'react';
import { documentApi } from '../../api/documentApi';

const DocumentManagement = () => {
    const [documents, setDocuments] = useState([]);
    const [loading, setLoading] = useState(false);
    
    // State cho Form (Thêm/Sửa)
    const [showModal, setShowModal] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [editId, setEditId] = useState(null);
    const [file, setFile] = useState(null);
    
    const [formData, setFormData] = useState({
        title: '',
        description: '',
        subjectCode: 'Toán',
        grade: 10,
        examType: 'MID_TERM'
    });

    // Load dữ liệu khi vào trang
    useEffect(() => {
        fetchDocuments();
    }, []);

    const fetchDocuments = async () => {
        try {
            setLoading(true);
            const res = await documentApi.getAll();
            if (res.success) {
                setDocuments(res.data);
            }
        } catch (error) {
            console.error("Lỗi khi tải dữ liệu:", error);
            alert("Không thể tải dữ liệu từ server.");
        } finally {
            setLoading(false);
        }
    };

    // Xử lý thay đổi input text
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    // Mở modal Thêm mới
    const handleOpenAdd = () => {
        setIsEditing(false);
        setEditId(null);
        setFile(null);
        setFormData({ title: '', description: '', subjectCode: 'Toán', grade: 10, examType: 'MID_TERM' });
        setShowModal(true);
    };

    // Mở modal Sửa
    const handleOpenEdit = (doc) => {
        setIsEditing(true);
        setEditId(doc.id);
        setFile(null); // Khi sửa không cần upload lại file
        setFormData({
            title: doc.title,
            description: doc.description || '',
            subjectCode: doc.subjectCode,
            grade: doc.grade,
            examType: doc.examType
        });
        setShowModal(true);
    };

    // Xử lý Submit Form
    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (isEditing) {
                // UPDATE (Sửa)
                await documentApi.update(editId, formData);
                alert("Cập nhật thành công!");
            } else {
                // CREATE (Thêm mới)
                if (!file) {
                    alert("Vui lòng chọn file PDF!");
                    return;
                }
                await documentApi.upload(
                    file, formData.title, formData.description, 
                    formData.subjectCode, formData.grade, formData.examType
                );
                alert("Upload thành công!");
            }
            setShowModal(false);
            fetchDocuments(); // Tải lại danh sách
        } catch (error) {
            console.error("Lỗi lưu dữ liệu:", error);
            alert("Có lỗi xảy ra khi lưu dữ liệu.");
        }
    };

    // Xử lý Xóa
    const handleDelete = async (id) => {
        if (window.confirm("Bạn có chắc chắn muốn xóa tài liệu này? File vật lý cũng sẽ bị xóa!")) {
            try {
                await documentApi.delete(id);
                alert("Xóa thành công!");
                fetchDocuments(); // Tải lại danh sách
            } catch (error) {
                console.error("Lỗi xóa:", error);
                alert("Lỗi khi xóa tài liệu.");
            }
        }
    };

    return (
        <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
            <h2>Quản lý Tài Liệu (PDF)</h2>
            <button onClick={handleOpenAdd} style={{ padding: '10px', marginBottom: '20px', background: '#4CAF50', color: 'white', border: 'none', cursor: 'pointer' }}>
                + Thêm tài liệu mới
            </button>

            {/* Bảng hiển thị danh sách */}
            {loading ? <p>Đang tải dữ liệu...</p> : (
                <table border="1" cellPadding="10" style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead style={{ background: '#f4f4f4' }}>
                        <tr>
                            <th>ID</th>
                            <th>Tiêu đề</th>
                            <th>Môn học</th>
                            <th>Khối</th>
                            <th>Loại đề</th>
                            <th>Dung lượng</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        {documents.map((doc) => (
                            <tr key={doc.id}>
                                <td>{doc.id}</td>
                                <td>
                                    <a href={`http://localhost:8080${doc.filePath}`} target="_blank" rel="noreferrer">
                                        {doc.title}
                                    </a>
                                </td>
                                <td>{doc.subjectCode}</td>
                                <td>{doc.grade}</td>
                                <td>{doc.examType}</td>
                                <td>{doc.fileSize ? (doc.fileSize / 1024 / 1024).toFixed(2) + ' MB' : 'N/A'}</td>
                                <td>
                                    <button onClick={() => handleOpenEdit(doc)} style={{ marginRight: '10px' }}>Sửa</button>
                                    <button onClick={() => handleDelete(doc.id)} style={{ background: 'red', color: 'white' }}>Xóa</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}

            {/* Modal Thêm/Sửa */}
            {showModal && (
                <div style={{
                    position: 'fixed', top: 0, left: 0, width: '100%', height: '100%',
                    background: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center'
                }}>
                    <div style={{ background: 'white', padding: '20px', borderRadius: '8px', width: '400px' }}>
                        <h3>{isEditing ? "Cập nhật tài liệu" : "Upload tài liệu mới"}</h3>
                        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                            
                            <label>Tiêu đề:</label>
                            <input name="title" value={formData.title} onChange={handleInputChange} required />

                            <label>Mô tả:</label>
                            <textarea name="description" value={formData.description} onChange={handleInputChange} />

                            <label>Mã Môn học:</label>
                            <input name="subjectCode" value={formData.subjectCode} onChange={handleInputChange} required />

                            <label>Khối lớp:</label>
                            <input name="grade" type="number" value={formData.grade} onChange={handleInputChange} required />

                            <label>Loại đề:</label>
                            <select name="examType" value={formData.examType} onChange={handleInputChange}>
                                <option value="MID_TERM">Giữa kỳ</option>
                                <option value="FINAL">Cuối kỳ</option>
                                <option value="THEORY">Lý thuyết</option>
                            </select>

                            {/* Chỉ hiển thị chọn file khi thêm mới */}
                            {!isEditing && (
                                <>
                                    <label>Chọn file PDF:</label>
                                    <input type="file" accept="application/pdf" onChange={(e) => setFile(e.target.files[0])} required />
                                </>
                            )}

                            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '20px' }}>
                                <button type="button" onClick={() => setShowModal(false)}>Hủy</button>
                                <button type="submit" style={{ background: 'blue', color: 'white' }}>Lưu</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default DocumentManagement;