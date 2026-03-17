package vn.anhtuan.finalProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.anhtuan.finalProject.entity.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private Long id;
    private String title;
    private String description;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private String subjectCode;
    private Integer grade;
    private String examType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Helper lọc ngôn ngữ - Đã sửa lỗi chống Null
    public static String getLocalizedText(String textVi, String textEn, String lang) {
        if ("en".equalsIgnoreCase(lang) && textEn != null && !textEn.trim().isEmpty()) {
            return textEn;
        }
        return textVi != null ? textVi : ""; // An toàn tuyệt đối
    }

    public static DocumentResponse fromEntity(Document doc, String lang) {
        DocumentResponse res = new DocumentResponse();
        res.setId(doc.getId());
        res.setTitle(getLocalizedText(doc.getTitleVi(), doc.getTitleEn(), lang));
        res.setDescription(getLocalizedText(doc.getDescriptionVi(), doc.getDescriptionEn(), lang));
        res.setFilePath(doc.getFilePath());
        res.setFileType(doc.getFileType());
        res.setFileSize(doc.getFileSize());
        res.setSubjectCode(doc.getSubjectCode());
        res.setGrade(doc.getGrade());
        res.setExamType(getLocalizedText(doc.getExamTypeVi(), doc.getExamTypeEn(), lang));
        res.setCreatedAt(doc.getCreatedAt());
        res.setUpdatedAt(doc.getUpdatedAt());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}