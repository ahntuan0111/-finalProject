package vn.anhtuan.finalProject.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.anhtuan.finalProject.entity.ContentType;

@Entity
@Table(name = "question_contents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;

    @Column(name = "content_value", columnDefinition = "TEXT", nullable = false)
    private String contentValue;

    // THÊM: Thứ tự hiển thị nội dung
    @Column(name = "content_order", nullable = false)
    private Integer contentOrder = 1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public Integer getContentOrder() {
        return contentOrder;
    }

    public void setContentOrder(Integer contentOrder) {
        this.contentOrder = contentOrder;
    }

    public String getContentValue() {
        return contentValue;
    }

    public void setContentValue(String contentValue) {
        this.contentValue = contentValue;
    }
}