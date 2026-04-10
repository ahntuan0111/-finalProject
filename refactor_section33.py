"""
refactor_section33.py
─────────────────────────────────────────────────────────────────────────────
Reads MaSci_Final_Report_V18.docx.
Locates Section 3.3 and deletes ALL its paragraphs.
Replaces them with the clean, OUM-compliant structure:
  3.3.1 Actors and System Boundary
  3.3.2 Use Case Diagrams  (3 Functional Clusters: Figure 3.1 / 3.2 / 3.3)
  3.3.3 Use Case Descriptions (UC-05 & UC-07 with Main + Exception Flow tables)
Saves as MaSci_Final_Report_V19.docx.
─────────────────────────────────────────────────────────────────────────────
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

def del_paragraph(para):
    p = para._element
    p.getparent().remove(p)


def ip(anchor, text, bold=False, italic=False,
       align=WD_PARAGRAPH_ALIGNMENT.JUSTIFY, sa=Pt(12)):
    p = anchor.insert_paragraph_before()
    p.alignment = align
    pf = p.paragraph_format
    pf.line_spacing_rule = WD_LINE_SPACING.MULTIPLE
    pf.line_spacing = 1.5
    pf.space_after = sa
    pf.space_before = Pt(0)
    if text:
        r = p.add_run(text)
        r.font.name = 'Times New Roman'
        r.font.size = Pt(12)
        r.bold = bold
        r.italic = italic
    return p


def ih(anchor, text, space_before=Pt(18)):
    p = anchor.insert_paragraph_before()
    p.alignment = WD_PARAGRAPH_ALIGNMENT.LEFT
    pf = p.paragraph_format
    pf.space_before = space_before
    pf.space_after = Pt(6)
    pf.line_spacing_rule = WD_LINE_SPACING.MULTIPLE
    pf.line_spacing = 1.5
    r = p.add_run(text)
    r.font.name = 'Times New Roman'
    r.font.size = Pt(12)
    r.bold = True
    return p


def icaption(anchor, text):
    p = anchor.insert_paragraph_before()
    p.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
    pf = p.paragraph_format
    pf.space_before = Pt(4)
    pf.space_after = Pt(16)
    r = p.add_run(text)
    r.font.name = 'Times New Roman'
    r.font.size = Pt(11)
    r.italic = True
    return p


def diagram_placeholder(anchor, figure_label, mermaid_code):
    """Shaded placeholder with label + Mermaid source."""
    # Header label
    hdr = anchor.insert_paragraph_before()
    hdr.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
    hdr.paragraph_format.space_before = Pt(10)
    hdr.paragraph_format.space_after = Pt(4)
    r = hdr.add_run("[PASTE DIAGRAM IMAGE HERE -- " + figure_label + "]")
    r.font.name = 'Times New Roman'
    r.font.size = Pt(11)
    r.bold = True
    r.italic = True

    for line in mermaid_code.strip().split('\n'):
        p = anchor.insert_paragraph_before()
        p.alignment = WD_PARAGRAPH_ALIGNMENT.LEFT
        p.paragraph_format.left_indent = Inches(0.3)
        p.paragraph_format.space_after = Pt(0)
        p.paragraph_format.line_spacing_rule = WD_LINE_SPACING.SINGLE

        pPr = p._element.get_or_add_pPr()
        shd = OxmlElement('w:shd')
        shd.set(qn('w:val'), 'clear')
        shd.set(qn('w:color'), 'auto')
        shd.set(qn('w:fill'), 'EEEEEE')
        pPr.append(shd)

        run = p.add_run(line if line else " ")
        run.font.name = 'Courier New'
        run.font.size = Pt(9)

    # Spacer
    sp = anchor.insert_paragraph_before()
    sp.paragraph_format.space_after = Pt(10)


def insert_table(anchor, doc, caption_text, headers, rows_data):
    """Insert a grid table + caption before anchor."""
    cols = len(headers)
    tbl = doc.add_table(rows=1, cols=cols)
    tbl.style = 'Table Grid'

    # Header row
    for i, h in enumerate(headers):
        c = tbl.rows[0].cells[i]
        r = c.paragraphs[0].add_run(h)
        r.font.name = 'Times New Roman'
        r.font.size = Pt(10)
        r.bold = True
        c.paragraphs[0].paragraph_format.space_after = Pt(3)

    # Data rows
    for row_data in rows_data:
        row = tbl.add_row()
        for i, val in enumerate(row_data[:cols]):
            c = row.cells[i]
            r = c.paragraphs[0].add_run(val or "")
            r.font.name = 'Times New Roman'
            r.font.size = Pt(10)
            c.paragraphs[0].paragraph_format.space_after = Pt(3)

    anchor._element.addprevious(tbl._element)

    # Caption
    cap = anchor.insert_paragraph_before()
    cap.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
    cr = cap.add_run(caption_text)
    cr.font.name = 'Times New Roman'
    cr.font.size = Pt(11)
    cr.italic = True
    cap.paragraph_format.space_after = Pt(18)


# ─────────────────────────────────────────────────────────────────────────────
# MERMAID DIAGRAMS (English, clean labels)
# ─────────────────────────────────────────────────────────────────────────────

MERMAID_EXERCISE = """\
flowchart LR
    Student(["Student"])
    AI(["AI System (Gemini)"])

    subgraph PRACTICE["Mobile -- Exercises and Quizzes"]
        subgraph EX["Exercises"]
            UC_View(["View Exercise"])
            UC_Hint(["View Hint"])
            UC_Solution(["View Sample Solution"])
            UC_Submit(["Submit Handwritten Answer"])
            UC_SignedUrl(["Request Signed Upload URL"])
            UC_Upload(["Upload Image to Firebase"])
            UC_Grade(["Grade via AI"])
            UC_Save(["Save Submission Result"])
        end
        subgraph QUIZ["Quizzes"]
            UC_QuizList(["View Quiz List"])
            UC_TakeQuiz(["Take Quiz"])
            UC_SaveScore(["Save Quiz Score"])
            UC_Answers(["View Answers and Explanation"])
        end
    end

    Student --- UC_View
    Student --- UC_Submit
    Student --- UC_QuizList
    Student --- UC_TakeQuiz
    AI --- UC_Grade

    UC_Submit -.->|"<<include>>"| UC_SignedUrl
    UC_SignedUrl -.->|"<<include>>"| UC_Upload
    UC_Upload -.->|"<<include>>"| UC_Grade
    UC_Grade -.->|"<<include>>"| UC_Save
    UC_TakeQuiz -.->|"<<include>>"| UC_SaveScore

    UC_Hint -.->|"<<extend>>"| UC_View
    UC_Solution -.->|"<<extend>>"| UC_View
    UC_Answers -.->|"<<extend>>"| UC_TakeQuiz"""

MERMAID_THEORY = """\
flowchart LR
    Student(["Student"])
    AI(["AI System (Gemini)"])

    subgraph THEORY["Mobile -- Theory Study and AI Tutor"]
        subgraph LT["Theory Learning"]
            UC_Chapters(["View Chapter List"])
            UC_Lessons(["View Lesson List"])
            UC_Content(["View Lesson Content"])
            UC_Video(["Watch YouTube Video"])
            UC_Mark(["Mark Lesson Complete"])
            UC_Progress(["Update Subject Progress"])
        end
        subgraph TUTOR["AI Tutor"]
            UC_Chat(["Chat with AI Tutor"])
            UC_Session(["Create or Resume Chat Session"])
            UC_History(["Retrieve Chat History"])
            UC_Delete(["Delete Chat History"])
        end
    end

    Student --- UC_Chapters
    Student --- UC_Lessons
    Student --- UC_Content
    Student --- UC_Mark
    Student --- UC_Chat
    AI --- UC_Chat

    UC_Lessons -.->|"<<include>>"| UC_Chapters
    UC_Content -.->|"<<include>>"| UC_Lessons
    UC_Mark -.->|"<<include>>"| UC_Progress
    UC_Chat -.->|"<<include>>"| UC_Session
    UC_Chat -.->|"<<include>>"| UC_History

    UC_Video -.->|"<<extend>>"| UC_Content
    UC_Chat -.->|"<<extend>>"| UC_Content
    UC_Delete -.->|"<<extend>>"| UC_History"""

MERMAID_ADMIN = """\
flowchart LR
    Admin(["System Admin"])

    subgraph MGMT["Web Admin -- Users, Submissions and Dashboard"]
        subgraph DASH["Dashboard"]
            UC_Stats(["View Overview Statistics"])
            UC_StudentProfile(["View Student Learning Profile"])
        end
        subgraph USERS["User Management"]
            UC_UserList(["View User List"])
            UC_Search(["Search Users"])
            UC_Delete(["Delete User Account"])
        end
        subgraph SUBS["Submission Review"]
            UC_Submissions(["View All Submissions"])
            UC_Image(["View Handwritten Image"])
            UC_AIResult(["View AI Grading Result"])
        end
    end

    Admin --- UC_Stats
    Admin --- UC_UserList
    Admin --- UC_Submissions

    UC_Submissions -.->|"<<include>>"| UC_Image
    UC_Submissions -.->|"<<include>>"| UC_AIResult

    UC_StudentProfile -.->|"<<extend>>"| UC_Stats
    UC_Search -.->|"<<extend>>"| UC_UserList
    UC_Delete -.->|"<<extend>>"| UC_UserList"""


# ─────────────────────────────────────────────────────────────────────────────
# TABLE DATA
# ─────────────────────────────────────────────────────────────────────────────

UC05_HEADERS = ["Field", "Detail"]
UC05_DATA = [
    ("Use Case ID",    "UC-05"),
    ("Use Case Name",  "Submit Handwritten Answer and AI Grading"),
    ("Actor(s)",       "Student (primary); AI System — Google Gemini (supporting external system)"),
    ("Pre-condition",  "Student is authenticated. The exercise is of open-ended (handwritten) type. "
                       "The device camera or photo gallery is accessible."),
    ("Main Flow",
     "1. Student selects an exercise and activates the camera or photo library.\n"
     "2. System generates a Signed Upload URL by calling FirebaseStorageService "
     "(GET /exercises/{id}/upload-url). [<<include>> Request Signed URL]\n"
     "3. Android client performs a direct HTTP PUT to Firebase Storage using the "
     "Signed URL — the Spring Boot server does not receive image bytes. "
     "[<<include>> Upload Image to Firebase]\n"
     "4. Backend constructs a multimodal payload (4-criteria natural-language prompt "
     "+ Base64 image) and dispatches it to the Gemini REST API. "
     "[<<include>> Grade via AI]\n"
     "5. AI returns a structured JSON response containing isValid (Boolean) and "
     "aiReason (String).\n"
     "6. System persists the result (score, AI reason, image publicUrl) to the "
     "exercise_submissions table and displays it to the student. "
     "[<<include>> Save Submission Result]"),
    ("Alternative Flow",
     "View Hint: Student may request a hint before submitting. [<<extend>> View Hint]\n"
     "View Sample Solution: Student may view the model answer after submission. "
     "[<<extend>> View Sample Solution]"),
    ("Exception Flow — Blurred / No Math",
     "Gemini evaluates CRITERIA 2 and returns isValid=false. System displays: "
     "'Your image is unclear or does not contain mathematical content. "
     "Please retake the photo.' No submission record is created."),
    ("Exception Flow — AI Timeout / 429",
     "GeminiValidationService catches TooManyRequestsException or network timeout. "
     "System returns HTTP 503 and displays: 'AI is currently unavailable. "
     "Please try again in a moment.' Submission is not saved."),
    ("Post-condition",
     "A submission record is written to exercise_submissions. Student sees the AI "
     "score and reasoning. The sample solution button becomes available."),
]

UC07_HEADERS = ["Field", "Detail"]
UC07_DATA = [
    ("Use Case ID",    "UC-07"),
    ("Use Case Name",  "Socratic AI Tutor Chat"),
    ("Actor(s)",       "Student (primary); AI System — Google Gemini (supporting external system)"),
    ("Pre-condition",  "Student is authenticated and is viewing a Theory lesson screen. "
                       "An AiChatSession entity is available or can be created."),
    ("Main Flow",
     "1. Student opens the chat panel from within a lesson and types a mathematics "
     "question (virtual keyboard supports LaTeX formula input).\n"
     "2. System queries the database to retrieve the full prior conversation history "
     "for this session, ordered by timestamp. [<<include>> Retrieve Chat History]\n"
     "3. Backend prepends the SYSTEM_PROMPT (8 behavioral rules enforcing the Socratic "
     "no-answer prohibition) to the history array. [<<include>> Create or Resume Session]\n"
     "4. AiTutorService serializes the complete context array (SYSTEM_PROMPT + history "
     "+ new message) into the Gemini API JSON format (Context Injection).\n"
     "5. Backend dispatches the payload to the Gemini REST endpoint and receives a "
     "Socratic hint or guiding question in response. The AI never provides a direct answer.\n"
     "6. System persists both the student's message (role=USER) and the AI response "
     "(role=ASSISTANT) to ai_chat_messages.\n"
     "7. Jetpack Compose renders the AI response inline, with LaTeX rendered by "
     "Markwon + ext-latex library."),
    ("Alternative Flow",
     "Delete Chat History: Student may reset the conversation. "
     "[<<extend>> Delete Chat History]"),
    ("Exception Flow — Prohibited Content",
     "Gemini Safety Filter detects a policy-violating question. API returns a "
     "SAFETY-blocked response. System displays: 'This question cannot be answered. "
     "Please rephrase.' No message is saved."),
    ("Exception Flow — API Timeout / Network Error",
     "callGeminiApi() throws an Exception (timeout or HTTP 5xx). "
     "AiTutorController returns HTTP 500. Mobile UI displays: "
     "'The AI Tutor is currently busy. Please try again.' No message is saved."),
    ("Post-condition",
     "Both user message and AI response are persisted to ai_chat_messages. "
     "The context window grows by two entries, improving the quality of the "
     "next Socratic response."),
]


# ─────────────────────────────────────────────────────────────────────────────
# MAIN
# ─────────────────────────────────────────────────────────────────────────────

def main():
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')

    input_path  = r"e:\Final_Project\docx\MaSci_Final_Report_V18.docx"
    output_path = r"e:\Final_Project\docx\MaSci_Final_Report_V19.docx"

    if not os.path.exists(input_path):
        print("[ERROR] File not found: " + input_path)
        return

    doc = Document(input_path)
    paras = doc.paragraphs

    # ── Step 1: Find Section 3.3 start and end ────────────────────────────────
    sec33_start = -1
    sec34_start = -1

    for i, p in enumerate(paras):
        txt = p.text.strip()
        if sec33_start == -1 and txt.startswith("3.3") and "USE CASE" in txt.upper():
            sec33_start = i
        elif sec33_start != -1 and sec34_start == -1:
            # End is either 3.4 or Chapter 4
            if (txt.startswith("3.4") or
                    ("CHAPTER 4" in txt.upper() and "IMPLEMENTATION" in txt.upper())):
                sec34_start = i
                break

    if sec33_start == -1:
        print("[ERROR] Could not locate '3.3 Use Case' heading.")
        return
    if sec34_start == -1:
        # Try Chapter 4 fallback
        for i, p in enumerate(paras):
            if "CHAPTER 4" in p.text.strip().upper():
                sec34_start = i
                break
    if sec34_start == -1:
        print("[ERROR] Could not locate end boundary (3.4 or Chapter 4).")
        return

    print("[INFO] Section 3.3 spans paragraphs " +
          str(sec33_start) + " to " + str(sec34_start - 1))

    # ── Step 2: Collect paragraphs to DELETE (everything inside 3.3) ──────────
    # We'll delete paras from sec33_start..sec34_start-1 (inclusive)
    paras_to_delete = list(paras[sec33_start:sec34_start])

    # Anchor = the paragraph at sec34_start (we insert BEFORE it)
    anchor = paras[sec34_start]

    # ── Step 3: INSERT the new clean 3.3 structure (before anchor) ────────────

    # ── 3.3.1 ─────────────────────────────────────────────────────────────────
    ih(anchor, "3.3 Use Case Analysis")
    ip(anchor,
       "The Use Case Analysis identifies all system actors and the behavioral "
       "contracts that govern their interactions with the MaSci platform. Rather than "
       "enumerating every routine function (such as Login or View Profile), this chapter "
       "concentrates analysis on the three Strategic Functional Clusters that "
       "architecturally differentiate MaSci from conventional e-learning systems: the "
       "AI-automated exercise grading pipeline, the Socratic AI tutoring engine, and the "
       "administrative monitoring and moderation layer.")

    ih(anchor, "3.3.1 Actors and System Boundary")
    ip(anchor,
       "The MaSci system boundary encompasses three application tiers (Android Mobile, "
       "Web Administration, and Spring Boot Backend) and interacts with two categories "
       "of actors.")
    ip(anchor,
       "Human Actors: (1) Student — the primary consumer of the platform. Students "
       "interact with the mobile application to study content, submit exercises, take "
       "quizzes, and converse with the AI Tutor. (2) System Administrator — accesses "
       "the React-based Web Administration Portal to manage curriculum content, monitor "
       "student submissions, review AI grading outcomes, and administer user accounts.")
    ip(anchor,
       "System Actor: (3) AI System (Google Gemini) — participates as a supporting "
       "external system in two use cases: UC-05 (exercise grading via multimodal vision) "
       "and UC-07 (Socratic tutoring via conversational text generation). All "
       "interactions with this actor are mediated by the Spring Boot backend, which "
       "constructs the API payloads and enforces the behavioral rules embedded in the "
       "SYSTEM_PROMPT.")

    # ── 3.3.2 ─────────────────────────────────────────────────────────────────
    ih(anchor, "3.3.2 Use Case Diagrams")
    ip(anchor,
       "To reflect the depth of the core business logic rather than listing basic "
       "functions such as Registration or Login, the system's Use Case architecture is "
       "represented through three Strategic Functional Clusters. Each cluster is "
       "modelled using UML notation with explicit <<include>> (mandatory sub-use-case) "
       "and <<extend>> (conditional extension) relationships.")

    # Cluster 1
    ip(anchor,
       "1. Exercises and Quizzes (Mobile) — Figure 3.1",
       bold=True, sa=Pt(6))
    ip(anchor,
       "Figure 3.1 illustrates the student assessment workflow. The central architectural "
       "feature is the mandatory <<include>> chain linking Submit Handwritten Answer to "
       "the four-step AI Grading Pipeline: Request Signed Upload URL → Upload Image to "
       "Firebase → Grade via AI → Save Submission Result. This chain design ensures "
       "that every open-ended submission is unconditionally routed through the Gemini "
       "Vision engine before any result is persisted. The Signed URL delegation pattern "
       "means that image binary data flows directly from the mobile client to Firebase "
       "Storage — the Spring Boot server acts exclusively as a permissions broker and "
       "never holds image bytes in JVM heap memory. View Hint and View Sample Solution "
       "are designed as <<extend>> use cases, preserving the student's autonomy when "
       "encountering difficulty without interrupting the primary submission flow.")
    diagram_placeholder(anchor, "Figure 3.1: Exercises and Quizzes Use Case Diagram",
                        MERMAID_EXERCISE)
    icaption(anchor, "Figure 3.1: Exercises and Quizzes Use Case Diagram (Mobile Application)")

    # Cluster 2
    ip(anchor,
       "2. Theory Study and AI Tutor (Mobile) — Figure 3.2",
       bold=True, sa=Pt(6))
    ip(anchor,
       "Figure 3.2 models the knowledge acquisition and Socratic tutoring workflow. "
       "Content navigation follows a mandatory three-level <<include>> chain: View "
       "Lesson List <<includes>> View Chapter List; View Lesson Content <<includes>> "
       "View Lesson List — enforcing the correct backend API call sequence. Watch "
       "YouTube Video is an <<extend>> activated only when the lesson entity carries a "
       "non-null video_url field. In the AI Tutor sub-cluster, Chat with AI Tutor "
       "<<includes>> both Create or Resume Chat Session and Retrieve Chat History, "
       "reflecting the Context Injection Architecture: the backend reconstructs the "
       "full conversation history from MySQL on every request, prepends the Socratic "
       "SYSTEM_PROMPT, and serializes the resulting context array before invoking the "
       "Gemini API. This design enables stateful Socratic dialogue over a stateless "
       "REST infrastructure, ensuring the AI maintains conversational memory across "
       "multiple study sessions.")
    diagram_placeholder(anchor, "Figure 3.2: Theory Study and AI Tutor Use Case Diagram",
                        MERMAID_THEORY)
    icaption(anchor, "Figure 3.2: Theory Study and AI Tutor Use Case Diagram (Mobile Application)")

    # Cluster 3
    ip(anchor,
       "3. Web Administration — Users, Submissions and Dashboard — Figure 3.3",
       bold=True, sa=Pt(6))
    ip(anchor,
       "Figure 3.3 captures the System Admin's monitoring and moderation capabilities "
       "within the React 19 Web Administration Portal. View All Submissions "
       "mandatorily <<includes>> both View Handwritten Image and View AI Grading "
       "Result, because a submission record without its image URL and AI evaluation "
       "verdict has no administrative value. View Student Learning Profile is an "
       "<<extend>> of View Overview Statistics, representing a granular drill-down "
       "to the individual student's engagement timeline, quiz history, and AI chat "
       "session count. In the User Management sub-cluster, Search Users and Delete "
       "User Account are both <<extend>> relationships of View User List: deletion "
       "triggers ON DELETE CASCADE across all foreign-keyed student data entities "
       "(lesson_completion, quiz_results, exercise_submissions, ai_chat_sessions), "
       "ensuring the database remains free of orphan records.")
    diagram_placeholder(anchor,
                        "Figure 3.3: Web Admin Users, Submissions and Dashboard Use Case Diagram",
                        MERMAID_ADMIN)
    icaption(anchor,
             "Figure 3.3: Web Administration — Users, Submissions and Dashboard Use Case Diagram")

    # ── 3.3.3 ─────────────────────────────────────────────────────────────────
    ih(anchor, "3.3.3 Use Case Descriptions")
    ip(anchor,
       "Detailed Use Case specifications are provided for the two architecturally "
       "significant use cases: UC-05 (AI Grading, corresponding to Figure 3.1) and "
       "UC-07 (Socratic AI Tutor, corresponding to Figure 3.2). Each specification "
       "follows a structured template defining actors, pre-conditions, main flow, "
       "alternative flows, exception flows, and post-conditions.")

    ip(anchor, "UC-05: Submit Handwritten Answer and AI Grading",
       bold=True, sa=Pt(6))
    insert_table(anchor, doc,
                 "Table 3.3: Use Case Specification — UC-05 (AI Grading)",
                 UC05_HEADERS, UC05_DATA)

    ip(anchor, "UC-07: Socratic AI Tutor Chat",
       bold=True, sa=Pt(6))
    insert_table(anchor, doc,
                 "Table 3.4: Use Case Specification — UC-07 (Socratic AI Tutor Chat)",
                 UC07_HEADERS, UC07_DATA)

    # ── Step 4: DELETE the old Section 3.3 paragraphs ────────────────────────
    for p in paras_to_delete:
        try:
            del_paragraph(p)
        except Exception:
            pass  # already detached

    # ── Step 5: SAVE ──────────────────────────────────────────────────────────
    doc.save(output_path)
    print("")
    print("SUCCESS! Refactored Section 3.3 saved to V19.")
    print("   Input : " + input_path)
    print("   Output: " + output_path)
    print("")
    print("Structure inserted:")
    print("   3.3   Use Case Analysis (intro)")
    print("   3.3.1 Actors and System Boundary")
    print("   3.3.2 Use Case Diagrams")
    print("         Cluster 1: Exercises & Quizzes  -> Figure 3.1")
    print("         Cluster 2: Theory & AI Tutor    -> Figure 3.2")
    print("         Cluster 3: Web Admin Dashboard  -> Figure 3.3")
    print("   3.3.3 Use Case Descriptions")
    print("         Table 3.3: UC-05 spec (Main + Exception Flow)")
    print("         Table 3.4: UC-07 spec (Main + Exception Flow)")
    print("")
    print("Next steps:")
    print("   1. Open V19.docx in MS Word")
    print("   2. Replace each [PASTE DIAGRAM IMAGE HERE] with PNG from mermaid.live")
    print("   3. References -> Update Table of Contents")


if __name__ == "__main__":
    main()
