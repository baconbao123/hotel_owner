package com.hotel.webapp.controller.owner;

import com.hotel.webapp.dto.admin.request.AuthReq;
import com.hotel.webapp.dto.admin.response.ApiResponse;
import com.hotel.webapp.dto.admin.response.AuthResponse;
import com.hotel.webapp.service.admin.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
  AuthService authService;

  @PostMapping("/login")
  public ApiResponse<AuthResponse> authentication(@Valid @RequestBody AuthReq authReq) {
    return ApiResponse.<AuthResponse>builder()
                      .result(authService.authenticate(authReq))
                      .build();
  }
}
