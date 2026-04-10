import os
import sys

try:
    import docx
except ImportError:
    import subprocess
    subprocess.check_call([sys.executable, "-m", "pip", "install", "python-docx"])
    import docx

from docx import Document
from docx.shared import Pt, Cm, Inches
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
        apply_format(new_p, f"[IMAGE NOT FOUND: {img_path}]", is_bold=True)
        new_p.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER

def main():
    doc_path = r"e:\Final_Project\docx\MaSci_Final_Report_V9.docx"
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
        
        def insert_p(text, bold=False, center=False):
            new_p = p_chap4.insert_paragraph_before()
            run = apply_format(new_p, text, is_bold=bold)
            if center:
                new_p.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
            return new_p
            
        def insert_heading(text):
            new_p = p_chap4.insert_paragraph_before()
            new_p.paragraph_format.space_before = Pt(18)
            new_p.paragraph_format.space_after = Pt(12)
            run = new_p.add_run(text)
            run.font.name = 'Times New Roman'
            run.font.size = Pt(12)
            run.bold = True

        insert_heading("3.1 System Requirements")
        insert_p("In the development of the MaSci platform, system requirements are meticulously gathered and categorized into Functional Requirements (defining what the system must do from the user's perspective) and Non-functional Requirements (defining system attributes such as speed, security, and AI accuracy). These requirements act as the foundational blueprint guiding all subsequent conceptual and technical architectural decisions.")

        insert_heading("3.2 System Flowcharts")
        insert_p("Flowcharts are extensively utilized to map out the sequential operational logic of complex business processes within the MaSci framework. The detailed deep-dive analysis of these graphical representations ensures a comprehensive understanding of module interaction, systematic branching, and critical path validations.")
        
        insert_p("3.2.1 Learning Journey Flowchart")
        insert_p("Figure 3.1 illustrates the core business flow of the learning journey on the mobile application. The process begins when the learner accesses a specific chapter; at this point, the application executes an external API call to load the corresponding list of modular lessons from the backend database.")
        insert_p("The first and most critical decision node lies in the system evaluating the boolean condition 'Is an instructional video attached to the lesson?'. This structural design allows the platform to flexibly handle multiple contextual formats: if the lesson contains a video link (such as YouTube via an iframe), the system embeds the multimedia player directly into the interface; conversely, if the lesson is purely theoretical, the UI renders mathematical formulas alongside Rich Text leveraging the native Markwon Android library.")
        insert_p("Parallel to the continuous theoretical ingestion process, the system concurrently provisions an extended execution branch (Extend) allowing the learner to 'Ask the AI Tutor' at any temporal moment. This auxiliary sub-flow assists learners in resolving ambiguous queries instantaneously without navigating away from the active lesson context. Ultimately, assuming standard execution, the workflow concludes at the 'Mark as Complete' step. At this juncture, the application asynchronously triggers an API sequence to update the lesson state, logging the Boolean progress into the 'lesson_completion' table within the MySQL database. This definitive action not only enables the learner to track individual knowledge progression but also feeds analytical telemetry into the gamification engine to recalculate learning streaks and competitive achievement vectors.")
        insert_image(p_chap4, r"e:\Final_Project\docx\flowchart_01.png", "Figure 3.1: Learning Journey Flowchart")

        insert_p("3.2.2 Assignment Submission and AI Grading Flowchart")
        insert_p("Figure 3.2 illustrates the most complex business execution flow of the platform: Assignment Submission and Automated AI Grading. The initialization process occurs when a student dynamically accesses a specifically targeted scholastic exercise. At this juncture, the system provides a supplementary decision branch (Extend) permitting the learner to retrieve 'Exercise Hints' if they encounter an intellectual impasse, a mechanism explicitly engineered to maintain cognitive motivation before formally proceeding to solve the problem.")
        insert_p("The gravitational center of this operational sequence lies within the structural image upload and validation protocol. To absolutely guarantee high-fidelity input data for the AI engine, the system enforces a stringent Two-tier Exception Handling mechanism. The primary tier of validation executes natively on the Client device (Mobile App): the system mandates the logical heuristic decision node 'Is the captured image sharp and adequately lit?'. If the localized verification algorithm detects a motion-blurred image, the execution flow diverges along the False branch to display a strict alert requesting a resubmission, thereby blocking the transmission of unreadable payload data to the server. If the image clears this local validation hurdle (True), the application actively dodges routing the heavy binary file directly through Backend bottlenecks; instead, it requests a secure, time-sensitive Signed URL from Firebase, subsequently pushing the image payload directly onto the cloud via an HTTP PUT request.")
        insert_p("The secondary exception tier occurs strictly within the designated server-side AI Validation architecture. Consequent to a verified cloud upload event, the backend infrastructure delegates the image analysis operation directly to the Google Gemini API. At the crucial semantic decision node 'Is the submission valid for AI grading?', the generative model evaluates the visual mathematical context. Should Gemini detect an absence of logic structures or if the handwritten proportion is excessively minimal, it returns an explicit invalidation flag coupled with an analytical diagnostic string. The system terminates this specific execution sub-flow gracefully, displaying the translated error onto the Mobile UI to enforce a manual retry scenario. Conversely, should the submission be definitively validated as mathematically sound, the execution flow proceeds unchallenged into the terminal execution phase: logging the evaluated empirical score, saving the image URI reference, and persisting the AI's textual feedback directly into the 'exercise_submissions' database table prior to unlocking the 'Sample Solution' functionality for comparative learning.")
        insert_image(p_chap4, r"e:\Final_Project\docx\flowchart_02.png", "Figure 3.2: Assignment Submission and AI Grading Flowchart")

        insert_p("3.2.3 AI Tutor Interaction Flowchart")
        insert_p("Figure 3.3 presents a granular visualization of the conversational execution mechanics governing the AI Tutor Interaction stream. Similar to the preceding flowcharts, analyzing the specific trigger, nodes, and downstream database impacts clarifies system operations.")
        insert_p("Trigger & Decision Nodes: The execution fires instantaneously when the student invokes the chat UI. At initialization, the backend evaluates the primary logical branch point: 'Does a previous chat session exist?'. If the condition evaluates to True, it triggers Context Injection, bridging a connection to the database to rebuild the complete conversational logic tree. If False, the system initializes a fresh tabular construct. Concurrently, it performs heuristic string sanitation validating if the user's input string adheres to acceptable format policies.")
        insert_p("Database Impact: Succeeding the interaction logic, the structural output generated by the LLM is forcibly appended to the 'ai_chat_messages' entity matrix, solidifying the operational memory state of the current instructional context.")
        insert_image(p_chap4, r"e:\Final_Project\docx\flowchart_03.png", "Figure 3.3: AI Tutor Interaction Flowchart")

        insert_heading("3.3 Sequence Diagrams")
        insert_p("Sequence diagrams are heavily utilized to structurally demonstrate inter-object communication sequences and object lifecycles across the systemic architecture. This visualization separates external participants, controllers, and database services.")

        insert_p("3.3.1 AI Grading Sequence Diagram")
        insert_p("[1. Participants & Lifecycle]\nAs depicted in Figure 3.4, the system architecture explicitly distributes responsibilities across defined functional participants: the Mobile Client, 'ExerciseController', 'FirebaseStorageService', 'GeminiValidationService', and the underlying MySQL framework.")
        insert_p("[2. Synchronous & Asynchronous Messages]\nThe execution lifecycle maps synchronous HTTP GET requests initiating from the Mobile Client soliciting upload parameters. Following successful cloud uploads via PUT requests, the 'ExerciseController' invokes internal methods, converting them into asynchronous AI evaluations.")
        insert_p("[3. Fragments & Exceptions]\nInside the analyzed 'alt' fragment, fault tolerance is strictly implemented. If the Firebase ecosystem fails or rejects the identity credential, the Spring container intercepts the boundary failure, emitting an HTTP 500 status payload ensuring the application does not maliciously crash on client terminals.")
        insert_image(p_chap4, r"e:\Final_Project\docx\SequenceDIagram.png", "Figure 3.4: AI Grading Sequence Diagram")

        insert_p("3.3.2 AI Tutor Sequence Diagram")
        insert_p("[1. Business Logic & Purpose]\nThe Sequence Diagram (Figure 3.5) meticulously maps the interaction flow between the student device and the Artificial Intelligence operating under the AI Tutor subsystem. Fundamentally distinct from static Q&A chatbot routines, the MaSci AI Tutor is engineered around the Socratic Method—a paradigm commanding the system to persistently maintain contextual conversational memory to dispense highly personalized, interrogative guidance rather than instantaneously rendering absolute final answers.")
        insert_p("[2. Step-by-Step Technical Execution & Data Flow]\nThe technical lifecycle originates within an Optional Fragment (opt block). In this fragment, the 'AiTutorService' runs an explicit existence check regarding the current chat session. If this interaction comprises the very first initialization, the system actively instantiates and commits a novel record within the 'ai_chat_sessions' entity. Should a session logically pre-exist, the service queries the entire holotypic message history from the 'ai_chat_messages' table, loading it efficiently into operational memory constraints.")
        insert_p("Operating across the critical Loop Fragment (loop block), an architectural necessity becomes apparent. Whenever the Mobile App pushes a new message payload via the 'AiTutorController', the Backend never dispatches that isolated string directly toward Gemini. Instead, the 'AiTutorService' executes a crucial serialization protocol, packaging the entire accumulated chat history array (Context Window) into a strictly sanitized Google Gemini JSON payload specification. This Context Injection subroutine, whilst consuming more bandwidth with every repetitive cycle, is irrevocably compulsory; it instructs the Large Language Model (LLM) to inherit the student's preceding cognitive trajectory. Post-inference, the system concurrently saves both the user's prompt (role: USER) and the LLM's response (role: ASSISTANT) down into the fundamental database repositories.")
        insert_p("[3. Exception Handling & Edge Cases]\nFault Tolerance and Graceful Degradation are explicitly exhibited within the Alternative Fragment (alt block). In the event that the external network connection governing the Gemini API infrastructure encounters an operational failure—manifesting as timeout scenarios or HTTP 503 downstream faults—the 'AiTutorService' actively catches the propagating operational anomaly. Rather than terminating the thread and crashing the application stack, the service generates a controlled internal exception, commanding the 'AiTutorController' to propagate a secure HTTP 500 error payload to the Client. The mobile boundary intercepts this network code, displaying a resilient UX warning ('The AI Tutor is currently busy, please try again'), strictly preserving user interaction flow.")
        insert_p("[4. Architectural Justification]\nThe conscious architectural decision to persist unstructured exhaustive conversational memory within a strict relational schema (MySQL InnoDB) rather than deferring to schema-less NoSQL clusters (like MongoDB) or volatile caching layers is a calculated tradeoff favoring referential integrity. Within complex educational ecosystems, dialog context inherits rigid relational schemas directly linked to global User IDs and specialized mathematical Exercise IDs. Adhering to MySQL empowers the system to exploit robust Foreign Key rules, preventing data obsolescence and accommodating vast cross-table analytical heuristics crucial for subsequent big-data pedagogical assessments.")
        insert_image(p_chap4, r"e:\Final_Project\docx\SequenceDiagram (2).png", "Figure 3.5: AI Tutor Sequence Diagram")

        insert_heading("3.4 Database Design and Entity-Relationship")
        insert_p("A highly optimized physical schema operates as the bedrock for the platform's intensive I/O throughput matrix. Comprising an expansive 24-entity mapping visualized in Figure 3.6, the database hinges completely on relational MySQL constraints.")
        insert_p("Data Normalization and Redundancy Minimization:\nThe central tenet of the physical database design strictly enforces operational compliance with the Third Normal Form (3NF), deliberately structured to mathematically minimize arbitrary data redundancy constraints. A prime analytical example is localized within the 'quiz_results' entity. The architecture is configured to definitively capture and statically persist the final normalized integer associated with a student's quantitative 'score'. Rather than forcing the server runtime logic to perpetually aggregate answers sequentially across hundreds of child entities during every distinct query request triggered by the Dashboard load event, utilizing targeted denormalization parameters dramatically accelerates the fundamental query operation rates.")
        insert_p("Academic Content Cluster & Referential Integrity:\nTo effectively manage the scale of these 24 tables, the schema (Figure 3.6) is logically delineated into distinct communicating clusters. Within the central Academic Cluster, the layout deploys a Cascading Tree hierarchical archetype (streaming deterministically through 'grades' -> 'subjects' -> 'chapters' -> 'lessons' and 'quizzes'). A paramount architectural fortification is the strict and widespread implementation of the 'ON DELETE CASCADE' framework spanning these foreign keys. Analytically, if an administrative action purges an overarching 'chapter' entity, all intrinsically dependent 'lessons' attached identically inherit the deletion vector. This ensures the eradication process cascades precisely across the ecosystem, neutralizing the absolute possibility of orphan data records polluting the persistence layer.")
        insert_p("Interaction & Progress Cluster:\nThe far-right layout visualizes the User and Progress telemetry ecosystem, clustering tables such as 'lesson_completion', 'quiz_results', 'exercise_submissions', alongside AI dialog histories ('ai_chat_sessions', 'ai_chat_messages'). A vital architectural decision materialized inside the dialogue constraints is the distinct implementation of the 'role' field boundary identifier inside 'ai_chat_messages' (delineating precisely between generic 'USER' inputs and processed 'ASSISTANT' machine outputs). This isolated column ensures the Spring partition logic accurately packages human context versus historical machine conclusions prior to delegating the array over the multimodal transmission payload layer.")
        insert_image(p_chap4, r"e:\Final_Project\docx\ERD.png", "Figure 3.6: System Entity-Relationship Diagram (ERD)")

        paragraphs_to_delete = doc.paragraphs[chap3_idx+1:chap4_idx]
        for p in paragraphs_to_delete:
            delete_paragraph(p)
            
        output_path = r"e:\Final_Project\docx\MaSci_Final_Report_V10.docx"
        doc.save(output_path)
        print("Success! Chapter 3 has been expanded with detailed UML analysis and saved to " + output_path)
    else:
        print("Error: Could not find CHAPTER 3 or CHAPTER 4 in the document.")
        
if __name__ == '__main__':
    main()
