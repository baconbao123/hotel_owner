package com.hotel.webapp.controller.owner;

import com.hotel.webapp.dto.admin.request.UserDTO;
import com.hotel.webapp.dto.admin.response.ApiResponse;
import com.hotel.webapp.entity.HotelUser;
import com.hotel.webapp.service.admin.HotelUserServiceImpl;
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
public class HotelUserController {
  HotelUserServiceImpl userService;

  @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<HotelUser> create(@Valid @ModelAttribute UserDTO userDTO) throws IOException {
    return ApiResponse.<HotelUser>builder()
                      .result(userService.create(userDTO))
                      .build();
  }

  @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<HotelUser> update(@PathVariable int id,
        @Valid @ModelAttribute UserDTO userDTO) throws IOException {
    return ApiResponse.<HotelUser>builder()
                      .result(userService.update(id, userDTO))
                      .build();
  }

  @GetMapping("/get-all")
  public ApiResponse<List<HotelUser>> getAll() {
    return ApiResponse.<List<HotelUser>>builder()
                      .result(userService.getAll())
                      .build();
  }

  @GetMapping("/find-by-id/{id}")
  public ApiResponse<HotelUser> findById(@PathVariable int id) {
    return ApiResponse.<HotelUser>builder()
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
