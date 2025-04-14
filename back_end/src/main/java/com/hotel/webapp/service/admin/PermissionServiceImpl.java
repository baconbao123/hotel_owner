package com.hotel.webapp.service.admin;

import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.admin.request.PermissionDTO;
import com.hotel.webapp.dto.admin.request.properties.PermissionProperties;
import com.hotel.webapp.entity.Permissions;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.MapResourceActionRepository;
import com.hotel.webapp.repository.MapUserRoleRepository;
import com.hotel.webapp.repository.PermissionsRepository;
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
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionServiceImpl extends BaseServiceImpl<Permissions, Integer, PermissionDTO, PermissionsRepository> {
  PermissionsRepository permissionsRepository;
  MapResourceActionRepository mapResourceActionRepository;
  MapUserRoleRepository mapUserRoleRepository;

  public PermissionServiceImpl(
        AuthService authService,
        PermissionsRepository permissionsRepository,
        MapResourceActionRepository mapResourceActionRepository,
        MapUserRoleRepository mapUserRoleRepository
  ) {
    super(permissionsRepository, authService);
    this.permissionsRepository = permissionsRepository;
    this.mapResourceActionRepository = mapResourceActionRepository;
    this.mapUserRoleRepository = mapUserRoleRepository;
  }

  @Override
  public List<Permissions> createCollectionBulk(PermissionDTO createDto) {
    for (PermissionProperties properties : createDto.getProperties()) {
      for (Integer mapResourcesActionId : properties.getMapResourcesActionId()) {
        if (!mapResourceActionRepository.existsByIdAndDeletedAtIsNull(mapResourcesActionId))
          throw new AppException(ErrorCode.MAPPING_RA_NOT_ACTIVE);
      }
    }

    for (PermissionProperties properties : createDto.getProperties()) {
      if (!mapUserRoleRepository.existsByIdAndDeletedAtIsNull(properties.getMapUserRolesId()))
        throw new AppException(ErrorCode.MAPPING_UR_NOT_ACTIVE);
    }

    List<Permissions> listPermissions = new ArrayList<>();
    for (PermissionProperties properties : createDto.getProperties()) {
      for (Integer mapResourcesActionId : properties.getMapResourcesActionId()) {
        var permission = new Permissions();
        permission.setMapResourcesActionId(mapResourcesActionId);
        permission.setMapUserRolesId(properties.getMapUserRolesId());
        permission.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        permission.setCreatedBy(getAuthId());
        listPermissions.add(permission);
      }
    }

    List<Permissions> savedPermissions = permissionsRepository.saveAll(listPermissions);
    if (savedPermissions.isEmpty())
      throw new AppException(ErrorCode.CREATION_FAILED);

    return savedPermissions;
  }

  @Override
  public List<Permissions> updateCollectionBulk(Integer id, PermissionDTO updateDto) {
    getById(id);
    // Valid map user role
    Set<Integer> mapURs = updateDto.getProperties()
                                   .stream()
                                   .map(PermissionProperties::getMapUserRolesId)
                                   .collect(Collectors.toSet());

    for (Integer mapURIds : mapURs) {
      if (!mapUserRoleRepository.existsByIdAndDeletedAtIsNull(mapURIds))
        throw new AppException(ErrorCode.MAPPING_UR_NOT_ACTIVE);
    }

    // Valid map resource action
    Set<Integer> mapRAIds = updateDto.getProperties()
                                     .stream()
                                     .flatMap(prop -> prop.getMapResourcesActionId().stream())
                                     .collect(Collectors.toSet());

    for (Integer mapRAId : mapRAIds) {
      if (!mapResourceActionRepository.existsByIdAndDeletedAtIsNull(mapRAId))
        throw new AppException(ErrorCode.MAPPING_RA_NOT_ACTIVE);
    }

    //    Delete At old permission
    List<Permissions> oldMappings = permissionsRepository.findAllByMapURId(mapURs);

    for (Permissions permissions : oldMappings) {
      permissions.setDeletedAt(LocalDateTime.now());
      permissionsRepository.save(permissions);
    }

    //    Create new permission
    List<Permissions> newPermissions = new ArrayList<>();
    for (PermissionProperties prop : updateDto.getProperties()) {
      for (Integer mapResourcesActionId : prop.getMapResourcesActionId()) {
        Permissions permission = Permissions.builder()
                                            .mapResourcesActionId(mapResourcesActionId)
                                            .mapUserRolesId(prop.getMapUserRolesId())
                                            .updatedAt(new Timestamp(System.currentTimeMillis()))
                                            .updatedBy(getAuthId())
                                            .build();
        newPermissions.add(permission);
      }
    }

    List<Permissions> savedMappings = permissionsRepository.saveAll(newPermissions);
    if (savedMappings.isEmpty()) {
      throw new AppException(ErrorCode.CREATION_FAILED);
    }

    return savedMappings;
  }


  @Override
  protected void validateCreate(PermissionDTO create) {

  }

  @Override
  protected void validateUpdate(Integer id, PermissionDTO update) {

  }

  @Override
  protected void validateDelete(Integer integer) {

  }

  @Override
  protected RuntimeException createNotFoundException(Integer integer) {
    return new AppException(ErrorCode.PERMISSION_NOTFOUND);
  }
}
