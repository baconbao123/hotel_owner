package com.hotel.webapp.config;

import com.hotel.webapp.entity.Role;
import com.hotel.webapp.entity.User;
import com.hotel.webapp.entity.MapUserRoles;
import com.hotel.webapp.repository.RoleRepository;
import com.hotel.webapp.repository.UserRepository;
import com.hotel.webapp.repository.MapUserRoleRepository;
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
  ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository,
        MapUserRoleRepository mapUserRoleRepository) {
    return args -> {
      User user = userRepository.findByEmail("sa@gmail.com")
                                .orElseGet(() -> {
                                  User newUser = User.builder()
                                                     .email("sa@gmail.com")
                                                     .password(passwordEncoder.encode("123"))
                                                     .createdAt(new Timestamp(System.currentTimeMillis()))
                                                     .build();
                                  return userRepository.save(newUser);
                                });

      Role role = roleRepository.findByName("Admin")
                                .orElseGet(() -> {
                                  Role newRole = Role.builder().name("Admin")
                                                     .isActive(true)
                                                     .createdAt(new Timestamp(System.currentTimeMillis()))
                                                     .build();
                                  return roleRepository.save(newRole);
                                });

      if (mapUserRoleRepository.findByRoleIdAndUserId(role.getId(), user.getId()).isEmpty()) {
        MapUserRoles userRole = MapUserRoles.builder()
                                            .roleId(role.getId())
                                            .userId(user.getId())
                                            .build();
        mapUserRoleRepository.save(userRole);
      }
    };
  }
}
