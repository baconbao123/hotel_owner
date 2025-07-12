package com.hotel.webapp.controller;

import com.hotel.webapp.dto.request.AuthReq;
import com.hotel.webapp.dto.response.ApiResponse;
import com.hotel.webapp.dto.response.AuthResponse;
import com.hotel.webapp.service.owner.UserService;
import com.hotel.webapp.service.owner.interfaces.AuthService;
import com.hotel.webapp.service.system.EmailService;
import com.hotel.webapp.service.system.OtpService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
  AuthService authService;
  EmailService emailService;
  OtpService otpService;
  UserService userService;

  @PostMapping("/login")
  public ApiResponse<AuthResponse> authentication(@Valid @RequestBody AuthReq authReq) {
    return ApiResponse.<AuthResponse>builder()
                      .result(authService.authenticate(authReq))
                      .build();
  }

  @PostMapping("/refresh-token")
  public ApiResponse<AuthResponse> refreshToken(@RequestBody AuthReq.TokenRefreshReq tokenRefreshReq) {
    return ApiResponse.<AuthResponse>builder()
                      .result(authService.refreshToken(tokenRefreshReq))
                      .build();
  }

  @PostMapping("/forgot-password")
  public ApiResponse<Object> forgotPassword(@RequestParam String email) {
    if (!userService.existsByEmail(email)) {
      return ApiResponse.builder()
                        .code(404)
                        .message("Email not found")
                        .build();
    }

    String otp = otpService.generateOtp();
    otpService.saveOtp(email, otp);

    String subject = "Reset Password - OTP Code";
    String text = "Your otp is: " + otp + " It is valid for 3 minutes.";
    emailService.sendEmail(email, subject, text);

    return ApiResponse.builder()
                      .message("OTP Code has been sent to your email")
                      .build();

  }

  @PostMapping("/change-password-profile")
  public ApiResponse<Object> changeProfilePassword(@RequestParam String email,
        @RequestParam("oldPassword") String oldPassword) {
    if (!userService.matchPassword(email, oldPassword)) {
      return ApiResponse.builder()
                        .code(404)
                        .message("Password don't match")
                        .build();
    }

    String token = authService.generatePasswordResetToken(email);

    String subject = "Reset Password";
    String text = "http://localhost:5173/reset-password-profile?token=" + token;
    emailService.sendEmail(email, subject, text);

    return ApiResponse.builder()
                      .message("Reset password has been sent to your email")
                      .build();

  }

  @PostMapping("/reset-password-profile")
  public ApiResponse<Object> resetPassword(@RequestParam("token") String token,
        @RequestParam("newPassword") String newPassword) throws ParseException, JOSEException {

    authService.resetPassword(token, newPassword);

    return ApiResponse.builder()
                      .message("Successfully reset your password")
                      .build();

  }

  @PostMapping("/verify-otp")
  public ApiResponse<Object> verifyOtp(@RequestParam String email, @RequestParam String otp) {
    if (otpService.validateOtp(email, otp)) {
      return ApiResponse.builder()
                        .code(200)
                        .message("Otp verified")
                        .build();
    }

    return ApiResponse.builder()
                      .code(400)
                      .message("Otp Invalid Or Expired")
                      .build();
  }

  @PostMapping("/reset-password")
  public ApiResponse<Object> resetPassword(@RequestParam String email,
        @RequestParam String otp, @RequestParam String password) {
    if (!otpService.validateOtp(email, otp)) {
      return ApiResponse.builder()
                        .code(400)
                        .message("Otp Invalid Or Expired")
                        .build();
    }

    userService.changePassword(email, password);
    otpService.deleteOtp(email);

    return ApiResponse.builder()
                      .code(200)
                      .message("Change password successful")
                      .build();
  }
}
