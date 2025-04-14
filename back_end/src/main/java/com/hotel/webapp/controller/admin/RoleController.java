package com.hotel.webapp.controller.admin;

import com.hotel.webapp.dto.admin.request.RoleDTO;
import com.hotel.webapp.dto.admin.response.ApiResponse;
import com.hotel.webapp.entity.Role;
import com.hotel.webapp.service.admin.RoleServiceImpl;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
  RoleServiceImpl roleService;

  @PostMapping("/create")
  public ApiResponse<Role> create(@Valid RoleDTO roleDTO) {
    return ApiResponse.<Role>builder()
                      .result(roleService.create(roleDTO))
                      .build();
  }

  @PutMapping("/update/{id}")
  public ApiResponse<Role> update(@PathVariable int id, @Valid RoleDTO roleDTO) {
    return ApiResponse.<Role>builder()
                      .result(roleService.update(id, roleDTO))
                      .build();
  }

  @GetMapping("/get-all")
  public ApiResponse<List<Role>> getAll() {
    return ApiResponse.<List<Role>>builder()
                      .result(roleService.getAll())
                      .build();
  }

  @GetMapping("/find-by-id/{id}")
  public ApiResponse<Role> findById(@PathVariable int id) {
    return ApiResponse.<Role>builder()
                      .result(roleService.getById(id))
                      .build();
  }

  @DeleteMapping("/delete/{id}")
  public ApiResponse<Void> deleteById(@PathVariable int id) {
    roleService.delete(id);
    return ApiResponse.<Void>builder()
                      .message("Deleted role with id " + id + " successfully")
                      .build();
  }
}
