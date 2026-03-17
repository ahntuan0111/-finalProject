package vn.anhtuan.finalProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseSolutionResponse {
    private Long id;
    private String solutionType;
    private String solutionValue; // Đã được Service chọn lọc
    private Integer solutionOrder;

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

    public String getSolutionValue() {
        return solutionValue;
    }

    public void setSolutionValue(String solutionValue) {
        this.solutionValue = solutionValue;
    }

    public String getSolutionType() {
        return solutionType;
    }

    public void setSolutionType(String solutionType) {
        this.solutionType = solutionType;
    }
}