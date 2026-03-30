package vn.anhtuan.finalProject.dto.response;

import lombok.Data;

@Data
public class SearchItemDto {
    private Long id;              // ID của thực thể
    private String titleVi;       // Tiêu đề Tiếng Việt
    private String titleEn;       // Tiêu đề Tiếng Anh
    private String type;          // LESSON, QUIZ, DOCUMENT
    private String subjectCode;   // Mã môn học để điều hướng
    private Long chapterId;       // (Tùy chọn) ID chương

    // 1. Constructor rỗng (bắt buộc phải có cho Spring/Jackson)
    public SearchItemDto() {
    }

    // 2. Constructor đầy đủ 6 tham số (Fix lỗi Default constructor is invoked...)
    public SearchItemDto(Long id, String titleVi, String titleEn, String type, String subjectCode, Long chapterId) {
        this.id = id;
        this.titleVi = titleVi;
        this.titleEn = titleEn;
        this.type = type;
        this.subjectCode = subjectCode;
        this.chapterId = chapterId;
    }
}
