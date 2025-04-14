package com.hotel.webapp.service.admin;

import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.admin.request.RoleDTO;
import com.hotel.webapp.entity.MapUserRoles;
import com.hotel.webapp.entity.Permissions;
import com.hotel.webapp.entity.Role;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.mapper.admin.RoleMapper;
import com.hotel.webapp.repository.MapUserRoleRepository;
import com.hotel.webapp.repository.PermissionsRepository;
import com.hotel.webapp.repository.RoleRepository;
import com.hotel.webapp.service.admin.interfaces.AuthService;
import com.hotel.webapp.util.ValidateDataInput;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl extends BaseServiceImpl<Role, Integer, RoleDTO, RoleRepository> {
  ValidateDataInput validateDataInput;
  RoleRepository roleRepository;
  RoleMapper roleMapper;
  MapUserRoleRepository mapUserRoleRepository;
  PermissionsRepository permissionsRepository;

  public RoleServiceImpl(
        RoleRepository roleRepository,
        AuthService authService,
        ValidateDataInput validateDataInput,
        RoleMapper roleMapper,
        MapUserRoleRepository mapUserRoleRepository,
        PermissionsRepository permissionsRepository
  ) {
    super(roleRepository, roleMapper, authService);
    this.validateDataInput = validateDataInput;
    this.roleRepository = roleRepository;
    this.roleMapper = roleMapper;
    this.mapUserRoleRepository = mapUserRoleRepository;
    this.permissionsRepository = permissionsRepository;
  }

  @Override
  protected void validateCreate(RoleDTO create) {
    if (roleRepository.existsByNameAndDeletedAtIsNull(create.getName()))
      throw new AppException(ErrorCode.ROLE_EXISTED);

    create.setName(validateDataInput.capitalizeFirstLetter(create.getName()));
  }

  @Override
  protected void validateUpdate(Integer id, RoleDTO update) {
    if (roleRepository.existsByNameAndIdNotAndDeletedAtIsNull(update.getName(), id))
      throw new AppException(ErrorCode.ROLE_EXISTED);

    update.setName(validateDataInput.capitalizeFirstLetter(update.getName()));
  }

  @Override
  protected RuntimeException createNotFoundException(Integer integer) {
    return new AppException(ErrorCode.ROLE_NOTFOUND);
  }

  @Override
  protected void validateDelete(Integer id) {
    updateMapURIfRoleDelete(id, getAuthId());
  }

  private void updateMapURIfRoleDelete(int roleId, int authId) {
    List<MapUserRoles> mapUserRolesList = mapUserRoleRepository.findAllByRoleId(roleId);

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
