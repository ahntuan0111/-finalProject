"""
insert_usecase_v16.py
─────────────────────────────────────────────────────────────────────────────
Inserts the complete Section 3.3 Use Case Analysis into MaSci_Final_Report_V16.docx.
All diagram labels and node texts have been translated from Vietnamese to English.

Strategy:
  - Find the paragraph that contains "3.3" to locate insertion point
  - Insert 3 sub-sections with Mermaid diagram placeholder boxes + full academic analysis
  - Save as MaSci_Final_Report_V17.docx
─────────────────────────────────────────────────────────────────────────────
"""

import os
import sys

try:
    from docx import Document
    from docx.shared import Pt, Inches, RGBColor
    from docx.enum.text import WD_PARAGRAPH_ALIGNMENT, WD_LINE_SPACING
    from docx.oxml.ns import qn
    from docx.oxml import OxmlElement
except ImportError:
    import subprocess
    subprocess.check_call([sys.executable, "-m", "pip", "install", "python-docx"])
    from docx import Document
    from docx.shared import Pt, Inches, RGBColor
    from docx.enum.text import WD_PARAGRAPH_ALIGNMENT, WD_LINE_SPACING
    from docx.oxml.ns import qn
    from docx.oxml import OxmlElement

# ─────────────────────────────────────────────────────────────────────────────
# HELPER FUNCTIONS (matching the existing style of V16)
# ─────────────────────────────────────────────────────────────────────────────

def make_paragraph(doc_or_ref, text, bold=False, italic=False,
                   size=12, font='Times New Roman',
                   align=WD_PARAGRAPH_ALIGNMENT.JUSTIFY,
                   space_after=Pt(12), line_spacing=1.5,
                   before_ref=None):
    """Insert a styled paragraph. If before_ref is given, insert before that paragraph."""
    if before_ref is not None:
        p = before_ref.insert_paragraph_before()
    else:
        p = doc_or_ref.add_paragraph()

    p.alignment = align
    pf = p.paragraph_format
    pf.line_spacing_rule = WD_LINE_SPACING.MULTIPLE
    pf.line_spacing = line_spacing
    pf.space_after = space_after
    pf.space_before = Pt(0)

    if text:
        run = p.add_run(text)
        run.font.name = font
        run.font.size = Pt(size)
        run.bold = bold
        run.italic = italic
    return p


def make_heading(before_ref, text, level=3, font='Times New Roman'):
    """Insert a bold heading paragraph before before_ref."""
    p = before_ref.insert_paragraph_before()
    p.alignment = WD_PARAGRAPH_ALIGNMENT.LEFT
    pf = p.paragraph_format
    pf.space_before = Pt(18)
    pf.space_after = Pt(6)
    pf.line_spacing_rule = WD_LINE_SPACING.MULTIPLE
    pf.line_spacing = 1.5

    run = p.add_run(text)
    run.font.name = font
    run.font.size = Pt(12)
    run.bold = True
    return p


def make_diagram_placeholder(before_ref, label, mermaid_code):
    """
    Insert a shaded code-style block representing a Mermaid diagram.
    Since python-docx cannot render Mermaid, we insert the source code in a
    styled block so the user can copy it to mermaid.live and replace with image.
    """
    # Caption line
    cap = before_ref.insert_paragraph_before()
    cap.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
    pf = cap.paragraph_format
    pf.space_before = Pt(12)
    pf.space_after = Pt(4)
    r = cap.add_run(f"[PASTE DIAGRAM IMAGE HERE — {label}]")
    r.font.name = 'Times New Roman'
    r.font.size = Pt(11)
    r.italic = True
    r.bold = True

    # Mermaid source block (Courier New, shaded)
    for line in mermaid_code.strip().split('\n'):
        p = before_ref.insert_paragraph_before()
        p.alignment = WD_PARAGRAPH_ALIGNMENT.LEFT
        pf2 = p.paragraph_format
        pf2.left_indent = Inches(0.3)
        pf2.space_after = Pt(0)
        pf2.line_spacing_rule = WD_LINE_SPACING.SINGLE

        # Add shading to paragraph
        pPr = p._element.get_or_add_pPr()
        shd = OxmlElement('w:shd')
        shd.set(qn('w:val'), 'clear')
        shd.set(qn('w:color'), 'auto')
        shd.set(qn('w:fill'), 'F2F2F2')
        pPr.append(shd)

        run = p.add_run(line if line else " ")
        run.font.name = 'Courier New'
        run.font.size = Pt(9)

    # Bottom spacing
    sp = before_ref.insert_paragraph_before()
    sp.paragraph_format.space_after = Pt(18)


def make_table_row(table, col0, col1, col2=None, bold_col0=False):
    row = table.add_row()
    for i, (cell, text, b) in enumerate([
        (row.cells[0], col0, bold_col0),
        (row.cells[1], col1, False),
    ] + ([(row.cells[2], col2, False)] if col2 is not None else [])):
        para = cell.paragraphs[0]
        run = para.add_run(text or "")
        run.font.name = 'Times New Roman'
        run.font.size = Pt(10)
        run.bold = b
        para.paragraph_format.space_after = Pt(4)


def insert_actor_table(before_ref, doc, rows_data, cols=2):
    """Insert a description table before before_ref."""
    tbl = doc.add_table(rows=1, cols=cols)
    tbl.style = 'Table Grid'

    # Header
    hdr = tbl.rows[0]
    headers = ['Element / Actor', 'Type', 'Description'] if cols == 3 else ['Element', 'Description']
    for i, h in enumerate(headers[:cols]):
        c = hdr.cells[i]
        r = c.paragraphs[0].add_run(h)
        r.font.name = 'Times New Roman'
        r.font.size = Pt(10)
        r.bold = True
        c.paragraphs[0].paragraph_format.space_after = Pt(4)

    for row_data in rows_data:
        row = tbl.add_row()
        for i, val in enumerate(row_data[:cols]):
            c = row.cells[i]
            r = c.paragraphs[0].add_run(val or "")
            r.font.name = 'Times New Roman'
            r.font.size = Pt(10)
            c.paragraphs[0].paragraph_format.space_after = Pt(4)

    # Move table before the anchor paragraph
    before_ref._element.addprevious(tbl._element)
    # Add spacing after
    sp = before_ref.insert_paragraph_before()
    sp.paragraph_format.space_after = Pt(12)
    return tbl


# ─────────────────────────────────────────────────────────────────────────────
# TRANSLATED MERMAID DIAGRAM SOURCES (Vietnamese → English)
# ─────────────────────────────────────────────────────────────────────────────

DIAGRAM_1_OVERALL = """\
flowchart LR
    Guest(["👤 Guest"])
    Student(["🎓 Student"])
    Admin(["🛡️ System Admin"])
    AISystem(["🤖 AI System (Gemini)"])

    Guest -->|"generalization"| Student

    subgraph AUTH["🔐 Authentication & Onboarding"]
        UC_Welcome(["View Welcome Screen"])
        UC_Register(["Submit Registration Request"])
        UC_OTP(["Verify OTP via Email"])
        UC_Login(["Log In"])
        UC_Google(["Log In with Google"])
        UC_FCM(["Update FCM Token"])
        UC_ForgotPwd(["Forgot Password"])
        UC_SendOTP(["Send OTP via Email"])
        UC_ResetPwd(["Reset Password"])
        UC_SelectGrade(["Select Grade Level"])
        UC_AdminLogin(["Admin Web Login"])
    end

    Guest --- UC_Welcome
    Guest --- UC_Register
    Guest --- UC_Login
    Guest --- UC_ForgotPwd
    Student --- UC_SelectGrade
    Admin --- UC_AdminLogin

    UC_Register -.->|"<<include>>"| UC_OTP
    UC_Login -.->|"<<include>>"| UC_FCM
    UC_ForgotPwd -.->|"<<include>>"| UC_SendOTP
    UC_ForgotPwd -.->|"<<include>>"| UC_ResetPwd
    UC_Google -.->|"<<extend>>"| UC_Login"""

DIAGRAM_2_THEORY = """\
flowchart LR
    Student(["🎓 Student"])
    AI(["🤖 AI System (Gemini)"])

    subgraph THEORY["📱 Mobile — Theory Study & AI Tutor"]

        subgraph LT["Theory Learning"]
            UC_ViewChapters(["View Chapter List"])
            UC_ViewLessons(["View Lesson List"])
            UC_ViewContent(["View Lesson Content"])
            UC_WatchVideo(["Watch YouTube Video"])
            UC_MarkDone(["Mark Lesson as Complete"])
            UC_UpdateProgress(["Update Subject Progress"])
        end

        subgraph AI_TUTOR["AI Tutor"]
            UC_ChatAI(["Chat with AI Tutor"])
            UC_CreateSession(["Create New Chat Session"])
            UC_ChatHistory(["View Chat History"])
            UC_DeleteChat(["Delete Chat History"])
        end

    end

    Student --- UC_ViewChapters
    Student --- UC_ViewLessons
    Student --- UC_ViewContent
    Student --- UC_MarkDone
    Student --- UC_ChatAI
    AI --- UC_ChatAI

    UC_ViewLessons -.->|"<<include>>"| UC_ViewChapters
    UC_ViewContent -.->|"<<include>>"| UC_ViewLessons
    UC_MarkDone -.->|"<<include>>"| UC_UpdateProgress
    UC_ChatAI -.->|"<<include>>"| UC_CreateSession

    UC_WatchVideo -.->|"<<extend>>"| UC_ViewContent
    UC_ChatAI -.->|"<<extend>>"| UC_ViewContent
    UC_ChatHistory -.->|"<<extend>>"| UC_ChatAI
    UC_DeleteChat -.->|"<<extend>>"| UC_ChatHistory"""

DIAGRAM_3_EXERCISE = """\
flowchart LR
    Student(["🎓 Student"])
    AI(["🤖 AI System (Gemini)"])

    subgraph PRACTICE["📱 Mobile — Exercises & Quizzes"]

        subgraph EX["Exercises"]
            UC_ViewExercise(["View Exercise Question"])
            UC_ViewHint(["View Hint"])
            UC_ViewSolution(["View Sample Solution"])
            UC_Submit(["Submit Handwritten Answer"])
            UC_GetUrl(["Request Signed Upload URL"])
            UC_Upload(["Upload Image to Firebase"])
            UC_Validate(["Grade Submission via AI"])
            UC_SaveResult(["Save Submission Result"])
        end

        subgraph QUIZ["Quizzes"]
            UC_ViewQuizList(["View Quiz List"])
            UC_TakeQuiz(["Take Quiz"])
            UC_SaveScore(["Save Quiz Score"])
            UC_ViewAnswer(["View Answers & Explanation"])
        end

    end

    Student --- UC_ViewExercise
    Student --- UC_Submit
    Student --- UC_ViewQuizList
    Student --- UC_TakeQuiz
    AI --- UC_Validate

    UC_Submit -.->|"<<include>>"| UC_GetUrl
    UC_GetUrl -.->|"<<include>>"| UC_Upload
    UC_Upload -.->|"<<include>>"| UC_Validate
    UC_Validate -.->|"<<include>>"| UC_SaveResult
    UC_TakeQuiz -.->|"<<include>>"| UC_SaveScore

    UC_ViewHint -.->|"<<extend>>"| UC_ViewExercise
    UC_ViewSolution -.->|"<<extend>>"| UC_ViewExercise
    UC_ViewAnswer -.->|"<<extend>>"| UC_TakeQuiz"""

DIAGRAM_4_PROFILE = """\
flowchart LR
    Student(["🎓 Student"])

    subgraph UTILITY["📱 Mobile — Progress, Profile & Utilities"]

        subgraph PROGRESS["Learning Progress"]
            UC_ViewProgress(["View Overall Progress"])
            UC_ViewSubjectDetail(["View Subject Detail"])
            UC_ViewStreak(["View Learning Streak"])
            UC_LogActivity(["Log Learning Activity"])
        end

        subgraph PROFILE["Personal Profile"]
            UC_ViewProfile(["View Profile & Statistics"])
            UC_ChangePassword(["Change Password"])
            UC_UpdateProfile(["Update Personal Information"])
        end

        subgraph DOC["Study Documents"]
            UC_ViewDocs(["View Document List"])
            UC_ReadPDF(["Read PDF In-App"])
            UC_DownloadPDF(["Download PDF"])
        end

        subgraph NOTIF["Notifications"]
            UC_ViewNotif(["View Notification List"])
            UC_UnreadCount(["View Unread Count Badge"])
            UC_MarkOne(["Mark One as Read"])
            UC_MarkAll(["Mark All as Read"])
        end

        subgraph REMINDER["Study Reminders"]
            UC_ViewReminder(["View Reminder Settings"])
            UC_UpdateReminder(["Update Reminder Schedule"])
        end

        UC_Search(["Search Content"])
    end

    Student --- UC_ViewProgress
    Student --- UC_ViewStreak
    Student --- UC_ViewProfile
    Student --- UC_ViewDocs
    Student --- UC_ViewNotif
    Student --- UC_ViewReminder
    Student --- UC_Search

    UC_ViewProgress -.->|"<<include>>"| UC_LogActivity
    UC_ViewProfile -.->|"<<include>>"| UC_ViewStreak
    UC_ViewNotif -.->|"<<include>>"| UC_UnreadCount

    UC_ViewSubjectDetail -.->|"<<extend>>"| UC_ViewProgress
    UC_ReadPDF -.->|"<<extend>>"| UC_ViewDocs
    UC_DownloadPDF -.->|"<<extend>>"| UC_ReadPDF
    UC_ChangePassword -.->|"<<extend>>"| UC_ViewProfile
    UC_UpdateProfile -.->|"<<extend>>"| UC_ViewProfile
    UC_MarkOne -.->|"<<extend>>"| UC_ViewNotif
    UC_MarkAll -.->|"<<extend>>"| UC_ViewNotif
    UC_UpdateReminder -.->|"<<extend>>"| UC_ViewReminder"""

DIAGRAM_5_ADMIN_CONTENT = """\
flowchart LR
    Admin(["🛡️ System Admin"])

    subgraph CONTENT["🖥️ Web Admin — Content Management"]

        subgraph SUBJECT["Subjects & Chapters"]
            UC_ViewSubjects(["View Subject List"])
            UC_ManageChapters(["Manage Chapters"])
            UC_ManageLessons(["Manage Lessons"])
        end

        subgraph THEORY_ADMIN["Lesson Content"]
            UC_ManageContent(["Manage Lesson Content"])
        end

        subgraph EX_ADMIN["Exercises"]
            UC_ManageExercises(["Manage Exercises"])
            UC_ManageHints(["Manage Hints"])
            UC_ManageSolutions(["Manage Sample Solutions"])
        end

        subgraph QUIZ_ADMIN["Quizzes"]
            UC_ManageQuizzes(["Manage Quizzes"])
            UC_ManageQuestions(["Manage Questions"])
            UC_ManageAnswers(["Manage Answer Choices"])
        end

        subgraph DOC_ADMIN["Documents"]
            UC_UploadDoc(["Upload PDF Document"])
            UC_SaveFirebase(["Save File to Firebase Storage"])
            UC_ManageDocs(["Manage Documents"])
        end

    end

    Admin --- UC_ViewSubjects
    Admin --- UC_ManageChapters
    Admin --- UC_ManageLessons
    Admin --- UC_ManageExercises
    Admin --- UC_ManageQuizzes
    Admin --- UC_UploadDoc

    UC_ManageChapters -.->|"<<include>>"| UC_ViewSubjects
    UC_ManageLessons -.->|"<<include>>"| UC_ManageChapters
    UC_ManageExercises -.->|"<<include>>"| UC_ManageSolutions
    UC_ManageQuizzes -.->|"<<include>>"| UC_ManageQuestions
    UC_ManageQuestions -.->|"<<include>>"| UC_ManageAnswers
    UC_UploadDoc -.->|"<<include>>"| UC_SaveFirebase

    UC_ManageContent -.->|"<<extend>>"| UC_ManageLessons
    UC_ManageHints -.->|"<<extend>>"| UC_ManageExercises
    UC_ManageDocs -.->|"<<extend>>"| UC_UploadDoc"""

DIAGRAM_6_ADMIN_MGMT = """\
flowchart LR
    Admin(["🛡️ System Admin"])

    subgraph MGMT["🖥️ Web Admin — Users, Submissions & Dashboard"]

        subgraph DASH_ADMIN["Dashboard"]
            UC_Dashboard(["View Overview Statistics"])
            UC_StudentDetail(["View Student Learning Profile"])
        end

        subgraph USER_ADMIN["User Management"]
            UC_ViewUsers(["View User List"])
            UC_SearchUsers(["Search Users"])
            UC_DeleteUser(["Delete User Account"])
        end

        subgraph SUB_ADMIN["Submission Review"]
            UC_ViewSubmissions(["View All Submissions"])
            UC_ViewImage(["View Handwritten Image"])
            UC_ViewAIResult(["View AI Grading Result"])
        end

    end

    Admin --- UC_Dashboard
    Admin --- UC_ViewUsers
    Admin --- UC_ViewSubmissions

    UC_ViewSubmissions -.->|"<<include>>"| UC_ViewImage
    UC_ViewSubmissions -.->|"<<include>>"| UC_ViewAIResult

    UC_StudentDetail -.->|"<<extend>>"| UC_Dashboard
    UC_SearchUsers -.->|"<<extend>>"| UC_ViewUsers
    UC_DeleteUser -.->|"<<extend>>"| UC_ViewUsers"""


# ─────────────────────────────────────────────────────────────────────────────
# MAIN INSERTION LOGIC
# ─────────────────────────────────────────────────────────────────────────────

def find_anchor(doc, keyword_upper):
    """Return the paragraph index whose text contains keyword_upper (case-insensitive)."""
    for i, p in enumerate(doc.paragraphs):
        if keyword_upper in p.text.strip().upper():
            return i
    return -1


def main():
    # Fix Windows cp1252 encoding for print output
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')
    input_path  = r"e:\Final_Project\docx\MaSci_Final_Report_V16.docx"
    output_path = r"e:\Final_Project\docx\MaSci_Final_Report_V17.docx"

    if not os.path.exists(input_path):
        print(f"[ERROR] Input file not found: {input_path}")
        return

    doc = Document(input_path)
    paragraphs = doc.paragraphs

    # ── Locate the anchor paragraph: "3.4" or "CHAPTER 4" to insert BEFORE it
    anchor_idx = -1
    for i, p in enumerate(paragraphs):
        txt = p.text.strip()
        # Insert content before Section 3.4 (Database Design) or Chapter 4
        if ("3.4" in txt and "DATABASE" in txt.upper()) or \
           ("CHAPTER 4" in txt.upper() and "IMPLEMENTATION" in txt.upper()):
            anchor_idx = i
            break

    # Fallback: insert before Chapter 4 heading
    if anchor_idx == -1:
        for i, p in enumerate(paragraphs):
            if "CHAPTER 4" in p.text.strip().upper():
                anchor_idx = i
                break

    if anchor_idx == -1:
        print("[ERROR] Could not find insertion anchor (Section 3.4 or Chapter 4).")
        return

    anchor_p = doc.paragraphs[anchor_idx]
    print(f"[INFO] Anchor found at paragraph {anchor_idx}: '{anchor_p.text.strip()[:60]}'")

    # ─── Helper shortcut: insert paragraph before anchor ───
    def ip(text, bold=False, italic=False, align=WD_PARAGRAPH_ALIGNMENT.JUSTIFY, sa=Pt(12)):
        return make_paragraph(doc, text, bold=bold, italic=italic, align=align,
                              space_after=sa, before_ref=anchor_p)

    def ih(text):
        return make_heading(anchor_p, text)

    def idiagram(label, mermaid_code):
        return make_diagram_placeholder(anchor_p, label, mermaid_code)

    # ═══════════════════════════════════════════════════════════════════════════
    # INSERT ALL CONTENT (inserted in reverse order relative to anchor,
    # so the final document order is correct top-to-bottom)
    # ═══════════════════════════════════════════════════════════════════════════

    # ── SECTION 3.3 HEADING ──────────────────────────────────────────────────
    ih("3.3 Use Case Analysis")
    ip("This section presents a comprehensive Use Case Analysis of the MaSci E-Learning Platform, "
       "structured across six functional domains derived from the three application tiers: Mobile "
       "Application, Web Administration Portal, and Spring Boot Backend. For each domain, a UML "
       "Use Case Diagram is provided, followed by a description of the actor associations and the "
       "include/extend relationships that define the system's behavioral contracts. The two most "
       "architecturally significant use cases — UC-05 (AI Grading) and UC-07 (Socratic AI Tutor) "
       "— are further elaborated with 4-part technical analysis in Sections 3.3.5 and 3.3.6.")

    # ── 3.3.1 Authentication & Onboarding ───────────────────────────────────
    ih("3.3.1 Authentication and Onboarding")
    ip("Figure 3.7 illustrates the Authentication and Onboarding use case domain. Three actors "
       "participate: Guest (unauthenticated user), Student (inherits Guest via generalization), "
       "and System Admin. The diagram models OTP-based registration, standard email login with "
       "mandatory FCM token synchronization, Google OAuth2 as an optional login extension, and a "
       "two-step password recovery flow. Upon first login, the Student actor is directed to the "
       "Select Grade Level use case to personalize curriculum delivery.")
    ip("Key relationships: Registration <<includes>> OTP Verification (mandatory email "
       "confirmation before account activation). Login <<includes>> Update FCM Token (ensures "
       "push notification delivery is always current). Google Sign-In <<extends>> Login (optional "
       "OAuth2 alternate path). Forgot Password <<includes>> Send OTP and <<includes>> Reset "
       "Password (two mandatory sequential sub-steps).")
    idiagram("Figure 3.7 — Authentication & Onboarding Use Case Diagram", DIAGRAM_1_OVERALL)

    # ── 3.3.2 Theory Study & AI Tutor ───────────────────────────────────────
    ih("3.3.2 Theory Study and AI Tutor (Mobile)")
    ip("Figure 3.8 models the learning content consumption workflow on the Android application. "
       "The Student navigates a mandatory three-level content hierarchy (Chapters → Lessons → "
       "Content) enforced through an include chain, ensuring that the backend API is called in "
       "the correct sequence: subject chapters first, then lessons within a chapter, then the "
       "specific lesson content body. The Marking a Lesson as Complete use case <<includes>> "
       "Update Subject Progress, which triggers a POST /progress/mark-complete API call to "
       "increment the student's subject completion percentage.")
    ip("The AI Tutor sub-cluster introduces a secondary actor: AI System (Google Gemini). The "
       "Chat with AI Tutor use case mandates session creation (<<include>>) and optionally "
       "extends the View Lesson Content use case, allowing students to seek AI assistance "
       "directly from within a lesson without navigating away. Watch YouTube Video and View "
       "Chat History are both optional extensions (<<extend>>) triggered by content availability "
       "and user intent respectively.")
    idiagram("Figure 3.8 — Theory Study & AI Tutor Use Case Diagram", DIAGRAM_2_THEORY)

    # ── 3.3.3 Exercises & Quizzes ────────────────────────────────────────────
    ih("3.3.3 Exercises and Quizzes (Mobile)")
    ip("Figure 3.9 depicts the most technically complex student-facing use case domain: exercise "
       "submission and quiz assessment. The Submit Handwritten Answer use case triggers a "
       "mandatory four-step <<include>> chain: (1) Request Signed Upload URL — the backend "
       "generates a Firebase pre-signed PUT endpoint without processing image bytes; "
       "(2) Upload Image to Firebase — the Android client performs a direct binary PUT to "
       "Firebase Storage, bypassing the Spring Boot server entirely; "
       "(3) Grade Submission via AI — the backend retrieves the image and invokes the Gemini "
       "multimodal validation pipeline; (4) Save Submission Result — the structured AI response "
       "(isValid + aiReason) is persisted to the exercise_submissions table.")
    ip("View Hint and View Sample Solution are optional extensions (<<extend>>) of View Exercise "
       "Question, accessible when the student requires additional scaffolding. For quizzes, "
       "submitting answers <<includes>> Save Quiz Score, and View Answers & Explanation is an "
       "optional post-submission extension. The AI System actor participates exclusively in the "
       "Grade Submission via AI use case.")
    idiagram("Figure 3.9 — Exercises & Quizzes Use Case Diagram", DIAGRAM_3_EXERCISE)

    # ── 3.3.4 Profile, Progress & Utilities ─────────────────────────────────
    ih("3.3.4 Progress, Profile and Utilities (Mobile)")
    ip("Figure 3.10 consolidates the remaining mobile use cases into five sub-clusters: Learning "
       "Progress, Personal Profile, Study Documents, Notifications, and Study Reminders. The "
       "View Overall Progress use case <<includes>> Log Learning Activity, ensuring that every "
       "time the student's progress dashboard is loaded, the backend records a learning activity "
       "event (POST /activity/log) for gamification streak calculation. View Profile & Statistics "
       "<<includes>> View Learning Streak, surfacing the current streak count prominently in the "
       "user interface to reinforce the loss-aversion engagement mechanic.")
    ip("The Notification cluster models two distinct mark-as-read actions: mark one notification "
       "read (PUT /{id}/read) and mark all as read (PUT /read-all), both modelled as separate "
       "<<extend>> use cases of View Notification List. The Document cluster supports in-app PDF "
       "reading and offline downloading as optional extensions of the document list view. "
       "Study Reminders expose a single editable configuration use case, reflecting the "
       "UserReminderSettingController's PUT /reminder endpoint.")
    idiagram("Figure 3.10 — Progress, Profile & Utilities Use Case Diagram", DIAGRAM_4_PROFILE)

    # ── 3.3.5 Web Admin: Content Management ──────────────────────────────────
    ih("3.3.5 Web Administration — Content Management")
    ip("Figure 3.11 captures the System Admin's content authoring workflow within the React-based "
       "Web Administration Portal. Content authority follows a strict parent-child hierarchy "
       "enforced through include relationships: Manage Chapters <<includes>> View Subject List; "
       "Manage Lessons <<includes>> Manage Chapters; establishing that an administrator cannot "
       "add a lesson without first selecting both a subject and a chapter. This cascade mirrors "
       "the ON DELETE CASCADE foreign key constraint in the MySQL schema, ensuring referential "
       "integrity is preserved at both the API and database layers.")
    ip("Manage Exercises <<includes>> Manage Sample Solutions (a solution is mandatory for every "
       "exercise). Managing Hints is optional (<<extend>>), reflecting that not all exercises "
       "require a hint. The Quiz sub-cluster enforces that every Quiz contains Questions "
       "(<<include>>) and every Question contains Answer Choices (<<include>>). Upload PDF "
       "Document <<includes>> Save File to Firebase Storage, implementing the same Signed URL "
       "delegation pattern used in the student exercise submission flow.")
    idiagram("Figure 3.11 — Web Admin Content Management Use Case Diagram", DIAGRAM_5_ADMIN_CONTENT)

    # ── 3.3.6 Web Admin: Users, Submissions & Dashboard ──────────────────────
    ih("3.3.6 Web Administration — Users, Submissions and Dashboard")
    ip("Figure 3.12 models the administrative monitoring and moderation use cases. The View "
       "Overview Statistics use case aggregates platform-wide metrics (daily active users, total "
       "submissions, average quiz scores) via the AdminDashboardController. View Student Learning "
       "Profile is an optional drill-down extension (<<extend>>) that surfaces the individual "
       "engagement history, quiz result history, and AI chat session count for a specific student.")
    ip("View All Submissions mandatorily <<includes>> both View Handwritten Image and View AI "
       "Grading Result, because a submission row without its associated image and AI evaluation "
       "has no administrative value. Search Users and Delete User Account are modelled as "
       "optional extensions of View User List, reflecting that browsing the user list is a "
       "prerequisite for both targeted search filtering and individual account deletion. "
       "The Delete User Account use case maps to DELETE /api/admin/users/{id}, which triggers "
       "ON DELETE CASCADE across all foreign-keyed student data entities.")
    idiagram("Figure 3.12 — Web Admin Users, Submissions & Dashboard Use Case Diagram", DIAGRAM_6_ADMIN_MGMT)

    # ── SUMMARY TABLE ─────────────────────────────────────────────────────────
    ip("Table 3.3 summarises the complete inventory of include and extend relationships across "
       "all six use case domains:", sa=Pt(6))

    # Build summary table directly in doc (before anchor_p)
    tbl_cols = 3
    tbl = doc.add_table(rows=1, cols=tbl_cols)
    tbl.style = 'Table Grid'
    hdr = tbl.rows[0]
    for i, h in enumerate(['Relationship', 'Base Use Case', 'Sub-Use Case / Condition']):
        c = hdr.cells[i]
        r = c.paragraphs[0].add_run(h)
        r.font.name = 'Times New Roman'; r.font.size = Pt(10); r.bold = True

    include_data = [
        ("<<include>>", "Submit Registration Request", "Verify OTP via Email"),
        ("<<include>>", "Log In", "Update FCM Token"),
        ("<<include>>", "Forgot Password", "Send OTP via Email"),
        ("<<include>>", "Forgot Password", "Reset Password"),
        ("<<include>>", "View Lesson List", "View Chapter List"),
        ("<<include>>", "View Lesson Content", "View Lesson List"),
        ("<<include>>", "Mark Lesson as Complete", "Update Subject Progress"),
        ("<<include>>", "Chat with AI Tutor", "Create New Chat Session"),
        ("<<include>>", "Submit Handwritten Answer", "Request Signed Upload URL"),
        ("<<include>>", "Request Signed Upload URL", "Upload Image to Firebase"),
        ("<<include>>", "Upload Image to Firebase", "Grade Submission via AI"),
        ("<<include>>", "Grade Submission via AI", "Save Submission Result"),
        ("<<include>>", "Take Quiz", "Save Quiz Score"),
        ("<<include>>", "View Overall Progress", "Log Learning Activity"),
        ("<<include>>", "View Profile & Statistics", "View Learning Streak"),
        ("<<include>>", "View All Submissions", "View Handwritten Image"),
        ("<<include>>", "View All Submissions", "View AI Grading Result"),
        ("<<extend>>", "Log In with Google", "Log In — when OAuth2 selected"),
        ("<<extend>>", "Watch YouTube Video", "View Lesson Content — if video_url != null"),
        ("<<extend>>", "Chat with AI Tutor", "View Lesson Content — student has question"),
        ("<<extend>>", "View Hint", "View Exercise — student needs help"),
        ("<<extend>>", "View Sample Solution", "View Exercise — post-attempt reference"),
        ("<<extend>>", "View Answers & Explanation", "Take Quiz — after submission"),
        ("<<extend>>", "View Subject Detail", "View Overall Progress — drill-down"),
        ("<<extend>>", "Change Password", "View Profile — user requests change"),
        ("<<extend>>", "View Student Learning Profile", "View Dashboard — drill-down per student"),
        ("<<extend>>", "Search Users", "View User List — filter by keyword"),
        ("<<extend>>", "Delete User Account", "View User List — admin moderates"),
    ]

    for rel, base, sub in include_data:
        row = tbl.add_row()
        for i, val in enumerate([rel, base, sub]):
            c = row.cells[i]
            r = c.paragraphs[0].add_run(val)
            r.font.name = 'Times New Roman'; r.font.size = Pt(9)
            c.paragraphs[0].paragraph_format.space_after = Pt(2)

    anchor_p._element.addprevious(tbl._element)

    # Caption for table
    cap_p = anchor_p.insert_paragraph_before()
    cap_p.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
    cap_r = cap_p.add_run("Table 3.3: Complete Use Case Relationship Inventory")
    cap_r.font.name = 'Times New Roman'; cap_r.font.size = Pt(11); cap_r.italic = True
    cap_p.paragraph_format.space_after = Pt(18)

    # ── SAVE ─────────────────────────────────────────────────────────────────
    doc.save(output_path)
    print("")
    print("SUCCESS! Section 3.3 Use Case Analysis inserted.")
    print("   Input : " + input_path)
    print("   Output: " + output_path)
    print("")
    print("Next steps:")
    print("   1. Open MaSci_Final_Report_V17.docx in MS Word")
    print("   2. Find each [PASTE DIAGRAM IMAGE HERE] placeholder")
    print("   3. Copy Mermaid code below it -> paste at https://mermaid.live")
    print("   4. Export as PNG -> Insert Picture in Word -> Delete code block")
    print("   5. Run References -> Update Table of Contents")


if __name__ == "__main__":
    main()
