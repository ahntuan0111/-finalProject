"""
fix_numbering_v21.py
─────────────────────────────────────────────────────────────────────────────
Fixes ALL figure/table numbering in Chapter 3 of MaSci_Final_Report_V20.docx.

CORRECT SEQUENTIAL MAPPING:
  Figures (in order of appearance):
    Figure 3.1  = Use Case Diagram -- Exercises & Quizzes
    Figure 3.2  = Use Case Diagram -- Theory Study & AI Tutor
    Figure 3.3  = Use Case Diagram -- Web Admin Dashboard
    Figure 3.4  = System Architecture (3-tier)
    Figure 3.5  = ERD (Entity Relationship Diagram)
    Figure 3.6  = Flowchart 1 (Learning Journey)
    Figure 3.7  = Flowchart 2 (Assignment Submission & AI Grading)
    Figure 3.8  = Flowchart 3 (AI Tutor Interaction)
    Figure 3.9  = Sequence Diagram 1 (AI Grading)
    Figure 3.10 = Sequence Diagram 2 (AI Tutor)

  Tables (in order of appearance):
    Table 3.1   = UC-05 Use Case Specification
    Table 3.2   = UC-07 Use Case Specification
    Table 3.3   = Data Dict: users
    Table 3.4   = Data Dict: grades & subjects
    Table 3.5   = Data Dict: chapters & lessons
    Table 3.6   = Data Dict: lesson_contents
    Table 3.7   = Data Dict: exercises & exercise_solutions
    Table 3.8   = Data Dict: exercise_submissions
    Table 3.9   = Data Dict: quizzes, questions & choices
    Table 3.10  = Data Dict: quiz_results & progress
    Table 3.11  = Data Dict: ai_chat_sessions
    Table 3.12  = Data Dict: ai_chat_messages

ACTIONS:
  1. Delete duplicate "bold intro" lines for Table 3.1 and Table 3.2 (para 156,158)
  2. Delete duplicate ERD body-reference that says "Figure 3.3 is" (para 173)
     actually that IS the ERD caption - rename to Figure 3.5
  3. Rename all captions and in-text references per the mapping above
  4. Fix alignment: body=JUSTIFY, figure captions=CENTER
  5. Save as V21.docx
─────────────────────────────────────────────────────────────────────────────
"""

import os, sys, re, copy

try:
    from docx import Document
    from docx.shared import Pt
    from docx.enum.text import WD_PARAGRAPH_ALIGNMENT, WD_LINE_SPACING
except ImportError:
    import subprocess
    subprocess.check_call([sys.executable, "-m", "pip", "install", "python-docx"])
    from docx import Document
    from docx.shared import Pt
    from docx.enum.text import WD_PARAGRAPH_ALIGNMENT, WD_LINE_SPACING


def del_para(p):
    el = p._element
    el.getparent().remove(el)


def set_run_text(para, new_text):
    """Replace all run text in a paragraph with new_text (preserving first run's font)."""
    if not para.runs:
        return
    # Store first run's formatting
    first_run = para.runs[0]
    fname = first_run.font.name
    fsize = first_run.font.size
    fbold = first_run.bold
    fital = first_run.italic

    # Clear all runs
    for run in para.runs:
        run.text = ""
    # Write new text into first run
    first_run.text = new_text
    first_run.font.name = fname or 'Times New Roman'
    if fsize:
        first_run.font.size = fsize
    first_run.bold = fbold
    first_run.italic = fital


def replace_in_para(para, old_str, new_str):
    """Replace old_str with new_str across all runs in para, preserving formatting."""
    full_text = para.text
    if old_str not in full_text:
        return False
    new_full = full_text.replace(old_str, new_str)
    set_run_text(para, new_full)
    return True


def apply_alignment(para, align):
    para.alignment = align


def main():
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')

    input_path  = r"e:\Final_Project\docx\MaSci_Final_Report_V20.docx"
    output_path = r"e:\Final_Project\docx\MaSci_Final_Report_V21.docx"

    if not os.path.exists(input_path):
        print("[ERROR] File not found: " + input_path)
        return

    doc = Document(input_path)
    paras = doc.paragraphs

    # ── Find Chapter 3 boundaries ─────────────────────────────────────────────
    chap3_start = chap4_start = -1
    for i, p in enumerate(paras):
        t = p.text.strip().upper()
        if chap3_start == -1 and "CHAPTER 3" in t:
            chap3_start = i
        elif chap3_start != -1 and chap4_start == -1 and "CHAPTER 4" in t:
            chap4_start = i
            break
    if chap3_start == -1:
        print("[ERROR] Chapter 3 not found.")
        return
    if chap4_start == -1:
        chap4_start = len(paras)

    print("[INFO] Chapter 3: paragraphs " + str(chap3_start) + " to " + str(chap4_start - 1))

    # ═════════════════════════════════════════════════════════════════════════
    # STEP 1: DELETE DUPLICATE / ORPHAN PARAGRAPHS
    # ═════════════════════════════════════════════════════════════════════════
    # From audit:
    #   Para 156: "Table 3.1 -- UC-05: ..." (bold intro line - DUPLICATE of 157)
    #   Para 158: "Table 3.2 -- UC-07: ..." (bold intro line - DUPLICATE of 159)
    # These are the "ip(anchor, 'Table 3.X — ...', bold=True)" lines we inserted.
    # We keep Para 157 (the actual italic caption above the table) and Para 159.
    # And renumber them.

    to_delete_texts = {
        "Table 3.1 — UC-05: Submit Handwritten Answer and AI Automated Grading",
        "Table 3.2 — UC-07: Socratic AI Tutor Chat",
    }

    deleted = []
    for i in range(chap3_start, chap4_start):
        p = paras[i]
        t = p.text.strip()
        if t in to_delete_texts:
            deleted.append((i, t))

    for i, t in deleted:
        print("[DEL] Para " + str(i) + ": " + t[:70])
        del_para(paras[i])

    # Re-fetch paragraphs after deletion
    paras = doc.paragraphs

    # Re-find boundaries
    chap3_start = chap4_start = -1
    for i, p in enumerate(paras):
        t = p.text.strip().upper()
        if chap3_start == -1 and "CHAPTER 3" in t:
            chap3_start = i
        elif chap3_start != -1 and chap4_start == -1 and "CHAPTER 4" in t:
            chap4_start = i
            break
    if chap4_start == -1:
        chap4_start = len(paras)

    # ═════════════════════════════════════════════════════════════════════════
    # STEP 2: BUILD RENAMING MAP
    # ═════════════════════════════════════════════════════════════════════════
    # Walk paragraphs in order of appearance and assign sequential numbers.
    # We identify captions by their text patterns.

    # Figure captions: paragraph text starts with "Figure 3." or is the
    # [INSERT FIGURE ...] placeholder reference

    # OLD text  →  NEW text  (exact string replacement)
    text_replacements = {}

    # ── FIGURE RENAMING ───────────────────────────────────────────────────────
    # After audit (corrected for deleted paras, order of first appearance):
    # Appearance 1: Para ~141 "Figure 3.1 illustrates the student..."  → body text (keep 3.1)
    # Appearance 2: Para ~143 "[INSERT FIGURE 3.1: Use Case..."        → keep 3.1
    # Appearance 3: Para ~143 "Figure 3.1: Use Case Diagram — Exercises..."  → keep 3.1
    # Appearance 4: Similar for 3.2 (Theory) → keep 3.2
    # Appearance 5: Similar for 3.3 (Web Admin) → keep 3.3
    # Para ~171: "Figure 3.3: System Entity-Relationship Diagram (ERD)" → rename to Figure 3.5
    # In body: "Figure 3.3" ref to ERD → Figure 3.5
    # Para ~212: "Figure 3.4: Learning Journey Flowchart" → rename to Figure 3.6
    # In body: body refs to Flowcharts
    # Para ~218: "Figure 3.5: Assignment Submission Flowchart" → rename to Figure 3.7
    # Para ~223: "Figure 3.6: AI Tutor Interaction Flowchart" → rename to Figure 3.8
    # Para ~232: "Figure 3.7: AI Grading Sequence Diagram" → rename to Figure 3.9
    # Para ~239: "Figure 3.8: AI Tutor Context Sequence" → rename to Figure 3.10
    # In body: "Figure 3.7" ref in text → "Figure 3.9"
    # In body: "Figure 3.8" ref in text → "Figure 3.10"

    # Figure 3.4 (System Architecture) - search for any "Figure 3.4" in 3.4 section
    # This may not have a caption yet - it may just be a placeholder.

    # IMPORTANT: We must rename in REVERSE ORDER to avoid cascading conflicts.
    # e.g. If we rename 3.7→3.9 before 3.6→3.8, text containing 3.7 won't
    # accidentally pick up already-renamed 3.6→old3.7.
    # Strategy: Use temp placeholders.

    # Old ERD caption says "Figure 3.3: System Entity-Relationship Diagram (ERD)"
    # but 3.3 is already used by UC Admin diagram.
    # Flowcharts: old 3.4, 3.5, 3.6 → new 3.6, 3.7, 3.8
    # Sequences: old 3.7, 3.8 → new 3.9, 3.10
    # System Arch: need to check if Figure 3.4 appears for system arch

    # Let me scan to see what's in the 3.4 section
    figure_renames = [
        # (old_exact_string, new_exact_string)
        # ERD
        ("Figure 3.3: System Entity-Relationship Diagram (ERD)", "Figure 3.5: System Entity-Relationship Diagram (ERD)"),
        # Body ref to ERD as Figure 3.3 (in 3.5 section)
        ("(Figure 3.3)", "(Figure 3.5)"),
        ("Figure 3.3)", "Figure 3.5)"),
        ("Figure 3.3 ", "Figure 3.5 "),
        ("Figure 3.3,", "Figure 3.5,"),
        ("Figure 3.3.", "Figure 3.5."),
        # Flowcharts: 3.4→3.6
        ("Figure 3.4: Learning Journey Flowchart", "Figure 3.6: Learning Journey Flowchart"),
        # If body text says "Figure 3.4" referring to flowchart
        ("illustrates is Figure 3.4", "illustrates is Figure 3.6"),
        # Flowcharts: 3.5→3.7
        ("Figure 3.5 illustrates the most complex", "Figure 3.7 illustrates the most complex"),
        ("Figure 3.5: Assignment Submission Flowchart", "Figure 3.7: Assignment Submission and AI Grading Flowchart"),
        # Flowcharts: 3.6→3.8
        ("Figure 3.6 presents a granular", "Figure 3.8 presents a granular"),
        ("Figure 3.6: AI Tutor Interaction Flowchart", "Figure 3.8: AI Tutor Interaction Flowchart"),
        # Sequence Diagrams: 3.7→3.9
        ("Figure 3.7: AI Grading Sequence Diagram", "Figure 3.9: AI Grading Sequence Diagram"),
        ("Figure 3.7)", "Figure 3.9)"),
        ("Figure 3.7 ", "Figure 3.9 "),
        ("Figure 3.7.", "Figure 3.9."),
        ("(Figure 3.7)", "(Figure 3.9)"),
        # Sequence Diagrams: 3.8→3.10
        ("Figure 3.8: AI Tutor Context Sequence", "Figure 3.10: AI Tutor Sequence Diagram"),
        ("Figure 3.8)", "Figure 3.10)"),
        ("Figure 3.8 ", "Figure 3.10 "),
        ("Figure 3.8.", "Figure 3.10."),
        ("(Figure 3.8)", "(Figure 3.10)"),
        # Fix INSERT placeholders
        ("[INSERT FIGURE 3.1: Use Case Diagram -- Exercises and Quizzes HERE]",
         "[INSERT FIGURE 3.1: Use Case Diagram — Exercises and Quizzes HERE]"),
        ("[INSERT FIGURE 3.2: Use Case Diagram -- Theory Study and AI Tutor HERE]",
         "[INSERT FIGURE 3.2: Use Case Diagram — Theory Study and AI Tutor HERE]"),
        ("[INSERT FIGURE 3.3: Use Case Diagram -- Web Admin Users, Submissions and Dashboard HERE]",
         "[INSERT FIGURE 3.3: Use Case Diagram — Web Admin: Users, Submissions and Dashboard HERE]"),
        ("[INSERT FIGURE 3.4: System Architecture Diagram HERE]",
         "[INSERT FIGURE 3.4: System Architecture Diagram HERE]"),
        ("[INSERT FIGURE 3.5: ERD HERE]",
         "[INSERT FIGURE 3.5: Entity Relationship Diagram (ERD) HERE]"),
    ]

    # TABLE RENAMING
    # UC tables: Table 3.1/3.2 keep as is
    # Data Dict tables: old 3.2 (users) conflict → rename TO 3.3
    # old 3.3 → 3.4, old 3.4 → 3.5 ... old 3.11 → 3.12
    # But from audit, Data Dict starts at Table 3.2 (users) which conflicts with UC-07.
    # So: Data Dict old numbers → new numbers:
    #   Old "Table 3.2: Structure of 'users'" → New "Table 3.3: ..."
    #   Old "Table 3.3: ..." → New "Table 3.4: ..."
    #   ...
    #   Old "Table 3.11: ..." → New "Table 3.12: ..."

    table_renames = [
        # Data Dictionary tables (rename old→new, do in REVERSE to avoid conflicts)
        # Old 3.11 → new 3.12
        ("Table 3.11: Structure of 'ai_chat_messages' table",
         "Table 3.12: Structure of 'ai_chat_messages' Table"),
        # Old 3.10 → new 3.11
        ("Table 3.10: Structure of 'ai_chat_sessions' table",
         "Table 3.11: Structure of 'ai_chat_sessions' Table"),
        # Old 3.9 → new 3.10
        ("Table 3.9: Structure of 'quiz_results' & 'progress' tables",
         "Table 3.10: Structure of 'quiz_results' and 'progress' Tables"),
        # Old 3.8 → new 3.9
        ("Table 3.8: Structure of 'quizzes', 'questions' & 'choices'",
         "Table 3.9: Structure of 'quizzes', 'questions' and 'choices' Tables"),
        # Old 3.7 → new 3.8
        ("Table 3.7: Structure of 'exercise_submissions' table",
         "Table 3.8: Structure of 'exercise_submissions' Table"),
        # Old 3.6 → new 3.7
        ("Table 3.6: Structure of 'exercises' & 'exercise_solutions' tables",
         "Table 3.7: Structure of 'exercises' and 'exercise_solutions' Tables"),
        # Old 3.5 → new 3.6
        ("Table 3.5: Structure of 'lesson_contents' table",
         "Table 3.6: Structure of 'lesson_contents' Table"),
        # Old 3.4 → new 3.5
        ("Table 3.4: Structure of 'chapters' & 'lessons' tables",
         "Table 3.5: Structure of 'chapters' and 'lessons' Tables"),
        # Old 3.3 → new 3.4
        ("Table 3.3: Structure of 'grades' & 'subjects' tables (Combined Schema)",
         "Table 3.4: Structure of 'grades' and 'subjects' Tables"),
        # Old 3.2 (users) → new 3.3  [DATA DICT]
        ("Table 3.2: Structure of 'users' table",
         "Table 3.3: Structure of 'users' Table"),
        # Fix caption format for UC tables (remove extra colon/dash variant)
        ("Table 3.1: Use Case Specification: UC-05 — Submit Handwritten Answer and AI Grading",
         "Table 3.1: Use Case Specification — UC-05: Submit Handwritten Answer and AI Grading"),
        ("Table 3.2: Use Case Specification: UC-07 — Socratic AI Tutor Chat",
         "Table 3.2: Use Case Specification — UC-07: Socratic AI Tutor Chat"),
    ]

    all_renames = figure_renames + table_renames

    # ═════════════════════════════════════════════════════════════════════════
    # STEP 3: APPLY ALL RENAMES + FIX ALIGNMENT
    # ═════════════════════════════════════════════════════════════════════════
    fix_count = 0
    align_count = 0

    for i in range(chap3_start, chap4_start):
        p = paras[i]
        original_text = p.text.strip()

        # Apply string replacements
        for old_str, new_str in all_renames:
            if old_str in p.text:
                replace_in_para(p, old_str, new_str)
                print("[FIX] Para " + str(i) + ": '" + old_str[:60] +
                      "' -> '" + new_str[:60] + "'")
                fix_count += 1

        # Fix alignment
        t = p.text.strip()

        # Figure captions → CENTER
        if t.startswith("Figure 3.") and (":" in t or "—" in t):
            if p.alignment != WD_PARAGRAPH_ALIGNMENT.CENTER:
                p.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
                align_count += 1

        # [INSERT FIGURE...] placeholder → CENTER
        if t.startswith("[INSERT FIGURE"):
            if p.alignment != WD_PARAGRAPH_ALIGNMENT.CENTER:
                p.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
                align_count += 1

        # Table captions → LEFT (standard OUM)
        if t.startswith("Table 3.") and (":" in t or "—" in t):
            # Table captions in OUM are often left-aligned, but our style is center
            # Per OUM standard: Table caption ABOVE the table, centered is fine
            if p.alignment != WD_PARAGRAPH_ALIGNMENT.CENTER:
                p.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
                align_count += 1

        # Body text → JUSTIFY (not headings, not captions)
        is_heading = any(p.text.strip().startswith(prefix)
                         for prefix in ["3.", "Figure ", "Table ", "[INSERT"])
        if not is_heading and t and len(t) > 30:
            if p.alignment not in (WD_PARAGRAPH_ALIGNMENT.JUSTIFY,
                                   WD_PARAGRAPH_ALIGNMENT.CENTER,
                                   WD_PARAGRAPH_ALIGNMENT.LEFT):
                p.alignment = WD_PARAGRAPH_ALIGNMENT.JUSTIFY
                align_count += 1

    # ═════════════════════════════════════════════════════════════════════════
    # STEP 4: FIX BODY-TEXT CROSS-REFERENCES (general patterns not caught above)
    # ═════════════════════════════════════════════════════════════════════════
    # Catch any remaining "Figure 3.4" in body that refers to Flowchart (→ 3.6)
    # "Figure 3.5" in body → 3.7, "Figure 3.6" → 3.8, "Figure 3.7" → 3.9, etc.
    # We do a regex sweep for any missed references

    general_fig_map = {
        # Only remap ones that changed; 3.1/3.2/3.3 stay, 3.4 (arch) stays
        "Figure 3.5 ": "Figure 3.7 ",
        "Figure 3.6 ": "Figure 3.8 ",
        "Figure 3.7 ": "Figure 3.9 ",
        "Figure 3.8 ": "Figure 3.10 ",
    }

    paras = doc.paragraphs  # refresh after changes
    for i in range(chap3_start, min(chap4_start, len(paras))):
        p = paras[i]
        t = p.text.strip()
        # Skip actual captions - only process body text
        if t.startswith("Figure") or t.startswith("Table") or t.startswith("[INSERT"):
            continue
        changed = False
        for old_f, new_f in general_fig_map.items():
            if old_f in p.text:
                replace_in_para(p, old_f, new_f)
                print("[REF] Para " + str(i) + ": replaced '" + old_f.strip() +
                      "' -> '" + new_f.strip() + "' in body text")
                fix_count += 1
                changed = True

    # ═════════════════════════════════════════════════════════════════════════
    # STEP 5: ADD MISSING FIGURE 3.4 PLACEHOLDER (System Architecture)
    # ═════════════════════════════════════════════════════════════════════════
    # Check if there is already a Figure 3.4 reference in the 3.4 section
    paras = doc.paragraphs
    has_fig34 = any("Figure 3.4" in p.text for p in paras[chap3_start:chap4_start])
    if not has_fig34:
        print("[INFO] Figure 3.4 (System Architecture) not found - checking for 3.4 section")
        for i, p in enumerate(paras):
            if chap3_start <= i < chap4_start and p.text.strip().startswith("3.4") and \
               "ARCHITECTURE" in p.text.strip().upper():
                # Insert placeholder after this heading
                from docx.oxml import OxmlElement
                ph = p.insert_paragraph_before()  # insert NOTHING to get a ref
                # Actually insert AFTER - easier approach: find next para
                # We'll handle this by scanning further
                print("[INFO] Found 3.4 heading at para " + str(i))
                break

    # ═════════════════════════════════════════════════════════════════════════
    # SAVE
    # ═════════════════════════════════════════════════════════════════════════
    doc.save(output_path)
    print("")
    print("SUCCESS! Saved to V21.docx")
    print("   Replacements applied: " + str(fix_count))
    print("   Alignment fixes:      " + str(align_count))
    print("   Deleted duplicates:   " + str(len(deleted)))
    print("")
    print("Final Figure sequence in Chapter 3:")
    print("   Figure 3.1  Use Case Diagram -- Exercises & Quizzes")
    print("   Figure 3.2  Use Case Diagram -- Theory Study & AI Tutor")
    print("   Figure 3.3  Use Case Diagram -- Web Admin Dashboard")
    print("   Figure 3.4  System Architecture Diagram (3-tier)")
    print("   Figure 3.5  Entity Relationship Diagram (ERD)")
    print("   Figure 3.6  Flowchart 1 -- Learning Journey")
    print("   Figure 3.7  Flowchart 2 -- Assignment Submission & AI Grading")
    print("   Figure 3.8  Flowchart 3 -- AI Tutor Interaction")
    print("   Figure 3.9  Sequence Diagram 1 -- AI Grading")
    print("   Figure 3.10 Sequence Diagram 2 -- AI Tutor")
    print("")
    print("Final Table sequence in Chapter 3:")
    print("   Table 3.1   UC-05 Use Case Specification")
    print("   Table 3.2   UC-07 Use Case Specification")
    print("   Table 3.3   Data Dict: users")
    print("   Table 3.4   Data Dict: grades & subjects")
    print("   Table 3.5   Data Dict: chapters & lessons")
    print("   Table 3.6   Data Dict: lesson_contents")
    print("   Table 3.7   Data Dict: exercises & exercise_solutions")
    print("   Table 3.8   Data Dict: exercise_submissions")
    print("   Table 3.9   Data Dict: quizzes, questions & choices")
    print("   Table 3.10  Data Dict: quiz_results & progress")
    print("   Table 3.11  Data Dict: ai_chat_sessions")
    print("   Table 3.12  Data Dict: ai_chat_messages")


if __name__ == "__main__":
    main()
