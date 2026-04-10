# 📋 Use Case Diagram — MaSci E-Learning (UML Chuẩn · Đã rà soát 3 nền tảng)

> **Nguồn rà soát:**
> - 📱 Android: 15 screen groups — `auth`, `class_selection`, `course`, `exercise`, `theory`, `quizes`, `ai`, `document`, `profile`, `reminder`, `search`, `dashboard`
> - 🖥️ Web Admin: 8 page groups — `Auth`, `Dashboard`, `Course`, `Exercise`, `QuizManagement`, `Document`, `Submission`, `User`
> - ⚙️ Backend: 22 controllers — `AuthController`, `UserController`, `AdminController`, `AdminDashboardController`, `AdminExerciseController`, `AdminQuizController`, `AdminSubmissionController`, `AiTutorController`, `DocumentController`, `ExerciseController`, `LessonProgressController`, `NotificationController`, `QuizUserController`, `SearchController`, `StatisticsController`, `TheoryUserController`, `UserActivityController`, `UserReminderSettingController`, `AdminChapterController`, `AdminLessonController`, `UploadController`, `TestNotificationController`

---

## 🔑 Notation UML

| Ký hiệu | Tên | Mermaid | Quy tắc |
|---|---|---|---|
| Hình người que | **Actor** | `(["👤 Tên"])` ngoài subgraph | Luôn nằm ngoài boundary |
| Hình bầu dục | **Use Case** | `([Tên UC])` | Tên bắt đầu bằng động từ |
| Hình chữ nhật | **System Boundary** | `subgraph` | Bao bọc UC |
| Nét liền | **Association** | `---` | Actor ↔ UC |
| Nét đứt `<<include>>` | **Include** | Base `-.->` Included | Bắt buộc, Base không thiếu được |
| Nét đứt `<<extend>>` | **Extend** | Extension `-.->` Base | Tùy chọn, điều kiện kích hoạt |
| Tam giác rỗng | **Generalization** | `-->` với label | Kế thừa |

---

## 📐 Diagram 1 — Xác thực & Onboarding

> **Actors:** Khách (Guest), Học Sinh (kế thừa Khách), Quản Trị Viên
> **Nguồn:** `AuthController`, `UserController`, Mobile `auth/` + `class_selection/`

```mermaid
flowchart LR
    Khach(["👤 Khách"])
    HocSinh(["🎓 Học Sinh"])
    QTV(["🛡️ Quản Trị Viên"])

    %% Generalization
    Khach -->|"generalization"| HocSinh

    subgraph AUTH["🔐 MaSci — Xác thực & Onboarding"]
        UC_Welcome(["Xem màn hình Welcome"])
        UC_DangKy(["Gửi yêu cầu đăng ký"])
        UC_OTP(["Xác thực OTP email"])
        UC_DangNhap(["Đăng nhập"])
        UC_Google(["Đăng nhập bằng Google"])
        UC_FCM(["Cập nhật FCM Token"])
        UC_QuenMK(["Quên mật khẩu"])
        UC_GuiOTP(["Gửi mã OTP qua email"])
        UC_ResetMK(["Đặt lại mật khẩu"])
        UC_ChonLop(["Chọn khối lớp"])
        UC_AdminLogin(["Đăng nhập Web Admin"])
    end

    %% Association — Khách
    Khach --- UC_Welcome
    Khach --- UC_DangKy
    Khach --- UC_DangNhap
    Khach --- UC_QuenMK

    %% Association — Học Sinh (sau đăng nhập)
    HocSinh --- UC_ChonLop

    %% Association — QTV
    QTV --- UC_AdminLogin

    %% INCLUDE (bắt buộc)
    UC_DangKy -.->|"<<include>>"| UC_OTP
    UC_DangNhap -.->|"<<include>>"| UC_FCM
    UC_QuenMK -.->|"<<include>>"| UC_GuiOTP
    UC_QuenMK -.->|"<<include>>"| UC_ResetMK

    %% EXTEND (tùy chọn)
    UC_Google -.->|"<<extend>>"| UC_DangNhap
```

**Mô tả:**
- **Khách** tương tác: Xem Welcome → Đăng ký (bắt buộc xác thực OTP) hoặc Đăng nhập (bắt buộc cập nhật FCM token để nhận push)
- **Học Sinh** sau khi đăng nhập lần đầu bắt buộc Chọn khối lớp
- **Đăng nhập Google** là phương thức thay thế tùy chọn (extend Đăng nhập)
- **Quên mật khẩu** gồm 2 bước bắt buộc nối tiếp: Gửi OTP → Đặt lại MK

---

## 📐 Diagram 2 — Mobile: Học lý thuyết & AI Tutor

> **Actors:** Học Sinh, AI System (Gemini)
> **Nguồn:** `TheoryUserController`, `AiTutorController`, `LessonProgressController`, Mobile `theory/`, `ai/`, `course/`

```mermaid
flowchart LR
    HocSinh(["🎓 Học Sinh"])
    AI(["🤖 AI System\n(Gemini)"])

    subgraph THEORY["📱 Mobile — Học lý thuyết & AI Tutor"]

        subgraph LT["Học lý thuyết"]
            UC_XemChuong(["Xem danh sách chương"])
            UC_XemBaiHoc(["Xem danh sách bài học"])
            UC_XemNoiDung(["Xem nội dung bài học"])
            UC_XemVideo(["Xem video YouTube"])
            UC_DanhDau(["Đánh dấu hoàn thành bài học"])
            UC_CapNhatTD(["Cập nhật tiến độ môn học"])
        end

        subgraph AI_TUTOR["AI Tutor"]
            UC_ChatAI(["Hỏi đáp cùng AI Tutor"])
            UC_TaoSession(["Tạo phiên chat mới"])
            UC_LichSuChat(["Xem lịch sử hội thoại"])
            UC_XoaChat(["Xóa lịch sử hội thoại"])
        end

    end

    %% Association
    HocSinh --- UC_XemChuong
    HocSinh --- UC_XemBaiHoc
    HocSinh --- UC_XemNoiDung
    HocSinh --- UC_DanhDau
    HocSinh --- UC_ChatAI

    AI --- UC_ChatAI

    %% INCLUDE
    UC_XemBaiHoc -.->|"<<include>>"| UC_XemChuong
    UC_XemNoiDung -.->|"<<include>>"| UC_XemBaiHoc
    UC_DanhDau -.->|"<<include>>"| UC_CapNhatTD
    UC_ChatAI -.->|"<<include>>"| UC_TaoSession

    %% EXTEND
    UC_XemVideo -.->|"<<extend>>"| UC_XemNoiDung
    UC_ChatAI -.->|"<<extend>>"| UC_XemNoiDung
    UC_LichSuChat -.->|"<<extend>>"| UC_ChatAI
    UC_XoaChat -.->|"<<extend>>"| UC_LichSuChat
```

**Mô tả:**
- Học viên phải duyệt theo cây: **Chương → Bài học → Nội dung** (include chain)
- **Đánh dấu hoàn thành** bắt buộc gọi `POST /progress/mark-complete` (include)
- **Hỏi AI Tutor** bắt buộc tạo session (include), nhưng chỉ xảy ra khi học viên có thắc mắc (extend Xem nội dung)
- **Xem video** chỉ khi `video_url != null` (extend)
- Lịch sử chat và xóa lịch sử là tùy chọn (extend)

---

## 📐 Diagram 3 — Mobile: Bài tập & Quiz

> **Actors:** Học Sinh, AI System (Gemini)
> **Nguồn:** `ExerciseController`, `QuizUserController`, Mobile `exercise/`, `quizes/`

```mermaid
flowchart LR
    HocSinh(["🎓 Học Sinh"])
    AI(["🤖 AI System\n(Gemini)"])

    subgraph PRACTICE["📱 Mobile — Bài tập & Quiz"]

        subgraph BT["Làm bài tập"]
            UC_XemDe(["Xem đề bài"])
            UC_XemHint(["Xem gợi ý Hint"])
            UC_XemLoiGiai(["Xem lời giải mẫu"])
            UC_NopBai(["Nộp bài làm"])
            UC_GetUrl(["Lấy Signed Upload URL"])
            UC_Upload(["Upload ảnh lên Firebase"])
            UC_ValidateAI(["Chấm bài bằng AI"])
            UC_LuuSubmission(["Lưu kết quả bài nộp"])
        end

        subgraph QUIZ["Làm quiz"]
            UC_XemDSQuiz(["Xem danh sách quiz"])
            UC_LamQuiz(["Làm bài quiz"])
            UC_LuuKQ(["Lưu kết quả quiz"])
            UC_XemDapAn(["Xem đáp án & giải thích"])
        end

    end

    %% Association
    HocSinh --- UC_XemDe
    HocSinh --- UC_NopBai
    HocSinh --- UC_XemDSQuiz
    HocSinh --- UC_LamQuiz

    AI --- UC_ValidateAI

    %% INCLUDE (bắt buộc)
    UC_NopBai -.->|"<<include>>"| UC_GetUrl
    UC_GetUrl -.->|"<<include>>"| UC_Upload
    UC_Upload -.->|"<<include>>"| UC_ValidateAI
    UC_ValidateAI -.->|"<<include>>"| UC_LuuSubmission
    UC_LamQuiz -.->|"<<include>>"| UC_LuuKQ

    %% EXTEND (tùy chọn)
    UC_XemHint -.->|"<<extend>>"| UC_XemDe
    UC_XemLoiGiai -.->|"<<extend>>"| UC_XemDe
    UC_XemDapAn -.->|"<<extend>>"| UC_LamQuiz
```

**Mô tả:**
- **Nộp bài** là chuỗi include bắt buộc 4 bước: Lấy URL → Upload Firebase → AI chấm → Lưu kết quả (theo thứ tự `GET /upload-url` → Firebase PUT → `POST /validate-image` → `POST /save-submission`)
- **Làm quiz** bắt buộc gọi `POST /quizzes/{id}/submit` khi hoàn thành (include)
- **Hint** và **Lời giải mẫu** là tùy chọn khi học viên cần trợ giúp (extend)
- **Xem đáp án** chỉ sau khi nộp quiz (extend)

---

## 📐 Diagram 4 — Mobile: Tiến độ, Hồ sơ & Tiện ích

> **Actors:** Học Sinh
> **Nguồn:** `LessonProgressController`, `UserController`, `UserActivityController`, `UserReminderSettingController`, `NotificationController`, `DocumentController`, `SearchController`, Mobile `profile/`, `reminder/`, `document/`, `search/`, `dashboard/`

```mermaid
flowchart LR
    HocSinh(["🎓 Học Sinh"])

    subgraph UTILITY["📱 Mobile — Tiến độ, Hồ sơ & Tiện ích"]

        subgraph PROGRESS["Tiến độ học tập"]
            UC_XemTD(["Xem tiến độ tổng quan"])
            UC_XemTongQuan(["Xem tổng quan môn học"])
            UC_XemStreak(["Xem streak học tập"])
            UC_LogActivity(["Ghi nhận hoạt động học"])
        end

        subgraph PROFILE["Hồ sơ cá nhân"]
            UC_XemHS(["Xem hồ sơ & thống kê"])
            UC_DoiMK(["Đổi mật khẩu"])
            UC_CapNhatHS(["Cập nhật thông tin cá nhân"])
        end

        subgraph DOC["Tài liệu ôn tập"]
            UC_XemDoc(["Xem danh sách tài liệu"])
            UC_DocPDF(["Đọc PDF trong app"])
            UC_TaiXuong(["Tải xuống PDF"])
        end

        subgraph NOTIF["Thông báo"]
            UC_XemTB(["Xem danh sách thông báo"])
            UC_SoBadge(["Xem số thông báo chưa đọc"])
            UC_DaDoc1(["Đánh dấu 1 thông báo đã đọc"])
            UC_DaDocAll(["Đánh dấu tất cả đã đọc"])
        end

        subgraph REMINDER["Nhắc nhở"]
            UC_XemReminder(["Xem cài đặt nhắc nhở"])
            UC_CapNhatReminder(["Cập nhật lịch nhắc nhở"])
        end

        UC_TimKiem(["Tìm kiếm nội dung"])
    end

    %% Association
    HocSinh --- UC_XemTD
    HocSinh --- UC_XemStreak
    HocSinh --- UC_XemHS
    HocSinh --- UC_XemDoc
    HocSinh --- UC_XemTB
    HocSinh --- UC_XemReminder
    HocSinh --- UC_TimKiem

    %% INCLUDE (bắt buộc)
    UC_XemTD -.->|"<<include>>"| UC_LogActivity
    UC_XemHS -.->|"<<include>>"| UC_XemStreak
    UC_XemTB -.->|"<<include>>"| UC_SoBadge

    %% EXTEND (tùy chọn)
    UC_XemTongQuan -.->|"<<extend>>"| UC_XemTD
    UC_DocPDF -.->|"<<extend>>"| UC_XemDoc
    UC_TaiXuong -.->|"<<extend>>"| UC_DocPDF
    UC_DoiMK -.->|"<<extend>>"| UC_XemHS
    UC_CapNhatHS -.->|"<<extend>>"| UC_XemHS
    UC_DaDoc1 -.->|"<<extend>>"| UC_XemTB
    UC_DaDocAll -.->|"<<extend>>"| UC_XemTB
    UC_CapNhatReminder -.->|"<<extend>>"| UC_XemReminder
```

**Mô tả:**
- **Xem tiến độ** bắt buộc ghi nhận `POST /activity/log` (include `LogActivity`)
- **Xem hồ sơ** bắt buộc hiển thị streak (`GET /activity/streak` — include)
- **Xem thông báo** bắt buộc load badge count (`GET /notifications/unread-count` — include)
- **Đọc PDF, Tải xuống, Xem tổng quan môn** là tùy chọn (extend)
- Hai hành động đọc thông báo tách riêng: đọc 1 (`PUT /{id}/read`) vs đọc tất cả (`PUT /read-all`)

---

## 📐 Diagram 5 — Web Admin: Quản lý nội dung

> **Actors:** Quản Trị Viên
> **Nguồn:** `AdminController`, `AdminChapterController`, `AdminLessonController`, `AdminExerciseController`, `AdminQuizController`, `DocumentController`, `UploadController`, Web Admin pages `Course/`, `Exercise/`, `QuizManagement/`, `Document/`

```mermaid
flowchart LR
    QTV(["🛡️ Quản Trị Viên"])

    subgraph CONTENT["🖥️ Web Admin — Quản lý nội dung"]

        subgraph SUBJECT["Môn học & Chương"]
            UC_XemSubject(["Xem danh sách môn học"])
            UC_CRUD_Chuong(["Quản lý Chương học"])
            UC_CRUD_Bai(["Quản lý Bài học"])
        end

        subgraph THEORY_ADMIN["Nội dung lý thuyết"]
            UC_CRUD_ND(["Quản lý Nội dung bài học"])
        end

        subgraph EX_ADMIN["Bài tập"]
            UC_CRUD_BT(["Quản lý Bài tập"])
            UC_CRUD_Hint(["Quản lý Gợi ý Hint"])
            UC_CRUD_Giai(["Quản lý Lời giải"])
        end

        subgraph QUIZ_ADMIN["Quiz"]
            UC_CRUD_Quiz(["Quản lý Quiz"])
            UC_CRUD_CH(["Quản lý Câu hỏi"])
            UC_CRUD_DA(["Quản lý Đáp án"])
        end

        subgraph DOC_ADMIN["Tài liệu"]
            UC_UploadDoc(["Upload tài liệu PDF"])
            UC_LuuFirebase(["Lưu file Firebase Storage"])
            UC_CRUD_Doc(["Quản lý Tài liệu"])
        end

    end

    %% Association
    QTV --- UC_XemSubject
    QTV --- UC_CRUD_Chuong
    QTV --- UC_CRUD_Bai
    QTV --- UC_CRUD_BT
    QTV --- UC_CRUD_Quiz
    QTV --- UC_UploadDoc

    %% INCLUDE (bắt buộc)
    UC_CRUD_Chuong -.->|"<<include>>"| UC_XemSubject
    UC_CRUD_Bai -.->|"<<include>>"| UC_CRUD_Chuong
    UC_CRUD_BT -.->|"<<include>>"| UC_CRUD_Giai
    UC_CRUD_Quiz -.->|"<<include>>"| UC_CRUD_CH
    UC_CRUD_CH -.->|"<<include>>"| UC_CRUD_DA
    UC_UploadDoc -.->|"<<include>>"| UC_LuuFirebase

    %% EXTEND (tùy chọn)
    UC_CRUD_ND -.->|"<<extend>>"| UC_CRUD_Bai
    UC_CRUD_Hint -.->|"<<extend>>"| UC_CRUD_BT
    UC_CRUD_Doc -.->|"<<extend>>"| UC_UploadDoc
```

**Mô tả:**
- Quản lý nội dung theo **cây phân cấp bắt buộc**: Môn → Chương → Bài học (include chain)
- **Bài tập** bắt buộc có Lời giải (include). Gợi ý Hint là tùy chọn (extend)
- **Quiz** bắt buộc có Câu hỏi (include), Câu hỏi bắt buộc có Đáp án (include)
- **Upload tài liệu** bắt buộc lưu Firebase (include). Quản lý tài liệu sau upload là tùy chọn (extend)
- Nội dung lý thuyết chi tiết của bài học là tùy chọn — bài có thể chỉ có tiêu đề chưa có nội dung (extend)

---

## 📐 Diagram 6 — Web Admin: Người dùng, Bài nộp & Dashboard

> **Actors:** Quản Trị Viên
> **Nguồn:** `AdminController`, `AdminDashboardController`, `AdminSubmissionController`, `StatisticsController`, Web Admin pages `Dashboard/`, `User/`, `Submission/`

```mermaid
flowchart LR
    QTV(["🛡️ Quản Trị Viên"])

    subgraph MGMT["🖥️ Web Admin — Người dùng, Bài nộp & Dashboard"]

        subgraph DASH_ADMIN["Dashboard"]
            UC_Dashboard(["Xem thống kê tổng quan"])
            UC_ChiTietHS(["Xem hồ sơ học tập học sinh"])
        end

        subgraph USER_ADMIN["Quản lý người dùng"]
            UC_XemUser(["Xem danh sách người dùng"])
            UC_TimUser(["Tìm kiếm người dùng"])
            UC_XoaUser(["Xóa người dùng"])
        end

        subgraph SUB_ADMIN["Bài nộp"]
            UC_XemBaiNop(["Xem tất cả bài nộp"])
            UC_XemAnh(["Xem ảnh bài làm"])
            UC_XemKetQua(["Xem kết quả AI chấm"])
        end

    end

    %% Association
    QTV --- UC_Dashboard
    QTV --- UC_XemUser
    QTV --- UC_XemBaiNop

    %% INCLUDE (bắt buộc)
    UC_XemBaiNop -.->|"<<include>>"| UC_XemAnh
    UC_XemBaiNop -.->|"<<include>>"| UC_XemKetQua

    %% EXTEND (tùy chọn)
    UC_ChiTietHS -.->|"<<extend>>"| UC_Dashboard
    UC_TimUser -.->|"<<extend>>"| UC_XemUser
    UC_XoaUser -.->|"<<extend>>"| UC_XemUser
```

**Mô tả:**
- **Dashboard** (`GET /api/admin/dashboard`) tổng hợp số liệu. **Xem hồ sơ từng HS** (`GET /admin/dashboard/student/{id}`) là extend tùy chọn
- **Xem danh sách bài nộp** (`GET /api/admin/submissions`) bắt buộc hiển thị ảnh và kết quả AI — không có 2 thành phần này bài nộp không có ý nghĩa (include)
- **Tìm kiếm** và **Xóa user** là hành động tùy chọn qua danh sách user (extend)
- **Xóa user** (`DELETE /admin/users/{id}`) chỉ xảy ra khi Admin chủ động chọn

---

## 📊 Bảng tổng hợp đầy đủ

### Include — 16 quan hệ bắt buộc

| # | Base UC | Included UC | Endpoint |
|---|---|---|---|
| 1 | Gửi yêu cầu đăng ký | Xác thực OTP email | `POST /auth/register-otp` → `/verify-register` |
| 2 | Đăng nhập | Cập nhật FCM Token | `POST /users/update-fcm-token` |
| 3 | Quên mật khẩu | Gửi mã OTP qua email | `POST /auth/forgot-password` |
| 4 | Quên mật khẩu | Đặt lại mật khẩu | `POST /auth/reset-password` |
| 5 | Xem danh sách bài học | Xem danh sách chương | `GET /student/subjects/{id}/chapters` |
| 6 | Xem nội dung bài học | Xem danh sách bài học | `GET /student/lessons/{id}` |
| 7 | Đánh dấu hoàn thành bài học | Cập nhật tiến độ môn học | `POST /progress/mark-complete` |
| 8 | Hỏi đáp cùng AI Tutor | Tạo phiên chat mới | `ai_chat_sessions` table |
| 9 | Nộp bài làm | Lấy Signed Upload URL | `GET /exercises/{id}/upload-url` |
| 10 | Lấy Signed Upload URL | Upload ảnh lên Firebase | Firebase Storage PUT |
| 11 | Upload ảnh lên Firebase | Chấm bài bằng AI | `POST /exercises/{id}/validate-image` |
| 12 | Chấm bài bằng AI | Lưu kết quả bài nộp | `POST /exercises/{id}/save-submission` |
| 13 | Làm bài quiz | Lưu kết quả quiz | `POST /quizzes/{id}/submit` |
| 14 | Xem tiến độ tổng quan | Ghi nhận hoạt động học | `POST /activity/log` |
| 15 | Xem hồ sơ & thống kê | Xem streak học tập | `GET /activity/streak` |
| 16 | Xem tất cả bài nộp | Xem ảnh & kết quả AI chấm | `GET /admin/submissions` |

### Extend — 20 quan hệ tùy chọn

| # | Extension UC | Base UC | Điều kiện |
|---|---|---|---|
| 1 | Đăng nhập bằng Google | Đăng nhập | Chọn OAuth2 thay vì email |
| 2 | Xem video YouTube | Xem nội dung bài học | `video_url != null` |
| 3 | Hỏi đáp cùng AI Tutor | Xem nội dung bài học | Học viên có thắc mắc |
| 4 | Xem lịch sử hội thoại | Hỏi đáp cùng AI Tutor | Muốn xem lại chat cũ |
| 5 | Xóa lịch sử hội thoại | Xem lịch sử hội thoại | Muốn reset session |
| 6 | Xem gợi ý Hint | Xem đề bài | Học viên cần trợ giúp |
| 7 | Xem lời giải mẫu | Xem đề bài | Muốn xem giải thích |
| 8 | Xem đáp án & giải thích | Làm bài quiz | Sau khi nộp quiz |
| 9 | Xem tổng quan môn học | Xem tiến độ tổng quan | Drill-down 1 môn |
| 10 | Đọc PDF trong app | Xem danh sách tài liệu | Chọn 1 tài liệu |
| 11 | Tải xuống PDF | Đọc PDF trong app | Muốn lưu offline |
| 12 | Đổi mật khẩu | Xem hồ sơ & thống kê | Muốn đổi mật khẩu |
| 13 | Cập nhật thông tin cá nhân | Xem hồ sơ & thống kê | Muốn sửa avatar/bio |
| 14 | Đánh dấu 1 thông báo đã đọc | Xem danh sách thông báo | Nhấn 1 thông báo cụ thể |
| 15 | Đánh dấu tất cả đã đọc | Xem danh sách thông báo | Muốn clear toàn bộ badge |
| 16 | Cập nhật lịch nhắc nhở | Xem cài đặt nhắc nhở | Muốn thay đổi lịch học |
| 17 | Quản lý Nội dung bài học | Quản lý Bài học | Thêm/sửa nội dung lý thuyết |
| 18 | Quản lý Gợi ý Hint | Quản lý Bài tập | Thêm gợi ý cho bài tập |
| 19 | Quản lý Tài liệu | Upload tài liệu PDF | Xem/sửa/xóa sau upload |
| 20 | Xem hồ sơ học tập học sinh | Xem thống kê tổng quan | Drill-down từng học sinh |
