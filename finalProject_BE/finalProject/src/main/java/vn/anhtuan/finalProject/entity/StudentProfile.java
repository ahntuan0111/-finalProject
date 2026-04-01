package vn.anhtuan.finalProject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;

@Entity
@Table(name = "student_profiles")
@Data
@NoArgsConstructor
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ 1-1 với User
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    // DB: student_profiles.current_grade là TINYINT(4)
    @JdbcTypeCode(Types.TINYINT)
    @Column(name = "current_grade", columnDefinition = "TINYINT")
    private Integer currentGrade; // Lưu lớp học hiện tại (6, 7, 8, 9)

    // Có thể thêm trường khác sau này: trường học, sở thích, v.v.
    // private String schoolName;

    public StudentProfile(User user, Integer currentGrade) {
        this.user = user;
        this.currentGrade = currentGrade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getCurrentGrade() {
        return currentGrade;
    }

    public void setCurrentGrade(Integer currentGrade) {
        this.currentGrade = currentGrade;
    }
}