"""
final_section33.py
─────────────────────────────────────────────────────────────────────────────
Reads MaSci_Final_Report_V19.docx.
1. Locates Section 3.3 (start) and Section 3.4 (end boundary).
2. DELETES every paragraph in between.
3. INSERTS the definitive clean structure:
     3.3   Use Case Analysis  (intro)
     3.3.1 Actors and System Boundary
     3.3.2 Use Case Diagrams  (3 clusters + Figure 3.1 / 3.2 / 3.3)
     3.3.3 Use Case Descriptions  (Table 3.1 UC-05 | Table 3.2 UC-07)
4. Saves as MaSci_Final_Report_V20.docx.
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
# STYLE HELPERS
# ─────────────────────────────────────────────────────────────────────────────

def del_para(p):
    el = p._element
    el.getparent().remove(el)


def ip(anchor, text, bold=False, italic=False,
       align=WD_PARAGRAPH_ALIGNMENT.JUSTIFY, sa=Pt(12)):
    """Insert a body paragraph before anchor."""
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


def ih(anchor, text, sb=Pt(18), sa=Pt(8)):
    """Insert a bold heading before anchor."""
    p = anchor.insert_paragraph_before()
    p.alignment = WD_PARAGRAPH_ALIGNMENT.LEFT
    pf = p.paragraph_format
    pf.space_before = sb
    pf.space_after = sa
    pf.line_spacing_rule = WD_LINE_SPACING.MULTIPLE
    pf.line_spacing = 1.5
    r = p.add_run(text)
    r.font.name = 'Times New Roman'
    r.font.size = Pt(12)
    r.bold = True
    return p


def ifig_placeholder(anchor, figure_label):
    """Insert a single-line figure placeholder (image goes here)."""
    p = anchor.insert_paragraph_before()
    p.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
    pf = p.paragraph_format
    pf.space_before = Pt(12)
    pf.space_after = Pt(4)
    r = p.add_run("[INSERT " + figure_label + " HERE]")
    r.font.name = 'Times New Roman'
    r.font.size = Pt(11)
    r.bold = True
    r.italic = True
    return p


def icaption(anchor, text):
    """Figure caption line."""
    p = anchor.insert_paragraph_before()
    p.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
    pf = p.paragraph_format
    pf.space_before = Pt(2)
    pf.space_after = Pt(18)
    r = p.add_run(text)
    r.font.name = 'Times New Roman'
    r.font.size = Pt(11)
    r.italic = True
    return p


def insert_uc_table(anchor, doc, table_num, table_title, rows_data):
    """
    Insert a 2-column Use Case Specification table
    with caption 'Table X.X: ...' before anchor.
    Rows with long text (Main Flow / Exception Flow) get pre-formatted
    line breaks rendered as separate runs.
    """
    tbl = doc.add_table(rows=0, cols=2)
    tbl.style = 'Table Grid'

    # Set column widths via first row trick
    for field, detail in rows_data:
        row = tbl.add_row()
        # Field cell
        c0 = row.cells[0]
        r0 = c0.paragraphs[0].add_run(field)
        r0.font.name = 'Times New Roman'
        r0.font.size = Pt(10)
        r0.bold = True
        c0.paragraphs[0].paragraph_format.space_after = Pt(3)

        # Detail cell — handle \n as paragraph breaks inside the cell
        c1 = row.cells[1]
        lines = detail.replace('\r\n', '\n').split('\n')
        first_para = c1.paragraphs[0]
        first_para.paragraph_format.space_after = Pt(2)
        r1 = first_para.add_run(lines[0])
        r1.font.name = 'Times New Roman'
        r1.font.size = Pt(10)

        for line in lines[1:]:
            new_p = c1.add_paragraph()
            new_p.paragraph_format.space_after = Pt(2)
            new_p.paragraph_format.space_before = Pt(0)
            nr = new_p.add_run(line)
            nr.font.name = 'Times New Roman'
            nr.font.size = Pt(10)

    anchor._element.addprevious(tbl._element)

    # Table caption
    cap = anchor.insert_paragraph_before()
    cap.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
    cr = cap.add_run("Table " + table_num + ": " + table_title)
    cr.font.name = 'Times New Roman'
    cr.font.size = Pt(11)
    cr.italic = True
    cap.paragraph_format.space_after = Pt(18)


# ─────────────────────────────────────────────────────────────────────────────
# TABLE DATA  (2 tables only)
# ─────────────────────────────────────────────────────────────────────────────

UC05_ROWS = [
    ("Use Case ID",         "UC-05"),
    ("Use Case Name",       "Submit Handwritten Answer and AI Automated Grading"),
    ("Actor(s)",            "Student (primary); AI System — Google Gemini (supporting external system)"),
    ("Pre-condition",       "Student is authenticated and is viewing an open-ended (handwritten) exercise.\n"
                            "Device camera or photo gallery is accessible."),
    ("Main Flow",
     "Step 1 — Student activates the submit button and captures a photo of their "
     "handwritten solution using the device camera or selects an image from the gallery.\n"
     "Step 2 — [<<include>> Request Signed Upload URL]\n"
     "   System calls GET /exercises/{id}/upload-url. The backend invokes "
     "FirebaseStorageService.generateSignedUploadUrl(), returning a time-limited "
     "HTTP PUT endpoint scoped to submissions/{exerciseId}/{uuid}.jpg. "
     "The server does not receive image bytes.\n"
     "Step 3 — [<<include>> Upload Image to Firebase Storage]\n"
     "   Android client performs a direct binary HTTP PUT to the Signed URL via Retrofit. "
     "The image is stored in Firebase Storage. The Spring Boot server's heap memory is "
     "not touched.\n"
     "Step 4 — [<<include>> Grade Submission via AI]\n"
     "   Backend constructs a multimodal payload: a four-criteria natural-language prompt "
     "(content relevance, legibility, coverage, anti-cheating) combined with the "
     "Base64-encoded image. The payload is dispatched to the Gemini REST API endpoint.\n"
     "Step 5 — Gemini AI analyses the image and returns a structured JSON response "
     "containing isValid (Boolean) and aiReason (String).\n"
     "Step 6 — [<<include>> Save Submission Result]\n"
     "   Backend persists isValid, aiReason, publicUrl, and submittedAt to the "
     "exercise_submissions table. Result and AI reasoning are displayed to the student."),
    ("Alternative Flow",
     "[<<extend>> View Hint] — Student may request a Hint before submitting.\n"
     "[<<extend>> View Sample Solution] — Student may view the model answer after submission."),
    ("Exception Flow 1 — Blurred Image / No Math Content",
     "Gemini evaluates CRITERIA 2 (legibility) or CRITERIA 1 (mathematical content) "
     "and returns isValid = false.\n"
     "System displays: 'Your photo is unclear or does not show mathematical work. "
     "Please retake the photo with better lighting.'\n"
     "No submission record is created. Student returns to Step 1."),
    ("Exception Flow 2 — AI Timeout / Quota Exceeded",
     "GeminiValidationService catches TooManyRequestsException (HTTP 429) or a "
     "network timeout.\n"
     "Backend returns HTTP 503. Mobile UI displays: "
     "'The AI system is currently busy. Please try again in a moment.'\n"
     "The exercise_submissions record is not written."),
    ("Post-condition",
     "A submission record is persisted in exercise_submissions.\n"
     "Student sees their AI score and reasoning text.\n"
     "The View Sample Solution button becomes unlocked."),
]

UC07_ROWS = [
    ("Use Case ID",         "UC-07"),
    ("Use Case Name",       "Socratic AI Tutor Chat"),
    ("Actor(s)",            "Student (primary); AI System — Google Gemini (supporting external system)"),
    ("Pre-condition",       "Student is authenticated and is currently viewing a Theory lesson screen.\n"
                            "An AiChatSession for this user can be created or retrieved."),
    ("Main Flow",
     "Step 1 — Student opens the AI Tutor panel from within the lesson screen "
     "and types a mathematics question. The virtual keyboard supports inline LaTeX "
     "formula input via MathLive component.\n"
     "Step 2 — [<<include>> Create or Resume Chat Session]\n"
     "   AiTutorService checks sessionRepository for an existing AiChatSession "
     "for this user. If none exists, a new session entity is created and saved "
     "(POST ai_chat_sessions). If one exists, the session ID is retrieved.\n"
     "Step 3 — [<<include>> Retrieve Chat History]\n"
     "   System queries AiChatMessageRepository.findBySessionOrderByCreatedAtAsc() "
     "to load all prior conversation turns ordered by timestamp.\n"
     "Step 4 — Context Injection: AiTutorService.buildContents() prepends the "
     "SYSTEM_PROMPT (8 Socratic behavioral rules, including the prohibition on "
     "providing direct answers) to the history array as the first user/model turn pair. "
     "The student's new message is appended as the final entry.\n"
     "Step 5 — The complete serialized context array is dispatched to the Gemini "
     "REST API (callGeminiApi()). The model returns a Socratic guiding question or "
     "partial hint — never a direct answer.\n"
     "Step 6 — Both the student's message (role=USER) and the AI response "
     "(role=ASSISTANT) are persisted to ai_chat_messages.\n"
     "Step 7 — Jetpack Compose renders the AI reply inline. LaTeX formulae in the "
     "response are rendered by the Markwon + ext-latex library."),
    ("Alternative Flow",
     "[<<extend>> Delete Chat History] — Student resets the session; "
     "all ai_chat_messages for the session are deleted. The next message "
     "starts a fresh context window."),
    ("Exception Flow 1 — Prohibited Content",
     "Gemini Safety Filter returns a SAFETY-blocked response.\n"
     "System displays: 'This question cannot be answered. Please rephrase your request.'\n"
     "Neither the student message nor the AI response is persisted."),
    ("Exception Flow 2 — API Timeout / Network Error",
     "callGeminiApi() throws any Exception (network timeout, HTTP 5xx).\n"
     "AiTutorController returns HTTP 500. Mobile UI displays: "
     "'The AI Tutor is currently busy. Please try again.'\n"
     "No messages are saved to ai_chat_messages."),
    ("Post-condition",
     "Student message and AI response are persisted to ai_chat_messages.\n"
     "The context window grows by two entries, enhancing the quality of the "
     "next Socratic response in the same session."),
]


# ─────────────────────────────────────────────────────────────────────────────
# MAIN
# ─────────────────────────────────────────────────────────────────────────────

def main():
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')

    input_path  = r"e:\Final_Project\docx\MaSci_Final_Report_V19.docx"
    output_path = r"e:\Final_Project\docx\MaSci_Final_Report_V20.docx"

    if not os.path.exists(input_path):
        print("[ERROR] File not found: " + input_path)
        return

    doc = Document(input_path)
    paras = doc.paragraphs

    # ── Locate Section 3.3 start ──────────────────────────────────────────────
    sec33_start = -1
    sec34_start = -1

    for i, p in enumerate(paras):
        txt = p.text.strip()
        # Heading must start with "3.3" and relate to Use Case
        if sec33_start == -1 and txt.startswith("3.3") and \
                ("USE CASE" in txt.upper() or "UC" in txt.upper()):
            sec33_start = i
            print("[INFO] 3.3 heading found at para " + str(i) +
                  ": '" + txt[:60] + "'")
            continue

        if sec33_start != -1 and sec34_start == -1:
            # Next numbered section after 3.3
            if txt.startswith("3.4") or txt.startswith("3.5") or \
               ("CHAPTER 4" in txt.upper() and "IMPLEMENTATION" in txt.upper()):
                sec34_start = i
                print("[INFO] End boundary found at para " + str(i) +
                      ": '" + txt[:60] + "'")
                break

    # Fallback: find Chapter 4
    if sec33_start != -1 and sec34_start == -1:
        for i, p in enumerate(paras):
            if i > sec33_start and "CHAPTER 4" in p.text.strip().upper():
                sec34_start = i
                break

    if sec33_start == -1:
        print("[ERROR] Cannot find '3.3 Use Case' heading. Scanning all headings:")
        for i, p in enumerate(paras[:200]):
            if p.text.strip().startswith("3."):
                print("   " + str(i) + ": " + p.text.strip()[:80])
        return

    if sec34_start == -1:
        print("[ERROR] Cannot find end boundary. Aborting.")
        return

    print("[INFO] Section 3.3 occupies paragraphs " +
          str(sec33_start) + " to " + str(sec34_start - 1) +
          " (" + str(sec34_start - sec33_start) + " paragraphs).")

    # ── Collect paragraphs to delete ─────────────────────────────────────────
    to_delete = list(paras[sec33_start:sec34_start])
    anchor = paras[sec34_start]       # insert new content BEFORE this paragraph

    # ── INSERT NEW CLEAN SECTION 3.3 ─────────────────────────────────────────

    # ===== 3.3 intro =====
    ih(anchor, "3.3 Use Case Analysis", sb=Pt(24))
    ip(anchor,
       "The Use Case Analysis identifies the actors who interact with the MaSci "
       "E-Learning Platform and formally specifies the behavioral contracts that "
       "govern each interaction. Rather than cataloguing every standard function "
       "(such as login or profile management), this analysis concentrates on the "
       "three Strategic Functional Clusters that architecturally differentiate MaSci "
       "from conventional e-learning systems: the AI-automated exercise grading "
       "pipeline, the Socratic AI tutoring engine, and the administrative monitoring "
       "and moderation layer.")

    # ===== 3.3.1 =====
    ih(anchor, "3.3.1 Actors and System Boundary")
    ip(anchor,
       "The MaSci system boundary encompasses three application tiers — Android "
       "Mobile Application, React Web Administration Portal, and Spring Boot Backend "
       "— and involves three categories of actors.")
    ip(anchor,
       "Human Actors: (1) Student — the primary consumer of the platform. Students "
       "interact with the Android mobile application to study theory content, photograph "
       "and submit handwritten exercises for AI grading, take MCQ quizzes, and conduct "
       "Socratic tutoring dialogues. (2) System Administrator — accesses the React 19 "
       "Web Administration Portal to author curriculum content, monitor student "
       "submissions, review AI grading decisions, and manage user accounts.")
    ip(anchor,
       "System Actor: (3) AI System (Google Gemini) — participates as a supporting "
       "external system in two use cases: UC-05, in which it evaluates a multimodal "
       "payload (prompt + handwritten image) and returns a structured grading result; "
       "and UC-07, in which it receives a fully serialized conversational context and "
       "returns a Socratic guiding question. All interactions with this actor are "
       "mediated by the Spring Boot backend, which constructs the API payloads, "
       "enforces the behavioral SYSTEM_PROMPT, and handles all exception cases.")

    # ===== 3.3.2 =====
    ih(anchor, "3.3.2 Use Case Diagrams")
    ip(anchor,
       "To reflect the depth of the core business logic rather than listing basic "
       "functions, the system's Use Case architecture is represented through three "
       "Strategic Functional Clusters. Each cluster is modelled using standard UML "
       "notation with explicit <<include>> (mandatory sub-use-case) and <<extend>> "
       "(conditional extension) relationships.")

    # Cluster 1
    ip(anchor, "Cluster 1: Exercises and Quizzes (Mobile Application)",
       bold=True, sa=Pt(6))
    ip(anchor,
       "Figure 3.1 illustrates the student assessment workflow. The central "
       "architectural feature is the mandatory <<include>> chain of four sequential "
       "sub-use-cases linking 'Submit Handwritten Answer' to the AI Grading Pipeline: "
       "Request Signed Upload URL → Upload Image to Firebase Storage → Grade via AI "
       "→ Save Submission Result. This chain design ensures that every open-ended, "
       "handwritten submission is unconditionally routed through the Gemini Vision "
       "engine before any result is persisted to the database. The Signed URL "
       "delegation pattern means image binary data flows directly from the mobile "
       "client to Firebase Storage — the Spring Boot server acts exclusively as a "
       "permissions broker and never allocates JVM heap memory for image bytes. "
       "'View Hint' and 'View Sample Solution' are designed as <<extend>> use cases, "
       "providing optional scaffolding without interrupting the mandatory submission flow.")
    ifig_placeholder(anchor, "FIGURE 3.1: Use Case Diagram -- Exercises and Quizzes")
    icaption(anchor, "Figure 3.1: Use Case Diagram — Exercises and Quizzes (Mobile Application)")

    # Cluster 2
    ip(anchor, "Cluster 2: Theory Study and AI Tutor (Mobile Application)",
       bold=True, sa=Pt(6))
    ip(anchor,
       "Figure 3.2 models the knowledge acquisition and Socratic tutoring workflow. "
       "Content navigation enforces a mandatory three-level <<include>> chain: "
       "'View Lesson List' <<includes>> 'View Chapter List', and 'View Lesson Content' "
       "<<includes>> 'View Lesson List', imposing the correct backend API call sequence. "
       "'Watch YouTube Video' is an <<extend>> activated only when the lesson entity "
       "carries a non-null video_url field. In the AI Tutor sub-cluster, 'Chat with "
       "AI Tutor' <<includes>> both 'Create or Resume Chat Session' and 'Retrieve Chat "
       "History', reflecting the Context Injection Architecture: the backend "
       "reconstructs the full conversation history from MySQL before every Gemini API "
       "call and prepends the Socratic SYSTEM_PROMPT, enabling stateful dialogue over "
       "a stateless REST infrastructure.")
    ifig_placeholder(anchor, "FIGURE 3.2: Use Case Diagram -- Theory Study and AI Tutor")
    icaption(anchor, "Figure 3.2: Use Case Diagram — Theory Study and AI Tutor (Mobile Application)")

    # Cluster 3
    ip(anchor, "Cluster 3: Web Administration — Users, Submissions and Dashboard",
       bold=True, sa=Pt(6))
    ip(anchor,
       "Figure 3.3 captures the System Administrator's monitoring and moderation "
       "capabilities within the React 19 Web Administration Portal. 'View All "
       "Submissions' mandatorily <<includes>> both 'View Handwritten Image' and "
       "'View AI Grading Result', because a submission record without its associated "
       "image and AI evaluation has no administrative value. 'View Student Learning "
       "Profile' is an <<extend>> of 'View Overview Statistics', providing an optional "
       "drill-down to the individual student's engagement timeline. 'Search Users' "
       "and 'Delete User Account' are both <<extend>> relationships of 'View User List'; "
       "the deletion action triggers ON DELETE CASCADE across all foreign-keyed student "
       "data entities, ensuring referential integrity is preserved.")
    ifig_placeholder(anchor,
                     "FIGURE 3.3: Use Case Diagram -- Web Admin Users, Submissions and Dashboard")
    icaption(anchor,
             "Figure 3.3: Use Case Diagram — Web Administration: Users, Submissions and Dashboard")

    # ===== 3.3.3 =====
    ih(anchor, "3.3.3 Use Case Descriptions")
    ip(anchor,
       "Detailed specifications are provided for the two architecturally most "
       "significant use cases: UC-05 (AI Grading, corresponding to Cluster 1 / "
       "Figure 3.1) and UC-07 (Socratic AI Tutor, corresponding to Cluster 2 / "
       "Figure 3.2). Each specification follows a structured template defining actors, "
       "pre-conditions, numbered main flow steps annotated with <<include>> and "
       "<<extend>> relationships, alternative flows, exception flows, and post-conditions.")

    ip(anchor, "Table 3.1 — UC-05: Submit Handwritten Answer and AI Automated Grading",
       bold=True, sa=Pt(6))
    insert_uc_table(anchor, doc,
                    "3.1",
                    "Use Case Specification: UC-05 — Submit Handwritten Answer and AI Grading",
                    UC05_ROWS)

    ip(anchor, "Table 3.2 — UC-07: Socratic AI Tutor Chat",
       bold=True, sa=Pt(6))
    insert_uc_table(anchor, doc,
                    "3.2",
                    "Use Case Specification: UC-07 — Socratic AI Tutor Chat",
                    UC07_ROWS)

    # ── DELETE the stale Section 3.3 paragraphs ───────────────────────────────
    deleted = 0
    for p in to_delete:
        try:
            del_para(p)
            deleted += 1
        except Exception:
            pass

    print("[INFO] Deleted " + str(deleted) + " old paragraphs from Section 3.3.")

    # ── SAVE ──────────────────────────────────────────────────────────────────
    doc.save(output_path)
    print("")
    print("SUCCESS! MaSci_Final_Report_V20.docx saved.")
    print("   Input : " + input_path)
    print("   Output: " + output_path)
    print("")
    print("Final Section 3.3 structure:")
    print("   3.3   Use Case Analysis (intro)")
    print("   3.3.1 Actors and System Boundary")
    print("   3.3.2 Use Case Diagrams")
    print("         Cluster 1 -> Figure 3.1: Exercises and Quizzes")
    print("         Cluster 2 -> Figure 3.2: Theory Study and AI Tutor")
    print("         Cluster 3 -> Figure 3.3: Web Admin Dashboard")
    print("   3.3.3 Use Case Descriptions")
    print("         Table 3.1: UC-05 (AI Grading)    -- 8 fields, detailed flow")
    print("         Table 3.2: UC-07 (AI Tutor Chat) -- 8 fields, detailed flow")
    print("")
    print("MS Word next steps:")
    print("   1. Replace [INSERT FIGURE 3.X HERE] with PNG exported from mermaid.live")
    print("   2. References -> Update Table of Contents, List of Figures, List of Tables")


if __name__ == "__main__":
    main()
