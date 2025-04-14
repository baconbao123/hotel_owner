package com.hotel.webapp.service.admin;

import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.admin.request.ActionResourceDTO;
import com.hotel.webapp.entity.MapResourcesAction;
import com.hotel.webapp.entity.Permissions;
import com.hotel.webapp.entity.Resources;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.mapper.admin.ResourceMapper;
import com.hotel.webapp.repository.MapResourceActionRepository;
import com.hotel.webapp.repository.PermissionsRepository;
import com.hotel.webapp.repository.ResourcesRepository;
import com.hotel.webapp.service.admin.interfaces.AuthService;
import com.hotel.webapp.util.ValidateDataInput;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResourceServiceImpl extends BaseServiceImpl<Resources, Integer, ActionResourceDTO, ResourcesRepository> {
  AuthService authService;
  ValidateDataInput validateDataInput;
  ResourceMapper resourceMapper;
  ResourcesRepository resourcesRepository;
  MapResourceActionRepository mapResourceActionRepository;
  PermissionsRepository permissionsRepository;

  public ResourceServiceImpl(
        ResourceMapper resourceMapper,
        AuthService authService,
        ValidateDataInput validateDataInput,
        ResourcesRepository resourcesRepository,
        MapResourceActionRepository mapResourceActionRepository,
        PermissionsRepository permissionsRepository
  ) {
    super(resourcesRepository, resourceMapper, authService);
    this.authService = authService;
    this.validateDataInput = validateDataInput;
    this.resourceMapper = resourceMapper;
    this.resourcesRepository = resourcesRepository;
    this.mapResourceActionRepository = mapResourceActionRepository;
    this.permissionsRepository = permissionsRepository;
  }

  @Override
  protected void validateCreate(ActionResourceDTO create) {
    if (resourcesRepository.existsByNameAndDeletedAtIsNull(create.getName()))
      throw new AppException(ErrorCode.RESOURCE_EXISTED);
    create.setName(validateDataInput.capitalizeFirstLetter(create.getName()));
  }

  @Override
  protected void validateUpdate(Integer id, ActionResourceDTO update) {
    if (resourcesRepository.existsByNameAndIdNotAndDeletedAtIsNull(update.getName(), id))
      throw new AppException(ErrorCode.RESOURCE_EXISTED);
    update.setName(validateDataInput.capitalizeFirstLetter(update.getName()));
  }

  @Override
  protected void validateDelete(Integer id) {
    updateMapRAIfResourceDelete(id, getAuthId());
  }

  @Override
  protected RuntimeException createNotFoundException(Integer integer) {
    return new AppException(ErrorCode.RESOURCE_NOTFOUND);
  }

  private void updateMapRAIfResourceDelete(int resourceId, int authId) {
    List<MapResourcesAction> mapRAList = mapResourceActionRepository.findAllByResourceId(resourceId);

    List<Integer> mapRAIds = mapRAList.stream()
                                      .map(MapResourcesAction::getId)
                                      .toList();

    updatePermissionIfActionDelete(mapRAIds, authId);

    for (MapResourcesAction mapRA : mapRAList) {
      mapRA.setDeletedAt(LocalDateTime.now());
      mapRA.setUpdatedBy(authId);
      mapResourceActionRepository.save(mapRA);
    }
  }

  private void updatePermissionIfActionDelete(Collection<Integer> mapRAs, int authId) {
    List<Permissions> findAllPermissions = permissionsRepository.findAllByMapRAId(mapRAs);

    for (Permissions permission : findAllPermissions) {
      permission.setDeletedAt(LocalDateTime.now());
      permission.setUpdatedBy(authId);
      permissionsRepository.save(permission);
    }
  }

}
