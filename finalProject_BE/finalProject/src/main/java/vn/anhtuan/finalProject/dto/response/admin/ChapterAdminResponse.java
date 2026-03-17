package vn.anhtuan.finalProject.dto.response.admin;

import lombok.Data;
import vn.anhtuan.finalProject.entity.Chapter;

@Data
public class ChapterAdminResponse {
    private Long id;
    private String titleVi; // Trả về nguyên bản Vi
    private String titleEn; // Trả về nguyên bản En
    private Integer orderNo;
    private Integer subjectId;

    public static ChapterAdminResponse fromEntity(Chapter chapter) {
        ChapterAdminResponse res = new ChapterAdminResponse();
        res.setId(chapter.getId());
        res.setTitleVi(chapter.getTitleVi());
        res.setTitleEn(chapter.getTitleEn());
        res.setOrderNo(chapter.getOrderNo());

        if (chapter.getSubject() != null) {
            res.setSubjectId(chapter.getSubject().getId());
        }
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleVi() {
        return titleVi;
    }

    public void setTitleVi(String titleVi) {
        this.titleVi = titleVi;
    }
}
