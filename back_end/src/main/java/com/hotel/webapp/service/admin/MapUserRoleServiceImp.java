package com.hotel.webapp.service.admin;

import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.admin.request.MapURDTO;
import com.hotel.webapp.dto.admin.request.properties.MapURProperties;
import com.hotel.webapp.entity.MapUserRoles;
import com.hotel.webapp.entity.Permissions;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.MapUserRoleRepository;
import com.hotel.webapp.repository.PermissionsRepository;
import com.hotel.webapp.repository.RoleRepository;
import com.hotel.webapp.repository.UserRepository;
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
public class MapUserRoleServiceImp extends BaseServiceImpl<MapUserRoles, Integer, MapURDTO, MapUserRoleRepository> {
  MapUserRoleRepository mapUserRoleRepository;
  RoleRepository roleRepository;
  UserRepository userRepository;
  PermissionsRepository permissionsRepository;

  public MapUserRoleServiceImp(
        AuthService authService,
        MapUserRoleRepository mapUserRoleRepository,
        RoleRepository roleRepository,
        UserRepository userRepository,
        PermissionsRepository permissionsRepository
  ) {
    super(mapUserRoleRepository, authService);
    this.mapUserRoleRepository = mapUserRoleRepository;
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
    this.permissionsRepository = permissionsRepository;
  }

  @Override
  @Transactional
  public List<MapUserRoles> createCollectionBulk(MapURDTO createDto) {
    // Validate role IDs exist and are active
    for (MapURProperties prop : createDto.getProperties()) {
      for (Integer roleId : prop.getRoleId()) {
        if (!roleRepository.existsByIdAndIsActiveIsTrueAndDeletedAtIsNull(roleId)) {
          throw new AppException(ErrorCode.ROLE_NOT_ACTIVE);
        }
      }
    }

    // Validate user IDs exist and are active
    for (MapURProperties prop : createDto.getProperties()) {
      if (!userRepository.existsByIdAndIsActiveIsTrueAndDeletedAtIsNull(prop.getUserId())) {
        throw new AppException(ErrorCode.USER_NOT_ACTIVE);
      }
    }

    // Create user-role mappings
    List<MapUserRoles> listURs = new ArrayList<>();
    for (MapURProperties prop : createDto.getProperties()) {
      for (Integer roleId : prop.getRoleId()) {
        var mapUserRoles = new MapUserRoles();
        mapUserRoles.setUserId(prop.getUserId());
        mapUserRoles.setRoleId(roleId);
        mapUserRoles.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        mapUserRoles.setCreatedBy(getAuthId());
        listURs.add(mapUserRoles);
      }
    }

    List<MapUserRoles> savedMappings = mapUserRoleRepository.saveAll(listURs);
    if (savedMappings.isEmpty()) {
      throw new AppException(ErrorCode.CREATION_FAILED);
    }

    return savedMappings;
  }

  @Override
  @Transactional
  public List<MapUserRoles> updateCollectionBulk(Integer id, MapURDTO updateDto) {
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
      if (!userRepository.existsByIdAndIsActiveIsTrueAndDeletedAtIsNull(userId)) {
        throw new AppException(ErrorCode.USER_NOT_ACTIVE);
      }
    }

    // 3. Validate role
    for (Integer roleId : roleIds) {
      if (!roleRepository.existsByIdAndIsActiveIsTrueAndDeletedAtIsNull(roleId)) {
        throw new AppException(ErrorCode.ROLE_NOT_ACTIVE);
      }
    }

    // 4. Find all old Map User Role
    List<MapUserRoles> oldMappings = mapUserRoleRepository.findAllByUserIdInAndDeletedAtIsNull(userIds);

    // 5. Delete at old user role + permission contain old map user role
    for (MapUserRoles old : oldMappings) {
      old.setDeletedAt(LocalDateTime.now());
      updatePermissionsIfMapURUpdate(old.getId());
    }
    mapUserRoleRepository.saveAll(oldMappings);

    // 6. Tạo bản ghi mới
    List<MapUserRoles> newMappings = new ArrayList<>();
    for (MapURProperties prop : updateDto.getProperties()) {
      for (Integer roleId : prop.getRoleId()) {
        MapUserRoles newMap = MapUserRoles.builder()
                                          .userId(prop.getUserId())
                                          .roleId(roleId)
                                          .createdAt(new Timestamp(System.currentTimeMillis()))
                                          .createdBy(getAuthId())
                                          .build();
        newMappings.add(newMap);
      }
    }
    List<MapUserRoles> savedMappings = mapUserRoleRepository.saveAll(newMappings);

    if (savedMappings.isEmpty()) {
      throw new AppException(ErrorCode.UPDATED_FAILED);
    }

    return savedMappings;
  }


  private void updatePermissionsIfMapURUpdate(Integer oldMapId) {
    List<Permissions> permissions = permissionsRepository.findByMapUserRolesId(oldMapId);

    for (Permissions permission : permissions) {
      permission.setDeletedAt(LocalDateTime.now());
      permissionsRepository.save(permission);
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
