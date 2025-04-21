package com.hotel.webapp.controller.owner;

import com.hotel.webapp.dto.admin.request.MapRADTO;
import com.hotel.webapp.dto.admin.response.ApiResponse;
import com.hotel.webapp.entity.HotelMapResourcesAction;
import com.hotel.webapp.service.admin.HotelMapResourceActionServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/map-resource-action")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HotelMapResourceActionController {
  HotelMapResourceActionServiceImpl mapResourceActionService;

  @PostMapping(value = "/create")
  public ApiResponse<List<HotelMapResourcesAction>> create(@RequestBody MapRADTO mapRADTO) {
    return ApiResponse.<List<HotelMapResourcesAction>>builder()
                      .result(mapResourceActionService.createCollectionBulk(mapRADTO))
                      .build();
  }

  @PutMapping(value = "/update/{id}")
  public ApiResponse<List<HotelMapResourcesAction>> update(@PathVariable Integer id,@RequestBody MapRADTO updateReq) {
    return ApiResponse.<List<HotelMapResourcesAction>>builder()
                      .result(mapResourceActionService.updateCollectionBulk(id, updateReq))
                      .build();
  }
}
