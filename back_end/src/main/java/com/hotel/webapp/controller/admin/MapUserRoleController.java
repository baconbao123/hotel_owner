package com.hotel.webapp.controller.admin;

import com.hotel.webapp.dto.admin.request.MapURDTO;
import com.hotel.webapp.dto.admin.response.ApiResponse;
import com.hotel.webapp.entity.MapUserRoles;
import com.hotel.webapp.service.admin.MapUserRoleServiceImp;
import com.hotel.webapp.service.admin.interfaces.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/map-user-role")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MapUserRoleController {
  AuthService authService;
  MapUserRoleServiceImp mapUserRoleService;

  @PostMapping(value = "/create")
  public ApiResponse<List<MapUserRoles>> create(@RequestBody MapURDTO mapURDTO) {
    return ApiResponse.<List<MapUserRoles>>builder()
                      .result(mapUserRoleService.createCollectionBulk(mapURDTO))
                      .build();
  }

  @PutMapping(value = "/update/{id}")
  public ApiResponse<List<MapUserRoles>> update(@PathVariable Integer id, @RequestBody MapURDTO updateReq) {
    return ApiResponse.<List<MapUserRoles>>builder()
                      .result(mapUserRoleService.updateCollectionBulk(id, updateReq))
                      .build();
  }

//  @GetMapping("/get-all")
//  public ApiResponse<List<Map>> getAll() {
//    return ApiResponse.<List<Map>>builder()
//                      .result(userService.getAll())
//                      .build();
//  }
//
//  @GetMapping("/find-by-id/{id}")
//  public ApiResponse<Map> findById(@PathVariable int id) {
//    return ApiResponse.<Map>builder()
//                      .result(userService.getById(id))
//                      .build();
//  }
//
//  @GetMapping("/delete/{id}")
//  public ApiResponse<Void> deleteById(@PathVariable int id) {
//    userService.delete(id, authService.getAuthLogin());
//    return ApiResponse.<Void>builder()
//                      .message("Deleted user with id " + id + " successfully")
//                      .build();
//  }
}
