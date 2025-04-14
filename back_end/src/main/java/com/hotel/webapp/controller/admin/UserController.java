package com.hotel.webapp.controller.admin;

import com.hotel.webapp.dto.admin.request.UserDTO;
import com.hotel.webapp.dto.admin.response.ApiResponse;
import com.hotel.webapp.entity.User;
import com.hotel.webapp.service.admin.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
  UserServiceImpl userService;

  @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<User> create(@Valid @ModelAttribute UserDTO userDTO) throws IOException {
    return ApiResponse.<User>builder()
                      .result(userService.create(userDTO))
                      .build();
  }

  @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<User> update(@PathVariable int id, @Valid @ModelAttribute UserDTO userDTO) throws IOException {
    return ApiResponse.<User>builder()
                      .result(userService.update(id, userDTO))
                      .build();
  }

  @GetMapping("/get-all")
  public ApiResponse<List<User>> getAll() {
    return ApiResponse.<List<User>>builder()
                      .result(userService.getAll())
                      .build();
  }

  @GetMapping("/find-by-id/{id}")
  public ApiResponse<User> findById(@PathVariable int id) {
    return ApiResponse.<User>builder()
                      .result(userService.getById(id))
                      .build();
  }

  @DeleteMapping("/delete/{id}")
  public ApiResponse<Void> deleteById(@PathVariable int id) {
    userService.delete(id);
    return ApiResponse.<Void>builder()
                      .message("Deleted user with id " + id + " successfully")
                      .build();
  }

}
