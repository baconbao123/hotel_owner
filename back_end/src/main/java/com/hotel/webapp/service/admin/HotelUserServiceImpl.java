package com.hotel.webapp.service.admin;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.admin.request.UserDTO;
import com.hotel.webapp.entity.HotelMapUserRoles;
import com.hotel.webapp.entity.HotelPermissions;
import com.hotel.webapp.entity.HotelUser;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.HotelMapUserRoleRepository;
import com.hotel.webapp.repository.HotelPermissionsRepository;
import com.hotel.webapp.repository.HotelUserRepository;
import com.hotel.webapp.repository.shared.HotelsRepository;
import com.hotel.webapp.service.admin.interfaces.AuthService;
import com.hotel.webapp.service.system.StorageFileService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HotelUserServiceImpl extends BaseServiceImpl<HotelUser, Integer, UserDTO, HotelUserRepository> {
  HotelMapUserRoleRepository hotelMapUserRoleRepository;
  StorageFileService storageFileService;
  PasswordEncoder passwordEncoder;
  HotelPermissionsRepository hotelPermissionsRepository;
  HotelsRepository hotelsRepository;

  public HotelUserServiceImpl(
        HotelUserRepository repository,
        AuthService authService,
        BaseMapper<HotelUser, UserDTO> mapper,
        HotelMapUserRoleRepository hotelMapUserRoleRepository,
        StorageFileService storageFileService,
        PasswordEncoder passwordEncoder,
        HotelPermissionsRepository hotelPermissionsRepository,
        HotelsRepository hotelsRepository
  ) {
    super(repository, mapper, authService);
    this.hotelMapUserRoleRepository = hotelMapUserRoleRepository;
    this.storageFileService = storageFileService;
    this.passwordEncoder = passwordEncoder;
    this.hotelPermissionsRepository = hotelPermissionsRepository;
    this.hotelsRepository = hotelsRepository;
  }

  @Override
  public HotelUser create(UserDTO createDto) {
    if (repository.existsByEmailAndDeletedAtIsNull(createDto.getEmail()))
      throw new AppException(ErrorCode.EMAIL_EXISTED);

    if (!hotelsRepository.existsByIdAndDeletedAtIsNull(createDto.getHotelId()))
      throw new AppException(ErrorCode.HOTEL_NOTFOUND);

    var user = mapper.toCreate(createDto);

    if (createDto.getAvatarUrl() != null && !createDto.getAvatarUrl().isEmpty()) {
      try {
        String filePath = storageFileService.uploadUserImg(createDto.getAvatarUrl());
        user.setAvatarUrl(filePath);
      } catch (IOException ioException) {
        throw new RuntimeException(ioException);
      }
    } else {
      user.setAvatarUrl("");
    }
    user.setPassword(passwordEncoder.encode(createDto.getPassword()));
    user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    user.setCreatedBy(getAuthId());
    return repository.save(user);
  }

  @Override
  public HotelUser update(Integer id, UserDTO updateDto) {
    var user = getById(id);

    if (repository.existsByEmailAndIdNotAndDeletedAtIsNull(updateDto.getEmail(), id))
      throw new AppException(ErrorCode.EMAIL_EXISTED);

    if (!hotelsRepository.existsByIdAndDeletedAtIsNull(updateDto.getHotelId()))
      throw new AppException(ErrorCode.HOTEL_NOTFOUND);

    mapper.toUpdate(user, updateDto);

    if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
      user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
    } else {
      user.setPassword(user.getPassword());
    }

    if (updateDto.getAvatarUrl() != null && !updateDto.getAvatarUrl().isEmpty()) {
      try {
        String fileName = storageFileService.uploadUserImg(updateDto.getAvatarUrl());
        user.setAvatarUrl(fileName);
      } catch (IOException ioException) {
        throw new RuntimeException(ioException);
      }

    }
    user.setIsActive(updateDto.getIsActive());
    user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    user.setUpdatedBy(getAuthId());

    return repository.save(user);
  }


  @Override
  protected void validateCreate(UserDTO create) {
  }

  @Override
  protected void validateUpdate(Integer id, UserDTO update) {
  }

  @Override
  protected void validateDelete(Integer id) {
    updateMapURIfUserDelete(id, getAuthId());
  }

  @Override
  protected RuntimeException createNotFoundException(Integer integer) {
    return new AppException(ErrorCode.USER_NOTFOUND);
  }


  private void updateMapURIfUserDelete(int userId, int authId) {
    List<HotelMapUserRoles> hotelMapUserRolesList = hotelMapUserRoleRepository.findAllByUserId(userId);

    List<Integer> mapURIds = hotelMapUserRolesList.stream()
                                                  .map(HotelMapUserRoles::getId)
                                                  .toList();

    updatePermissionIfUserDelete(mapURIds, authId);

    for (HotelMapUserRoles hotelMapUserRoles : hotelMapUserRolesList) {
      hotelMapUserRoles.setDeletedAt(LocalDateTime.now());
      hotelMapUserRoles.setUpdatedBy(authId);
      hotelMapUserRoleRepository.save(hotelMapUserRoles);
    }
  }

  private void updatePermissionIfUserDelete(Collection<Integer> mapUserRoleId, int authId) {
    List<HotelPermissions> findAllPermissions = hotelPermissionsRepository.findAllByMapURId(mapUserRoleId);

    for (HotelPermissions permission : findAllPermissions) {
      permission.setDeletedAt(LocalDateTime.now());
      permission.setUpdatedBy(authId);
      hotelPermissionsRepository.save(permission);
    }
  }
}
