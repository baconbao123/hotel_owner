package com.hotel.webapp.controller.owner;

import com.hotel.webapp.dto.admin.request.NameDTO;
import com.hotel.webapp.dto.admin.response.ApiResponse;
import com.hotel.webapp.entity.HotelActions;
import com.hotel.webapp.service.admin.HotelActionServiceImpl;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// rest full api
@RestController
@RequestMapping("/api/action")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HotelActionController {
  HotelActionServiceImpl hotelActionServiceImpl;

  @PostMapping("/create")
  public ApiResponse<HotelActions> create(@Valid @RequestBody NameDTO nameDTO) {
    return ApiResponse.<HotelActions>builder()
                      .result(hotelActionServiceImpl.create(nameDTO))
                      .build();
  }

  @PutMapping("/update/{id}")
  public ApiResponse<HotelActions> update(@PathVariable int id, @Valid @RequestBody NameDTO updateReq) {
    return ApiResponse.<HotelActions>builder()
                      .result(hotelActionServiceImpl.update(id, updateReq))
                      .build();
  }

  @GetMapping("/get-all")
  public ApiResponse<List<HotelActions>> getAll() {
    return ApiResponse.<List<HotelActions>>builder()
                      .result(hotelActionServiceImpl.getAll())
                      .build();
  }

  @GetMapping("/find-by-id/{id}")
  public ApiResponse<HotelActions> findById(@PathVariable int id) {
    return ApiResponse.<HotelActions>builder()
                      .result(hotelActionServiceImpl.getById(id))
                      .build();
  }

  @DeleteMapping("/delete/{id}")
  public ApiResponse<Void> deleteById(@PathVariable int id) {
    hotelActionServiceImpl.delete(id);
    return ApiResponse.<Void>builder()
                      .message("Deleted action with id " + id + " successfully")
                      .build();
  }
}
