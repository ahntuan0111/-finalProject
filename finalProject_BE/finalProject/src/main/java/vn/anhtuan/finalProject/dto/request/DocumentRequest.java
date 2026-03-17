package vn.anhtuan.finalProject.dto.request;

import lombok.Data;

@Data
public class DocumentRequest {
    private String titleVi;
    private String titleEn;
    private String descriptionVi;
    private String descriptionEn;
    private String subjectCode;
    private Integer grade;
    private String examTypeVi;
    private String examTypeEn;

    public DocumentRequest() {
    }

    public DocumentRequest(String titleVi, String examTypeEn, String examTypeVi, Integer grade, String subjectCode, String descriptionEn, String descriptionVi, String titleEn) {
        this.titleVi = titleVi;
        this.examTypeEn = examTypeEn;
        this.examTypeVi = examTypeVi;
        this.grade = grade;
        this.subjectCode = subjectCode;
        this.descriptionEn = descriptionEn;
        this.descriptionVi = descriptionVi;
        this.titleEn = titleEn;
    }

    public String getTitleVi() {
        return titleVi;
    }

    public void setTitleVi(String titleVi) {
        this.titleVi = titleVi;
    }

    public String getExamTypeEn() {
        return examTypeEn;
    }

    public void setExamTypeEn(String examTypeEn) {
        this.examTypeEn = examTypeEn;
    }

    public String getExamTypeVi() {
        return examTypeVi;
    }

    public void setExamTypeVi(String examTypeVi) {
        this.examTypeVi = examTypeVi;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionVi() {
        return descriptionVi;
    }

    public void setDescriptionVi(String descriptionVi) {
        this.descriptionVi = descriptionVi;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }
}
