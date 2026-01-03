package vn.anhtuan.finalProject.service.impl;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.anhtuan.finalProject.dto.request.PasswordChangeRequest;
import vn.anhtuan.finalProject.dto.response.UserResponse;
import vn.anhtuan.finalProject.entity.StudentProfile;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.exception.BadRequestException;
import vn.anhtuan.finalProject.exception.ResourceNotFoundException;
import vn.anhtuan.finalProject.repository.StudentProfileRepository;
import vn.anhtuan.finalProject.repository.UserRepository;
import vn.anhtuan.finalProject.service.IUserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentProfileRepository profileRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, StudentProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileRepository = profileRepository;
    }

    @Override
    public void changePassword(PasswordChangeRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy người dùng"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu cũ không chính xác");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream().map(user -> new UserResponse(
                user.getId(),           // 1. Long id
                user.getUsername(),     // 2. String username
                user.getEmail(),        // 3. String email (Đúng thứ tự field trong DTO)
                user.getRole().name(),  // 4. String role
                user.getCreatedAt()     // 5. LocalDateTime createdAt
        )).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User không tồn tại với ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateGrade(String username, Integer grade) {
        logger.info("--- Service: Bắt đầu cập nhật lớp cho user [{}]", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("--- Service Error: Không tìm thấy user [{}]", username);
                    return new ResourceNotFoundException("User not found");
                });

        StudentProfile profile = profileRepository.findByUser(user).orElse(null);

        if (profile == null) {
            logger.info("--- Service: User chưa có Profile -> Tạo mới Profile");
            profile = new StudentProfile(user, grade);
        } else {
            logger.info("--- Service: User đã có Profile (Lớp cũ: {}) -> Cập nhật sang Lớp {}", profile.getCurrentGrade(), grade);
            profile.setCurrentGrade(grade);
        }

        profileRepository.save(profile);
        logger.info("--- Service: Lưu xuống DB thành công");
    }

    @Override
    public Integer getCurrentGrade(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Integer grade = profileRepository.findByUser(user)
                .map(StudentProfile::getCurrentGrade)
                .orElse(null);

        logger.info("--- Service: Lấy lớp hiện tại của [{}] -> Kết quả: {}", username, grade);
        return grade;
    }
}