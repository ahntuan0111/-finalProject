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
    # We will use V4 as the base, since it is the latest
    doc_path = r"e:\Final_Project\docx\MaSci_Final_Report_V4.docx"
    if not os.path.exists(doc_path):
        print(f"File not found: {doc_path}")
        return
        
    doc = Document(doc_path)
    
    # Locate Chapter 1 and Chapter 2
    chap1_idx = -1
    chap2_idx = -1
    for i, p in enumerate(doc.paragraphs):
        text = p.text.strip().upper()
        if "CHAPTER 1" in text and "INTRODUCTION" in text:
            chap1_idx = i
        elif "CHAPTER 2" in text and ("LITERATURE REVIEW" in text or "THEORETICAL" in text):
            chap2_idx = i
            break
            
    if chap1_idx != -1 and chap2_idx != -1:
        p_chap2 = doc.paragraphs[chap2_idx]
        
        def insert_p(text, bold=False):
            new_p = p_chap2.insert_paragraph_before()
            apply_format(new_p, text, is_bold=bold)
            return new_p
            
        def insert_heading(text):
            new_p = p_chap2.insert_paragraph_before()
            new_p.paragraph_format.space_before = Pt(18)
            new_p.paragraph_format.space_after = Pt(12)
            run = new_p.add_run(text)
            run.font.name = 'Times New Roman'
            run.font.size = Pt(12)
            run.bold = True
            
        insert_heading("1.1 Background to the Study")
        insert_p("Following the COVID-19 pandemic, the trend of online learning in Vietnam has reflected a paradigm shift from traditional educational environments to digital platforms. However, this rapid development has inadvertently widened the gap in educational quality between urban and rural areas. Specifically, schools in remote areas are facing a severe shortage of highly qualified teachers, particularly in natural science subjects such as Mathematics. Students in these regions often lack access to personalized learning resources.")
        insert_p("Simultaneously, the boom of Generative AI (such as ChatGPT and Gemini launched between 2023 and 2024) has created unprecedented opportunities for Personalized Learning. The systematic application of Artificial Intelligence into E-Learning not only helps bridge the gap in the quantity and quality of educators but also provides a highly interactive learning environment. Recognizing this potential, the MaSci system is proposed as an urgent and comprehensive solution specifically designed for secondary school students, fostering equity in educational opportunities.")

        insert_heading("1.2 Problem Statement")
        insert_p("The current state of online education for secondary school students presents several critical barriers:")
        insert_p("- Lack of 1-on-1 interaction: During self-study, students face an absence of personalized support. When encountering a difficult math problem, traditional systems cannot act as a tutor to analyze and suggest the right approach.")
        insert_p("- Technological barrier in entering mathematical formulas: Typing math formulas via standard keyboards is extremely inefficient. Students often have to spend considerable time learning complex syntax like LaTeX rather than focusing on mathematical reasoning, creating fatigue and a significant technological barrier.")
        insert_p("- Limitations in grading handwritten essays: Multiple-choice exercises fail to evaluate the step-by-step logic and methodology of students. Furthermore, handwritten essay assignments cannot be graded quickly remotely because teachers do not have enough time to manually review every submitted image.")
        insert_p("- Disjointed content management: The management of learning resources and the creation of questions containing mathematical formulas by teachers and administrators are fragmented, consuming a large amount of time for maintenance and updates.")

        insert_heading("1.3 Objectives of the Study")
        insert_p("The core objective of the project is to build a multifaceted E-Learning platform (comprising a Web Admin, an Android Mobile App, and a Backend Server) to materialize the following functions:")
        insert_p("- Develop a high-performance Web Admin portal to help teachers digitize multimedia theory and organize tests effortlessly.")
        insert_p("- Build a user-friendly Mobile App for students to easily access lectures and submit assignments.")
        insert_p("- Integrate a Socratic AI Tutor to provide intelligent, context-aware feedback based on conversation history.")
        insert_p("- Utilize multimodal Artificial Intelligence to analyze and recognize images of handwritten exercises, grading them on a step-by-step basis.")

        insert_heading("1.4 Research Questions")
        insert_p("To guide the analysis and development process, this study poses the following focal questions:")
        insert_p("1. How can a 3-tier architecture (Android, React, Spring Boot) be optimized for an e-learning platform?")
        insert_p("2. To what extent can multimodal AI (Google Gemini) accurately grade handwritten mathematical exercises?")
        insert_p("3. How does the integration of a Socratic AI Tutor impact the self-study experience of secondary school students?")

        insert_heading("1.5 Significance of the Study")
        insert_p("The study yields profound significance across four primary vectors:")
        insert_p("- For Students (Student): Provides a 24/7 AI tutor, personalizes the learning path, and offers immediate math-solving support via image captures.")
        insert_p("- For Teachers (Teacher): Reduces the workload of manually grading essay assignments and provides a standardized, scientific content authoring system.")
        insert_p("- For Society (Society): Shortens the gap in access to high-quality education among different geographical regions.")
        insert_p("- For Technical Applications (Technical): Affirms the performance of combining modern architectures (Spring Boot, Jetpack Compose, React) with Generative AI models through complex APIs.")

        insert_heading("1.6 Scope and Limitation")
        insert_p("Scope: The scope of the study focuses on secondary school students (grades 6 to 9) in Vietnam. The platform is built for mobile education on the Android operating system using Kotlin, with user interfaces designed via Jetpack Compose. The administrative side utilizes a Web Admin developed with React 19, and the Backend is powered by Spring Boot 3.2.5.")
        insert_p("Limitation: Currently, the system primarily focuses on Android devices and does not yet support iOS (Apple) users. Furthermore, the system has not integrated an online Payment Gateway for internal transactions.")

        insert_heading("1.7 Implementation Plan")
        insert_p("The project is executed following the Agile Scrum methodology over a 16-week timeline. The development is divided into distinct Sprints, covering Requirements Gathering, Architectural Design, Backend Implementation, AI Integration, and User Acceptance Testing. A detailed Gantt chart illustrating this timeline is presented in Chapter 3.")

        insert_heading("1.8 Definition of Terms")
        insert_p("To optimize the reading experience, the following technical terms are briefly defined:")
        insert_p("- Multimodal AI: Artificial intelligence models capable of simultaneously understanding and processing multiple forms of input data such as text, images, and audio (e.g., Gemini AI).")
        insert_p("- Socratic Method: An educational approach based on asking open-ended, thought-provoking questions, utilized by the AI Tutor to guide students toward discovering the answers themselves rather than immediately providing the final result.")
        insert_p("- JWT (JSON Web Token): An open standard representing secure claims between two parties, commonly used to authenticate users in software systems.")
        insert_p("- Jetpack Compose: A modern declarative UI toolkit used for natively building Android application interfaces.")
        insert_p("- Single Page Application (SPA): A web application that loads a single HTML page and dynamically updates the interface components (used for the React Admin system).")
        insert_p("- Gamification: The application of game-design elements and mechanics (such as the Streak progress tracking system) in learning processes to enhance competitiveness and motivation.")

        paragraphs_to_delete = doc.paragraphs[chap1_idx+1:chap2_idx]
        for p in paragraphs_to_delete:
            delete_paragraph(p)
            
        output_path = r"e:\Final_Project\docx\MaSci_Final_Report_V5.docx"
        doc.save(output_path)
        # Avoid printing unicode chars to prevent Windows console encoding errors!
        print("Success! Chapter 1 has been translated to English and saved to " + output_path)
    else:
        print("Error: Could not find CHAPTER 1 or CHAPTER 2 in the document.")
        
if __name__ == '__main__':
    main()
