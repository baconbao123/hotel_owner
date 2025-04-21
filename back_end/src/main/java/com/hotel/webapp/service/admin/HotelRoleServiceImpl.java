package com.hotel.webapp.service.admin;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.admin.request.RoleDTO;
import com.hotel.webapp.entity.HotelMapUserRoles;
import com.hotel.webapp.entity.HotelPermissions;
import com.hotel.webapp.entity.HotelRoles;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.HotelMapUserRoleRepository;
import com.hotel.webapp.repository.HotelPermissionsRepository;
import com.hotel.webapp.repository.HotelRoleRepository;
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
public class HotelRoleServiceImpl extends BaseServiceImpl<HotelRoles, Integer, RoleDTO, HotelRoleRepository> {
  ValidateDataInput validateDataInput;
  HotelMapUserRoleRepository hotelMapUserRoleRepository;
  HotelPermissionsRepository hotelPermissionsRepository;

  public HotelRoleServiceImpl(
        HotelRoleRepository repository,
        AuthService authService,
        ValidateDataInput validateDataInput,
        BaseMapper<HotelRoles, RoleDTO> mapper,
        HotelMapUserRoleRepository hotelMapUserRoleRepository,
        HotelPermissionsRepository hotelPermissionsRepository
  ) {
    super(repository, mapper, authService);
    this.validateDataInput = validateDataInput;
    this.hotelMapUserRoleRepository = hotelMapUserRoleRepository;
    this.hotelPermissionsRepository = hotelPermissionsRepository;
  }

  @Override
  protected void validateCreate(RoleDTO create) {
    if (repository.existsByNameAndDeletedAtIsNull(create.getName()))
      throw new AppException(ErrorCode.ROLE_EXISTED);

    create.setName(validateDataInput.capitalizeFirstLetter(create.getName()));
  }

  @Override
  protected void validateUpdate(Integer id, RoleDTO update) {
    if (repository.existsByNameAndIdNotAndDeletedAtIsNull(update.getName(), id))
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
    List<HotelMapUserRoles> hotelMapUserRolesList = hotelMapUserRoleRepository.findAllByRoleId(roleId);

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
