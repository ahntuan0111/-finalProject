# 📋 Report Outline — MaSci E-Learning Platform
> ✅ **Đã xác minh công nghệ từ source code thực tế** (07/04/2026)

> **Tổng quan phân bổ trang (theo yêu cầu giảng viên):**
>
> | Chương | Nội dung | Số tuần | Số trang |
> |---|---|---|---|
> | Chapter 1 | Introduction | 2 weeks | **3–5 trang** |
> | Chapter 2 | Literature Review | 4 weeks | **6–10 trang** |
> | Chapter 3 | Analysis & Design | 4 weeks | **20–30 trang** |
> | Chapter 4 | Implementation & Testing | 4 weeks | **20–30 trang** |
> | Chapter 5 | Summary & Conclusion | 2 weeks | **3–5 trang** |
> | References & Appendix | — | — | ~3–5 trang |
> | **TỔNG** | | **16 weeks** | **50–80 trang** |

---

## ✅ Bảng xác minh công nghệ thực tế

> Đã kiểm tra từ: `pom.xml`, `application.properties`, `app/build.gradle.kts`, `package.json`

### Backend (Spring Boot)
| Công nghệ | Nguồn xác minh | Ghi chú |
|---|---|---|
| **Spring Boot 3.2.5** | `pom.xml` — `<version>3.2.5</version>` | ✅ |
| **MySQL** (không phải MariaDB) | `application.properties` — `jdbc:mysql://localhost:3306/e_learningdb` | ✅ Driver: `com.mysql:mysql-connector-j` |
| **Spring Security** | `pom.xml` — `spring-boot-starter-security` | ✅ |
| **Spring Validation** | `pom.xml` — `spring-boot-starter-validation` | ✅ |
| **JWT (jjwt)** | `pom.xml` — `jjwt-impl`, `jjwt-jackson` | ✅ |
| **Lombok** | `pom.xml` — `org.projectlombok:lombok` | ✅ |
| **Firebase Admin SDK** | `pom.xml` — `com.google.firebase:firebase-admin` | ✅ Signed URL + FCM server-side |
| **Gmail SMTP** | `application.properties` — `smtp.gmail.com:587` | ✅ JavaMailSender |
| **Gemini AI** | `application.properties` — `gemini.api.key` | ✅ Gọi qua REST API |
| **Spring Boot Mail** | `pom.xml` — `spring-boot-starter-mail` | ✅ |
| **JPA/Hibernate** | `application.properties` — `ddl-auto=none` | ✅ Schema quản lý thủ công |
| **File Upload** | `application.properties` — `max-file-size=20MB` | ✅ Multipart |

### Mobile App (Android)
| Công nghệ | Nguồn xác minh | Ghi chú |
|---|---|---|
| **Kotlin** | `build.gradle.kts` — `alias(libs.plugins.kotlin.android)` | ✅ |
| **Jetpack Compose** | `build.gradle.kts` — `compose = true` | ✅ |
| **Material 3** | `build.gradle.kts` — `material3:1.3.0` | ✅ |
| **Navigation Compose** | `build.gradle.kts` — `libs.androidx.navigation.compose` | ✅ |
| **Hilt (DI)** | `build.gradle.kts` — `hilt.android` + `hilt.compiler` | ✅ |
| **Retrofit + Gson** | `build.gradle.kts` — `libs.retrofit` + `retrofit.converter.gson` | ✅ HTTP client |
| **OkHttp Logging** | `build.gradle.kts` — `libs.okhttp.logging` | ✅ |
| **DataStore** | `build.gradle.kts` — `androidx.datastore.preferences` | ✅ Lưu token local |
| **Coil** | `build.gradle.kts` — `libs.coil.compose` | ✅ Image loading |
| **Firebase BOM 34.9.0** | `build.gradle.kts` — `firebase-bom:34.9.0` | ✅ |
| **Firebase Analytics** | `build.gradle.kts` — `firebase-analytics` | ✅ |
| **Firebase Messaging (FCM)** | `build.gradle.kts` — `firebase-messaging` | ✅ Push notification |
| **Firebase Storage** | `build.gradle.kts` — `firebase-storage` | ✅ Upload ảnh |
| **Vico Charts** | `build.gradle.kts` — `vico:compose:1.13.0` | ✅ Biểu đồ tiến độ |
| **Markwon** | `build.gradle.kts` — `markwon:core:4.6.2` | ✅ Render Markdown + LaTeX |
| **Markwon ext-latex** | `build.gradle.kts` — `markwon:ext-latex:4.6.2` | ✅ LaTeX trong AI Chat |

### Web Admin (React)
| Công nghệ | Nguồn xác minh | Ghi chú |
|---|---|---|
| **React 19.2.0** | `package.json` — `"react": "^19.2.0"` | ✅ (không phải React 18) |
| **Vite (rolldown-vite 7.2.5)** | `package.json` — `"vite": "npm:rolldown-vite@7.2.5"` | ✅ |
| **Ant Design 6.1.2** | `package.json` — `"antd": "^6.1.2"` | ✅ |
| **@ant-design/icons** | `package.json` — `"@ant-design/icons": "^6.1.0"` | ✅ |
| **@ant-design/plots** | `package.json` — `"@ant-design/plots": "^2.6.8"` | ✅ Biểu đồ thống kê |
| **Axios 1.13.6** | `package.json` — `"axios": "^1.13.6"` | ✅ HTTP client |
| **React Router DOM 7** | `package.json` — `"react-router-dom": "^7.11.0"` | ✅ |
| **CKEditor 5** | `package.json` — `@ckeditor/ckeditor5-build-classic` | ✅ Rich text editor nội dung |
| **KaTeX 0.16.27** | `package.json` — `"katex": "^0.16.27"` | ✅ Render công thức toán |
| **MathLive** | `package.json` — `"mathlive": "^0.108.2"` | ✅ Input công thức toán |
| **jwt-decode** | `package.json` — `"jwt-decode": "^4.0.0"` | ✅ Decode JWT token client-side |

---

## 📘 CHAPTER 1: INTRODUCTION
> 🎯 **Mục tiêu trang: 3–5 trang** | Dự kiến: ~5 trang
> 📄 **File đầy đủ:** [chapter1_introduction.md](chapter1_introduction.md)

### 1.1 Background to the Study *(~1 trang)*
- Xu hướng học tập trực tuyến tại Việt Nam sau đại dịch COVID-19
- Khoảng cách chất lượng giáo dục giữa thành thị và nông thôn
- Nhu cầu cá nhân hóa việc học tập theo trình độ
- Sự phát triển của AI trong giáo dục (AI-assisted learning)
- MaSci ra đời như một giải pháp e-learning cho học sinh THCS

### 1.2 Problem Statement *(~0.75 trang)*
1. Thiếu tài liệu học tập có cấu trúc, phù hợp chương trình cụ thể
2. Bài tập viết tay không thể chấm và phản hồi kịp thời từ xa
3. Thiếu sự hỗ trợ thông minh theo ngữ cảnh khi học tự lập
4. Thiếu cơ chế theo dõi tiến độ học tập cá nhân hóa
5. Quản lý nội dung song ngữ cho giáo viên còn rời rạc và phức tạp

### 1.3 Objectives of the Study *(~0.75 trang)*
**Primary:** Mobile App (Android) + Web Admin + Spring Boot Backend

**Specific:**
- Học lý thuyết đa phương tiện (văn bản, KaTeX, video)
- AI chấm bài tập viết tay qua ảnh (Gemini Vision)
- AI Tutor Chat theo ngữ cảnh (Gemini + history)
- Hệ thống quiz theo môn/lớp
- Theo dõi tiến độ và streak học tập

### 1.4 Research Questions *(~0.5 trang)*
- RQ1: Kiến trúc 3-tier cho e-learning mobile toàn diện?
- RQ2: Gemini AI tích hợp chấm bài và tutor hiệu quả?
- RQ3: Thiết kế DB và API đảm bảo hiệu năng và mở rộng?

### 1.5 Significance of the Study *(~0.5 trang)*
- Học sinh, Giáo viên/Admin, Xã hội, Kỹ thuật

### 1.6 Scope of the Study *(~0.5 trang)*

| Thành phần | Công nghệ | Phạm vi |
|---|---|---|
| **Mobile App** | Android / Kotlin / Jetpack Compose | Học sinh lớp 6–9 |
| **Web Admin** | React 19 / Vite / JavaScript | Quản trị viên |
| **Backend** | Spring Boot 3.2.5 / MySQL / REST API | Server-side, JWT |
| **External** | Firebase Storage, Gemini AI, Gmail SMTP, FCM | Dịch vụ bên ngoài |

*Ngoài phạm vi: iOS App, thanh toán, live streaming*

### 1.7 Definitions of Terms *(~1 trang)*
Jetpack Compose, Spring Boot, JWT, OTP, KaTeX, Gemini AI, Firebase Storage, FCM, AI Tutor, Streak, MVVM, DTO, MySQL, CKEditor5, MathLive

---

## 📗 CHAPTER 2: LITERATURE REVIEW / THEORETICAL BACKGROUND
> 🎯 **Mục tiêu trang: 6–10 trang** | Dự kiến: ~9 trang

### 2.1 Previous Studies *(~2.5 trang)*
| Ứng dụng | Ưu điểm | Hạn chế so với MaSci |
|---|---|---|
| Khan Academy | Nội dung phong phú, miễn phí | Không AI chấm ảnh, tiếng Anh |
| Duolingo | Gamification mạnh | Chỉ ngôn ngữ |
| Hocmai.vn | Video chuẩn CT Việt Nam | Thiếu AI, không chấm ảnh |
| Olm.vn | Hướng đến THCS | Giao diện cũ, không Mobile App |
| Photomath | Chấm bài Toán qua ảnh | Không có hệ thống học đầy đủ |

**Research Gap:** MaSci kết hợp nội dung học thuật + AI chấm ảnh + AI Tutor ngữ cảnh + theo dõi tiến độ trong 1 nền tảng đa thiết bị.

### 2.2 Technology Stack Overview *(~5 trang)*

#### 2.2.1 Android Development: Kotlin & Jetpack Compose *(~1.25 trang)*
- Kotlin: null safety, coroutines, extension functions
- Jetpack Compose + Material 3: declarative UI, state với ViewModel + StateFlow
- Hilt (DI), Retrofit + OkHttp (HTTP), Coil (images), DataStore (local storage)
- Markwon + ext-latex: render Markdown/LaTeX trong AI Chat
- Vico: biểu đồ tiến độ học tập
- Firebase BOM 34.9.0: Analytics, FCM, Storage

#### 2.2.2 Backend: Spring Boot 3.2.5 & MySQL *(~1.5 trang)*
- Spring Boot 3.2.5: auto-configuration, REST Controller
- **MySQL** (jdbc:mysql): ACID compliance, InnoDB engine, Foreign Key constraints
- Spring Security 6: JWT Filter Chain, BCrypt, RBAC
- JPA/Hibernate: `ddl-auto=none` — Schema quản lý thủ công qua SQL
- DTO Pattern: tách biệt API response với JPA Entity
- Lombok: giảm boilerplate (@Data, @Builder...)
- Spring Validation: validate request DTO
- Spring Boot Mail: JavaMailSender + Gmail SMTP port 587

#### 2.2.3 Web Admin: React 19 & Vite *(~1 trang)*
- **React 19.2.0**: Component-based, hooks
- **Vite (rolldown-vite 7.2.5)**: build tool tốc độ cao, HMR
- **Ant Design 6.1.2** + **@ant-design/icons**: UI component library
- **@ant-design/plots**: charts cho dashboard thống kê
- **Axios 1.13.6**: HTTP client với interceptor JWT
- **React Router DOM 7**: client-side routing
- **CKEditor 5**: rich text editor cho nhập nội dung bài học
- **KaTeX 0.16.27**: render công thức toán trong preview
- **MathLive**: input công thức toán dạng WYSIWYG
- **jwt-decode**: giải mã JWT để lấy role phía client

#### 2.2.4 Cloud Services *(~1.25 trang)*
- **Firebase Storage** (BOM 34.9.0): Lưu ảnh bài làm — Signed URL pattern
- **Gemini AI** (REST API + `gemini.api.key`): AI Tutor Chat + Chấm bài ảnh
- **Gmail SMTP** (smtp.gmail.com:587): Gửi OTP email
- **FCM** (Firebase Cloud Messaging): Push Notification Android

### 2.3 Conceptual Framework *(~1.5 trang)*
- Modular Monolith Architecture
- AI-Augmented Learning (hỗ trợ, không thay thế giáo viên)
- Gamification Theory (Streak System)
- Formative Assessment (AI phản hồi tức thì)

---

## 📙 CHAPTER 3: METHODOLOGY / SYSTEM DESIGN
> 🎯 **Mục tiêu trang: 20–30 trang** | Dự kiến: ~28 trang

### 3.1 Software Development Life Cycle *(~1 trang)*
Agile/Scrum — 4 Sprint (2 tuần/sprint)

### 3.2 Requirement Analysis *(~3 trang)*
#### 3.2.1 Functional Requirements *(~2 trang)*
6 nhóm: Auth, Lý thuyết, Bài tập, Quiz, AI Tutor, Web Admin

#### 3.2.2 Non-Functional Requirements *(~1 trang)*
Hiệu năng, Bảo mật JWT, Đa ngôn ngữ, Khả năng mở rộng, Graceful error

### 3.3 Use Case Analysis *(~5 trang)*
#### 3.3.1 Actors và Use Cases *(~0.75 trang)*
#### 3.3.2 Use Case Diagrams *(~2 trang)*
- Sơ đồ UC Mobile (Học Sinh)
- Sơ đồ UC Web Admin (Quản Trị Viên)
#### 3.3.3 Use Case Descriptions *(~2.25 trang)*
- UC-05: Nộp bài tập + AI chấm
- UC-07: AI Tutor Chat

### 3.4 System Architecture *(~2 trang)*
3-tier: Client → Spring Boot → MySQL/Firebase/Gemini

### 3.5 Database Design *(~5 trang)*
> Database: **MySQL** | Engine: InnoDB | Charset: utf8mb4

#### 3.5.1 Entity Relationship Diagram (ERD) *(~2 trang)*
24 bảng — xác minh từ SQL dump thực tế

#### 3.5.2 Mô tả bảng quan trọng *(~3 trang)*

### 3.6 Process Design — Flowchart *(~4 trang)*
3 luồng chính; chuẩn ANSI/BPMN

### 3.7 Sequence Diagrams *(~5 trang)*
5 sơ đồ UML: Auth, Theory, Exercise+AI, Quiz, AI Tutor

### 3.8 UI/UX Design *(~3 trang)*
#### 3.8.1 Mobile App — Material 3, Dark Mode, KaTeX WebView
#### 3.8.2 Web Admin — Ant Design, CKEditor5, MathLive

---

## 📕 CHAPTER 4: SYSTEM IMPLEMENTATION AND TESTING
> 🎯 **Mục tiêu trang: 20–30 trang** | Dự kiến: ~28 trang

### 4.1 System Implementation *(~22 trang)*

#### 4.1.1 Backend Implementation *(~8 trang)*

**Kiến trúc phân tầng:**
`Controller → Service → Repository → MySQL (JPA/Hibernate)`

**Module Backend:**
| Module | Controller | Endpoint |
|---|---|---|
| Auth | AuthController | POST /auth/login, /register-otp, /verify-register, /forgot-password |
| User | UserController | GET/PUT /users/profile, POST /users/update-fcm-token |
| Theory | TheoryUserController | GET /student/chapters, /lessons/{id} |
| Exercise | ExerciseController | POST /validate-image, /save-submission, GET /upload-url |
| Quiz | QuizUserController | GET /quizzes/filter, POST /quizzes/{id}/submit |
| AI Tutor | AiTutorController | POST /ai/chat, GET/DELETE /ai/chat/history |
| Progress | LessonProgressController | POST /progress/mark-complete, GET /progress/overview |
| Admin Content | AdminController | CRUD /admin/subjects, /chapters, /lessons |
| Dashboard | AdminDashboardController | GET /admin/dashboard |

**Spring Security:** JWT Filter → BCrypt → RBAC (USER / ADMIN)

#### 4.1.2 Android App *(~8 trang)*

**MVVM + Repository Pattern:**
`Compose UI → ViewModel → Repository → Retrofit API Service`

**Màn hình chính:**
LoginScreen, RegisterScreen, HomeScreen, CourseScreen, TheoryScreen, ExerciseScreen, QuizScreen, AiTutorScreen, ProfileScreen

**Kỹ thuật nổi bật:**
- KaTeX: local WebView + `.woff2` fonts trong `assets/`
- Markwon + ext-latex: LaTeX trong AI chat bubble
- Vico: biểu đồ progress
- DataStore: lưu JWT token thay vì SharedPreferences
- FCM: Background notification handler

#### 4.1.3 Web Admin *(~4 trang)*

**Stack:** React 19 + Ant Design 6 + Vite (rolldown)
**Pages:** Dashboard, Subjects/Chapters/Lessons, Exercises, Quizzes, Documents, Submissions
**Rich Text:** CKEditor5 cho soạn thảo nội dung + MathLive cho nhập công thức

#### 4.1.4 Integration: Gemini AI & Firebase Storage *(~2 trang)*
- Signed URL flow qua Firebase Admin SDK
- Gemini API: Text-only (AI Tutor) + Multimodal (chấm ảnh bài tập)

### 4.2 System Testing *(~6 trang)*

#### 4.2.1 Unit Testing *(~3 trang)*
JUnit 5 + Mockito — Service Layer

#### 4.2.2 Integration Testing & UAT *(~3 trang)*
Postman + 3–5 học sinh thực tế

---

## 📒 CHAPTER 5: DISCUSSION, CONCLUSION AND RECOMMENDATIONS
> 🎯 **Mục tiêu trang: 3–5 trang** | Dự kiến: ~5 trang

### 5.1 Discussion of Results *(~1.5 trang)*
### 5.2 Limitations *(~1 trang)*
### 5.3 Conclusion *(~1 trang)*
### 5.4 Recommendations for Future Work *(~1.5 trang)*
Flutter iOS, Gamification, Live Class, Learning Analytics, Freemium, Fine-tune AI, Microservices

---

## 📚 REFERENCES
## 📎 APPENDIX

---

## 📊 Phân bổ trang tổng kết

| Chương | Ước tính |
|---|---|
| Chapter 1 | ~5 trang |
| Chapter 2 | ~9 trang |
| Chapter 3 | ~28 trang |
| Chapter 4 | ~28 trang |
| Chapter 5 | ~5 trang |
| References + Appendix | ~5 trang |
| **TỔNG** | **~80 trang** |
