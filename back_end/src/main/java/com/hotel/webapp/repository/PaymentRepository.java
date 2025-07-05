package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends BaseRepository<Payment, Integer> {
  @Query("select p from Payment p " +
        "join Booking b on b.paymentId = p.id " +
        "where b.id = :bookingId and p.deletedAt is null")
  Optional<Payment> findPaymentByBookingId(Integer bookingId);
}
