package com.hotel.webapp.controller;

import com.hotel.webapp.dto.request.UserDTO;
import com.hotel.webapp.dto.response.ApiResponse;
import com.hotel.webapp.dto.response.UserRes;
import com.hotel.webapp.entity.User;
import com.hotel.webapp.repository.UserRepository;
import com.hotel.webapp.service.owner.UserService;
import com.hotel.webapp.validation.Permission;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
  UserService userService;
  UserRepository repository;

  // find-all customer
  @GetMapping("/profile")
  public ApiResponse<UserRes.UserProfileRes> findProfileLogin() {
    return ApiResponse.<UserRes.UserProfileRes>builder()
                      .result(userService.findProfile())
                      .build();
  }

  @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<User> updateProfile(@RequestParam("id") Integer id,
        @Valid @ModelAttribute UserDTO.ProfileDTO profileDTO) throws IOException {
    return ApiResponse.<User>builder()
                      .result(userService.updateProfile(id, profileDTO))
                      .build();
  }

  @PutMapping("/change-password")
  @Permission(name = "change_password")
  public ApiResponse<Object> changePassword(@RequestParam("email") String email,
        @RequestParam("password") String newPassword) {
    userService.changePassword(email, newPassword);
    return ApiResponse.builder()
                      .result("Change password successfully")
                      .build();
  }
}
