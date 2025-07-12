package com.hotel.webapp.repository;

import com.hotel.webapp.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Integer> {
  void deleteByEmail(String email);

  Otp findByEmailAndCode(String email, String code);
}
