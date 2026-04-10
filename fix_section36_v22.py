"""
fix_section36_v22.py
─────────────────────────────────────────────────────────────────────────────
1. Replaces the content of Section 3.6.3 (currently "AI Tutor Interaction")
   with "Quiz Generation and Execution Flowchart" analysis text.
2. Inserts actual PNG images at Figure 3.6, 3.7, and 3.8 positions,
   replacing the text-only placeholder lines.
3. Fixes duplicate "Diagram 3.6" reference to "Figure 3.6".
Saves as MaSci_Final_Report_V22.docx.
─────────────────────────────────────────────────────────────────────────────
"""

import os, sys

try:
    from docx import Document
    from docx.shared import Pt, Inches, Cm
    from docx.enum.text import WD_PARAGRAPH_ALIGNMENT, WD_LINE_SPACING
    from docx.oxml.ns import qn
    from docx.oxml import OxmlElement
except ImportError:
    import subprocess
    subprocess.check_call([sys.executable, "-m", "pip", "install", "python-docx"])
    from docx import Document
    from docx.shared import Pt, Inches, Cm
    from docx.enum.text import WD_PARAGRAPH_ALIGNMENT, WD_LINE_SPACING
    from docx.oxml.ns import qn
    from docx.oxml import OxmlElement


def del_para(p):
    el = p._element
    el.getparent().remove(el)


def fix_runs(para, new_text, font='Times New Roman', size=Pt(12),
             bold=False, italic=False):
    """Replace all run text with new_text, preserving first run format."""
    for r in para.runs:
        r.text = ''
    if para.runs:
        r = para.runs[0]
        r.text = new_text
        r.font.name = font
        r.font.size = size
        r.bold = bold
        r.italic = italic
    else:
        r = para.add_run(new_text)
        r.font.name = font
        r.font.size = size
        r.bold = bold
        r.italic = italic


def ih(anchor, text, sb=Pt(18)):
    p = anchor.insert_paragraph_before()
    p.alignment = WD_PARAGRAPH_ALIGNMENT.LEFT
    pf = p.paragraph_format
    pf.space_before = sb
    pf.space_after = Pt(8)
    pf.line_spacing_rule = WD_LINE_SPACING.MULTIPLE
    pf.line_spacing = 1.5
    r = p.add_run(text)
    r.font.name = 'Times New Roman'
    r.font.size = Pt(12)
    r.bold = True
    return p


def ip(anchor, text, bold=False, sa=Pt(12),
       align=WD_PARAGRAPH_ALIGNMENT.JUSTIFY):
    p = anchor.insert_paragraph_before()
    p.alignment = align
    pf = p.paragraph_format
    pf.line_spacing_rule = WD_LINE_SPACING.MULTIPLE
    pf.line_spacing = 1.5
    pf.space_after = sa
    pf.space_before = Pt(0)
    r = p.add_run(text)
    r.font.name = 'Times New Roman'
    r.font.size = Pt(12)
    r.bold = bold
    return p


def insert_image(anchor, img_path, width_cm=14.0):
    """Insert an image paragraph centered before anchor."""
    p = anchor.insert_paragraph_before()
    p.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
    p.paragraph_format.space_before = Pt(8)
    p.paragraph_format.space_after = Pt(4)
    run = p.add_run()
    run.add_picture(img_path, width=Cm(width_cm))
    return p


def insert_fig_caption(anchor, caption_text):
    """Insert a centered italic figure caption before anchor."""
    p = anchor.insert_paragraph_before()
    p.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
    pf = p.paragraph_format
    pf.space_before = Pt(2)
    pf.space_after = Pt(18)
    r = p.add_run(caption_text)
    r.font.name = 'Times New Roman'
    r.font.size = Pt(11)
    r.italic = True
    return p


# ─────────────────────────────────────────────────────────────────────────────
# QUIZ ANALYSIS TEXT
# ─────────────────────────────────────────────────────────────────────────────

QUIZ_ANALYSIS = [
    ("[1. Business Logic and Configuration]",
     "Figure 3.8 models the end-to-end business logic of the Quiz Generation and "
     "Execution module, spanning both the Web Administration Portal (teacher side) "
     "and the Android Mobile Application (student side). The administrative flow "
     "initiates when the teacher creates a new quiz record in the system. The backend "
     "persists the quiz configuration parameters — lesson_id (subject scope), "
     "time_limit_minutes (countdown duration), and total_questions (validation "
     "threshold) — to the quizzes table via QuizAdminController "
     "(POST /api/admin/quizzes). This configuration record acts as the structural "
     "container for all subsequent question data."),
    ("[2. Data Population and Decision Logic]",
     "Following quiz initialization, the system transitions to the Data Population "
     "phase. The critical decision node in this flowchart is the validation gate: "
     "'Sufficient questions added?'. This node implements a Validation Loop — a "
     "repeating conditional check that prevents quiz publication until the number of "
     "inserted questions reaches the configured total_questions threshold. While the "
     "condition evaluates to False, the teacher continues adding question records to "
     "the questions table and their associated answer options to the choices table, "
     "each choice carrying an is_correct flag. Once the threshold is satisfied "
     "(True), the teacher may set the quiz status to Published, making the quiz "
     "visible to students in the mobile application via GET /api/quizzes/filter."),
    ("[3. Client Execution and Graceful Timeout Handling]",
     "On the mobile client side, when the student initiates a quiz session, the "
     "Android application loads the complete question payload in a single API call "
     "(GET /api/quizzes/{id}/questions). A CountDownTimer instance is initialized "
     "with the configured time_limit_minutes value. The flowchart captures a critical "
     "exception-handling branch: the continuous check 'Time expired?' that runs in "
     "parallel with the student's interaction. If the countdown reaches zero while "
     "the student has not yet submitted — i.e., the condition evaluates to True — "
     "the application triggers a Graceful Force-Submit sequence: the answer selection "
     "state is locked, the UI is frozen, and the collected answers are automatically "
     "dispatched to the backend via POST /quizzes/{id}/submit. This mechanism "
     "preserves assessment integrity and prevents data loss for partially completed "
     "quizzes."),
    ("[4. Evaluation, Persistence and Gamification Feedback]",
     "Upon receiving the submission payload, the backend's QuizUserController "
     "iterates over each submitted choice and cross-references its choice_id against "
     "the is_correct flag in the choices table. The final integer score is calculated "
     "as the count of correct answers. Two persistence operations are then executed "
     "atomically: the score, quiz_id, and user_id are written to the quiz_results "
     "table (POST /quizzes/{id}/submit), and the API response is dispatched to the "
     "mobile client. The Jetpack Compose UI renders the final score with immediate "
     "Visual Feedback — displaying the result alongside correct/incorrect indicators "
     "for each question. This gamification feedback loop directly contributes to the "
     "student motivation engine by surfacing score progress in the HomeScreen "
     "dashboard and fueling the learning streak calculation."),
]


def main():
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')

    input_path   = r"e:\Final_Project\docx\MaSci_Final_Report_V21.docx"
    output_path  = r"e:\Final_Project\docx\MaSci_Final_Report_V22.docx"
    img_fc1      = r"e:\Final_Project\docx\flowchart_01.png"
    img_fc2      = r"e:\Final_Project\docx\flowchart_02.png"
    img_fc3      = r"e:\Final_Project\docx\flowchart_03.png"

    for f in [input_path, img_fc1, img_fc2, img_fc3]:
        if not os.path.exists(f):
            print("[ERROR] Missing: " + f)
            return

    doc = Document(input_path)
    paras = doc.paragraphs

    # ── Locate key paragraph indices ─────────────────────────────────────────
    idx = {
        'sec_363'    : -1,   # "3.6.3 AI Tutor Interaction Flowchart"
        'body_363'   : -1,   # "Figure 3.8 presents a granular..."
        'placeholder_368':  -1,  # extra line in 3.6.3 if any
        'cap_38'     : -1,   # "Figure 3.8: AI Tutor Interaction Flowchart"
        'sec_37'     : -1,   # "3.7 Sequence Diagrams"
        'cap_36'     : -1,   # "Figure 3.6: Learning Journey Flowchart"
        'cap_37'     : -1,   # "Figure 3.7: Assignment Submission..."
        'diag_36'    : -1,   # "Diagram 3.6 illustrates..."
    }

    for i, p in enumerate(paras):
        t = p.text.strip()
        if '3.6.3' in t and 'TUTOR' in t.upper():
            idx['sec_363'] = i
        elif 'Figure 3.8 presents a granular' in t:
            idx['body_363'] = i
        elif 'Figure 3.8: AI Tutor Interaction Flowchart' in t:
            idx['cap_38'] = i
        elif t.startswith('3.7') and 'Sequence' in t:
            idx['sec_37'] = i
        elif 'Figure 3.6: Learning Journey Flowchart' in t:
            idx['cap_36'] = i
        elif 'Figure 3.7: Assignment Submission' in t:
            idx['cap_37'] = i
        elif 'Diagram 3.6 illustrates' in t:
            idx['diag_36'] = i

    print("[INFO] Key paragraphs found:")
    for k, v in idx.items():
        if v != -1:
            print("  " + k + " at para " + str(v) + ": " + paras[v].text.strip()[:70])
        else:
            print("  " + k + " : NOT FOUND")

    # ═══════════════════════════════════════════════════════════════════════════
    # STEP 1: FIX "Diagram 3.6" → "Figure 3.6" in body text
    # ═══════════════════════════════════════════════════════════════════════════
    if idx['diag_36'] != -1:
        p = paras[idx['diag_36']]
        new_t = p.text.replace('Diagram 3.6', 'Figure 3.6')
        fix_runs(p, new_t)
        print("[FIX] Renamed 'Diagram 3.6' -> 'Figure 3.6' in para " + str(idx['diag_36']))

    # ═══════════════════════════════════════════════════════════════════════════
    # STEP 2: INSERT ACTUAL IMAGES AT FIGURE 3.6 AND 3.7
    #         Replace placeholder caption lines with image + caption
    # ═══════════════════════════════════════════════════════════════════════════
    # Figure 3.6 caption is at idx['cap_36']. Insert image BEFORE caption, after
    # the caption stays as is.
    # We insert image paragraph before the "Figure 3.6:" line.

    # Re-fetch paras in case any changes
    paras = doc.paragraphs

    if idx['cap_36'] != -1:
        anchor36 = paras[idx['cap_36']]
        insert_image(anchor36, img_fc1, width_cm=14.0)
        print("[IMG] Inserted flowchart_01.png before Figure 3.6 caption")

    paras = doc.paragraphs
    # Refresh cap_37 index after insertion
    cap_37_new = -1
    for i, p in enumerate(paras):
        if 'Figure 3.7: Assignment Submission' in p.text:
            cap_37_new = i
            break

    if cap_37_new != -1:
        anchor37 = paras[cap_37_new]
        insert_image(anchor37, img_fc2, width_cm=14.0)
        print("[IMG] Inserted flowchart_02.png before Figure 3.7 caption")
    else:
        print("[WARN] Figure 3.7 caption not found for image insertion")

    # ═══════════════════════════════════════════════════════════════════════════
    # STEP 3: REPLACE SECTION 3.6.3 CONTENT
    #         Old: "AI Tutor Interaction Flowchart"
    #         New: "Quiz Generation and Execution Flowchart"
    # ═══════════════════════════════════════════════════════════════════════════
    paras = doc.paragraphs

    # Find 3.6.3 heading, body, and caption again
    s363 = b363 = c38 = s37 = -1
    for i, p in enumerate(paras):
        t = p.text.strip()
        if '3.6.3' in t:
            s363 = i
        elif s363 != -1 and b363 == -1 and t.startswith('Figure 3.8'):
            b363 = i
        elif 'Figure 3.8:' in t and 'Tutor' in t:
            c38 = i
        elif t.startswith('3.7') and 'Sequence' in t:
            s37 = i
            break

    print("[INFO] After image insert -- 3.6.3 at " + str(s363) +
          ", body at " + str(b363) + ", cap at " + str(c38) +
          ", 3.7 at " + str(s37))

    if s363 == -1 or s37 == -1:
        print("[ERROR] Cannot find 3.6.3 or 3.7 boundary.")
        return

    # Collect paragraphs belonging to old 3.6.3 (from s363 to s37-1, inclusive)
    old_content = list(paras[s363:s37])

    # Anchor = the 3.7 heading paragraph (insert new content before it)
    anchor_sec37 = paras[s37]

    # Insert NEW 3.6.3 content (in document order = code order here since
    # each call inserts before anchor_sec37)
    ih(anchor_sec37, "3.6.3 Quiz Generation and Execution Flowchart")

    for section_title, body_text in QUIZ_ANALYSIS:
        ip(anchor_sec37, section_title, bold=True, sa=Pt(4))
        ip(anchor_sec37, body_text, sa=Pt(12))

    # Insert actual flowchart_03 image
    insert_image(anchor_sec37, img_fc3, width_cm=14.0)
    insert_fig_caption(anchor_sec37,
                       "Figure 3.8: Quiz Generation and Execution Flowchart")

    print("[OK] Inserted new 3.6.3 content with 4-part analysis + Figure 3.8 image")

    # Now DELETE old 3.6.3 paragraphs
    deleted = 0
    for p in old_content:
        try:
            del_para(p)
            deleted += 1
        except Exception:
            pass
    print("[DEL] Removed " + str(deleted) + " old 3.6.3 paragraphs")

    # ═══════════════════════════════════════════════════════════════════════════
    # STEP 4: UPDATE SECTION HEADER NAME
    # Fix "3.6 System Flowcharts" to "3.6 Process Design — System Flowcharts"
    # And update sub-heading 3.6.3 reference in body text if any
    # ═══════════════════════════════════════════════════════════════════════════
    paras = doc.paragraphs
    for p in paras:
        t = p.text.strip()
        if t == '3.6 System Flowcharts':
            fix_runs(p, '3.6 Process Design — System Flowcharts', bold=True)
            print("[FIX] Renamed 3.6 heading")
            break

    # ═══════════════════════════════════════════════════════════════════════════
    # SAVE
    # ═══════════════════════════════════════════════════════════════════════════
    doc.save(output_path)
    print("")
    print("SUCCESS! MaSci_Final_Report_V22.docx saved.")
    print("   " + output_path)
    print("")
    print("Section 3.6 final structure:")
    print("   3.6   Process Design -- System Flowcharts")
    print("   3.6.1 Learning Journey Flowchart   -> [Image: flowchart_01.png] + Figure 3.6 caption")
    print("   3.6.2 AI Grading Flowchart         -> [Image: flowchart_02.png] + Figure 3.7 caption")
    print("   3.6.3 Quiz Generation Flowchart    -> [Image: flowchart_03.png] + Figure 3.8 caption")
    print("")
    print("Next: Open V22.docx -> References -> Update Table of Contents")


if __name__ == "__main__":
    main()
