package com.hotel.webapp.controller.owner;

import com.hotel.webapp.dto.admin.request.PermissionDTO;
import com.hotel.webapp.dto.admin.response.ApiResponse;
import com.hotel.webapp.entity.HotelPermissions;
import com.hotel.webapp.service.admin.HotelPermissionServiceImpl;
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
public class HotelPermissionController {
  HotelPermissionServiceImpl permissionService;

  @PostMapping("/create")
  public ApiResponse<List<HotelPermissions>> create(@Valid @RequestBody PermissionDTO permissionDTO) {
    return ApiResponse.<List<HotelPermissions>>builder()
                      .result(permissionService.createCollectionBulk(permissionDTO))
                      .build();
  }

  @PutMapping("/update/{id}")
  public ApiResponse<List<HotelPermissions>> update(@PathVariable int id,
        @Valid @RequestBody PermissionDTO permissionUpdate) {
    return ApiResponse.<List<HotelPermissions>>builder()
                      .result(permissionService.updateCollectionBulk(id, permissionUpdate))
                      .build();
  }
}
