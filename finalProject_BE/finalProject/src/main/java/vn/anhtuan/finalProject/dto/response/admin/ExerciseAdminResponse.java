package vn.anhtuan.finalProject.dto.response.admin;

import lombok.Data;
import vn.anhtuan.finalProject.entity.Exercise;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ExerciseAdminResponse {
    private Long id;
    private String questionType;
    private String questionValueVi; // Trả về nguyên bản Vi
    private String questionValueEn; // Trả về nguyên bản En
    
    private String hint1Vi;
    private String hint1En;
    private String hint2Vi;
    private String hint2En;
    private String hint3Vi;
    private String hint3En;

    private Integer orderNo;
    private List<ExerciseSolutionAdminResponse> solutions;

    public static ExerciseAdminResponse fromEntity(Exercise exercise) {
        ExerciseAdminResponse res = new ExerciseAdminResponse();
        res.setId(exercise.getId());
        res.setQuestionType(exercise.getQuestionType().name());
        res.setQuestionValueVi(exercise.getQuestionValueVi());
        res.setQuestionValueEn(exercise.getQuestionValueEn());
        
        res.setHint1Vi(exercise.getHint1Vi());
        res.setHint1En(exercise.getHint1En());
        res.setHint2Vi(exercise.getHint2Vi());
        res.setHint2En(exercise.getHint2En());
        res.setHint3Vi(exercise.getHint3Vi());
        res.setHint3En(exercise.getHint3En());
        
        res.setOrderNo(exercise.getOrderNo());

        if (exercise.getSolutions() != null) {
            res.setSolutions(exercise.getSolutions().stream()
                    .map(ExerciseSolutionAdminResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ExerciseSolutionAdminResponse> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<ExerciseSolutionAdminResponse> solutions) {
        this.solutions = solutions;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getQuestionValueEn() {
        return questionValueEn;
    }

    public void setQuestionValueEn(String questionValueEn) {
        this.questionValueEn = questionValueEn;
    }

    public String getQuestionValueVi() {
        return questionValueVi;
    }

    public void setQuestionValueVi(String questionValueVi) {
        this.questionValueVi = questionValueVi;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }
}