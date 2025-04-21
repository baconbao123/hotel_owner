package com.hotel.webapp.service.admin;

import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.admin.request.MapURDTO;
import com.hotel.webapp.dto.admin.request.properties.MapURProperties;
import com.hotel.webapp.entity.HotelMapUserRoles;
import com.hotel.webapp.entity.HotelPermissions;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.HotelMapUserRoleRepository;
import com.hotel.webapp.repository.HotelPermissionsRepository;
import com.hotel.webapp.repository.HotelRoleRepository;
import com.hotel.webapp.repository.HotelUserRepository;
import com.hotel.webapp.service.admin.interfaces.AuthService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HotelMapUserRoleServiceImp extends BaseServiceImpl<HotelMapUserRoles, Integer, MapURDTO, HotelMapUserRoleRepository> {
  HotelRoleRepository hotelRoleRepository;
  HotelUserRepository hotelUserRepository;
  HotelPermissionsRepository hotelPermissionsRepository;

  public HotelMapUserRoleServiceImp(
        AuthService authService,
        HotelMapUserRoleRepository repository,
        HotelRoleRepository hotelRoleRepository,
        HotelUserRepository hotelUserRepository,
        HotelPermissionsRepository hotelPermissionsRepository
  ) {
    super(repository, authService);
    this.hotelRoleRepository = hotelRoleRepository;
    this.hotelUserRepository = hotelUserRepository;
    this.hotelPermissionsRepository = hotelPermissionsRepository;
  }

  @Override
  @Transactional
  public List<HotelMapUserRoles> createCollectionBulk(MapURDTO createDto) {
    // Validate role IDs exist and are active
    for (MapURProperties prop : createDto.getProperties()) {
      for (Integer roleId : prop.getRoleId()) {
        if (!hotelRoleRepository.existsByIdAndIsActiveIsTrueAndDeletedAtIsNull(roleId)) {
          throw new AppException(ErrorCode.ROLE_NOT_ACTIVE);
        }
      }
    }

    // Validate user IDs exist and are active
    for (MapURProperties prop : createDto.getProperties()) {
      if (!hotelUserRepository.existsByIdAndIsActiveIsTrueAndDeletedAtIsNull(prop.getUserId())) {
        throw new AppException(ErrorCode.USER_NOT_ACTIVE);
      }
    }

    // Create user-role mappings
    List<HotelMapUserRoles> listURs = new ArrayList<>();
    for (MapURProperties prop : createDto.getProperties()) {
      for (Integer roleId : prop.getRoleId()) {
        var mapUserRoles = new HotelMapUserRoles();
        mapUserRoles.setUserId(prop.getUserId());
        mapUserRoles.setRoleId(roleId);
        mapUserRoles.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        mapUserRoles.setCreatedBy(getAuthId());
        listURs.add(mapUserRoles);
      }
    }

    List<HotelMapUserRoles> savedMappings = repository.saveAll(listURs);
    if (savedMappings.isEmpty()) {
      throw new AppException(ErrorCode.CREATION_FAILED);
    }

    return savedMappings;
  }

  @Override
  @Transactional
  public List<HotelMapUserRoles> updateCollectionBulk(Integer id, MapURDTO updateDto) {
    getById(id);
    // 1. Collect userIds and roleIds
    Set<Integer> userIds = updateDto.getProperties().stream()
                                    .map(MapURProperties::getUserId)
                                    .collect(Collectors.toSet());

    Set<Integer> roleIds = updateDto.getProperties().stream()
                                    .flatMap(prop -> prop.getRoleId().stream())
                                    .collect(Collectors.toSet());

    // 2. Validate user
    for (Integer userId : userIds) {
      if (!hotelUserRepository.existsByIdAndIsActiveIsTrueAndDeletedAtIsNull(userId)) {
        throw new AppException(ErrorCode.USER_NOT_ACTIVE);
      }
    }

    // 3. Validate role
    for (Integer roleId : roleIds) {
      if (!hotelRoleRepository.existsByIdAndIsActiveIsTrueAndDeletedAtIsNull(roleId)) {
        throw new AppException(ErrorCode.ROLE_NOT_ACTIVE);
      }
    }

    // 4. Find all old Map User Role
    List<HotelMapUserRoles> oldMappings = repository.findAllByUserIdInAndDeletedAtIsNull(userIds);

    // 5. Delete at old user role + permission contain old map user role
    for (HotelMapUserRoles old : oldMappings) {
      old.setDeletedAt(LocalDateTime.now());
      updatePermissionsIfMapURUpdate(old.getId());
    }
    repository.saveAll(oldMappings);

    // 6. Tạo bản ghi mới
    List<HotelMapUserRoles> newMappings = new ArrayList<>();
    for (MapURProperties prop : updateDto.getProperties()) {
      for (Integer roleId : prop.getRoleId()) {
        HotelMapUserRoles newMap = HotelMapUserRoles.builder()
                                                    .userId(prop.getUserId())
                                                    .roleId(roleId)
                                                    .createdAt(new Timestamp(System.currentTimeMillis()))
                                                    .createdBy(getAuthId())
                                                    .build();
        newMappings.add(newMap);
      }
    }
    List<HotelMapUserRoles> savedMappings = repository.saveAll(newMappings);

    if (savedMappings.isEmpty()) {
      throw new AppException(ErrorCode.UPDATED_FAILED);
    }

    return savedMappings;
  }


  private void updatePermissionsIfMapURUpdate(Integer oldMapId) {
    List<HotelPermissions> permissions = hotelPermissionsRepository.findByMapUserRolesId(oldMapId);

    for (HotelPermissions permission : permissions) {
      permission.setDeletedAt(LocalDateTime.now());
      hotelPermissionsRepository.save(permission);
    }
  }

  @Override
  protected void validateCreate(MapURDTO create) {

  }

  @Override
  protected void validateUpdate(Integer id, MapURDTO update) {

  }

  @Override
  protected void validateDelete(Integer integer) {

  }

  @Override
  protected RuntimeException createNotFoundException(Integer integer) {
    return new AppException(ErrorCode.MAPPING_UR_NOTFOUND);
  }
}
