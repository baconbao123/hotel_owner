package com.hotel.webapp.controller.admin;

import com.hotel.webapp.dto.admin.request.ActionResourceDTO;
import com.hotel.webapp.dto.admin.response.ApiResponse;
import com.hotel.webapp.entity.Resources;
import com.hotel.webapp.service.admin.ResourceServiceImpl;
import com.hotel.webapp.service.admin.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResourceController {
  AuthService authService;
  ResourceServiceImpl resourceService;

  @PostMapping("/create")
  public ApiResponse<Resources> create(@Valid ActionResourceDTO actionResourceReq) {
    return ApiResponse.<Resources>builder()
                      .result(resourceService.create(actionResourceReq))
                      .build();
  }

  @PutMapping("/update/{id}")
  public ApiResponse<Resources> update(@PathVariable int id, @Valid ActionResourceDTO updateReq) {
    return ApiResponse.<Resources>builder()
                      .result(resourceService.update(id, updateReq))
                      .build();
  }

  @GetMapping("/get-all")
  public ApiResponse<List<Resources>> getAll() {
    return ApiResponse.<List<Resources>>builder()
                      .result(resourceService.getAll())
                      .build();
  }

  @GetMapping("/find-by-id/{id}")
  public ApiResponse<Resources> findById(@PathVariable int id) {
    return ApiResponse.<Resources>builder()
                      .result(resourceService.getById(id))
                      .build();
  }

  @DeleteMapping("/delete/{id}")
  public ApiResponse<Void> deleteById(@PathVariable int id) {
    resourceService.delete(id);
    return ApiResponse.<Void>builder()
                      .message("Deleted resource with id " + id + " successfully")
                      .build();
  }
}
