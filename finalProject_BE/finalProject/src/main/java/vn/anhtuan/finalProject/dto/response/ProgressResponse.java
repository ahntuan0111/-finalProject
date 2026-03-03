package vn.anhtuan.finalProject.dto.response;

import lombok.Data;

@Data
public class ProgressResponse {
    private Integer subjectId;
    private String subjectCode; // "toan", "khtn"
    private Integer gradeId;
    private Integer completedLessons;
    private Integer totalLessons;
    private Double progressPercent;

    public ProgressResponse() {
    }

    public ProgressResponse(Integer subjectId, String subjectCode, Integer gradeId, Integer completedLessons, Integer totalLessons, Double progressPercent) {
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.gradeId = gradeId;
        this.completedLessons = completedLessons;
        this.totalLessons = totalLessons;
        this.progressPercent = progressPercent;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public Integer getGradeId() {
        return gradeId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
    }

    public Integer getCompletedLessons() {
        return completedLessons;
    }

    public void setCompletedLessons(Integer completedLessons) {
        this.completedLessons = completedLessons;
    }

    public Double getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Double progressPercent) {
        this.progressPercent = progressPercent;
    }

    public Integer getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(Integer totalLessons) {
        this.totalLessons = totalLessons;
    }
}
