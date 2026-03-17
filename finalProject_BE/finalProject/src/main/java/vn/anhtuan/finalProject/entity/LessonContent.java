package vn.anhtuan.finalProject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "lesson_contents")
@Data
public class LessonContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType; // Enum: TEXT, IMAGE

    @Lob // Để lưu văn bản dài (TEXT trong MySQL)
    @Column(name = "content_value_vi", nullable = false, columnDefinition = "TEXT")
    private String contentValueVi;

    @Lob
    @Column(name = "content_value_en", columnDefinition = "TEXT")
    private String contentValueEn;

    @Column(name = "content_order", nullable = false)
    private Integer contentOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @JsonIgnore
    private Lesson lesson;

    public LessonContent() {
    }

    public LessonContent(Long id, Lesson lesson, Integer contentOrder, String contentValueEn, String contentValueVi, ContentType contentType) {
        this.id = id;
        this.lesson = lesson;
        this.contentOrder = contentOrder;
        this.contentValueEn = contentValueEn;
        this.contentValueVi = contentValueVi;
        this.contentType = contentType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public Integer getContentOrder() {
        return contentOrder;
    }

    public void setContentOrder(Integer contentOrder) {
        this.contentOrder = contentOrder;
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
}