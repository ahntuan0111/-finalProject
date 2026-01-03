package vn.anhtuan.finalProject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "subjects", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code", "grade_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String code; // Ví dụ: "toan", "khtn"

    @Column(nullable = false, length = 100)
    private String name; // Ví dụ: "Toán học", "Khoa học tự nhiên"

    @ManyToOne
    @JoinColumn(name = "grade_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Grade grade;

    // --- SỬA Ở ĐÂY ---
    // Thêm @JsonIgnore để ngắt vòng lặp Chapter -> Subject -> List<Chapter>
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Chapter> chapters;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }
}