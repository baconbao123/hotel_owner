package com.hotel.webapp.controller.owner;

import com.hotel.webapp.dto.admin.request.RoleDTO;
import com.hotel.webapp.dto.admin.response.ApiResponse;
import com.hotel.webapp.entity.HotelRoles;
import com.hotel.webapp.service.admin.HotelRoleServiceImpl;
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
public class HotelRoleController {
  HotelRoleServiceImpl roleService;

  @PostMapping("/create")
  public ApiResponse<HotelRoles> create(@Valid @RequestBody RoleDTO roleDTO) {
    return ApiResponse.<HotelRoles>builder()
                      .result(roleService.create(roleDTO))
                      .build();
  }

  @PutMapping("/update/{id}")
  public ApiResponse<HotelRoles> update(@PathVariable int id, @Valid @RequestBody RoleDTO roleDTO) {
    return ApiResponse.<HotelRoles>builder()
                      .result(roleService.update(id, roleDTO))
                      .build();
  }

  @GetMapping("/get-all")
  public ApiResponse<List<HotelRoles>> getAll() {
    return ApiResponse.<List<HotelRoles>>builder()
                      .result(roleService.getAll())
                      .build();
  }

  @GetMapping("/find-by-id/{id}")
  public ApiResponse<HotelRoles> findById(@PathVariable int id) {
    return ApiResponse.<HotelRoles>builder()
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
