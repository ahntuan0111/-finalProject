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
    @Column(name = "content_value", nullable = false, columnDefinition = "TEXT")
    private String contentValue; // Đây là nơi lưu HTML hoặc LaTeX raw

    @Column(name = "content_order", nullable = false)
    private Integer contentOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @JsonIgnore
    private Lesson lesson;

    public LessonContent() {
    }

    public LessonContent(Long id, ContentType contentType, String contentValue, Integer contentOrder, Lesson lesson) {
        this.id = id;
        this.contentType = contentType;
        this.contentValue = contentValue;
        this.contentOrder = contentOrder;
        this.lesson = lesson;
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

    public String getContentValue() {
        return contentValue;
    }

    public void setContentValue(String contentValue) {
        this.contentValue = contentValue;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }
}