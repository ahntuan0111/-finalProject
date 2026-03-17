package vn.anhtuan.finalProject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizType {
    @Id
    private Integer id;

    @Column(name = "name_vi", nullable = false, length = 50)
    private String nameVi;

    @Column(name = "name_en", length = 50)
    private String nameEn;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "question_count", nullable = false)
    private Integer questionCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameVi() {
        return nameVi;
    }

    public void setNameVi(String nameVi) {
        this.nameVi = nameVi;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }


}
