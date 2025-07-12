package com.hotel.webapp.controller;

import com.hotel.webapp.dto.request.BookingDTO;
import com.hotel.webapp.dto.response.ApiResponse;
import com.hotel.webapp.dto.response.BookingRes;
import com.hotel.webapp.dto.response.PricesDTO;
import com.hotel.webapp.entity.Booking;
import com.hotel.webapp.service.owner.BookingService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {
  BookingService bookingService;

  @PostMapping
        (consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<Booking> create(@Valid @ModelAttribute BookingDTO dto) {
    return ApiResponse.<Booking>builder()
                      .result(bookingService.create(dto))
                      .build();
  }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//  @PutMapping(value = "/{id}")
  public ApiResponse<Booking> update(@PathVariable int id, @Valid @ModelAttribute BookingDTO dto) {
    return ApiResponse.<Booking>builder()
                      .result(bookingService.update(id, dto))
                      .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteHotel(@PathVariable int id) {
    bookingService.delete(id);
    return ApiResponse.<Void>builder()
                      .message("Deleted room with id " + id + " successfully")
                      .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<BookingRes> getById(@PathVariable Integer id) {
    return ApiResponse.<BookingRes>builder()
                      .result(bookingService.findBookingById(id))
                      .build();
  }

  @GetMapping("/{roomId}/booking")
  public ApiResponse<Page<BookingRes>> getAll(
        @PathVariable Integer roomId,
        @RequestParam(required = false) Map<String, String> filters,
        @RequestParam(required = false) Map<String, String> sort,
        @RequestParam int size,
        @RequestParam int page
  ) {
    return ApiResponse.<Page<BookingRes>>builder()
                      .result(bookingService.findBookingsByRoomId(roomId, filters, sort, size, page))
                      .build();
  }

  @GetMapping("/{roomId}/prices")
  public ApiResponse<PricesDTO> getPricesData(@PathVariable Integer roomId) {
    return ApiResponse.<PricesDTO>builder()
                      .result(bookingService.getPriceData(roomId))
                      .build();
  }
}
