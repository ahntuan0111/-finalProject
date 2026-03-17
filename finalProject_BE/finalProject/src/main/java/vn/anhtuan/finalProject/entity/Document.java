package vn.anhtuan.finalProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Đa ngôn ngữ cho Tiêu đề ---
    @Column(name = "title_vi", nullable = false, length = 255)
    private String titleVi;

    @Column(name = "title_en", length = 255)
    private String titleEn;

    // --- Đa ngôn ngữ cho Mô tả ---
    @Column(name = "description_vi", columnDefinition = "TEXT")
    private String descriptionVi;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_type")
    private String fileType; // PDF, DOCX...

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "subject_code", nullable = false)
    private String subjectCode; // VD: "math", "physics"

    @Column(nullable = false)
    private Integer grade;      // VD: 10, 11, 12

    @Column(name = "exam_type_vi", length = 50)
    private String examTypeVi;

    @Column(name = "exam_type_en", length = 50)
    private String examTypeEn;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Document() {
    }

    public Document(String titleVi, Long id, String titleEn, String descriptionVi, String descriptionEn, String filePath, String fileType, String subjectCode, Long fileSize, Integer grade, String examTypeVi, String examTypeEn, LocalDateTime updatedAt, LocalDateTime createdAt) {
        this.titleVi = titleVi;
        this.id = id;
        this.titleEn = titleEn;
        this.descriptionVi = descriptionVi;
        this.descriptionEn = descriptionEn;
        this.filePath = filePath;
        this.fileType = fileType;
        this.subjectCode = subjectCode;
        this.fileSize = fileSize;
        this.grade = grade;
        this.examTypeVi = examTypeVi;
        this.examTypeEn = examTypeEn;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitleVi() {
        return titleVi;
    }

    public void setTitleVi(String titleVi) {
        this.titleVi = titleVi;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getDescriptionVi() {
        return descriptionVi;
    }

    public void setDescriptionVi(String descriptionVi) {
        this.descriptionVi = descriptionVi;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
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

    public String getExamTypeVi() {
        return examTypeVi;
    }

    public void setExamTypeVi(String examTypeVi) {
        this.examTypeVi = examTypeVi;
    }

    public String getExamTypeEn() {
        return examTypeEn;
    }

    public void setExamTypeEn(String examTypeEn) {
        this.examTypeEn = examTypeEn;
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
