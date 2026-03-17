package vn.anhtuan.finalProject.dto.response.admin;

import lombok.Data;
import vn.anhtuan.finalProject.entity.Document;

@Data
public class DocumentAdminResponse {
    private Long id;
    private String titleVi;
    private String titleEn;
    private String descriptionVi;
    private String descriptionEn;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private String subjectCode;
    private Integer grade;
    private String examTypeVi;
    private String examTypeEn;

    // Map thẳng từ Entity sang, KHÔNG lọc ngôn ngữ
    public static DocumentAdminResponse fromEntity(Document doc) {
        DocumentAdminResponse res = new DocumentAdminResponse();
        res.setId(doc.getId());
        res.setTitleVi(doc.getTitleVi());
        res.setTitleEn(doc.getTitleEn());
        res.setDescriptionVi(doc.getDescriptionVi());
        res.setDescriptionEn(doc.getDescriptionEn());
        res.setFilePath(doc.getFilePath());
        res.setFileType(doc.getFileType());
        res.setFileSize(doc.getFileSize());
        res.setSubjectCode(doc.getSubjectCode());
        res.setGrade(doc.getGrade());
        res.setExamTypeVi(doc.getExamTypeVi());
        res.setExamTypeEn(doc.getExamTypeEn());
        return res;
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
}
