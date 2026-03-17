package vn.anhtuan.finalProject.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.anhtuan.finalProject.entity.ContentType;

@Entity
@Table(name = "choices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Choice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // THÊM: Loại đáp án (TEXT/IMAGE)
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType = ContentType.TEXT;

    // SỬA: Đổi tên content thành content_value cho đồng bộ
    @Column(name = "content_value_vi", nullable = false, columnDefinition = "TEXT")
    private String contentValueVi;

    @Column(name = "content_value_en", columnDefinition = "TEXT")
    private String contentValueEn;

    @Column(name = "is_correct", nullable = false)
    private Boolean correct = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public String getContentValueVi() {
        return contentValueVi;
    }

    public void setContentValueVi(String contentValueVi) {
        this.contentValueVi = contentValueVi;
    }

    public String getContentValueEn() {
        return contentValueEn;
    }

    public void setContentValueEn(String contentValueEn) {
        this.contentValueEn = contentValueEn;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}