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

    @Column(name = "hint1_vi", columnDefinition = "TEXT")
    private String hint1Vi;

    @Column(name = "hint1_en", columnDefinition = "TEXT")
    private String hint1En;

    @Column(name = "hint2_vi", columnDefinition = "TEXT")
    private String hint2Vi;

    @Column(name = "hint2_en", columnDefinition = "TEXT")
    private String hint2En;

    @Column(name = "hint3_vi", columnDefinition = "TEXT")
    private String hint3Vi;

    @Column(name = "hint3_en", columnDefinition = "TEXT")
    private String hint3En;

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

    public String getHint1Vi() {
        return hint1Vi;
    }

    public void setHint1Vi(String hint1Vi) {
        this.hint1Vi = hint1Vi;
    }

    public String getHint3En() {
        return hint3En;
    }

    public void setHint3En(String hint3En) {
        this.hint3En = hint3En;
    }

    public String getHint3Vi() {
        return hint3Vi;
    }

    public void setHint3Vi(String hint3Vi) {
        this.hint3Vi = hint3Vi;
    }

    public String getHint2En() {
        return hint2En;
    }

    public void setHint2En(String hint2En) {
        this.hint2En = hint2En;
    }

    public String getHint2Vi() {
        return hint2Vi;
    }

    public void setHint2Vi(String hint2Vi) {
        this.hint2Vi = hint2Vi;
    }

    public String getHint1En() {
        return hint1En;
    }

    public void setHint1En(String hint1En) {
        this.hint1En = hint1En;
    }
}