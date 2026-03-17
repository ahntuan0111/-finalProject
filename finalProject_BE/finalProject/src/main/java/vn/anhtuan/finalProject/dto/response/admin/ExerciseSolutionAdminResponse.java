package vn.anhtuan.finalProject.dto.response.admin;

import lombok.Data;
import vn.anhtuan.finalProject.entity.ExerciseSolution;

@Data
public class ExerciseSolutionAdminResponse {
    private Long id;
    private String solutionType;
    private String solutionValueVi; // Trả về nguyên bản Vi
    private String solutionValueEn; // Trả về nguyên bản En
    private Integer solutionOrder;

    public static ExerciseSolutionAdminResponse fromEntity(ExerciseSolution sol) {
        ExerciseSolutionAdminResponse res = new ExerciseSolutionAdminResponse();
        res.setId(sol.getId());
        res.setSolutionType(sol.getContentType().name());
        res.setSolutionValueVi(sol.getSolutionValueVi());
        res.setSolutionValueEn(sol.getSolutionValueEn());
        res.setSolutionOrder(sol.getSolutionOrder());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSolutionOrder() {
        return solutionOrder;
    }

    public void setSolutionOrder(Integer solutionOrder) {
        this.solutionOrder = solutionOrder;
    }

    public String getSolutionValueEn() {
        return solutionValueEn;
    }

    public void setSolutionValueEn(String solutionValueEn) {
        this.solutionValueEn = solutionValueEn;
    }

    public String getSolutionValueVi() {
        return solutionValueVi;
    }

    public void setSolutionValueVi(String solutionValueVi) {
        this.solutionValueVi = solutionValueVi;
    }

    public String getSolutionType() {
        return solutionType;
    }

    public void setSolutionType(String solutionType) {
        this.solutionType = solutionType;
    }
}