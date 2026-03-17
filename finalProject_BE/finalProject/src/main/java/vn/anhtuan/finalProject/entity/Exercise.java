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

    @Column(name = "question_value_vi", nullable = false, columnDefinition = "TEXT")
    private String questionValueVi;

    @Column(name = "question_value_en", columnDefinition = "TEXT")
    private String questionValueEn;

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

    public Exercise(Long id, ContentType questionType, String questionValueVi, String questionValueEn, Integer orderNo, List<ExerciseSolution> solutions, Lesson lesson) {
        this.id = id;
        this.questionType = questionType;
        this.questionValueVi = questionValueVi;
        this.questionValueEn = questionValueEn;
        this.orderNo = orderNo;
        this.solutions = solutions;
        this.lesson = lesson;
    }


    // --- Cập nhật Getter/Setter cho field mới ---

    public ContentType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(ContentType questionType) {
        this.questionType = questionType;
    }

    public String getQuestionValueVi() {
        return questionValueVi;
    }

    public void setQuestionValueVi(String questionValueVi) {
        this.questionValueVi = questionValueVi;
    }

    public String getQuestionValueEn() {
        return questionValueEn;
    }

    public void setQuestionValueEn(String questionValueEn) {
        this.questionValueEn = questionValueEn;
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