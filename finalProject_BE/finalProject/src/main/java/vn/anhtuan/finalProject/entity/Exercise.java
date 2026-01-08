package vn.anhtuan.finalProject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercises")
@Data
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- THAY ĐỔI: Tách question thành Type và Value ---

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private ContentType questionType; // Sử dụng Enum TEXT/IMAGE có sẵn

    @Column(name = "question_value", nullable = false, columnDefinition = "TEXT")
    private String questionValue; // Nội dung text hoặc URL ảnh

    // ---------------------------------------------------

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Lesson lesson;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("solutionOrder ASC")
    private List<ExerciseSolution> solutions = new ArrayList<>();

    // Helper methods
    public void addSolution(ExerciseSolution solution) {
        solutions.add(solution);
        solution.setExercise(this);
    }

    public void removeSolution(ExerciseSolution solution) {
        solutions.remove(solution);
        solution.setExercise(null);
    }

    public Exercise() {
    }

    // Cập nhật Constructor
    public Exercise(Long id, ContentType questionType, String questionValue, Integer orderNo, Lesson lesson, List<ExerciseSolution> solutions) {
        this.id = id;
        this.questionType = questionType;
        this.questionValue = questionValue;
        this.orderNo = orderNo;
        this.lesson = lesson;
        this.solutions = solutions;
    }

    // --- Cập nhật Getter/Setter cho field mới ---

    public ContentType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(ContentType questionType) {
        this.questionType = questionType;
    }

    public String getQuestionValue() {
        return questionValue;
    }

    public void setQuestionValue(String questionValue) {
        this.questionValue = questionValue;
    }

    // ... Các getter/setter khác giữ nguyên ...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Lesson getLesson() { return lesson; }
    public void setLesson(Lesson lesson) { this.lesson = lesson; }
    public Integer getOrderNo() { return orderNo; }
    public void setOrderNo(Integer orderNo) { this.orderNo = orderNo; }
    public List<ExerciseSolution> getSolutions() { return solutions; }
    public void setSolutions(List<ExerciseSolution> solutions) { this.solutions = solutions; }
}