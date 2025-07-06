package com.hotel.webapp.controller;

import com.hotel.webapp.dto.request.HotelDTO;
import com.hotel.webapp.dto.response.ApiResponse;
import com.hotel.webapp.dto.response.HotelsRes;
import com.hotel.webapp.entity.Hotels;
import com.hotel.webapp.service.owner.HotelService;
import com.hotel.webapp.validation.Permission;
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
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HotelController {
  HotelService hotelService;

//  @Permission(name = "create")
//  @PostMapping(consumes = {"multipart/form-data"})
//  public ApiResponse<Hotels> create(
//        @Valid @ModelAttribute HotelDTO hotelDTO
//  ) {
//    return ApiResponse.<Hotels>builder()
//                      .result(hotelService.create(hotelDTO))
//                      .build();
//  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<Hotels> update(@PathVariable int id, @Valid @ModelAttribute HotelDTO.HotelUpdateDTO hotelDTO) {
    return ApiResponse.<Hotels>builder()
                      .result(hotelService.updateHotel(id, hotelDTO))
                      .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteHotel(@PathVariable int id) {
    hotelService.delete(id);
    return ApiResponse.<Void>builder()
                      .message("Deleted hotel with id " + id + " successfully")
                      .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<HotelsRes.HotelRes> getById(@PathVariable Integer id) {
    return ApiResponse.<HotelsRes.HotelRes>builder()
                      .result(hotelService.findHotel(id))
                      .build();
  }

  @GetMapping
  @Permission(name = "view")
  public ApiResponse<Page<HotelsRes>> getAll(
        @RequestParam(required = false) Map<String, String> filters,
        @RequestParam(required = false) Map<String, String> sort,
        @RequestParam int size,
        @RequestParam int page
//        @RequestHeader("Authorization") String token
  ) throws ParseException, JOSEException {
    return ApiResponse.<Page<HotelsRes>>builder()
                      .result(hotelService.findHotels(filters, sort, size, page))
                      .build();
  }
}
