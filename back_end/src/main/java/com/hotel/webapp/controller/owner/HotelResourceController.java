package com.hotel.webapp.controller.owner;

import com.hotel.webapp.dto.admin.request.NameDTO;
import com.hotel.webapp.dto.admin.response.ApiResponse;
import com.hotel.webapp.entity.HotelResources;
import com.hotel.webapp.service.admin.HotelResourceServiceImpl;
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
public class HotelResourceController {
  HotelResourceServiceImpl resourceService;

  @PostMapping("/create")
  public ApiResponse<HotelResources> create(@Valid @RequestBody NameDTO actionResourceReq) {
    return ApiResponse.<HotelResources>builder()
                      .result(resourceService.create(actionResourceReq))
                      .build();
  }

  @PutMapping("/update/{id}")
  public ApiResponse<HotelResources> update(@PathVariable int id, @Valid @RequestBody NameDTO updateReq) {
    return ApiResponse.<HotelResources>builder()
                      .result(resourceService.update(id, updateReq))
                      .build();
  }

  @GetMapping("/get-all")
  public ApiResponse<List<HotelResources>> getAll() {
    return ApiResponse.<List<HotelResources>>builder()
                      .result(resourceService.getAll())
                      .build();
  }

  @GetMapping("/find-by-id/{id}")
  public ApiResponse<HotelResources> findById(@PathVariable int id) {
    return ApiResponse.<HotelResources>builder()
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
