package com.hotel.webapp.service.owner;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.request.BookingDTO;
import com.hotel.webapp.dto.response.BookingRes;
import com.hotel.webapp.dto.response.PricesDTO;
import com.hotel.webapp.entity.Booking;
import com.hotel.webapp.entity.Payment;
import com.hotel.webapp.entity.Rooms;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.BookingRepository;
import com.hotel.webapp.repository.PaymentRepository;
import com.hotel.webapp.service.owner.interfaces.AuthService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService extends BaseServiceImpl<Booking, Integer, BookingDTO, BookingRepository> {
  PaymentRepository paymentRepository;

  public BookingService(
        BookingRepository repository,
        BaseMapper<Booking, BookingDTO> mapper,
        AuthService authService,
        PaymentRepository paymentRepository
  ) {
    super(repository, mapper, authService);
    this.paymentRepository = paymentRepository;
  }

  public Page<BookingRes> findBookingsByRoomId(Integer roomId, Map<String, String> filters, Map<String, String> sort,
        int size,
        int page) {
    Map<String, Object> filterMap = removedFiltersKey(filters);
    Map<String, Object> sortMap = removedSortedKey(sort);

    Specification<Rooms> spec = buildSpecification(filterMap);
    Pageable defaultPage = buildPageable(sortMap, page, size);

    return repository.findBookingsByRoomId(roomId, spec, defaultPage);
  }

  @Override
  public Booking create(BookingDTO create) {
    // booking
    var newBooking = mapper.toCreate(create);
    newBooking.setCreatedAt(LocalDateTime.now());
    newBooking.setCreatedBy(getAuthId());
    newBooking = repository.save(newBooking);

    // payment
    Payment payment = Payment.builder()
                             .methodId(create.getMethodId())
                             .amount(create.getAmount())
                             .note(create.getNotePayment())
                             .status(create.getStatus())
                             .createdAt(LocalDateTime.now())
                             .build();

    payment = paymentRepository.save(payment);

    newBooking.setPaymentId(payment.getId());
    newBooking = repository.save(newBooking);

    return newBooking;
  }

  @Override
  public Booking update(Integer id, BookingDTO update) {
    // booking
    Booking booking = repository.findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Booking"));

    // Update booking fields
    booking.setUserId(update.getUserId());
    booking.setRoomId(update.getRoomId());
    booking.setCheckInTime(update.getCheckInTime());
    booking.setCheckOutTime(update.getCheckOutTime());
    booking.setPaymentId(booking.getPaymentId());
    booking.setUpdatedAt(LocalDateTime.now());
    booking.setUpdatedBy(getAuthId());
    booking = repository.save(booking);

    // Find and update payment
    Payment payment = paymentRepository.findPaymentByBookingId(booking.getId())
                                       .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Payment"));

    payment.setMethodId(update.getMethodId());
    payment.setAmount(update.getAmount());
    payment.setNote(update.getNotePayment());
    payment.setStatus(update.getStatus());
    payment.setUpdatedAt(LocalDateTime.now());
    paymentRepository.save(payment);


    return booking;
  }

  public BookingRes findBookingById(Integer id) {
    var res = repository.findBookingById(id);

    if (res == null) {
      throw new AppException(ErrorCode.NOT_FOUND, "Booking or Payment");
    }

    return res;
  }

  public PricesDTO getPriceData(Integer roomId) {
    return repository.getPriceDataByRoomId(roomId);
  }

  @Override
  protected RuntimeException createNotFoundException(Integer integer) {
    throw new AppException(ErrorCode.NOT_FOUND, "Room");
  }
}
