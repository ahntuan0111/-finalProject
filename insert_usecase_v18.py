"""
insert_usecase_v18.py
Inserts Section 3.3 with 3 selected Use Case Diagrams into V17.docx -> V18.docx
Diagrams: Theory Study & AI Tutor | Exercises & Quizzes | Web Admin Dashboard
"""

import os
import sys

try:
    from docx import Document
    from docx.shared import Pt, Inches
    from docx.enum.text import WD_PARAGRAPH_ALIGNMENT, WD_LINE_SPACING
    from docx.oxml.ns import qn
    from docx.oxml import OxmlElement
except ImportError:
    import subprocess
    subprocess.check_call([sys.executable, "-m", "pip", "install", "python-docx"])
    from docx import Document
    from docx.shared import Pt, Inches
    from docx.enum.text import WD_PARAGRAPH_ALIGNMENT, WD_LINE_SPACING
    from docx.oxml.ns import qn
    from docx.oxml import OxmlElement


# ─────────────────────────────────────────────────────────────────────────────
# HELPERS
# ─────────────────────────────────────────────────────────────────────────────

def make_paragraph(before_ref, text, bold=False, italic=False,
                   align=WD_PARAGRAPH_ALIGNMENT.JUSTIFY, sa=Pt(12)):
    p = before_ref.insert_paragraph_before()
    p.alignment = align
    pf = p.paragraph_format
    pf.line_spacing_rule = WD_LINE_SPACING.MULTIPLE
    pf.line_spacing = 1.5
    pf.space_after = sa
    pf.space_before = Pt(0)
    if text:
        run = p.add_run(text)
        run.font.name = 'Times New Roman'
        run.font.size = Pt(12)
        run.bold = bold
        run.italic = italic
    return p


def make_heading(before_ref, text):
    p = before_ref.insert_paragraph_before()
    p.alignment = WD_PARAGRAPH_ALIGNMENT.LEFT
    pf = p.paragraph_format
    pf.space_before = Pt(18)
    pf.space_after = Pt(6)
    pf.line_spacing_rule = WD_LINE_SPACING.MULTIPLE
    pf.line_spacing = 1.5
    run = p.add_run(text)
    run.font.name = 'Times New Roman'
    run.font.size = Pt(12)
    run.bold = True
    return p


def make_caption(before_ref, text):
    p = before_ref.insert_paragraph_before()
    p.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
    pf = p.paragraph_format
    pf.space_before = Pt(4)
    pf.space_after = Pt(18)
    run = p.add_run(text)
    run.font.name = 'Times New Roman'
    run.font.size = Pt(11)
    run.italic = True
    return p


def make_diagram_block(before_ref, label, mermaid_code):
    """Insert placeholder caption + shaded Mermaid code block."""
    # Caption/placeholder header
    cap = before_ref.insert_paragraph_before()
    cap.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
    pf = cap.paragraph_format
    pf.space_before = Pt(12)
    pf.space_after = Pt(4)
    r = cap.add_run("[PASTE DIAGRAM IMAGE HERE -- " + label + "]")
    r.font.name = 'Times New Roman'
    r.font.size = Pt(11)
    r.italic = True
    r.bold = True

    # Each line of Mermaid as shaded Courier New
    for line in mermaid_code.strip().split('\n'):
        p = before_ref.insert_paragraph_before()
        p.alignment = WD_PARAGRAPH_ALIGNMENT.LEFT
        pf2 = p.paragraph_format
        pf2.left_indent = Inches(0.3)
        pf2.space_after = Pt(0)
        pf2.line_spacing_rule = WD_LINE_SPACING.SINGLE

        pPr = p._element.get_or_add_pPr()
        shd = OxmlElement('w:shd')
        shd.set(qn('w:val'), 'clear')
        shd.set(qn('w:color'), 'auto')
        shd.set(qn('w:fill'), 'F2F2F2')
        pPr.append(shd)

        run = p.add_run(line if line else " ")
        run.font.name = 'Courier New'
        run.font.size = Pt(9)

    # Bottom spacer
    sp = before_ref.insert_paragraph_before()
    sp.paragraph_format.space_after = Pt(18)


def make_desc_table(before_ref, doc, caption_text, rows_data):
    """Insert a 3-column description table then a caption before before_ref."""
    tbl = doc.add_table(rows=1, cols=3)
    tbl.style = 'Table Grid'

    for i, h in enumerate(["Element", "Type", "Description"]):
        c = tbl.rows[0].cells[i]
        run = c.paragraphs[0].add_run(h)
        run.font.name = 'Times New Roman'
        run.font.size = Pt(10)
        run.bold = True
        c.paragraphs[0].paragraph_format.space_after = Pt(3)

    for row_data in rows_data:
        row = tbl.add_row()
        for i, val in enumerate(row_data):
            c = row.cells[i]
            run = c.paragraphs[0].add_run(val)
            run.font.name = 'Times New Roman'
            run.font.size = Pt(10)
            c.paragraphs[0].paragraph_format.space_after = Pt(3)

    before_ref._element.addprevious(tbl._element)

    cap = before_ref.insert_paragraph_before()
    cap.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
    r = cap.add_run(caption_text)
    r.font.name = 'Times New Roman'
    r.font.size = Pt(11)
    r.italic = True
    cap.paragraph_format.space_after = Pt(18)


# ─────────────────────────────────────────────────────────────────────────────
# MERMAID DIAGRAM SOURCES (English)
# ─────────────────────────────────────────────────────────────────────────────

DIAGRAM_THEORY = """\
flowchart LR
    Student(["Student"])
    AI(["AI System (Gemini)"])

    subgraph THEORY["Mobile -- Theory Study & AI Tutor"]

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

DIAGRAM_EXERCISE = """\
flowchart LR
    Student(["Student"])
    AI(["AI System (Gemini)"])

    subgraph PRACTICE["Mobile -- Exercises & Quizzes"]

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

DIAGRAM_ADMIN = """\
flowchart LR
    Admin(["System Admin"])

    subgraph MGMT["Web Admin -- Users, Submissions & Dashboard"]

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
# DESCRIPTION TABLE DATA
# ─────────────────────────────────────────────────────────────────────────────

TABLE_THEORY = [
    ("Student",                "Primary Actor",   "Navigates chapters/lessons; marks completion; initiates AI Tutor chat"),
    ("AI System (Gemini)",     "Supporting Actor","Receives serialized context array; returns Socratic hint via REST API"),
    ("View Chapter List",      "Base Use Case",   "Entry point: GET /subjects/{id}/chapters"),
    ("View Lesson Content",    "Base Use Case",   "Loads LaTeX content body via GET /lessons/{id}"),
    ("Mark Lesson as Complete","Base Use Case",   "POST /progress/mark-complete — updates lesson_completion + recalculates streak"),
    ("Chat with AI Tutor",     "Primary Use Case","Sends message to AiTutorController with full context-injection"),
    ("Create New Chat Session","<<include>>",     "Mandatory — AiChatSession created/reused before every Gemini call"),
    ("Update Subject Progress","<<include>>",     "Mandatory — subject completion % atomically updated on mark-complete"),
    ("Watch YouTube Video",    "<<extend>>",      "Optional — only active when lesson.video_url is non-null"),
    ("View Chat History",      "<<extend>>",      "Optional — student reviews prior Socratic conversation threads"),
    ("Delete Chat History",    "<<extend>>",      "Optional — student resets session; DELETE /ai-tutor/sessions/{id}"),
]

TABLE_EXERCISE = [
    ("Student",                    "Primary Actor",   "Photographs handwritten solutions; takes MCQ quizzes; reviews results"),
    ("AI System (Gemini)",         "Supporting Actor","Evaluates multimodal payload; returns {isValid, aiReason} JSON"),
    ("Submit Handwritten Answer",  "Primary UC",      "Triggers 4-step AI Grading include chain"),
    ("Request Signed Upload URL",  "<<include>>",     "Step 1 — GET /exercises/{id}/upload-url; server generates Firebase PUT URL"),
    ("Upload Image to Firebase",   "<<include>>",     "Step 2 — Client direct binary PUT; Spring Boot server memory untouched"),
    ("Grade Submission via AI",    "<<include>>",     "Step 3 — 4-criteria Gemini prompt + Base64 image dispatched to Cloud"),
    ("Save Submission Result",     "<<include>>",     "Step 4 — Persist isValid + aiReason + publicUrl to exercise_submissions"),
    ("Take Quiz",                  "Primary UC",      "Student answers MCQ within countdown timer (CountDownTimer + StateFlow)"),
    ("Save Quiz Score",            "<<include>>",     "Mandatory — POST /quizzes/{id}/submit stores integer score to quiz_results"),
    ("View Hint",                  "<<extend>>",      "Optional — scaffolding revealed on student demand"),
    ("View Sample Solution",       "<<extend>>",      "Optional — reference solution after attempt"),
    ("View Answers & Explanation", "<<extend>>",      "Optional — post-submission correctness review with LaTeX explanations"),
]

TABLE_ADMIN = [
    ("System Admin",                "Primary Actor",   "Monitors platform health; moderates users; inspects AI grading outcomes"),
    ("View Overview Statistics",    "Primary UC",      "GET /api/admin/dashboard — COUNT/AVG aggregations over all student data"),
    ("View Student Learning Profile","<<extend>>",     "Optional drill-down — GET /admin/dashboard/student/{id}"),
    ("View User List",              "Primary UC",      "GET /api/admin/users — paginated registered accounts"),
    ("Search Users",                "<<extend>>",      "Optional — keyword filter applied to user list table on demand"),
    ("Delete User Account",         "<<extend>>",      "Optional — DELETE /api/admin/users/{id}; cascades across all FK tables"),
    ("View All Submissions",        "Primary UC",      "GET /api/admin/submissions — all exercise submission records"),
    ("View Handwritten Image",      "<<include>>",     "Mandatory — publicUrl always displayed alongside submission row"),
    ("View AI Grading Result",      "<<include>>",     "Mandatory — isValid + aiReason always shown per submission"),
]


# ─────────────────────────────────────────────────────────────────────────────
# MAIN
# ─────────────────────────────────────────────────────────────────────────────

def main():
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')

    input_path  = r"e:\Final_Project\docx\MaSci_Final_Report_V17.docx"
    output_path = r"e:\Final_Project\docx\MaSci_Final_Report_V18.docx"

    if not os.path.exists(input_path):
        print("[ERROR] File not found: " + input_path)
        return

    doc = Document(input_path)

    # ── Find anchor: insert before Section 3.4 (Database Design) or Chapter 4
    anchor_idx = -1
    for i, p in enumerate(doc.paragraphs):
        txt = p.text.strip()
        if ("3.4" in txt and "DATABASE" in txt.upper()) or \
           ("CHAPTER 4" in txt.upper() and "IMPLEMENTATION" in txt.upper()):
            anchor_idx = i
            break
    if anchor_idx == -1:
        for i, p in enumerate(doc.paragraphs):
            if "CHAPTER 4" in p.text.strip().upper():
                anchor_idx = i
                break
    if anchor_idx == -1:
        print("[ERROR] Cannot find insertion anchor.")
        return

    anchor_p = doc.paragraphs[anchor_idx]
    print("[INFO] Anchor: paragraph " + str(anchor_idx) +
          " -> '" + anchor_p.text.strip()[:70] + "'")

    # Shorthand helpers bound to anchor_p
    def ih(text):
        return make_heading(anchor_p, text)

    def ip(text, bold=False, sa=Pt(12),
           align=WD_PARAGRAPH_ALIGNMENT.JUSTIFY):
        return make_paragraph(anchor_p, text, bold=bold, align=align, sa=sa)

    def idiagram(label, code):
        return make_diagram_block(anchor_p, label, code)

    def itable(cap_text, rows):
        return make_desc_table(anchor_p, doc, cap_text, rows)

    # ===========================================================
    # SECTION 3.3 — 3 DIAGRAMS
    # All inserts go BEFORE anchor_p. Document order = code order.
    # ===========================================================

    ih("3.3 Use Case Analysis")
    ip("This section presents the Use Case Analysis for the three primary functional "
       "domains of the MaSci E-Learning Platform. Each subsection contains a UML Use "
       "Case Diagram accompanied by a description table that enumerates actor roles and "
       "the <<include>> (mandatory) and <<extend>> (conditional) behavioral relationships. "
       "The three selected domains are: (1) Theory Study and AI Tutor, which models the "
       "Socratic pedagogical engine; (2) Exercises and Quizzes, which models the "
       "AI-automated handwriting grading pipeline; and (3) Web Administration — Users, "
       "Submissions and Dashboard, which models the administrator's monitoring and "
       "moderation capabilities.")

    # ── 3.3.1 ────────────────────────────────────────────────────────────────
    ih("3.3.1 Theory Study and AI Tutor (Mobile Application)")
    ip("Figure 3.7 models the complete learning and tutoring workflow on the Android "
       "application. Two actors participate: Student (primary) and AI System — Google "
       "Gemini (supporting external system). The diagram comprises two functional "
       "sub-clusters: Theory Learning and AI Tutor.")
    ip("In the Theory Learning sub-cluster, content navigation is governed by a "
       "mandatory three-level <<include>> chain: View Lesson List <<includes>> View "
       "Chapter List; View Lesson Content <<includes>> View Lesson List. This chain "
       "enforces the backend API call sequence (GET chapters -> GET lessons -> GET "
       "content) and prevents a student from accessing lesson content without first "
       "selecting a valid chapter and lesson. Mark Lesson as Complete <<includes>> "
       "Update Subject Progress, which invokes POST /progress/mark-complete to atomically "
       "update the lesson_completion table and recalculate the subject completion "
       "percentage used by the Home Screen dashboard. Watch YouTube Video is an "
       "<<extend>> activated only when lesson.video_url is non-null.")
    ip("In the AI Tutor sub-cluster, Chat with AI Tutor <<includes>> Create New Chat "
       "Session — the AiChatSession MySQL entity is created or retrieved before the "
       "Gemini API is invoked, ensuring every conversation has a persistent session "
       "record. Chat with AI Tutor also <<extends>> View Lesson Content, making the "
       "AI tutoring entry point accessible directly from the lesson screen. "
       "View Chat History and Delete Chat History are optional extensions for "
       "session management.")
    idiagram("Figure 3.7 -- Theory Study and AI Tutor Use Case Diagram", DIAGRAM_THEORY)
    ip("Table 3.3 describes each element of Figure 3.7:", sa=Pt(6))
    itable("Table 3.3: Use Case Descriptions -- Theory Study & AI Tutor (Figure 3.7)",
           TABLE_THEORY)

    # ── 3.3.2 ────────────────────────────────────────────────────────────────
    ih("3.3.2 Exercises and Quizzes (Mobile Application)")
    ip("Figure 3.8 depicts the most technically complex student-facing use case "
       "domain: handwritten exercise submission with AI-automated grading, and MCQ "
       "quiz assessment. Two actors participate: Student (primary) and AI System — "
       "Google Gemini (supporting). The diagram contains two sub-clusters: Exercises "
       "and Quizzes.")
    ip("The Submit Handwritten Answer use case triggers a mandatory four-step "
       "<<include>> chain constituting the AI Grading Pipeline: (1) Request Signed "
       "Upload URL — the backend calls FirebaseStorageService.generateSignedUploadUrl(), "
       "returning a time-limited HTTP PUT endpoint scoped to the specific exercise; "
       "(2) Upload Image to Firebase — the Android client performs a direct binary PUT "
       "to Firebase Storage, bypassing the Spring Boot server entirely to prevent JVM "
       "heap memory exhaustion; (3) Grade Submission via AI — the backend constructs "
       "a multimodal JSON payload combining a four-criteria natural-language prompt "
       "with the Base64-encoded image and dispatches it to the Gemini REST endpoint; "
       "(4) Save Submission Result — the structured AI response (isValid: Boolean, "
       "aiReason: String) and the image's permanent publicUrl are persisted to the "
       "exercise_submissions MySQL table. View Hint and View Sample Solution are "
       "optional <<extend>> use cases available on demand. Take Quiz <<includes>> "
       "Save Quiz Score, and View Answers and Explanation is an <<extend>> available "
       "only after the quiz is formally submitted.")
    idiagram("Figure 3.8 -- Exercises and Quizzes Use Case Diagram", DIAGRAM_EXERCISE)
    ip("Table 3.4 describes each element of Figure 3.8:", sa=Pt(6))
    itable("Table 3.4: Use Case Descriptions -- Exercises & Quizzes (Figure 3.8)",
           TABLE_EXERCISE)

    # ── 3.3.3 ────────────────────────────────────────────────────────────────
    ih("3.3.3 Web Administration -- Users, Submissions and Dashboard")
    ip("Figure 3.9 models the administrative monitoring and moderation use case domain "
       "within the React 19 Web Administration Portal. A single actor participates: "
       "System Admin. Three functional sub-clusters are present: Dashboard, User "
       "Management, and Submission Review.")
    ip("The View Overview Statistics use case (GET /api/admin/dashboard) aggregates "
       "platform-wide metrics — total students, daily active users, submission counts, "
       "and average quiz scores — using pre-computed SQL COUNT and AVG queries "
       "executed by AdminDashboardController. View Student Learning Profile is an "
       "<<extend>> representing an optional drill-down to the individual student "
       "engagement timeline (GET /api/admin/dashboard/student/{id}). In the User "
       "Management sub-cluster, Search Users and Delete User Account are both "
       "<<extend>> use cases of View User List: search applies a keyword filter on "
       "demand, while Delete User Account (DELETE /api/admin/users/{id}) is a "
       "destructive moderation action that triggers ON DELETE CASCADE across all "
       "foreign-keyed student data entities including lesson_completion, quiz_results, "
       "exercise_submissions, and ai_chat_sessions. In the Submission Review "
       "sub-cluster, View All Submissions mandatorily <<includes>> both View "
       "Handwritten Image and View AI Grading Result, as a submission record without "
       "its image URL or AI evaluation verdict has no administrative value.")
    idiagram("Figure 3.9 -- Web Admin: Users, Submissions & Dashboard Use Case Diagram",
             DIAGRAM_ADMIN)
    ip("Table 3.5 describes each element of Figure 3.9:", sa=Pt(6))
    itable("Table 3.5: Use Case Descriptions -- Web Admin Dashboard (Figure 3.9)",
           TABLE_ADMIN)

    # ── SAVE ──────────────────────────────────────────────────────────────────
    doc.save(output_path)
    print("")
    print("SUCCESS! MaSci_Final_Report_V18.docx created.")
    print("   Output: " + output_path)
    print("")
    print("Next steps:")
    print("   1. Open V18.docx in MS Word")
    print("   2. Find [PASTE DIAGRAM IMAGE HERE] for Figure 3.7, 3.8, 3.9")
    print("   3. Copy Mermaid code -> mermaid.live -> Export PNG")
    print("   4. Insert Picture -> Delete grey code block")
    print("   5. References -> Update Table of Contents")


if __name__ == "__main__":
    main()
