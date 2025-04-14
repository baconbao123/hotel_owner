package com.hotel.webapp.service.admin;

import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.admin.request.UserDTO;
import com.hotel.webapp.entity.MapUserRoles;
import com.hotel.webapp.entity.Permissions;
import com.hotel.webapp.entity.User;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.mapper.admin.UserMapper;
import com.hotel.webapp.repository.MapUserRoleRepository;
import com.hotel.webapp.repository.PermissionsRepository;
import com.hotel.webapp.repository.UserRepository;
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
public class UserServiceImpl extends BaseServiceImpl<User, Integer, UserDTO, UserRepository> {
  UserRepository userRepository;
  UserMapper userMapper;
  MapUserRoleRepository mapUserRoleRepository;
  StorageFileService storageFileService;
  PasswordEncoder passwordEncoder;
  PermissionsRepository permissionsRepository;

  public UserServiceImpl(
        UserRepository userRepository,
        AuthService authService,
        UserMapper userMapper,
        MapUserRoleRepository mapUserRoleRepository,
        StorageFileService storageFileService,
        PasswordEncoder passwordEncoder,
        PermissionsRepository permissionsRepository
  ) {
    super(userRepository, userMapper, authService);
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.mapUserRoleRepository = mapUserRoleRepository;
    this.storageFileService = storageFileService;
    this.passwordEncoder = passwordEncoder;
    this.permissionsRepository = permissionsRepository;
  }

  @Override
  public User create(UserDTO createDto) {
    if (userRepository.existsByEmailAndDeletedAtIsNull(createDto.getEmail()))
      throw new AppException(ErrorCode.EMAIL_EXISTED);

    var user = userMapper.toCreate(createDto);

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
    return userRepository.save(user);
  }

  @Override
  public User update(Integer id, UserDTO updateDto) {
    var user = getById(id);

    if (userRepository.existsByEmailAndIdNotAndDeletedAtIsNull(updateDto.getEmail(), id))
      throw new AppException(ErrorCode.EMAIL_EXISTED);

    userMapper.toUpdate(user, updateDto);

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

    return userRepository.save(user);
  }


  @Override
  protected void validateCreate(UserDTO create) {}

  @Override
  protected void validateUpdate(Integer id, UserDTO update) {}

  @Override
  protected void validateDelete(Integer id) {
    updateMapURIfUserDelete(id, getAuthId());
  }

  @Override
  protected RuntimeException createNotFoundException(Integer integer) {
    return new AppException(ErrorCode.USER_NOTFOUND);
  }


  private void updateMapURIfUserDelete(int userId, int authId) {
    List<MapUserRoles> mapUserRolesList = mapUserRoleRepository.findAllByUserId(userId);

    List<Integer> mapURIds = mapUserRolesList.stream()
                                             .map(MapUserRoles::getId)
                                             .toList();

    updatePermissionIfUserDelete(mapURIds, authId);

    for (MapUserRoles mapUserRoles : mapUserRolesList) {
      mapUserRoles.setDeletedAt(LocalDateTime.now());
      mapUserRoles.setUpdatedBy(authId);
      mapUserRoleRepository.save(mapUserRoles);
    }
  }

  private void updatePermissionIfUserDelete(Collection<Integer> mapUserRoleId, int authId) {
    List<Permissions> findAllPermissions = permissionsRepository.findAllByMapURId(mapUserRoleId);

    for (Permissions permission : findAllPermissions) {
      permission.setDeletedAt(LocalDateTime.now());
      permission.setUpdatedBy(authId);
      permissionsRepository.save(permission);
    }
  }
}
