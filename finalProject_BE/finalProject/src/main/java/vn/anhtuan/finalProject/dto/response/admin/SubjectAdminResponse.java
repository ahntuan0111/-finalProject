package vn.anhtuan.finalProject.dto.response.admin;

import lombok.Data;
import vn.anhtuan.finalProject.entity.Subject;

@Data
public class SubjectAdminResponse {
    private Integer id;
    private String code;
    private String nameVi;
    private String nameEn;

    // SỬA Ở ĐÂY: Đổi từ Byte sang Integer để khớp với Grade Entity
    private Integer gradeId;

    public static SubjectAdminResponse fromEntity(Subject subject) {
        SubjectAdminResponse res = new SubjectAdminResponse();
        res.setId(subject.getId());
        res.setCode(subject.getCode());
        res.setNameVi(subject.getNameVi());
        res.setNameEn(subject.getNameEn());

        if (subject.getGrade() != null) {
            // Bây giờ cả 2 bên đều là Integer nên sẽ không còn lỗi đỏ nữa
            res.setGradeId(subject.getGrade().getId());
        }
        return res;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGradeId() {
        return gradeId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameVi() {
        return nameVi;
    }

    public void setNameVi(String nameVi) {
        this.nameVi = nameVi;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
