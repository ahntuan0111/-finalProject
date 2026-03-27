package vn.anhtuan.finalProject.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import vn.anhtuan.finalProject.service.IEmailService;

@Service
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("MaSci Learning <noreply@masci.com>");
            message.setTo(to);
            message.setSubject("Mã xác thực đăng ký MaSci");
            message.setText("Chào bạn,\n\nMã OTP để hoàn tất đăng ký tài khoản MaSci của bạn là: " + otp + 
                            "\n\nMã này có hiệu lực trong 5 phút. Vui lòng không gửi mã này cho bất kỳ ai.\n\nTrân trọng,\nĐội ngũ MaSci.");
            
            mailSender.send(message);
            System.out.println(">>> Đã gửi mã OTP đến " + to + " thành công!");
        } catch (Exception e) {
            // Log lỗi và in ra console để developer vẫn có thể lấy được mã OTP để test
            System.err.println("!!! LỖI GỬI MAIL: " + e.getMessage());
            System.out.println(">>> [FALLBACK] MÃ OTP CỦA " + to + " LÀ: " + otp);
        }
    }
}
