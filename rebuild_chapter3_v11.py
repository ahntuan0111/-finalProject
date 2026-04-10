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
        apply_format(new_p, f"[IMAGE NOT FOUND OR PLACEHOLDER: {img_path}]", is_bold=True)
        new_p.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
        
def create_uc_table(document, p_ref, uc_id, uc_name, actor, pre_cond, main_flow, exact_flow, post_cond):
    table = document.add_table(rows=7, cols=2)
    table.style = 'Table Grid'
    
    # Fill table
    data = [
        ("Use Case ID & Name", f"{uc_id}: {uc_name}"),
        ("Primary Actor", actor),
        ("Pre-conditions", pre_cond),
        ("Main Success Scenario", main_flow),
        ("Exception/Alternate Flows", exact_flow),
        ("Post-conditions", post_cond)
    ]
    
    for i, (col1, col2) in enumerate(data):
        cell1 = table.rows[i].cells[0]
        cell1.text = col1
        for paragraph in cell1.paragraphs:
            for run in paragraph.runs:
                run.font.name = 'Times New Roman'
                run.font.size = Pt(12)
                run.bold = True
        
        cell2 = table.rows[i].cells[1]
        cell2.text = col2
        for paragraph in cell2.paragraphs:
            for run in paragraph.runs:
                run.font.name = 'Times New Roman'
                run.font.size = Pt(12)

    # Move table before the reference paragraph
    p_ref._element.addprevious(table._element)
    
    # Add spacing after table
    sp_p = p_ref.insert_paragraph_before()
    sp_p.paragraph_format.space_after = Pt(24)

def main():
    doc_path = r"e:\Final_Project\docx\MaSci_Final_Report_V10.docx"
    if not os.path.exists(doc_path):
        print(f"File not found: {doc_path}")
        return
        
    doc = Document(doc_path)
    
    chap3_idx = -1
    chap4_idx = -1
    for i, p in enumerate(doc.paragraphs):
        text = p.text.strip().upper()
        if "CHAPTER 3" in text and "ANALYSIS" in text:
            chap3_idx = i
        elif "CHAPTER 4" in text and "IMPLEMENTATION" in text:
            chap4_idx = i
            break
            
    if chap3_idx != -1 and chap4_idx != -1:
        p_chap4 = doc.paragraphs[chap4_idx]
        
        def insert_p(text, bold=False, indent=False):
            new_p = p_chap4.insert_paragraph_before()
            apply_format(new_p, text, is_bold=bold)
            if indent:
                new_p.paragraph_format.left_indent = Inches(0.5)
            return new_p
            
        def insert_heading(text, level=2):
            new_p = p_chap4.insert_paragraph_before()
            new_p.paragraph_format.space_before = Pt(18)
            new_p.paragraph_format.space_after = Pt(12)
            run = new_p.add_run(text)
            run.font.name = 'Times New Roman'
            run.font.size = Pt(14 if level == 1 else 12)
            run.bold = True

        # 3.1 SDLC
        insert_heading("3.1 Software Development Life Cycle (SDLC)")
        insert_p("The development of the MaSci platform strictly adhered to the Agile Scrum methodology, employing a structured 16-week timeline consisting of iterative Sprints. This empirical approach allowed for continuous requirements gathering, dynamic architectural recalibration, and synchronized integration of cutting-edge AI technologies in parallel with traditional CRUD operations.")

        # 3.2 Requirement Analysis
        insert_heading("3.2 Requirement Analysis")
        insert_p("In order to engineer a robust educational ecosystem, the system prerequisites were meticulously segmented into comprehensive functional boundaries and high-fidelity non-functional performance constraints.")
        insert_heading("3.2.1 Functional Requirements", level=3)
        insert_p("The system guarantees the execution of fundamental business logic categorized into six distinct capability domains:", indent=True)
        insert_p("(1) Authentication & Authorization: Secure onboarding featuring JWT-based session persistence and password recovery mechanics.", indent=True)
        insert_p("(2) Theory Management: Hierarchical rendering of educational material spanning from Grade to distinct pedagogical Lessons.", indent=True)
        insert_p("(3) Exercise Execution: Allowing students to consume, solve, and digitally submit visual mathematical exercises.", indent=True)
        insert_p("(4) Quizzes & Analytics: Administration of multiple-choice evaluations culminating in the tabulation of progress streaks.", indent=True)
        insert_p("(5) AI Tutor Subsystem: Direct integration of Multimodal AI orchestrating handwritten diagnostic reviews and conversational dialog.", indent=True)
        insert_p("(6) Web Administration Dashboard: Empowering educators with rich-text mathematical entry systems and curriculum topology control.", indent=True)
        
        insert_heading("3.2.2 Non-Functional Requirements", level=3)
        insert_p("Performance Optimization: The platform guarantees high-speed telemetry, mandating that critical read queries traversing the Academic Concept clusters execute beneath a 1.5-second latency threshold under standard concurrent load.")
        insert_p("Security Implementations: The system employs Stateless JWT mechanisms coupled with BCrypt geographic hashing protocols to unequivocally secure user credentials against rainbow-table vulnerabilities.")
        insert_p("Internationalization (i18n): While initially deployed within the Vietnamese educational matrix, structural UI components are engineered adhering to i18n architectural boundaries to permit seamless future translation capabilities.")
        insert_p("System Scalability: The architecture utilizes a rigid Modular Monolith layout across the Spring framework, allowing distinct functional domain isolation to ease future transitions directly into Microservices architecture as concurrent user traffic expands.")
        insert_p("Graceful Error Handling: Core integrations involving Google API infrastructures possess rigorous fault tolerance via controlled try-catch blocks. Upon remote 500-level faults or timeouts, the platform circumvents total shutdown, projecting graceful degradation feedback interfaces directly to the end-user.")

        # 3.3 Use Case
        insert_heading("3.3 Use Case Analysis")
        insert_p("3.3.1 System Use Case Diagram")
        insert_image(p_chap4, "[INSERT COMPLETE SYSTEM USE CASE DIAGRAM HERE]", "Figure 3.1: Global System Use Case Diagram")
        
        insert_p("3.3.2 Use Case Descriptions")
        insert_p("To comprehensively map user-computer synergies, highly targeted system procedures are exhaustively detailed via standardized Use Case Specifications.")
        
        # Table 1: UC-05
        create_uc_table(
            doc, p_chap4, 
            "UC-05", "Assignment Submission and AI Grading",
            "Student",
            "The Student is authenticated and currently accessing a valid theoretical Exercise Module.",
            "1. Student opens the exercise interface.\n2. System provisions an upload UI.\n3. Student captures an image of their handwritten work.\n4. Application validates image sharpness.\n5. Application requests Firebase Signed URL.\n6. Image executes PUT request to cloud bucket.\n7. Server routes external URL to Gemini API.\n8. Gemini assigns pedagogical score and descriptive critique.\n9. Database records submission in exercise_submissions.\n10. UI displays AI assessment explicitly to Student.",
            "If the Client-side algorithm detects extreme image blur, it halts transmission and alerts 'Please recapture image'. If the Gemini API deems the image absent of mathematical logic, it flags it 'Invalid' and prompts manual retry.",
            "A valid exercise payload is durably recorded in the database, updating the student's gamification streak telemetry."
        )

        # Table 2: UC-07
        create_uc_table(
            doc, p_chap4, 
            "UC-07", "AI Tutor Socratic Conversation",
            "Student",
            "The Student is authenticated and desires immediate contextual clarification regarding mathematical theories.",
            "1. Student triggers the AI Assistant floating interface.\n2. Server queries ai_chat_sessions logic.\n3. Existing conversation state is re-hydrated from ai_chat_messages.\n4. Student dispatches a textual query.\n5. Backend encapsulates prior history alongside new query into JSON.\n6. JSON payload transits to Google Gemini API.\n7. Gemini returns a Socratic response.\n8. Server commits both USER and ASSISTANT permutations into database.\n9. Mobile UI renders conversational update.",
            "If the Gemini API times out (HTTP 503), the server intercepts the exception, triggering a 500 response leading to a graceful Mobile UI toast: 'AI currently busy, modifying request queue'.",
            "The updated Context Window is preserved identically traversing relational schema boundaries."
        )

        # 3.4 Architecture
        insert_heading("3.4 System Architecture")
        insert_p("The foundational stability of the MaSci ecosystem is explicitly defined by its macroscopic 3-Tier Distributed Architecture.")
        insert_image(p_chap4, "[INSERT 3-TIER ARCHITECTURE DIAGRAM HERE]", "Figure 3.2: 3-Tier System Architecture")
        insert_p("Presentation Tier (Client Layer): This tier comprises bifurcated interfaces. The primary student interaction channel is the native Android Application, systematically constructed utilizing Kotlin and the declarative Jetpack Compose framework. Conversely, the Web Admin dashboard strictly leverages the React 19 framework to power responsive, dense-data matrices via DOM manipulations.")
        insert_p("Application Tier (Business Logic Layer): Acting as the central nervous system, this tier revolves unequivocally around the Spring Boot 3.2.5 enterprise framework. Operating via embedded Apache Tomcat HTTP interfaces, it acts as the authoritative REST API dispatcher. This tier houses security middleware, enforces declarative transaction schemas, and controls execution logic orchestrations (via designated Controller-Service-Repository DTO pipelines).")
        insert_p("Data Tier (Persistence Layer): Physical data is segregated fundamentally by access pattern methodologies. Core structured relational persistence relies directly upon the MySQL InnoDB mechanism. In synchronized parallel, the Firebase Cloud Storage bucket governs heavy static Asset management, persisting localized profile pictures alongside cryptographic Signed URL image representations originating from academic submissions.")

        # 3.5 Database
        insert_heading("3.5 Database Design")
        insert_p("A highly optimized physical schema operates as the bedrock for the platform's intensive I/O throughput matrix. Comprising an exact 24-entity mapping topology, the database hinges completely on relational MySQL constraints.")
        insert_p("To systematically manage the complexity of 24 interconnected entities, the Entity Relationship Diagram (Figure 3.3) is analytically segmented into three primary logical data clusters, intercommunicating strictly through rigorous Foreign Key constraints:")
        insert_p("1. User & Authentication Cluster: This encompasses the users, student_profiles, password_reset_tokens, and registration_otps tables. This cluster assumes exclusive responsibility for identity management, personal data isolation, and enforcing the security lifecycle of the account.")
        insert_p("2. Academic Content Cluster: Designed operating a Cascading Tree hierarchical model, this incorporates the sequence of grades -> subjects -> chapters -> lessons and quizzes. This specific schema allows the academic syllabus to be organized systematically. The ON DELETE CASCADE constraint is widely applied within this geographical subset; for instance, if an overarching 'chapter' is administratively purged, all structurally dependent 'lessons' residing underneath it are automatically eradicated, definitively preventing database orphan anomalies.")
        insert_p("3. Interaction & Progress Cluster: This acts as the telemetry recorder for student mechanics, incorporating tables such as lesson_completion, quiz_results, exercise_submissions, and the conversational AI subset (ai_chat_sessions, ai_chat_messages). A profound architectural configuration within this group is the rigid foreign key linkage connecting ai_chat_sessions and exercises (via exercise_id), enabling the overarching system to meticulously trace the specific academic context whenever the student initiates an interaction with the AI Tutor.")
        insert_image(p_chap4, r"e:\Final_Project\docx\ERD.png", "Figure 3.3: System Entity-Relationship Diagram (ERD)")
        
        insert_heading("3.5.1 Data Dictionary", level=3)
        insert_p("To concisely represent the persistence logic, the following tables highlight the fundamental storage mechanics within the critical clusters (omitting repetitive enumerations for brevity):")
        insert_p("- users: Secures universal identity, maintaining global UUIDs, encrypted passwords (BCrypt), and role specifications (ADMIN vs. USER).")
        insert_p("- ai_chat_messages: Pivotal structure enforcing the LLM context. Crucially features the 'role' column governing 'USER' and 'ASSISTANT' permutations.")
        insert_p("- quiz_results: An archetype demonstrating intelligent 3NF denormalization. Selectively aggregates analytical scores dynamically conserving redundant processing cycles.")

        # 3.6 Flowcharts
        insert_heading("3.6 System Flowcharts")
        
        insert_p("3.6.1 Learning Journey Flowchart")
        insert_p("Figure 3.4 illustrates the core business flow of the learning journey on the mobile application. The process begins when the learner accesses a specific chapter; at this point, the application executes an external API call to load the corresponding list of modular lessons from the backend database.")
        insert_p("The first and most critical decision node lies in the system evaluating the boolean condition 'Is an instructional video attached to the lesson?'. This structural design allows the platform to flexibly handle multiple contextual formats: if the lesson contains a video link (such as YouTube via an iframe), the system embeds the multimedia player directly into the interface; conversely, if the lesson is purely theoretical, the UI renders mathematical formulas alongside Rich Text leveraging the native Markwon Android library.")
        insert_p("Parallel to the continuous theoretical ingestion process, the system concurrently provisions an extended execution branch (Extend) allowing the learner to 'Ask the AI Tutor' at any temporal moment. This auxiliary sub-flow assists learners in resolving ambiguous queries instantaneously without navigating away from the active lesson context. Ultimately, assuming standard execution, the workflow concludes at the 'Mark as Complete' step. At this juncture, the application asynchronously triggers an API sequence to update the lesson state, logging the progress into the lesson_completion table. This definitive action not only enables the learner to track individual knowledge progression but also feeds analytical telemetry into the gamification engine to recalculate learning streaks.")
        insert_image(p_chap4, r"e:\Final_Project\docx\flowchart_01.png", "Figure 3.4: Learning Journey Flowchart")

        insert_p("3.6.2 Assignment Submission and AI Grading Flowchart")
        insert_p("Figure 3.5 illustrates the most complex business execution flow of the platform: Assignment Submission and Automated AI Grading... To absolutely guarantee high-fidelity input data for the AI engine, the system enforces a stringent Two-tier Exception Handling mechanism.")
        insert_p("The primary tier of validation executes natively on the Client device (Mobile App): the system mandates the logical heuristic decision node 'Is the captured image sharp and adequately lit?'. If the localized verification algorithm detects a motion-blurred image, the execution flow diverges along the False branch to display a strict alert requesting a resubmission. If the image clears this local validation hurdle (True), the application requests a secure, time-sensitive Signed URL from Firebase, subsequently pushing the image payload directly onto the cloud via an HTTP PUT request.")
        insert_p("The secondary exception tier occurs strictly within the designated server-side AI Validation architecture. Consequent to a verified cloud upload event, the backend infrastructure delegates the image analysis operation directly to the Google Gemini API. At the crucial semantic decision node 'Is the submission valid for AI grading?', the generative model evaluates the visual mathematical context. Should Gemini detect an absence of logic structures, it returns an explicit invalidation flag coupled with an analytical diagnostic string. Conversely, should the submission be definitively validated as mathematically sound, the execution flow proceeds unchallenged into the terminal execution phase: logging the evaluated empirical score into the database.")
        insert_image(p_chap4, r"e:\Final_Project\docx\flowchart_02.png", "Figure 3.5: Assignment Submission Flowchart")

        insert_p("3.6.3 AI Tutor Interaction Flowchart")
        insert_p("Figure 3.6 presents a granular visualization of the conversational execution mechanics. The execution fires instantaneously when the student invokes the chat UI. At initialization, the backend evaluates the primary logical branch point: 'Does a previous chat session exist?'.")
        insert_p("If the condition evaluates to True, it triggers Context Injection, bridging a connection to the database to rebuild the complete conversational logic tree. If False, the system initializes a fresh tabular construct. Concurrently, it performs heuristic string sanitation validating if the user's input string adheres to acceptable format policies. Succeeding the interaction logic, the structural output generated by the LLM is forcibly appended to the ai_chat_messages entity matrix.")
        insert_image(p_chap4, r"e:\Final_Project\docx\flowchart_03.png", "Figure 3.6: AI Tutor Interaction Flowchart")

        # 3.7 Sequence Diagrams
        insert_heading("3.7 Sequence Diagrams")
        insert_p("Sequence diagrams are heavily utilized to structurally demonstrate inter-object communication sequences and object lifecycles across the systemic architecture.")

        insert_p("3.7.1 Authentication Execution Sequence")
        insert_p("Authentication hinges upon the precise transmission of credentials originating from the Mobile application crossing through the explicit Spring Boot Authentication Filter Chain. Upon verification, the backend cryptographic algorithm synthesizes a JSON Web Token (JWT). The Client persists this encrypted JWT exclusively within secure local boundaries, attaching it statically across HTTP authorization headers for subsequent restricted pipeline interactions.")
        insert_image(p_chap4, "[INSERT AUTHENTICATION SEQUENCE DIAGRAM HERE]", "Figure 3.7: Authentication Sequence")

        insert_p("3.7.2 Theory Management Execution Sequence")
        insert_p("Data access trajectories resolving specific chapters heavily prioritize efficient database READ metrics. The Mobile Application constructs specifically parameterized GET requests mapping towards nested architectural RestControllers. In sequence, the encapsulated Domain Data Transfer Objects (DTOs) stream recursively outward from isolated Data Access Objects (DAOs) connected deeply across MySQL.")
        insert_image(p_chap4, "[INSERT THEORY MANAGEMENT SEQUENCE DIAGRAM HERE]", "Figure 3.8: Theory Management Sequence")

        insert_p("3.7.3 Quiz Execution Sequence")
        insert_p("Quiz implementations necessitate continuous multi-way interaction loops. Progress queries calculate array subsets inside isolated memory bounds before orchestrating concluding API submissions executing final static validations.")
        insert_image(p_chap4, "[INSERT QUIZ EXECUTION SEQUENCE DIAGRAM HERE]", "Figure 3.9: Quiz Execution Sequence")

        insert_p("3.7.4 AI Grading Sequence Diagram")
        insert_p("[1. Participants & Lifecycle]\nAs depicted in Figure 3.10, responsibilities correctly distribute across defined functional participants: the Mobile Client, ExerciseController, FirebaseStorageService, and the GeminiValidationService.")
        insert_p("[2. Synchronous & Asynchronous Messages]\nThe execution lifecycle maps synchronous requests initiating from the Mobile Client soliciting upload parameters. Following successful cloud uploads, the Controller invokes internal methods, converting them into asynchronous AI evaluations.")
        insert_p("[3. Fragments & Exceptions]\nInside the analyzed 'alt' fragment, fault tolerance is securely instantiated. Should the remote Google service throw processing interrupts, the server boundary emits HTTP 500 fallback logic prohibiting dangerous memory crashes spanning across user endpoints.")
        insert_image(p_chap4, r"e:\Final_Project\docx\SequenceDIagram.png", "Figure 3.10: AI Grading Sequence Diagram")

        insert_p("3.7.5 AI Tutor Sequence Diagram")
        insert_p("[1. Business Logic & Purpose]\nUnlike static Q&A chatbot routines, the MaSci AI Tutor is engineered around the Socratic Method—commanding the backend platform to maintain contextual conversational memory throughout execution loops.")
        insert_p("[2. Technical Execution]\nThe lifecycle originates within an Optional Fragment (opt block). Here, the AiTutorService validates session continuation identifiers against the persistence matrix. Inside the dominant Loop Fragment, the Backend strictly parses collective arrays binding previous messages sequentially creating optimal JSON injection schemas designed natively for Gemini interpretation. This explicit Context Injection enables deep comprehension spans.")
        insert_p("[3. Exception Mapping]\nFault suppression protocols highlight Alternative Fragments. In timeout scenarios crossing HTTP 503 limits, AiTutorService catches anomalous vectors natively overriding the crash sequences via explicit Spring Exception handlers returning UI-safe error configurations.")
        insert_image(p_chap4, r"e:\Final_Project\docx\SequenceDiagram (2).png", "Figure 3.11: AI Tutor Context Sequence Diagram")

        # 3.8 UI/UX
        insert_heading("3.8 UI/UX Design Specifications")
        insert_p("Defining layout logic enables unparalleled accessibility protocols across fragmented display topologies.")
        
        insert_p("3.8.1 Mobile Application Interface", bold=True)
        insert_p("The native Mobile iteration is exhaustively configured respecting Google's modern Material Design 3 (M3) standards. This aesthetic framework guarantees intrinsic dynamic spacing scaling, heavily optimizing real estate across tablet configurations. Most prominently, the framework natively adopts Dark Mode toggles preserving optical safety. Engineering complexity arises solving Math visual representations: substituting standard text wrappers, the application interfaces deeply via embedded View abstractions natively translating dynamic KaTeX strings resulting in flawlessly crisp inline equation rendering.")
        
        insert_p("3.8.2 Web Administration Portal", bold=True)
        insert_p("Administrative density requires superior structural layout components cleanly achieved through implementing the React Ant Design (AntD) ecosystem, guaranteeing powerful table filtering mechanisms. Crucially, raw input fields are rendered obsolete; instructors strictly engage customized CKEditor 5 panels. This rich-text editing environment implements integrated MathLive extension libraries exposing WYSIWYG mathematical syntaxes immediately overriding grueling manual LaTeX compilations, massively accelerating content digitization workflows.")

        paragraphs_to_delete = doc.paragraphs[chap3_idx+1:chap4_idx]
        for p in paragraphs_to_delete:
            delete_paragraph(p)
            
        output_path = r"e:\Final_Project\docx\MaSci_Final_Report_V11.docx"
        doc.save(output_path)
        print("Success! Chapter 3 has been rebuilt with correct structure and saved to " + output_path)
    else:
        print("Error: Could not find CHAPTER 3 or CHAPTER 4 in the document.")
        
if __name__ == '__main__':
    main()
