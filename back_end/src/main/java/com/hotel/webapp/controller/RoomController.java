package com.hotel.webapp.controller;

import com.hotel.webapp.dto.request.RoomDTO;
import com.hotel.webapp.dto.response.ApiResponse;
import com.hotel.webapp.dto.response.RoomRes;
import com.hotel.webapp.entity.Rooms;
import com.hotel.webapp.service.owner.RoomService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {
  RoomService roomService;

  @PostMapping(consumes = {"multipart/form-data"})
  public ApiResponse<Rooms> create(
        @Valid @ModelAttribute RoomDTO roomDTO
  ) {
    return ApiResponse.<Rooms>builder()
                      .result(roomService.create(roomDTO))
                      .build();
  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<Rooms> update(@PathVariable int id, @Valid @ModelAttribute RoomDTO roomDTO) {
    return ApiResponse.<Rooms>builder()
                      .result(roomService.update(id, roomDTO))
                      .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteHotel(@PathVariable int id) {
    roomService.delete(id);
    return ApiResponse.<Void>builder()
                      .message("Deleted room with id " + id + " successfully")
                      .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<RoomRes> getById(@PathVariable Integer id) {
    return ApiResponse.<RoomRes>builder()
                      .result(roomService.findRoomById(id))
                      .build();
  }

  @GetMapping("/{hotelId}/rooms")
  public ApiResponse<Page<Rooms>> getAll(
        @PathVariable Integer hotelId,
        @RequestParam(required = false) Map<String, String> filters,
        @RequestParam(required = false) Map<String, String> sort,
        @RequestParam int size,
        @RequestParam int page,
        @RequestHeader("Authorization") String token
  ) throws ParseException, JOSEException {
    return ApiResponse.<Page<Rooms>>builder()
                      .result(roomService.findRoomsByHotelId(hotelId, filters, sort, size, page, token))
                      .build();
  }

}
