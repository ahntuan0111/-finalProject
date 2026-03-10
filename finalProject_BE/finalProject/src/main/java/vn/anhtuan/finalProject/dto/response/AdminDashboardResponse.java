package vn.anhtuan.finalProject.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class AdminDashboardResponse {
    private OverviewStats overview;
    private List<UserGrowthChart> userGrowth;
    private List<ContentDistribution> contentDistribution;
    private List<RecentStudent> recentStudents;
    private List<RecentContent> recentContents;

    public OverviewStats getOverview() {
        return overview;
    }

    public void setOverview(OverviewStats overview) {
        this.overview = overview;
    }

    public List<UserGrowthChart> getUserGrowth() {
        return userGrowth;
    }

    public void setUserGrowth(List<UserGrowthChart> userGrowth) {
        this.userGrowth = userGrowth;
    }

    public List<ContentDistribution> getContentDistribution() {
        return contentDistribution;
    }

    public void setContentDistribution(List<ContentDistribution> contentDistribution) {
        this.contentDistribution = contentDistribution;
    }

    public List<RecentStudent> getRecentStudents() {
        return recentStudents;
    }

    public void setRecentStudents(List<RecentStudent> recentStudents) {
        this.recentStudents = recentStudents;
    }

    public List<RecentContent> getRecentContents() {
        return recentContents;
    }

    public void setRecentContents(List<RecentContent> recentContents) {
        this.recentContents = recentContents;
    }


    // --- Các class con (Nested Classes) ---

    @Data
    public static class OverviewStats {
        private long totalStudents;
        private long onlineUsers; // Có thể mock tạm thời
        private long totalLessons;
        private long totalExams;
        // % tăng trưởng (mock hoặc tính toán)
        private String studentGrowth;
        private String lessonGrowth;

        public OverviewStats() {
        }

        public long getTotalStudents() {
            return totalStudents;
        }

        public void setTotalStudents(long totalStudents) {
            this.totalStudents = totalStudents;
        }

        public long getOnlineUsers() {
            return onlineUsers;
        }

        public void setOnlineUsers(long onlineUsers) {
            this.onlineUsers = onlineUsers;
        }

        public long getTotalLessons() {
            return totalLessons;
        }

        public void setTotalLessons(long totalLessons) {
            this.totalLessons = totalLessons;
        }

        public long getTotalExams() {
            return totalExams;
        }

        public void setTotalExams(long totalExams) {
            this.totalExams = totalExams;
        }

        public String getStudentGrowth() {
            return studentGrowth;
        }

        public void setStudentGrowth(String studentGrowth) {
            this.studentGrowth = studentGrowth;
        }

        public String getLessonGrowth() {
            return lessonGrowth;
        }

        public void setLessonGrowth(String lessonGrowth) {
            this.lessonGrowth = lessonGrowth;
        }
    }

    @Data
    public static class UserGrowthChart {
        private String date;
        private int count;

        public UserGrowthChart(String date, int count) {
            this.date = date;
            this.count = count;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    @Data
    public static class ContentDistribution {
        private String type; // Toán học, Khoa học...
        private int value;   // Số lượng bài

        public ContentDistribution(String type, int value) {
            this.type = type;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    @Data
    public static class RecentStudent {
        private String id;
        private String name;
        private String grade;
        private String joinDate;

        public RecentStudent() {
        }

        public RecentStudent(String id, String name, String grade, String joinDate) {
            this.id = id;
            this.name = name;
            this.grade = grade;
            this.joinDate = joinDate;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getJoinDate() {
            return joinDate;
        }

        public void setJoinDate(String joinDate) {
            this.joinDate = joinDate;
        }
    }

    @Data
    public static class RecentContent {
        private String id;
        private String title;
        private String subject;
        private String status; // PUBLISHED, DRAFT

        public RecentContent() {
        }

        public RecentContent(String id, String title, String subject, String status) {
            this.id = id;
            this.title = title;
            this.subject = subject;
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
