package vn.anhtuan.finalProject.dto.request;

import lombok.Data;

@Data
public class UpdateGradeRequest {
    private Integer grade;

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}