package vn.anhtuan.finalProject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "user_activity", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "activity_date"})
})
@Data
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @Column(name = "minutes_used", nullable = false)
    private int minutesUsed = 0;

    public UserActivity() {
    }

    public UserActivity(User user, LocalDate activityDate, int minutesUsed) {
        this.user = user;
        this.activityDate = activityDate;
        this.minutesUsed = minutesUsed;
    }

    public UserActivity(Long id, int minutesUsed, LocalDate activityDate, User user) {
        this.id = id;
        this.minutesUsed = minutesUsed;
        this.activityDate = activityDate;
        this.user = user;
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

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDate activityDate) {
        this.activityDate = activityDate;
    }

    public int getMinutesUsed() {
        return minutesUsed;
    }

    public void setMinutesUsed(int minutesUsed) {
        this.minutesUsed = minutesUsed;
    }
}
