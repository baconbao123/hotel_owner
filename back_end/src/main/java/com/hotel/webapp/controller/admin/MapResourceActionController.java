package com.hotel.webapp.controller.admin;

import com.hotel.webapp.dto.admin.request.MapRADTO;
import com.hotel.webapp.dto.admin.response.ApiResponse;
import com.hotel.webapp.entity.MapResourcesAction;
import com.hotel.webapp.service.admin.MapResourceActionServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/map-resource-action")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MapResourceActionController {
  MapResourceActionServiceImpl mapResourceActionService;

  @PostMapping(value = "/create")
  public ApiResponse<List<MapResourcesAction>> create(@RequestBody MapRADTO mapRADTO) {
    return ApiResponse.<List<MapResourcesAction>>builder()
                      .result(mapResourceActionService.createCollectionBulk(mapRADTO))
                      .build();
  }

  @PutMapping(value = "/update/{id}")
  public ApiResponse<List<MapResourcesAction>> update(@PathVariable Integer id,@RequestBody MapRADTO updateReq) {
    return ApiResponse.<List<MapResourcesAction>>builder()
                      .result(mapResourceActionService.updateCollectionBulk(id, updateReq))
                      .build();
  }
}
