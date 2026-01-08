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

    @Column(name = "solution_value", nullable = false, columnDefinition = "TEXT")
    private String solutionValue; // HTML/LaTeX hoặc URL Ảnh

    @Column(name = "solution_order", nullable = false)
    private Integer solutionOrder;

    public ExerciseSolution() {
    }

    public ExerciseSolution(Long id, Exercise exercise, ContentType contentType, String solutionValue, Integer solutionOrder) {
        this.id = id;
        this.exercise = exercise;
        this.contentType = contentType;
        this.solutionValue = solutionValue;
        this.solutionOrder = solutionOrder;
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

    public String getSolutionValue() {
        return solutionValue;
    }

    public void setSolutionValue(String solutionValue) {
        this.solutionValue = solutionValue;
    }

    public Integer getSolutionOrder() {
        return solutionOrder;
    }

    public void setSolutionOrder(Integer solutionOrder) {
        this.solutionOrder = solutionOrder;
    }
}