package vn.anhtuan.finalProject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "exercise_solutions")
@Data
public class ExerciseSolution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    @JsonIgnore
    private Exercise exercise;

    @Enumerated(EnumType.STRING)
    @Column(name = "solution_type", nullable = false)
    private ContentType contentType;

    @Column(name = "solution_value_vi", nullable = false, columnDefinition = "TEXT")
    private String solutionValueVi;

    @Column(name = "solution_value_en", columnDefinition = "TEXT")
    private String solutionValueEn;// HTML/LaTeX hoặc URL Ảnh

    @Column(name = "solution_order", nullable = false)
    private Integer solutionOrder;

    public ExerciseSolution() {
    }

    public ExerciseSolution(Long id, Integer solutionOrder, String solutionValueEn, String solutionValueVi, ContentType contentType, Exercise exercise) {
        this.id = id;
        this.solutionOrder = solutionOrder;
        this.solutionValueEn = solutionValueEn;
        this.solutionValueVi = solutionValueVi;
        this.contentType = contentType;
        this.exercise = exercise;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public String getSolutionValueVi() {
        return solutionValueVi;
    }

    public void setSolutionValueVi(String solutionValueVi) {
        this.solutionValueVi = solutionValueVi;
    }

    public String getSolutionValueEn() {
        return solutionValueEn;
    }

    public void setSolutionValueEn(String solutionValueEn) {
        this.solutionValueEn = solutionValueEn;
    }

    public Integer getSolutionOrder() {
        return solutionOrder;
    }

    public void setSolutionOrder(Integer solutionOrder) {
        this.solutionOrder = solutionOrder;
    }
}