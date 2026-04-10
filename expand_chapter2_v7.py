import os
import sys

try:
    import docx
except ImportError:
    import subprocess
    subprocess.check_call([sys.executable, "-m", "pip", "install", "python-docx"])
    import docx

from docx import Document
from docx.shared import Pt, Cm
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
    run = p.add_run(text)
    run.font.name = 'Times New Roman'
    run.font.size = Pt(12)
    run.bold = is_bold
    return run

def main():
    doc_path = r"e:\Final_Project\docx\MaSci_Final_Report_V6.docx"
    if not os.path.exists(doc_path):
        print(f"File not found: {doc_path}")
        return
        
    doc = Document(doc_path)
    
    # Locate Chapter 2 and Chapter 3
    chap2_idx = -1
    chap3_idx = -1
    for i, p in enumerate(doc.paragraphs):
        text = p.text.strip().upper()
        if "CHAPTER 2" in text and ("LITERATURE REVIEW" in text or "THEORETICAL" in text):
            chap2_idx = i
        elif "CHAPTER 3" in text and "ANALYSIS" in text:
            chap3_idx = i
            break
            
    if chap2_idx != -1 and chap3_idx != -1:
        p_chap3 = doc.paragraphs[chap3_idx]
        
        def insert_p(text, bold=False):
            new_p = p_chap3.insert_paragraph_before()
            apply_format(new_p, text, is_bold=bold)
            return new_p
            
        def insert_heading(text):
            new_p = p_chap3.insert_paragraph_before()
            new_p.paragraph_format.space_before = Pt(18)
            new_p.paragraph_format.space_after = Pt(12)
            run = new_p.add_run(text)
            run.font.name = 'Times New Roman'
            run.font.size = Pt(12)
            run.bold = True

        insert_heading("2.1 Previous Studies and Existing Platforms")
        insert_p("In the rapid evolution of educational technology, several legacy platforms have established foundational paradigms for online learning. However, each system possesses inherent limitations that prevent holistic adoption in specialized regional contexts such as Vietnam.")
        insert_p("Global e-learning giants like Khan Academy offer an exceptionally rich repository of instructional videos and exercises at zero cost, making them invaluable resources. Nevertheless, Khan Academy lacks a sophisticated mechanism to automatically grade handwritten mathematical assignments via image recognition and suffers from linguistic constraints, as its localized content is insufficiently aligned with the strict curriculum defined by the Vietnamese Ministry of Education.")
        insert_p("Similarly, Duolingo has revolutionized self-paced learning by successfully integrating Gamification metrics (e.g., progress streaks, leaderboards) that drastically enhance user retention. However, Duolingo's architecture is singularly optimized for language acquisition, leaving a distinct void in the domain of complex natural sciences like Mathematics and Physics, where the visual structure of equations is paramount.")
        insert_p("On the purely mechanical front, Photomath demonstrates incredible proficiency in optical character recognition (OCR) and step-by-step mathematical problem-solving from photographic inputs. Despite its computational brilliance, Photomath lacks a structured scholarly framework; it functions purely as an answering machine rather than an integrated educational ecosystem, completely devoid of lecture modules and progress tracking mechanisms.")
        insert_p("Domestically, platforms such as Hocmai.vn and Olm.vn have addressed localized curriculum constraints successfully. Nonetheless, their legacy software architectures often rely on outdated graphical user interfaces. They lack deep native integration on modern mobile operating systems and are critically deficient in leveraging Conversational Artificial Intelligence to interactively guide secondary school students.")
        insert_p("The Identified Research Gap:")
        insert_p("A rigorous comparative analysis reveals a glaring Research Gap within the Vietnamese market: There is no existing E-Learning ecosystem that successfully converges a localized academic syllabus with the cutting-edge capabilities of Multimodal AI for grading handwritten self-study assignments. MaSci is uniquely positioned to fill this gap, acting as the nexus between Gamification methodologies and highly interactive, AI-driven educational assessment.")

        insert_heading("2.2 Technology Stack and Architectural Justifications")
        insert_p("The selection of the technological stack for the MaSci platform was not merely based on industry popularity, but rather on rigorous architectural justifications aimed at ensuring high security, real-time performance, and profound scalability.")
        
        insert_p("Backend Infrastructure: Spring Boot 3.2.5 and MySQL")
        insert_p("The core server-side logic and Application Programming Interfaces (APIs) are engineered using Spring Boot 3.2.5. The primary justification for its utilization is the framework's unparalleled stability, strict type safety, and the robust Spring Security module, which effortlessly manages JWT (JSON Web Token) filter chains for stateless, highly secure user authentication. Furthermore, MySQL serves as the primary relational database system. Notably, the project employs the 'ddl-auto=none' configuration property within Hibernate. This specific DBA (Database Administrator) best practice ensures that the schema is completely decoupled from application-driven auto-generation, migrating schema control exclusively to dedicated SQL scripts thereby preventing catastrophic, accidental data definition mutations in production environments.")

        insert_p("Administrative Dashboard: React 19 and Vite")
        insert_p("For the Web Admin interface, React 19 was explicitly chosen over its predecessors. The defining architectural logic behind this is the implementation of the new React Compiler, which natively optimizes complex UI re-renders without the manual overhead of utilizing 'useMemo' or 'useCallback'. This is particularly crucial for the administrative dashboard, which must seamlessly render heavy DOM (Document Object Model) trees injected with sophisticated third-party libraries such as CKEditor 5 for rich-text authoring and MathLive/KaTeX for real-time mathematical WYSIWYG input. The bundler, 'rolldown-vite', was selected to drastically reduce Hot Module Replacement (HMR) times, significantly accelerating the development cycle.")

        insert_p("Frontend Mobile Application: Kotlin and Jetpack Compose")
        insert_p("The student-facing platform is a native Android application developed strictly in Kotlin. Jetpack Compose was exclusively favored over traditional XML layouts because its declarative paradigm simplifies UI logic and naturally complements the Model-View-ViewModel (MVVM) architecture architecture adopted in the project.")

        insert_heading("2.3 Conceptual Framework and Educational Philosophy")
        insert_p("The systematic engineering of the MaSci platform relies primarily on a Modular Monolith Architecture. This structure guarantees that highly cohesive domains (such as Authentication, Student Execution, AI Integration) are properly segregated logically, paving a seamless transition pathway should the platform necessitate an eventual migration to a Microservices Architecture.")
        insert_p("In pedagogical terms, the conceptual framework vehemently opposes the notion of autonomous technology rendering human educators obsolete. Instead, the integration of Google Gemini is founded entirely on the philosophy of 'AI-Augmented Learning'. In this context, the Socratic AI serves strictly as an intellectual facilitator—functioning systematically to prompt students with interrogative cues rather than blindly supplying outright answers.")
        insert_p("Crucially, the Multimodal AI integration acts strictly as an instrument for 'Formative Assessment'. Formative Assessment is an ongoing, real-time diagnostic process utilized to monitor student learning and provide immediate constructive feedback to improve ongoing educational outcomes. By automatically assessing structural methodology in handwritten documents via AI, students are instantly made aware of their minor logical missteps, fundamentally aligning the project with the apex of modern constructive educational theory.")

        paragraphs_to_delete = doc.paragraphs[chap2_idx+1:chap3_idx]
        for p in paragraphs_to_delete:
            delete_paragraph(p)
            
        output_path = r"e:\Final_Project\docx\MaSci_Final_Report_V7.docx"
        doc.save(output_path)
        print("Success! Chapter 2 has been expanded significantly and saved to " + output_path)
    else:
        print("Error: Could not find CHAPTER 2 or CHAPTER 3 in the document.")
        
if __name__ == '__main__':
    main()
