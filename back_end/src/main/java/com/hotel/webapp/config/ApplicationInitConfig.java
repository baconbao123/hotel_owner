package com.hotel.webapp.config;

import com.hotel.webapp.entity.HotelRoles;
import com.hotel.webapp.entity.HotelUser;
import com.hotel.webapp.entity.HotelMapUserRoles;
import com.hotel.webapp.repository.HotelRoleRepository;
import com.hotel.webapp.repository.HotelUserRepository;
import com.hotel.webapp.repository.HotelMapUserRoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
  PasswordEncoder passwordEncoder;

  @Bean
  ApplicationRunner applicationRunner(HotelUserRepository hotelUserRepository, HotelRoleRepository hotelRoleRepository,
        HotelMapUserRoleRepository hotelMapUserRoleRepository) {
    return args -> {
      HotelUser hotelUser = hotelUserRepository.findByEmail("sa@gmail.com")
                                               .orElseGet(() -> {
                                  HotelUser newHotelUser = HotelUser.builder()
                                                                    .email("sa@gmail.com")
                                                                    .password(passwordEncoder.encode("123"))
                                                                    .createdAt(new Timestamp(System.currentTimeMillis()))
                                                                    .build();
                                  return hotelUserRepository.save(newHotelUser);
                                });

      HotelRoles hotelRoles = hotelRoleRepository.findByName("Admin")
                                                 .orElseGet(() -> {
                                  HotelRoles newHotelRoles = HotelRoles.builder().name("Admin")
                                                                       .isActive(true)
                                                                       .createdAt(new Timestamp(System.currentTimeMillis()))
                                                                       .build();
                                  return hotelRoleRepository.save(newHotelRoles);
                                });

      if (hotelMapUserRoleRepository.findByRoleIdAndUserId(hotelRoles.getId(), hotelUser.getId()).isEmpty()) {
        HotelMapUserRoles userRole = HotelMapUserRoles.builder()
                                                      .roleId(hotelRoles.getId())
                                                      .userId(hotelUser.getId())
                                                      .build();
        hotelMapUserRoleRepository.save(userRole);
      }
    };
  }
}
