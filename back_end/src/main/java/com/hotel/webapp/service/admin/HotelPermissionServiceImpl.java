package com.hotel.webapp.service.admin;

import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.admin.request.PermissionDTO;
import com.hotel.webapp.dto.admin.request.properties.PermissionProperties;
import com.hotel.webapp.entity.HotelPermissions;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.HotelMapResourceActionRepository;
import com.hotel.webapp.repository.HotelMapUserRoleRepository;
import com.hotel.webapp.repository.HotelPermissionsRepository;
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
public class HotelPermissionServiceImpl extends BaseServiceImpl<HotelPermissions, Integer, PermissionDTO, HotelPermissionsRepository> {
  HotelMapResourceActionRepository hotelMapResourceActionRepository;
  HotelMapUserRoleRepository hotelMapUserRoleRepository;

  public HotelPermissionServiceImpl(
        AuthService authService,
        HotelPermissionsRepository repository,
        HotelMapResourceActionRepository hotelMapResourceActionRepository,
        HotelMapUserRoleRepository hotelMapUserRoleRepository
  ) {
    super(repository, authService);
    this.hotelMapResourceActionRepository = hotelMapResourceActionRepository;
    this.hotelMapUserRoleRepository = hotelMapUserRoleRepository;
  }

  @Override
  public List<HotelPermissions> createCollectionBulk(PermissionDTO createDto) {
    for (PermissionProperties properties : createDto.getProperties()) {
      for (Integer mapResourcesActionId : properties.getMapResourcesActionId()) {
        if (!hotelMapResourceActionRepository.existsByIdAndDeletedAtIsNull(mapResourcesActionId))
          throw new AppException(ErrorCode.MAPPING_RA_NOT_ACTIVE);
      }
    }

    for (PermissionProperties properties : createDto.getProperties()) {
      if (!hotelMapUserRoleRepository.existsByIdAndDeletedAtIsNull(properties.getMapUserRolesId()))
        throw new AppException(ErrorCode.MAPPING_UR_NOT_ACTIVE);
    }

    List<HotelPermissions> listPermissions = new ArrayList<>();
    for (PermissionProperties properties : createDto.getProperties()) {
      for (Integer mapResourcesActionId : properties.getMapResourcesActionId()) {
        var permission = new HotelPermissions();
        permission.setMapResourcesActionId(mapResourcesActionId);
        permission.setMapUserRolesId(properties.getMapUserRolesId());
        permission.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        permission.setCreatedBy(getAuthId());
        listPermissions.add(permission);
      }
    }

    List<HotelPermissions> savedPermissions = repository.saveAll(listPermissions);
    if (savedPermissions.isEmpty())
      throw new AppException(ErrorCode.CREATION_FAILED);

    return savedPermissions;
  }

  @Override
  public List<HotelPermissions> updateCollectionBulk(Integer id, PermissionDTO updateDto) {
    getById(id);
    // Valid map user role
    Set<Integer> mapURs = updateDto.getProperties()
                                   .stream()
                                   .map(PermissionProperties::getMapUserRolesId)
                                   .collect(Collectors.toSet());

    for (Integer mapURIds : mapURs) {
      if (!hotelMapUserRoleRepository.existsByIdAndDeletedAtIsNull(mapURIds))
        throw new AppException(ErrorCode.MAPPING_UR_NOT_ACTIVE);
    }

    // Valid map resource action
    Set<Integer> mapRAIds = updateDto.getProperties()
                                     .stream()
                                     .flatMap(prop -> prop.getMapResourcesActionId().stream())
                                     .collect(Collectors.toSet());

    for (Integer mapRAId : mapRAIds) {
      if (!hotelMapResourceActionRepository.existsByIdAndDeletedAtIsNull(mapRAId))
        throw new AppException(ErrorCode.MAPPING_RA_NOT_ACTIVE);
    }

    //    Delete At old permission
    List<HotelPermissions> oldMappings = repository.findAllByMapURId(mapURs);

    for (HotelPermissions hotelPermissions : oldMappings) {
      hotelPermissions.setDeletedAt(LocalDateTime.now());
      repository.save(hotelPermissions);
    }

    //    Create new permission
    List<HotelPermissions> newPermissions = new ArrayList<>();
    for (PermissionProperties prop : updateDto.getProperties()) {
      for (Integer mapResourcesActionId : prop.getMapResourcesActionId()) {
        HotelPermissions permission = HotelPermissions.builder()
                                                      .mapResourcesActionId(mapResourcesActionId)
                                                      .mapUserRolesId(prop.getMapUserRolesId())
                                                      .updatedAt(new Timestamp(System.currentTimeMillis()))
                                                      .updatedBy(getAuthId())
                                                      .build();
        newPermissions.add(permission);
      }
    }

    List<HotelPermissions> savedMappings = repository.saveAll(newPermissions);
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
