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
    doc_path = r"e:\Final_Project\docx\MaSci_Final_Report_V3.docx"
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
        # We will insert new chapter 1 content right before Chapter 2
        p_chap2 = doc.paragraphs[chap2_idx]
        
        # New Content Generation
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
        insert_p("Sau đại dịch COVID-19, xu hướng học tập trực tuyến tại Việt Nam đã phản ánh một sự chuyển đổi mạnh mẽ từ môi trường giáo dục truyền thống sang các nền tảng kỹ thuật số. Tuy nhiên, sự phát triển này đã vô tình nới rộng khoảng cách chất lượng giáo dục giữa khu vực thành thị và nông thôn. Cụ thể, các trường học tại các vùng sâu vùng xa đang đối mặt với tình trạng thiếu hụt trầm trọng giáo viên có chuyên môn cao, đặc biệt là các môn khoa học tự nhiên như Toán. Học sinh ở những khu vực này thường không tiếp cận được nguồn tài nguyên học tập cá nhân hóa.")
        insert_p("Đồng thời, sự bùng nổ của Generative AI (như ChatGPT, Gemini ra mắt trong giai đoạn 2023-2024) đã tạo ra cơ hội chưa từng có để cá nhân hóa giáo dục (Personalized Learning). Việc ứng dụng Trí tuệ Nhân tạo một cách có hệ thống vào E-Learning không chỉ giúp lấp đầy khoảng trống về số lượng và chất lượng giáo viên mà còn cung cấp một môi trường học tập tương tác cao. Nhận thấy tiềm năng này, hệ thống MaSci được đề xuất như một giải pháp cấp bách dành riêng cho học sinh trung học cơ sở, đem lại sự công bằng trong cơ hội học tập.")

        insert_heading("1.2 Problem Statement")
        insert_p("Thực trạng hiện nay trong giáo dục trực tuyến dành cho học sinh trung học cơ sở đang tồn tại nhiều rào cản nghiêm trọng:")
        insert_p("- Thiếu sự tương tác 1-1: Học sinh khi tự học đối mặt với việc thiếu sự hỗ trợ cá nhân hóa. Khi gặp bài toán khó, hệ thống truyền thống không thể đóng vai trò như một người gia sư để phân tích và gợi ý hướng đi đúng đắn.")
        insert_p("- Rào cản công nghệ khi nhập công thức toán học: Việc nhập công thức toán qua bàn phím là cực kỳ kém hiệu quả. Học sinh thường phải tốn nhiều thời gian học cú pháp phức tạp như LaTeX thay vì tập trung vào tư duy giải toán, tạo ra sự mệt mỏi và rào cản công nghệ lớn.")
        insert_p("- Hạn chế trong việc chấm bài tự luận: Các bài tập trắc nghiệm (Multiple-choice) không thể đánh giá được logic từng bước (step-by-step methodology) của học sinh. Bài tập viết tay tự luận thì lại không được kiểm tra nhanh chóng từ xa vì giáo viên không có đủ thời gian chấm từng ảnh chụp bài làm.")
        insert_p("- Quản lý nội dung rời rạc: Việc quản lý tài nguyên học tập, tạo câu hỏi có chứa công thức toán học từ phía giáo viên và hệ thống quản trị diễn ra rời rạc, làm tốn nhiều công sức bảo trì và cập nhật.")

        insert_heading("1.3 Objectives of the Study")
        insert_p("Mục tiêu cốt lõi của dự án là xây dựng nền tảng E-Learning đa diện (Web Admin, Mobile App Android và Backend Server) nhằm cụ thể hóa các chức năng sau:")
        insert_p("- Phát triển Website Quản trị (Web Admin) hiệu năng cao giúp giáo viên số hóa lý thuyết đa phương tiện và tổ chức bài kiểm tra dễ dàng.")
        insert_p("- Xây dựng Ứng dụng Di động (Mobile App) thân thiện để học sinh tiếp cận bài giảng và làm bài.")
        insert_p("- Tích hợp AI Tutor cung cấp phản hồi thông minh dựa trên ngữ cảnh và lịch sử hội thoại.")
        insert_p("- Ứng dụng Trí tuệ Nhân tạo đa phương thức để phân tích, nhận dạng ảnh chụp bài tập viết tay và chấm điểm từng bước.")

        insert_heading("1.4 Research Questions")
        insert_p("Để định hướng cho quá trình phân tích và phát triển, nghiên cứu này đặt ra các câu hỏi trọng tâm sau:")
        insert_p("1. How can a 3-tier architecture (Android, React, Spring Boot) be optimized for an e-learning platform?")
        insert_p("2. To what extent can multimodal AI (Google Gemini) accurately grade handwritten mathematical exercises?")
        insert_p("3. How does the integration of a Socratic AI Tutor impact the self-study experience of secondary school students?")

        insert_heading("1.5 Significance of the Study")
        insert_p("Nghiên cứu mang lại ý nghĩa sâu sắc trên 4 phương diện chính (Vectors):")
        insert_p("- Đối với Học sinh (Student): Mang đến gia sư AI 24/7, cá nhân hóa lộ trình học và hỗ trợ giải toán ngay lập tức qua ảnh chụp.")
        insert_p("- Đối với Giáo viên (Teacher): Giảm bớt khối lượng công việc chấm bài tự luận thủ công và cung cấp hệ thống soạn thảo nội dung khoa học chuẩn hóa.")
        insert_p("- Đối với Xã hội (Society): Rút ngắn khoảng cách tiếp cận giáo dục chất lượng cao giữa các khu vực địa lý khác nhau.")
        insert_p("- Đối với Kỹ thuật (Technical): Khẳng định hiệu năng của sự kết hợp giữa các kiến trúc hiện đại (Spring Boot, Compose, React) với các mô hình Generative AI qua các API phức hợp.")

        insert_heading("1.6 Scope and Limitation")
        insert_p("Phạm vi nghiên cứu (Scope) tập trung vào học sinh trung học cơ sở (lớp 6 đến lớp 9) tại Việt Nam. Nền tảng được xây dựng dành cho giáo dục di động trên hệ điều hành Android sử dụng Kotlin và thiết kế giao diện bằng Jetpack Compose. Phía quản trị sử dụng Web Admin phát triển bằng React 19, và Backend vận hành bởi Spring Boot 3.2.5.")
        insert_p("Giới hạn (Limitation): Hiện tại hệ thống chỉ mới tập trung phát triển cho thiết bị Android, chưa hỗ trợ người dùng iOS (Apple). Ngoài ra, hệ thống chưa được tích hợp cổng thanh toán trực tuyến (Payment Gateway) cho các giao dịch nội bộ.")

        insert_heading("1.7 Implementation Plan")
        insert_p("The project is executed following the Agile Scrum methodology over a 16-week timeline. The development is divided into distinct Sprints, covering Requirements Gathering, Architectural Design, Backend Implementation, AI Integration, and User Acceptance Testing. A detailed Gantt chart illustrating this timeline is presented in Chapter 3.")

        insert_heading("1.8 Definition of Terms")
        insert_p("Để tối ưu hóa trải nghiệm đọc, các thuật ngữ kỹ thuật sau được định nghĩa ngắn gọn:")
        insert_p("- Multimodal AI (Trí tuệ nhân tạo đa phương thức): Các mô hình AI có khả năng đồng thời hiểu và xử lý nhiều định dạng dữ liệu đầu vào như văn bản, hình ảnh, âm thanh (ví dụ: Gemini AI).")
        insert_p("- Socratic Method (Phương pháp tư duy Socrates): Phương pháp giáo dục dựa trên việc đặt câu hỏi gợi mở, được sử dụng cho AI Tutor để dẫn dắt học sinh tự tìm ra đáp án thay vì cung cấp ngay kết quả.")
        insert_p("- JWT (JSON Web Token): Tiêu chuẩn mở đại diện cho các yêu cầu bảo mật giữa hai bên, thường được dùng để xác thực người dùng trong các hệ thống phần mềm.")
        insert_p("- Jetpack Compose: Bộ công cụ UI mang tính khai báo hiện đại cho việc lập trình xây dựng giao diện ứng dụng Android tự nhiên.")
        insert_p("- Single Page Application (SPA): Ứng dụng web chỉ tải một trang HTML duy nhất, trong đó các thành phần giao diện được cập nhật linh hoạt (dùng cho hệ thống React Admin).")
        insert_p("- Gamification: Việc áp dụng các cơ chế của trò chơi (như Hệ thống Streak theo dõi tiến độ) vào quá trình học tập để tăng tính cạnh tranh và động lực.")

        # Delete old Chapter 1 paragraphs
        # chap1_idx is the index of the heading "CHAPTER 1: INTRODUCTION"
        # Since we inserted the new content right before chap2_idx, all paragraphs from chap1_idx+1 up to the current position of the inserted paragraphs need to be deleted.
        # But wait! If I just delete the paragraphs from chap1_idx+1 to chap2_idx-1, it works.
        paragraphs_to_delete = doc.paragraphs[chap1_idx+1:chap2_idx]
        for p in paragraphs_to_delete:
            delete_paragraph(p)
            
        output_path = r"e:\Final_Project\docx\MaSci_Final_Report_V4.docx"
        doc.save(output_path)
        print(f"Thành công! Đã cập nhật toàn bộ Chapter 1 và lưu thành: {output_path}")
    else:
        print("Lỗi: Không thể tìm thấy điểm bắt đầu của Chapter 1 hoặc Chapter 2 trong tài liệu.")
        
if __name__ == '__main__':
    main()
