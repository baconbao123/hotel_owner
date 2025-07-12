package com.hotel.webapp.service.system;

import com.hotel.webapp.entity.Otp;
import com.hotel.webapp.repository.OtpRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpService {
  OtpRepository otpRepository;

  public String generateOtp() {
    Random rand = new Random();
    int randomNum = 100000 + rand.nextInt(900000);
    return String.valueOf(randomNum);
  }

  @Transactional
  public void saveOtp(String email, String otp) {
    deleteOtp(email);

    var otpEntity = Otp.builder()
                       .email(email)
                       .code(otp)
                       .expTime(LocalDateTime.now().plusMinutes(3))
                       .build();

    otpRepository.save(otpEntity);
  }

  public boolean validateOtp(String email, String otp) {
    Otp otpEntity = otpRepository.findByEmailAndCode(email, otp);

    if (otpEntity == null) {
      return false;
    }

    return LocalDateTime.now().isBefore(otpEntity.getExpTime());
  }

  @Transactional
  public void deleteOtp(String email) {
    otpRepository.deleteByEmail(email);
  }
}
