<div align="center">

# 🎓 MaSci — Nền Tảng E-Learning Thông Minh

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Kotlin](https://img.shields.io/badge/Kotlin-Android-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://react.dev/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)

> **Hệ thống học tập trực tuyến toàn diện** dành cho học sinh THCS/THPT, tích hợp AI Tutor (Google Gemini), quản lý khóa học, quiz thông minh, và thông báo Push Real-time.

**Full-stack project | 3 repositories | Production-ready**

</div>

---

## 📋 Mục Lục

- [Tổng Quan Dự Án](#-tổng-quan-dự-án)
- [Kiến Trúc Hệ Thống](#-kiến-trúc-hệ-thống)
- [Màn Hình Ứng Dụng](#-màn-hình-ứng-dụng)
- [Repositories](#-repositories)
- [Tech Stack](#-tech-stack)
- [Tính Năng Chính](#-tính-năng-chính)
- [Cài Đặt & Chạy Dự Án](#-cài-đặt--chạy-dự-án)
- [Cấu Trúc Dự Án](#-cấu-trúc-dự-án)
- [API Endpoints](#-api-endpoints)

---

## 🎯 Tổng Quan Dự Án

**MaSci** là một hệ thống E-Learning đầy đủ tính năng dành cho học sinh từ lớp 6 đến lớp 9, bao gồm:

| Thành phần | Công nghệ | Mô tả |
|---|---|---|
| 📱 **Mobile App** | Android (Kotlin + Jetpack Compose) | Ứng dụng học tập cho học sinh |
| 🖥️ **Web Admin** | React 19 + Vite + Ant Design | Trang quản trị cho admin/giáo viên |
| ⚙️ **Backend API** | Spring Boot 3.2 + MySQL + Redis | RESTful API server |

### Điểm Nổi Bật

- 🤖 **AI Tutor tích hợp** — Google Gemini AI trả lời câu hỏi học thuật, render LaTeX công thức toán
- 🔔 **Push Notification Real-time** — Firebase FCM + WebSocket cho thông báo tức thì
- 📊 **Thống kê học tập** — Dashboard theo dõi tiến độ, điểm số, streak học tập
- 📄 **PDF Viewer tích hợp** — Đọc tài liệu học tập trực tiếp trong app
- ⏰ **Nhắc học thông minh** — WorkManager lên lịch nhắc nhở cá nhân hóa
- 🔐 **JWT Authentication** — Bảo mật với Access Token + Redis session management

---

## 🏗️ Kiến Trúc Hệ Thống

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                             │
│                                                                 │
│   📱 Android App                    🖥️ Web Admin                │
│   Kotlin + Jetpack Compose          React 19 + Ant Design       │
│   Retrofit + Hilt DI                Axios + React Router        │
└─────────────┬───────────────────────────────┬───────────────────┘
              │        REST API / JWT          │
              ▼                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                     BACKEND (Spring Boot 3.2)                   │
│                                                                 │
│  ┌──────────────┐  ┌───────────────┐  ┌─────────────────────┐  │
│  │  Controllers  │  │  Service Layer│  │  Security (JWT+     │  │
│  │  (22 APIs)   │→ │  (21 Services)│→ │  Spring Security)   │  │
│  └──────────────┘  └───────────────┘  └─────────────────────┘  │
│              │                │                                  │
│              ▼                ▼                                  │
│  ┌──────────────┐  ┌───────────────┐                           │
│  │  JPA/Repo    │  │  Redis Cache  │                           │
│  │  (MySQL)     │  │  (Sessions)   │                           │
│  └──────────────┘  └───────────────┘                           │
└───────────────────────┬─────────────────────────────────────────┘
                        │ External Services
          ┌─────────────┼──────────────────┐
          ▼             ▼                  ▼
   🔥 Firebase      🤖 Gemini AI      📧 Gmail SMTP
   Storage + FCM    (AI Tutor)        (Email OTP)
```

---

## 📸 Màn Hình Ứng Dụng

### 📱 Mobile App (Android)

#### Welcome & Home Screen
> Màn hình chào mừng với thiết kế gradient hiện đại và trang chủ hiển thị các môn học theo lớp

#### Lý Thuyết & AI Tutor
> Xem bài học với LaTeX rendering và chat với AI Tutor hỗ trợ giải toán

#### Quiz & Dashboard
> Làm bài kiểm tra trắc nghiệm và theo dõi tiến độ học tập bằng biểu đồ

---

### 🖥️ Web Admin Panel

#### Dashboard Tổng Quan
> Thống kê tổng quan: Users, Courses, Exercises với biểu đồ real-time

#### Quản Lý Khóa Học & Quiz
> CRUD đầy đủ cho môn học, chương, bài học và ngân hàng câu hỏi

---

## 📁 Repositories

```
Final_Project/
├── finalProject_BE/          # ⚙️ Spring Boot Backend
├── finalProject_Mobileapp/   # 📱 Android Mobile App
└── finalProject_Webadmin/    # 🖥️ React Web Admin
```

---

## 🛠️ Tech Stack

### ⚙️ Backend (`finalProject_BE`)

| Category | Technology |
|---|---|
| Framework | Spring Boot 3.2.5 |
| Language | Java 17 |
| Database | MySQL 8.0 |
| Cache | Redis |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT (JJWT 0.11.5) |
| Real-time | WebSocket (STOMP) |
| File Storage | Firebase Storage |
| Push Notification | Firebase Admin SDK 9.7.0 |
| AI Integration | Google Gemini API |
| Email | Spring Mail (Gmail SMTP) |
| Validation | Spring Validation + Bean Validation |
| Build Tool | Maven |

### 📱 Mobile App (`finalProject_Mobileapp`)

| Category | Technology |
|---|---|
| Language | Kotlin |
| UI Framework | Jetpack Compose + Material Design 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt (Dagger) |
| Navigation | Jetpack Navigation Compose |
| Networking | Retrofit 2 + OkHttp |
| Local Storage | DataStore Preferences |
| Image Loading | Coil |
| Push Notification | Firebase Cloud Messaging |
| Background Task | WorkManager |
| Camera | CameraX |
| Video | YouTube Android Player |
| Charts | Vico Charts |
| Math Rendering | Markwon + LaTeX extension |
| Min SDK | API 24 (Android 7.0) |

### 🖥️ Web Admin (`finalProject_Webadmin`)

| Category | Technology |
|---|---|
| Framework | React 19 |
| Build Tool | Vite (Rolldown) |
| UI Library | Ant Design 6 |
| Charts | @ant-design/plots |
| Routing | React Router DOM v7 |
| HTTP Client | Axios |
| Auth | JWT Decode |
| Rich Text | CKEditor 5 |
| Math | KaTeX + MathLive |

---

## ✨ Tính Năng Chính

### 👨‍🎓 Dành Cho Học Sinh (Mobile App)

| Tính năng | Mô tả |
|---|---|
| **Đăng ký / Đăng nhập** | OTP email verification, Forgot password flow |
| **Chọn lớp học** | Hỗ trợ lớp 6-12, tùy chỉnh môn học theo lớp |
| **Học lý thuyết** | Xem bài học có text, video YouTube, PDF tài liệu |
| **Làm bài tập** | Exercise với AI validation (Gemini check đáp án) |
| **Làm Quiz** | Trắc nghiệm có timer, lịch sử, xem đáp án |
| **AI Tutor** | Chat với Gemini AI, hỗ trợ nhập ký hiệu toán |
| **Dashboard** | Biểu đồ tiến độ, streak, điểm trung bình |
| **Tìm kiếm** | Full-text search môn học, bài học |
| **Nhắc nhở** | WorkManager lên lịch học cá nhân hóa |
| **Thông báo** | Push notification real-time (FCM) |
| **Hồ sơ** | Xem và cập nhật thông tin cá nhân |

### 👨‍💼 Dành Cho Admin (Web Panel)

| Tính năng | Mô tả |
|---|---|
| **Dashboard** | Thống kê tổng quan với biểu đồ @ant-design/plots |
| **Quản lý Users** | Danh sách, tìm kiếm, kích hoạt/vô hiệu hóa tài khoản |
| **Quản lý Khóa Học** | CRUD Môn học → Chương → Bài học → Nội dung |
| **Quản lý Bài Tập** | Tạo exercise với rich text (CKEditor 5) + LaTeX |
| **Xem Submissions** | Duyệt bài nộp của học sinh, chấm điểm |
| **Quản lý Documents** | Upload/quản lý tài liệu PDF lên Firebase Storage |
| **Quản lý Quiz** | Ngân hàng câu hỏi, tạo bộ đề theo môn/lớp/độ khó |

### ⚙️ Backend API (22 Controllers)

| Controller | Mô tả |
|---|---|
| `AuthController` | Login, Register, OTP, Forgot/Reset Password |
| `UserController` | CRUD user, profile management |
| `AdminController` | Admin-only operations |
| `AdminDashboardController` | Dashboard statistics |
| `AdminQuizController` | Quiz & Question management |
| `AdminExerciseController` | Exercise management |
| `AdminSubmissionController` | Submission review |
| `DocumentController` | PDF upload & management |
| `ExerciseController` | Student exercise operations |
| `QuizUserController` | Student quiz taking |
| `TheoryUserController` | Theory content delivery |
| `LessonProgressController` | Progress tracking |
| `AiTutorController` | Gemini AI integration |
| `NotificationController` | Push notification dispatch |
| `SearchController` | Full-text search |
| `StatisticsController` | Learning statistics |
| `UserActivityController` | Activity logging |
| `UserReminderSettingController` | Reminder management |
| `UploadController` | Firebase Storage upload |
| `WebSocket` | Real-time notifications |

---

## 🚀 Cài Đặt & Chạy Dự Án

### Yêu Cầu Hệ Thống

- Java 17+
- Node.js 18+
- MySQL 8.0
- Redis
- Android Studio (Hedgehog+)
- Firebase project (Storage + FCM)

---

### ⚙️ 1. Backend Setup

```bash
# Clone repository
cd finalProject_BE/finalProject

# Cấu hình database trong application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/e_learningdb
spring.datasource.username=your_username
spring.datasource.password=your_password

# Chạy MySQL, tạo database
mysql -u root -p
CREATE DATABASE e_learningdb;

# Chạy Redis
redis-server

# Build & Run
./mvnw spring-boot:run
# Server chạy tại: http://localhost:8080
```

**Biến môi trường cần cấu hình:**

```properties
# JWT
app.jwt.secret=your_jwt_secret_key
app.jwt.expiration=2592000

# Gemini AI
gemini.api.key=your_gemini_api_key

# Gmail SMTP
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password

# Firebase: đặt file firebase-service-account.json vào src/main/resources/
```

---

### 🖥️ 2. Web Admin Setup

```bash
cd finalProject_Webadmin/web-admin

# Cài dependencies
npm install

# Cấu hình API URL trong .env
VITE_API_BASE_URL=http://localhost:8080

# Chạy development server
npm run dev
# Mở: http://localhost:5173

# Build production
npm run build
```

---

### 📱 3. Android App Setup

```bash
# Mở Android Studio → Open Project → chọn finalProject_Mobileapp/

# Cấu hình API URL trong NetworkModule
# Tìm file: data/remote/NetworkModule.kt
BASE_URL = "http://your-server-ip:8080/"

# Thêm google-services.json từ Firebase Console
# vào: app/google-services.json

# Build & Run trên emulator/thiết bị thật
./gradlew assembleDebug
```

---

## 📂 Cấu Trúc Dự Án

### Backend (`finalProject_BE`)
```
src/main/java/vn/anhtuan/finalProject/
├── controller/          # 22 REST Controllers
├── service/             # 21 Business Logic Services
│   └── impl/            # Service implementations
├── entity/              # 31 JPA Entities
├── dto/                 # Data Transfer Objects
├── repository/          # Spring Data JPA Repositories
├── security/            # JWT Filter + Security Config
├── config/              # WebSocket, CORS, Firebase Config
├── scheduler/           # Scheduled tasks (reminders)
├── exception/           # Global Exception Handler
└── utils/               # Utility classes
```

**Domain Entities:**
`User`, `Subject`, `Chapter`, `Lesson`, `LessonContent`, `Exercise`, `ExerciseSolution`, `ExerciseSubmission`, `Quiz`, `Question`, `Choice`, `QuizResult`, `Document`, `Progress`, `LessonCompletion`, `Notification`, `AiChatSession`, `AiChatMessage`, `UserReminderSetting`, `UserActivity`, `StudentProfile`, `Grade`

### Mobile App (`finalProject_Mobileapp`)
```
app/src/main/java/com/example/finalproject/
├── ui/
│   ├── screens/
│   │   ├── auth/           # Welcome, Login, Register, OTP, ForgotPassword
│   │   ├── home/           # HomeScreen (subject grid)
│   │   ├── class_selection/# ClassSelectionScreen
│   │   ├── subject_detail/ # SubjectDetailScreen
│   │   ├── theory/         # TheoryScreen, TheoryDetailScreen
│   │   ├── exercise/       # ExerciseListScreen, ExerciseDetailScreen
│   │   ├── quizes/         # QuizListScreen, QuizTakingScreen, QuizResultScreen
│   │   ├── document/       # DocumentListScreen, PdfViewerScreen
│   │   ├── ai/             # ChatBoxAIScreen, MathSymbolBar
│   │   ├── dashboard/      # DashboardScreen
│   │   ├── profile/        # UserScreen
│   │   ├── search/         # SearchScreen
│   │   └── reminder/       # ReminderScreen
│   ├── viewmodel/          # ViewModels (MVVM)
│   ├── navigation/         # AppNavHost, AppRoutes
│   ├── components/         # Shared UI components
│   └── theme/              # Material3 theme
├── data/
│   ├── remote/             # Retrofit API services + DTOs
│   ├── local/              # DataStore Preferences
│   └── repository/         # Repository implementations
├── domain/                 # Use Cases
├── di/                     # Hilt DI modules
└── utils/                  # DataStoreManager, etc.
```

### Web Admin (`finalProject_Webadmin`)
```
src/
├── pages/
│   ├── Auth/               # LoginPage
│   ├── Dashboard/          # DashboardPage (charts)
│   ├── User/               # UserList
│   ├── Course/             # CoursePage (subjects/chapters/lessons)
│   ├── Exercise/           # ExercisePage
│   ├── Submission/         # SubmissionPage
│   ├── Document/           # DocumentManagement
│   └── QuizManagement/     # QuizManagement
├── components/
│   ├── layout/             # MainLayout (sidebar + header)
│   ├── common/             # Shared components
│   └── editor/             # CKEditor wrapper
├── api/                    # Axios API calls
├── context/                # React Context (auth state)
├── router/                 # AppRouter, PrivateRoute
└── utils/                  # Helper functions
```

---

## 🔐 Luồng Xác Thực

```
Register → OTP Email Verification → Login → JWT Token
         ↓
    Token stored in DataStore (Mobile) / LocalStorage (Web)
         ↓
    Mọi API request gửi kèm: Authorization: Bearer <token>
         ↓
    JWT Filter validate → Redis check → Cho phép/Từ chối
```

---

## 📊 Database Schema (Tóm tắt)

| Entity Group | Tables |
|---|---|
| **Users** | `users`, `student_profiles`, `roles` |
| **Content** | `subjects`, `chapters`, `lessons`, `lesson_contents` |
| **Assessment** | `exercises`, `exercise_solutions`, `exercise_submissions` |
| **Quiz** | `quizzes`, `questions`, `choices`, `quiz_results` |
| **Documents** | `documents` |
| **Progress** | `progresses`, `lesson_completions` |
| **AI** | `ai_chat_sessions`, `ai_chat_messages` |
| **Notifications** | `notifications`, `user_reminder_settings` |
| **Activity** | `user_activities` |

---

## 👨‍💻 Tác Giả

**Lê Anh Tuấn**
- 📧 hikatu137@gmail.com
- 🎓 Final Year Project — E-Learning Platform

---

<div align="center">

**⭐ Nếu dự án hữu ích, hãy cho một ngôi sao nhé!**

*MaSci E-Learning — Học thông minh, sống tốt hơn* 🚀

</div>
