package com.hotel.webapp.service.owner;

import com.hotel.webapp.dto.request.UserDTO;
import com.hotel.webapp.dto.response.UserRes;
import com.hotel.webapp.entity.Role;
import com.hotel.webapp.entity.User;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.MapUserRoleRepository;
import com.hotel.webapp.repository.RoleRepository;
import com.hotel.webapp.repository.UserRepository;
import com.hotel.webapp.service.owner.interfaces.AuthService;
import com.hotel.webapp.service.system.StorageFileService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
  UserRepository repository;
  AuthService authService;
  StorageFileService storageFileService;
  MapUserRoleRepository userRoleRepository;
  RoleRepository roleRepository;
  PasswordEncoder passwordEncoder;

  // Update Profile
  public User updateProfile(Integer id, UserDTO.ProfileDTO update) throws IOException {
    var user = findById(id);

    if (repository.existsByEmailAndIdNot(update.getEmail(), id))
      throw new AppException(ErrorCode.FIELD_EXISTED, "Email");

    user = User.builder()
               .id(user.getId())
               .fullName(update.getFullName())
               .email(update.getEmail())
               .password(user.getPassword())
               .avatarUrl(user.getAvatarUrl())
               .phoneNumber(update.getPhoneNumber())
               .createdBy(user.getCreatedBy())
               .createdAt(user.getCreatedAt())
               .updatedBy(authService.getAuthLogin())
               .updatedAt(LocalDateTime.now())
               .status(user.getStatus())
               .build();

    if (update.getAvatarUrl() != null && !update.getAvatarUrl().isEmpty()) {
      String fileName = storageFileService.uploadUserImg(update.getAvatarUrl());
      user.setAvatarUrl(fileName);
    } else {
      user.setAvatarUrl(user.getAvatarUrl());
    }

    return repository.save(user);
  }

  private User findById(Integer id) {
    return repository.findById(id)
                     .filter(u -> u.getDeletedAt() != null)
                     .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Not Found"));
  }

  // find profile
  public UserRes.UserProfileRes findProfile() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    var username = auth.getName();

    User user = repository.findByEmail(username)
                          .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Not Found Profile"));

    var userRoles = userRoleRepository.findAllByUserIdAndDeletedAtIsNull(user.getId());


    List<String> roles = userRoles.stream()
                                  .map(ur -> roleRepository.findRolesByDeletedAtIsNull(ur.getRoleId())
                                                           .orElseThrow(() -> new AppException(ErrorCode.COMMON_400,
                                                                 "Something went wrong with account")))
                                  .map(Role::getName)
                                  .toList();

    return new UserRes.UserProfileRes(
          user.getId(),
          user.getFullName(),
          user.getEmail(),
          user.getPhoneNumber(),
          user.getAvatarUrl(),
          roles
    );
  }

  // check email already existed
  @Transactional
  public boolean existsByEmail(String email) {
    return repository.existsByEmail(email);
  }

  // check match password
  @Transactional
  public boolean matchPassword(String email, String oldPassword) {
    var user = repository.findByEmail(email)
                         .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "User"));

    return passwordEncoder.matches(oldPassword, user.getPassword());
  }

  public void changePassword(String email, String newPassword) {
    var user = repository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "User"));
    user.setPassword(passwordEncoder.encode(newPassword));
    repository.save(user);
  }

  // find-all customer
  public List<UserRes.CustomerRes> findCustomer(String keyWord, int page) {
    Pageable defaultPage = PageRequest.of(page, 20);
    List<Object[]> customerObjs = repository.findCustomers(keyWord, defaultPage);
    return customerObjs.stream()
                       .map(o -> new UserRes.CustomerRes((Integer) o[0], (String) o[1], (String) o[2]))
                       .toList();
  }
}
