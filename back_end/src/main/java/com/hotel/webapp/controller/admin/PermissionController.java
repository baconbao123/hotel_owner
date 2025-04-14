package com.hotel.webapp.controller.admin;

import com.hotel.webapp.dto.admin.request.PermissionDTO;
import com.hotel.webapp.dto.admin.response.ApiResponse;
import com.hotel.webapp.entity.Permissions;
import com.hotel.webapp.service.admin.PermissionServiceImpl;
import com.hotel.webapp.service.admin.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
  PermissionServiceImpl permissionService;
  AuthService authService;

  @PostMapping("/create")
  public ApiResponse<List<Permissions>> create(@RequestBody @Valid PermissionDTO permissionDTO) {
    return ApiResponse.<List<Permissions>>builder()
                      .result(permissionService.createCollectionBulk(permissionDTO))
                      .build();
  }

  @PutMapping("/update/{id}")
  public ApiResponse<List<Permissions>> update(@PathVariable int id, @RequestBody @Valid PermissionDTO permissionUpdate) {
    return ApiResponse.<List<Permissions>>builder()
                      .result(permissionService.updateCollectionBulk(id, permissionUpdate))
                      .build();
  }
}
