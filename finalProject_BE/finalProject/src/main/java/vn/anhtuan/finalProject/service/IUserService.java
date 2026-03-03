package vn.anhtuan.finalProject.service;

import vn.anhtuan.finalProject.dto.request.PasswordChangeRequest;
import vn.anhtuan.finalProject.dto.request.UserProfileUpdateRequest;
import vn.anhtuan.finalProject.dto.response.UserProfileResponse;
import vn.anhtuan.finalProject.dto.response.UserResponse;
import vn.anhtuan.finalProject.entity.User;

import java.util.List;

public interface IUserService {
    void changePassword(PasswordChangeRequest request);

    // Thêm hàm này
    List<UserResponse> getAllUsers();

    // Thêm hàm xóa user (để dùng cho nút Xóa bên Admin)
    void deleteUser(Long id);

    void updateGrade(String username, Integer grade);
    Integer getCurrentGrade(String username);

    User getCurrentUser();
    Long getCurrentUserId();

    // Trong IUserService.java thêm:
    UserProfileResponse getMyProfile();
    UserProfileResponse updateMyProfile(UserProfileUpdateRequest request);
}