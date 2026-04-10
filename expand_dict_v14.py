import os
import sys

try:
    import docx
except ImportError:
    import subprocess
    subprocess.check_call([sys.executable, "-m", "pip", "install", "python-docx"])
    import docx

from docx import Document
from docx.shared import Pt, Inches
from docx.enum.text import WD_PARAGRAPH_ALIGNMENT, WD_LINE_SPACING

def apply_format(p, text, is_bold=False, is_italic=False):
    p.paragraph_format.line_spacing_rule = WD_LINE_SPACING.MULTIPLE
    p.paragraph_format.line_spacing = 1.5
    p.alignment = WD_PARAGRAPH_ALIGNMENT.JUSTIFY
    if text:
        run = p.add_run(text)
        run.font.name = 'Times New Roman'
        run.font.size = Pt(12)
        run.bold = is_bold
        run.italic = is_italic
        return run
    return None

def create_db_table(document, p_ref, table_title, table_desc, headers, rows_data):
    # Insert Title
    title_p = p_ref.insert_paragraph_before()
    title_p.paragraph_format.space_before = Pt(12)
    run_t = title_p.add_run(table_title)
    run_t.font.name = 'Times New Roman'
    run_t.font.size = Pt(12)
    run_t.bold = True

    # Insert Description
    desc_p = p_ref.insert_paragraph_before()
    desc_p.paragraph_format.space_after = Pt(6)
    run_d = desc_p.add_run(f"({table_desc})")
    run_d.font.name = 'Times New Roman'
    run_d.font.size = Pt(11)
    run_d.italic = True

    # Create Table
    table = document.add_table(rows=len(rows_data)+1, cols=len(headers))
    table.style = 'Table Grid'
    
    # Fill headers
    for col_idx, header in enumerate(headers):
        cell = table.cell(0, col_idx)
        cell.text = header
        for paragraph in cell.paragraphs:
            for run_h in paragraph.runs:
                run_h.font.name = 'Times New Roman'
                run_h.font.size = Pt(11)
                run_h.bold = True
                
    # Fill data
    for row_idx, row_data in enumerate(rows_data, start=1):
        for col_idx, cell_value in enumerate(row_data):
            cell = table.cell(row_idx, col_idx)
            cell.text = str(cell_value)
            for paragraph in cell.paragraphs:
                for run_c in paragraph.runs:
                    run_c.font.name = 'Times New Roman'
                    run_c.font.size = Pt(11)

    # Move table before the reference paragraph
    p_ref._element.addprevious(table._element)
    
    # Add spacing after table
    sp_p = p_ref.insert_paragraph_before()
    sp_p.paragraph_format.space_after = Pt(12)

def main():
    doc_path = r"e:\Final_Project\docx\MaSci_Final_Report_V13.docx"
    if not os.path.exists(doc_path):
        print(f"File not found: {doc_path}")
        return
        
    doc = Document(doc_path)
    
    start_idx = -1
    end_idx = -1
    
    for i, p in enumerate(doc.paragraphs):
        text = p.text.strip().upper()
        if "3.5.2 DATA DICTIONARY" in text:
            start_idx = i
        elif "3.6 SYSTEM FLOWCHARTS" in text:
            end_idx = i
            break
            
    if start_idx != -1 and end_idx != -1:
        # Delete old dictionary paragraphs
        paragraphs_to_delete = doc.paragraphs[start_idx:end_idx]
        p_ref = doc.paragraphs[end_idx]
        
        for p in paragraphs_to_delete:
            p_elem = p._element
            p_elem.getparent().remove(p_elem)
            p._p = p._element = None
            
        def insert_heading(text, level=2):
            new_p = p_ref.insert_paragraph_before()
            new_p.paragraph_format.space_before = Pt(18)
            new_p.paragraph_format.space_after = Pt(12)
            run = new_p.add_run(text)
            run.font.name = 'Times New Roman'
            run.font.size = Pt(14 if level == 1 else 12)
            run.bold = True

        insert_heading("3.5.2 Data Dictionary", level=3)
        
        intro_p = p_ref.insert_paragraph_before()
        apply_format(intro_p, "To optimize the presentation while maintaining analytical depth, the Data Dictionary below meticulously details the 17 core entity tables categorically mapping the primary business constraints. The remaining generic tables (e.g., OTP registries, isolated settings) are securely relegated to Appendix B.")
        
        headers = ["Field Name", "Data Type", "Constraint", "Description"]

        # ---------------- GROUP 0 ---------------- #
        g0_p = p_ref.insert_paragraph_before()
        apply_format(g0_p, "Group 1: Global Identity Management", is_bold=True)
        create_db_table(doc, p_ref, "Table 3.2: Structure of 'users' table", 
            "Description: The foundational root encompassing system identity, authentication tokens, and gamification gameworld statistics.", headers, [
            ["id", "BIGINT", "PK, AUTO_INCREMENT", "Global Unique identifier"],
            ["email", "VARCHAR(255)", "UNIQUE, NOT NULL", "Login credential boundary"],
            ["password", "VARCHAR(255)", "NOT NULL", "BCrypt encrypted payload"],
            ["current_streak", "INT", "DEFAULT 0", "Gamification consecutive days"],
            ["fcm_token", "VARCHAR(255)", "NULL", "Firebase Cloud Messaging token"]
        ])

        # ---------------- GROUP 1 ---------------- #
        g1_p = p_ref.insert_paragraph_before()
        apply_format(g1_p, "Group 2: Academic Content Organization", is_bold=True)
        
        create_db_table(doc, p_ref, "Table 3.3: Structure of 'grades' & 'subjects' tables (Combined Schema)", 
            "Description: Maps the uppermost tiers of the pedagogical Cascading Tree. Forces strict structural boundaries between logic domains (e.g., Math Grade 6 vs. Science Grade 7).", headers, [
            ["id (grades/subjects)", "INT", "PK", "Primary identification vector"],
            ["code", "VARCHAR", "UNIQUE", "Logical mapping constant (e.g., K7)"],
            ["grade_id (subjects)", "TINYINT", "FK (grades.id)", "Parent reference mapping"],
            ["name_en", "VARCHAR", "NULL", "i18n translation string"]
        ])
        
        create_db_table(doc, p_ref, "Table 3.4: Structure of 'chapters' & 'lessons' tables", 
            "Description: Granular sequential structure enforcing syllabus constraints. ON DELETE CASCADE strictly prevents orphan arrays when curriculum changes occur.", headers, [
            ["id", "BIGINT", "PK, AUTO_INCREMENT", "Unique identifier"],
            ["subject_id / chapter_id", "BIGINT", "FOREIGN KEYS", "Cascading parent bindings"],
            ["title_vi", "VARCHAR", "NOT NULL", "Standardized Vietnamese title"],
            ["sequence_order", "INT", "NOT NULL", "Display hierarchy logic"]
        ])
        
        create_db_table(doc, p_ref, "Table 3.5: Structure of 'lesson_contents' table", 
            "Description: Crucial for rendering diverse pedagogies. The content_type field explicitly dictates whether the Client UI should instantiate a YouTube Video or invoke Markwon Rich Text parsers.", headers, [
            ["id", "BIGINT", "PK", "Data node locator"],
            ["lesson_id", "BIGINT", "FK (lessons.id)", "Target lesson"],
            ["content_type", "ENUM", "NOT NULL", "'VIDEO' or 'IMAGE' or 'TEXT'"],
            ["content_body", "TEXT", "NULL", "Markdown/KaTeX theoretical body"],
            ["video_url", "VARCHAR", "NULL", "External multimedia source URL"]
        ])

        # ---------------- GROUP 2 ---------------- #
        g2_p = p_ref.insert_paragraph_before()
        apply_format(g2_p, "Group 3: Exercise & AI Validation Flow", is_bold=True)
        
        create_db_table(doc, p_ref, "Table 3.6: Structure of 'exercises' & 'exercise_solutions' tables", 
            "Description: Persists mathematical problem statements and multi-tiered scaffolding hints (triggering sequentially at 2-minute, 5-minute intervals) to autonomously guide struggling learners.", headers, [
            ["id", "BIGINT", "PK", "Atomic assignment identifier"],
            ["lesson_id", "BIGINT", "FK (lessons.id)", "Contextual mapping locus"],
            ["prompt_content", "TEXT", "NOT NULL", "The actual mathematical prompt"],
            ["hint1 / hint2 / hint3", "TEXT", "NULL", "Temporal scaffolding intervals"],
            ["solution_value", "TEXT", "NOT NULL", "Absolute ground-truth algorithm"]
        ])
        
        create_db_table(doc, p_ref, "Table 3.7: Structure of 'exercise_submissions' table", 
            "Description: The epicenter of the AI Grading capabilities. image_url explicitly persists Firebase Signed URLs, whereas score and ai_reasoning dynamically update following Google Gemini inference callbacks.", headers, [
            ["id", "BIGINT", "PK", "Unique record identifier"],
            ["user_id", "BIGINT", "FK (users.id)", "Student invoking submission"],
            ["exercise_id", "BIGINT", "FK (exercises.id)", "Targeted exercise ID"],
            ["image_url", "TEXT", "NOT NULL", "Firebase Storage secured URL"],
            ["is_valid", "BOOLEAN", "NOT NULL", "Failsafe validation mechanism"],
            ["score", "DOUBLE", "NULL", "Computed Multimodal AI score"],
            ["ai_reason", "TEXT", "NULL", "Gemini's heuristic assessment"]
        ])

        # ---------------- GROUP 3 ---------------- #
        g3_p = p_ref.insert_paragraph_before()
        apply_format(g3_p, "Group 4: Quiz & Progress Telemetry", is_bold=True)
        
        create_db_table(doc, p_ref, "Table 3.8: Structure of 'quizzes', 'questions' & 'choices'", 
            "Description: Highly segregated tables separating the Assessment structure (quizzes), generic mathematical questions, and their respective correct/incorrect Boolean choices.", headers, [
            ["id", "BIGINT", "PK", "Unique node identifier"],
            ["quiz_id / question_id", "BIGINT", "FOREIGN KEYS", "Strict dependency mappings"],
            ["content_type", "ENUM", "TEXT or IMAGE", "Visual payload format declaration"],
            ["explanation_en", "TEXT", "NULL", "Post-submission reasoning data"],
            ["is_correct", "BOOLEAN", "DEFAULT FALSE", "Ground truth delimiter (choices)"]
        ])
        
        create_db_table(doc, p_ref, "Table 3.9: Structure of 'quiz_results' & 'progress' tables", 
            "Description: Radically optimizes query execution architecture via deliberate 3NF denormalization. Persisting the final holistic percentage score globally circumvents exhaustive real-time computation logic.", headers, [
            ["id", "BIGINT", "PK", "Unique telemetry identifier"],
            ["user_id", "BIGINT", "FK (users.id)", "Executing learner"],
            ["score / percentage", "DOUBLE", "NOT NULL", "Post-calculation performance metric"],
            ["progress_percent", "DOUBLE", "DEFAULT 0.0", "Holistic mastery tracking"],
            ["completed_at", "DATETIME", "NULL", "Verification chronological stamp"]
        ])

        # ---------------- GROUP 4 ---------------- #
        g4_p = p_ref.insert_paragraph_before()
        apply_format(g4_p, "Group 5: Conversational AI Environment", is_bold=True)
        
        create_db_table(doc, p_ref, "Table 3.10: Structure of 'ai_chat_sessions' table", 
            "Description: Acts as the primary anchor for maintaining Context injection arrays. The nullable exercise_id permits the AI to function either holistically or strictly bound to a singular mathematical assignment.", headers, [
            ["id", "BIGINT", "PK", "Execution context boundary root"],
            ["user_id", "BIGINT", "FK (users.id)", "Session owner pointer"],
            ["exercise_id", "BIGINT", "FK (exercises.id)", "Specific assignment mapping (Optional)"],
            ["created_at", "DATETIME", "DEFAULT CURRENT", "Timestamp vector initialization"]
        ])
        
        create_db_table(doc, p_ref, "Table 3.11: Structure of 'ai_chat_messages' table", 
            "Description: The foundational chronologic array driving the Socratic AI logic subsystem. The 'role' attribute isolates message origins (USER vs. ASSISTANT), enabling the Backend API wrapper to correctly serialize Context Windows.", headers, [
            ["id", "BIGINT", "PK", "Chronological message node"],
            ["session_id", "BIGINT", "FK (ai_chat_sessions.id)", "Master conversation chain link"],
            ["role", "VARCHAR(10)", "NOT NULL", "'USER' or 'ASSISTANT' designation"],
            ["content", "TEXT", "NOT NULL", "Dense mathematical payload string"],
            ["created_at", "DATETIME", "DEFAULT CURRENT", "Timestamp vector mapping"]
        ])

        output_path = r"e:\Final_Project\docx\MaSci_Final_Report_V14.docx"
        doc.save(output_path)
        print("Success! Data dictionary expanded and rebuilt to V14: " + output_path)
    else:
        print("Error: Could not find Data Dictionary boundaries.")

if __name__ == '__main__':
    main()
