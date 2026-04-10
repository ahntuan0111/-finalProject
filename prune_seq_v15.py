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
from docx.enum.section import WD_SECTION

def delete_paragraph(paragraph):
    p = paragraph._element
    p.getparent().remove(p)
    paragraph._p = paragraph._element = None

def apply_format(p, text, is_bold=False):
    p.paragraph_format.line_spacing_rule = WD_LINE_SPACING.MULTIPLE
    p.paragraph_format.line_spacing = 1.5
    p.paragraph_format.space_after = Pt(24)
    p.alignment = WD_PARAGRAPH_ALIGNMENT.JUSTIFY
    if text:
        run = p.add_run(text)
        run.font.name = 'Times New Roman'
        run.font.size = Pt(12)
        run.bold = is_bold
        return run
    return None

def insert_image(p_ref, img_path, caption):
    if os.path.exists(img_path):
        new_p = p_ref.insert_paragraph_before()
        new_p.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
        new_p.paragraph_format.line_spacing_rule = WD_LINE_SPACING.SINGLE
        new_p.paragraph_format.space_after = Pt(0)
        run = new_p.add_run()
        run.add_picture(img_path, width=Inches(6.0))
        
        cap_p = p_ref.insert_paragraph_before()
        cap_p.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
        cap_p.paragraph_format.line_spacing_rule = WD_LINE_SPACING.SINGLE
        cap_p.paragraph_format.space_after = Pt(24)
        c_run = cap_p.add_run(caption)
        c_run.font.name = 'Times New Roman'
        c_run.font.size = Pt(12)
        c_run.italic = True
    else:
        new_p = p_ref.insert_paragraph_before()
        apply_format(new_p, f"[IMAGE NOT FOUND: {img_path}]", is_bold=True)
        new_p.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER

def main():
    doc_path = r"e:\Final_Project\docx\MaSci_Final_Report_V14.docx"
    if not os.path.exists(doc_path):
        print(f"File not found: {doc_path}")
        return
        
    doc = Document(doc_path)
    
    start_idx = -1
    end_idx = -1
    
    for i, p in enumerate(doc.paragraphs):
        text = p.text.strip().upper()
        if "3.7 SEQUENCE DIAGRAMS" in text:
            start_idx = i
        elif "3.8 UI/UX DESIGN SPECIFICATIONS" in text:
            end_idx = i
            break
            
    if start_idx != -1 and end_idx != -1:
        # Delete old sequence diagrams paragraphs
        paragraphs_to_delete = doc.paragraphs[start_idx:end_idx]
        p_ref = doc.paragraphs[end_idx]
        
        for p in paragraphs_to_delete:
            delete_paragraph(p)
            
        def insert_heading(text, level=2):
            new_p = p_ref.insert_paragraph_before()
            new_p.paragraph_format.space_before = Pt(18)
            new_p.paragraph_format.space_after = Pt(12)
            run = new_p.add_run(text)
            run.font.name = 'Times New Roman'
            run.font.size = Pt(14 if level == 1 else 12)
            run.bold = True

        def insert_p(text, bold=False):
            new_p = p_ref.insert_paragraph_before()
            apply_format(new_p, text, is_bold=bold)
            return new_p

        insert_heading("3.7 Sequence Diagrams")
        insert_p("Sequence diagrams are heavily utilized to structurally demonstrate inter-object communication sequences and object lifecycles across the systemic architecture. To highlight the sophisticated core technical operations of the MaSci platform, the analysis intentionally isolates the two most complex artificial intelligence workflows.")

        insert_p("3.7.1 AI Grading Sequence Diagram")
        insert_p("[1. Participants & Lifecycle]\nAs depicted in Figure 3.7, responsibilities correctly distribute across defined functional participants: the Mobile Client, ExerciseController, FirebaseStorageService, and the GeminiValidationService.")
        insert_p("[2. Synchronous & Asynchronous Messages]\nThe execution lifecycle maps synchronous HTTP GET requests initiating from the Mobile Client soliciting upload parameters. Following successful cloud uploads via HTTP PUT mechanisms, the structural Controller invokes internal domain logic methods, converting them into isolated, heavily contextualized asynchronous AI evaluations.")
        insert_p("[3. Fragments & Exceptions]\nInside the explicitly analyzed 'alt' conditional fragment, fault tolerance is securely instantiated. Should the remote Google service throw computational interrupts (e.g. 503 Gateway Timeouts), the container boundary actively intercepts the boundary failure, emitting secure HTTP 500 fallback logic prohibiting dangerous memory leaks spanning across end-user operating systems.")
        insert_image(p_ref, r"e:\Final_Project\docx\SequenceDIagram.png", "Figure 3.7: AI Grading Sequence Diagram")

        insert_p("3.7.2 AI Tutor Context Sequence Diagram")
        insert_p("[1. Business Logic & Purpose]\nUnlike static Q&A chatbot routines, the MaSci AI Tutor is engineered comprehensively around the Socratic Method—commanding the backend platform to maintain contextual conversational memory throughout dynamic execution loops.")
        insert_p("[2. Technical Execution]\nThe lifecycle originates within an Optional Fragment (opt block). Here, the AiTutorService validates session continuation identifiers against the MySQL persistence matrix. Inside the dominant Loop Fragment, the Backend strictly parses collective arrays binding previous messages sequentially, creating optimal JSON injection schemas designed natively for foundational Gemini interpretation. This explicit Context Injection enables profound heuristic comprehension spans.")
        insert_p("[3. Exception Mapping]\nFault suppression protocols firmly highlight Alternative Fragments. In timeout scenarios breaching standard API limits, the active AiTutorService natively intercepts anomalous vectors, overriding the destructive application crash sequences via explicit Spring Exception handlers, effectively safely returning friendly UI configurations to the mobile user.")
        insert_image(p_ref, r"e:\Final_Project\docx\SequenceDiagram (2).png", "Figure 3.8: AI Tutor Context Sequence")

        output_path = r"e:\Final_Project\docx\MaSci_Final_Report_V15.docx"
        doc.save(output_path)
        print("Success! Sequence diagrams pruned and saved to V15: " + output_path)
    else:
        print("Error: Could not find Sequence Diagrams boundaries.")

if __name__ == '__main__':
    main()
