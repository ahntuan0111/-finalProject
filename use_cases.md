# MaSci E-Learning: Use Case Diagrams (Mermaid)

Dưới đây là mã nguồn (Source Code) bằng ngôn ngữ **Mermaid.js** để vẽ 3 sơ đồ Use Case khớp chính xác 100% với các vị trí (Placeholder) mà chúng ta đã dàn sẵn trong file `MaSci_Final_Report_V16.docx`.

Bạn có thể copy mã nguồn này, dán vào [Mermaid Live Editor](https://mermaid.live/) hoặc áp dụng công cụ Diagram tích hợp sẵn trong ứng dụng ghi chú như Notion/Obsidian để export ra file ảnh ảnh `.png` chất lượng cao và chèn trực tiếp vào Word.

***

### 1. Placeholder 1: `[INSERT OVERALL USE CASE DIAGRAM HERE]`
*Tương ứng: Figure 3.1: Overall System Use Case Diagram*

```mermaid
flowchart LR
    %% Định nghĩa các Actor (Vòng tròn)
    Student((Student))
    Admin((System Admin))
    Gemini(((Google Gemini AI)))

    %% Định nghĩa Ranh giới Hệ thống (System Boundary)
    subgraph "MaSci E-Learning Platform"
        direction TB
        %% Các Use Case (Hình chữ nhật bo góc tròn)
        UC1([UC-01: Authentication & Gamification])
        UC2([UC-02: Manage Academic Content])
        UC3([UC-03: Study Theory & Watch Video])
        UC4([UC-04: Take Quizzes])
        UC5([UC-05: Submit & AI Grading])
        UC6([UC-06: Track Progress])
        UC7([UC-07: Socratic AI Tutor])
        UC8([UC-08: Push Notification & Reminder])
    end

    %% Các mũi tên kết nối tương tác
    Student ==> UC1
    Student ==> UC3
    Student ==> UC4
    Student ==> UC5
    Student ==> UC6
    Student ==> UC7

    Admin ==> UC1
    Admin ==> UC2
    Admin ==> UC8

    %% Tương tác với Hệ thống bên ngoài
    UC5 -.->|Trích xuất ảnh & Chấm điểm| Gemini
    UC7 -.->|Truyền Context JSON| Gemini
```

***

### 2. Placeholder 2: `[INSERT AI GRADING USE CASE IMAGE HERE]`
*Tương ứng: Figure 3.2: UC-05 Component Use Case Diagram*

```mermaid
flowchart LR
    %% Actor trực tiếp liên quan
    Student((Student))
    Gemini(((Google Gemini API)))
    Firebase(((Firebase Storage)))

    %% Cụm Use Case nội dung trong quá trình AI Grading (UC-05)
    subgraph "UC-05: AI Grading Sub-system"
        direction TB
        MainUC([Submit Handwritten Image])
        Sub1([Request Signed URL])
        Sub2([Upload Image to Cloud])
        Sub3([Validate Malicious Content])
        Sub4([Multimodal Prompt Evaluation])
        Sub5([Save Score & AI Reasoning])
        Sub6([Display Error / Fallback 503])
    end

    %% Tương tác Actor
    Student ==> MainUC

    %% Quan hệ <<include>> (Nét đứt)
    MainUC -.->|<< include >>| Sub1
    MainUC -.->|<< include >>| Sub2
    MainUC -.->|<< include >>| Sub4
    MainUC -.->|<< include >>| Sub5

    %% Quan hệ <<extend>> (Xảy ra khi có lỗi)
    Sub6 -.->|<< extend >>| MainUC
    Sub3 -.->|<< extend >>| MainUC

    %% Tương tác với External APIs
    Sub2 --> Firebase
    Sub4 --> Gemini
```

***

### 3. Placeholder 3: `[INSERT AI TUTOR USE CASE IMAGE HERE]`
*Tương ứng: Figure 3.3: UC-07 Component Use Case Diagram*

```mermaid
flowchart LR
    %% Actors
    Student((Student))
    Gemini(((Google Gemini API)))

    %% Cụm Use Case nội dung cho AI Tutor (UC-07)
    subgraph "UC-07: Socratic AI Tutor Sub-system"
        direction TB
        TMain([Trigger Chat Session])
        TSub1([Verify Previous Session ID])
        TSub2([Fetch Historical Messages])
        TSub3([Serialize Context Array])
        TSub4([Socratic Heuristic Interrogation])
        TSub5([Throw ANR Safety Exception])
    end

    Student ==> TMain

    %% Extend (Khởi tạo phiên mới hoặc dùng phiên cũ)
    TSub1 -.->|<< extend >>| TMain
    
    %% Include bắt buộc
    TMain -.->|<< include >>| TSub2
    TMain -.->|<< include >>| TSub3
    TMain -.->|<< include >>| TSub4

    %% Ngoại lệ Timeout
    TSub5 -.->|<< extend >>| TSub4

    %% API Hướng đích
    TSub4 --> Gemini
```

***
**Lưu ý kỹ thuật cho ảnh dán vào Word:**
- Các Actor (`( )`) đóng vai trò kích hoạt nghiệp vụ.
- Đường gạch đứt màu đen `-.->` biểu thị chuẩn xác các quan hệ `<<include>>` (Bắt buộc phải chạy) và `<<extend>>` (Ngoại lệ chạy khi điều kiện xảy ra) trong đặc tả UML Use Case.
- Tất cả Actor đều được móc nối logic chính xác đến hệ thống bên ngoài là `Google Gemini` và `Firebase` như trong nội dung báo cáo Chương 3!
