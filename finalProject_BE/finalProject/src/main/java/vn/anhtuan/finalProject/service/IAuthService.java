package vn.anhtuan.finalProject.service;

import vn.anhtuan.finalProject.dto.request.ForgotPasswordRequest;
import vn.anhtuan.finalProject.dto.request.LoginRequest;
import vn.anhtuan.finalProject.dto.request.RegisterRequest;
import vn.anhtuan.finalProject.dto.request.ResetPasswordRequest;
import vn.anhtuan.finalProject.dto.response.AuthResponse;

public interface IAuthService {
    AuthResponse login(LoginRequest loginRequest);
    String register(RegisterRequest registerRequest);

    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
