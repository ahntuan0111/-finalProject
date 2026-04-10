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
    doc_path = r"e:\Final_Project\docx\MaSci_Final_Report_V8.docx"
    if not os.path.exists(doc_path):
        print(f"File not found: {doc_path}")
        return
        
    doc = Document(doc_path)
    
    # Locate Chapter 3 and Chapter 4
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
        insert_p("Flowcharts are extensively utilized to map out the sequential operational logic of complex business processes within the MaSci framework. The detailed analysis of these graphical representations ensures a comprehensive understanding of module interaction, systematic branching, and critical path validations.")
        
        insert_p("3.2.1 Learning Journey Flowchart")
        insert_p("The logical sequence for the learning flow, as illustrated in Figure 3.1, initiates precisely when the student selects a specific academic subject via the Mobile graphical user interface. The initial trigger executes an asynchronous data retrieval process, fetching the structured list of subsequent chapters and lessons directly from the core database. The entire user experience dynamically adapts based on the central decision node evaluating: 'Is a video attached to the lesson?'. If this condition evaluates to true, the application transitions along the affirmative execution path to dynamically inject a native video player. Conversely, the negative path simply renders the lesson in a multimedia-rich text format.")
        insert_p("Furthermore, throughout the active learning phase, the system provides an auxiliary Extended process branch, manifesting as an 'Ask AI Tutor' pathway. This crucial decision node enables students to pause theoretical ingestion to solicit real-time clarification for unfamiliar concepts. Ultimately, assuming standard execution, the workflow concludes at the 'Mark as Complete' termination state. At this terminal point, a synchronous mutation occurs as the application writes an update payload to the 'user_progress' database table, effectively confirming the lesson completion and triggering consequential state changes in the Gamification Streak metrics.")
        insert_image(p_chap4, r"e:\Final_Project\docx\flowchart_01.png", "Figure 3.1: Learning Journey Flowchart")

        insert_p("3.2.2 Assignment Submission and AI Grading Flowchart")
        insert_p("Figure 3.2 illustrates the most complex business flow of the platform: Assignment Submission and Automated AI Grading. The initialization process occurs when a student accesses a specific targeted exercise. At this juncture, the system provisions an auxiliary decision branch (Extend) that permits the learner to retrieve 'Exercise Hints' if they encounter an intellectual impasse. This branch is instrumentally designed to maintain cognitive motivation and persistence before the student formally proceeds to solve the problem.")
        insert_p("The gravitational center of this operational sequence lies within the image upload and validation protocol. To absolutely guarantee high-fidelity input data for the Multimodal AI engine, the system enforces a stringent Two-tier Exception Handling mechanism. The primary tier of validation executes natively on the Client device (Mobile App). The system mandates the logical decision node: 'Is the captured image sharp and adequately lit?'. If the localized heuristic verification algorithm detects an obfuscated or intolerably blurry image, the execution flow diverges immediately along the False branch. This action interrupts the sequence to render an explicit UI dialogue commanding the user to discard the asset and recapture the handwriting. This proactive interception completely prevents the transmission of corrupted payload data to the server, preserving crucial operational bandwidth. If the image successfully clears this local heuristic standard (True), the application conspicuously avoids routing the dense binary file directly through the Backend server memory. Instead, it utilizes an asynchronous trigger to request a secure, time-limited Signed URL from Firebase. Upon successful token receipt, the client pushes the raw image file directly to the Cloud storage bucket via a dedicated HTTP PUT request.")
        insert_p("The secondary exception tier occurs strictly within the designated AI Validation block on the server side. Subsequent to a verified cloud upload event, the backend infrastructure delegates the operation to the Google Gemini API for profound visual-linguistic analysis. At the critical decision node defining 'Is the submission valid for AI grading?', the model evaluates the semantic content of the image. Should Gemini deduce an absence of mathematical methodology, or if the proportional density of textual handwriting is insignificantly small, the external service returns a predefined invalidation flag paired with a comprehensive diagnostic string. The backend propagates this exception gracefully, terminating the execution sub-flow and translating the error onto the Mobile UI context to enforce a retry scenario. Conversely, should the submission be definitively validated, the process flows unhindered into the terminal execution phase: logging the evaluated empirical score, persisting the absolute image URI reference, and saving the complex textual feedback provided by the Artificial Intelligence directly into the 'exercise_submissions' relational database table. Only upon successful transaction commit does the client UI unlock the comparative 'Sample Solution' for the student.")
        insert_image(p_chap4, r"e:\Final_Project\docx\flowchart_02.png", "Figure 3.2: Assignment Submission and AI Grading Flowchart")

        insert_p("3.2.3 AI Tutor Interaction Flowchart")
        insert_p("Figure 3.3 presents a granular visualization of the conversational execution mechanics governing the AI Tutor Interaction stream. The execution trigger fires instantaneously when the student invokes the dedicated chat UI. Immediately, the backend service encounters the primary logical branch point: 'Does a previous chat session already exist for this context?'.")
        insert_p("If the condition evaluates to True, it triggers a critical sequential process known as Context Injection. The system establishes a direct linkage with the database repository (symbolized by the primary sequence blocks) to comprehensively pull and rebuild the entire chronologic conversational history. This continuous loop mechanism mathematically guarantees that all subsequent semantic responses dynamically generated by the Large Language Model (LLM) are strictly contextualized by the preceding interaction memory. If the initial existence validation equates to False, the system provisions a brand new session object, commits it to the database, and begins populating the empty conversation array.")
        insert_image(p_chap4, r"e:\Final_Project\docx\flowchart_03.png", "Figure 3.3: AI Tutor Interaction Flowchart")

        insert_heading("3.3 Sequence Diagrams")
        insert_p("Sequence diagrams are leveraged to explicitly demonstrate the exact inter-object communication sequences and object lifecycles across the systemic architecture. This critical visualization meticulously segregates system participants, delineating communication vectors between Mobile UI clients, specific backend Controller endpoints, and autonomous external service layers.")

        insert_p("3.3.1 AI Grading Sequence Diagram")
        insert_p("The system architecture distributes responsibilities among participants clearly separated into external APIs, Controller logic, and core Business Services. The execution lifecycle, mapped in Figure 3.4, begins with the Mobile Client initiating a synchronous HTTP GET request towards the 'ExerciseController' to procure an upload token. The 'FirebaseStorageService' acts as the principal intermediary component dedicated exclusively to handling Firebase Cloud integrations, ultimately returning the signed access token.")
        insert_p("A paramount examination point in this flow relies upon the operational handoff following the image payload transmission. Once the image resides in the Cloud, the 'ExerciseController' invokes the specialized 'GeminiValidationService'. This component constructs the complex multimodal payload and facilitates the asynchronous HTTP POST request directed at the Gemini API. Inside the evaluated 'alt' fragment, fault tolerance is strictly defined. If Google's servers time out or respond with a 500 Internal Error, the service intercepts the failure without crashing, relaying a 500 error code down the execution stack so the Client UI can react smoothly. Otherwise, succeeding processing pushes the computed score and evaluative string down into the core 'ExerciseService' for final transactional commitment.")
        insert_image(p_chap4, r"e:\Final_Project\docx\SequenceDIagram.png", "Figure 3.4: AI Grading Sequence Diagram")

        insert_p("3.3.2 AI Tutor Sequence Diagram")
        insert_p("As depicted in Figure 3.5, the conversational lifecycle between the Client interaction and the 'AiTutorController' encapsulates numerous structural fragments. Focusing initially on the 'opt' (Optional) boundary condition at the start: when a new dialog commences, the 'AiTutorService' runs an existence check. If the interacting User does not possess a previously established conversational session associated with the target topic, the service forcefully navigates into the database integration layer, writing a new instantiation record into the 'ai_chat_sessions' entity. Conversely, if preexisting states are detected, it retrieves historical 'ai_chat_messages' elements, pushing them into short-lived operational memory.")
        insert_p("Examiners should heavily note the structural execution within the central 'loop' diagram fragment. Throughout this continuous communication loop, whenever the mobile user dispatches a fresh text payload, the 'AiTutorService' never transmits the isolated String to the LLM. Instead, structural logic dictates that the backend must fetch, aggregate, and definitively serialize the entire conversational Context Window into an extensive JSON array. Although this serialization process consumes larger communication bandwidth iteratively, it is absolutely compulsory. Without it, the stateless LLM would instantaneously lose continuity, fundamentally breaking the Socratic questioning methodology that the educational platform promises.")
        insert_p("Furthermore, examining the final 'alt' (Alternative) Exception Handling framework reveals robust Graceful Degradation behaviors. Subordinate to the 'loop', if the external Gemini API network connection faults—returning timeout states or 503 Service Unavailable HTTP codes—the 'AiTutorService' possesses explicit try-catch implementations. It intercepts the propagating exception, halting application crashes instantly. Rather than hanging maliciously, it cascades a normalized error payload back to the Client, which consequently renders a non-intrusive warning user interface, strictly keeping the User Session intact.")
        insert_image(p_chap4, r"e:\Final_Project\docx\SequenceDiagram (2).png", "Figure 3.5: AI Tutor Sequence Diagram")

        insert_heading("3.4 Database Design and Entity-Relationship")
        insert_p("A highly optimized physical schema serves as the foundation for the platform's high-speed querying logic. Comprising an expansive 24-table topography mapped within Figure 3.6, the database relies entirely on the robust MySQL InnoDB clustering storage engine.")
        insert_p("Data Normalization and Optimization:")
        insert_p("The database architecture strictly enforces the principles of Third Normal Form (3NF) to methodically eliminate data redundancy and modification anomalies. For instance, analyzing the 'quiz_results' entity reveals that the schema directly persists the final 'score' integer associated with the completed user attempt. Instead of granularly saving and mathematically recalculating logic gates across hundreds of individual quiz question submissions every time the Mobile Application requests a Dashboard refresh, this denormalization strategy is selectively employed to dramatically optimize READ query acceleration and decrease CPU resource loading on the Spring Boot Server.")
        insert_p("The Academic Relationship Cluster:")
        insert_p("Within the central zone of the ERD, a highly structured Academic Cascading Tree defines the curriculum hierarchy. The model utilizes strict One-to-Many (1:N) cardinalities sequentially flowing downward: from broadly defined 'grades' to specialized 'subjects', cascading into module 'chapters', and finally subdividing into atomic 'lessons'. A profound aspect of this diagram rests upon the structural integrity rules defined by the solid relationship connectors. These connectors represent non-nullable Foreign Keys rigidly fortified by 'ON DELETE CASCADE' constraint mechanisms. In analytical terms, this guarantees strict Referential Integrity—if an administrative user were to systematically purge an obsolete 'subject' record, the database engine inherently guarantees that all descendant 'chapters' and 'lessons' are concurrently annihilated, completely mitigating the risk of orphaned instructional data plaguing the application.")
        insert_p("The Interaction and User State Cluster:")
        insert_p("The far-right periphery of the diagram showcases the structural mapping of the highly dynamic conversational states involving the AI ecosystem, heavily reliant on the 'ai_chat_sessions' and 'ai_chat_messages' entities. The defining analytical property inside the 'ai_chat_messages' entity is the presence of the critical 'role' data field (configured natively to persist enums such as 'USER' or 'ASSISTANT'). This precise architectural choice is mandatory. When the Spring Boot Backend reconstructs the historical payload before transmitting to the Google Gemini API, it relies entirely upon iterating through queries filtered by this 'role' field. This structural logic is what effectively distinguishes human queries from prior machine conclusions, ensuring fluid and logically correct AI conversation generation.")
        insert_image(p_chap4, r"e:\Final_Project\docx\ERD.png", "Figure 3.6: System Entity-Relationship Diagram (ERD)")

        paragraphs_to_delete = doc.paragraphs[chap3_idx+1:chap4_idx]
        for p in paragraphs_to_delete:
            delete_paragraph(p)
            
        output_path = r"e:\Final_Project\docx\MaSci_Final_Report_V9.docx"
        doc.save(output_path)
        print("Success! Chapter 3 has been expanded with detailed UML analysis and saved to " + output_path)
    else:
        print("Error: Could not find CHAPTER 3 or CHAPTER 4 in the document.")
        
if __name__ == '__main__':
    main()
