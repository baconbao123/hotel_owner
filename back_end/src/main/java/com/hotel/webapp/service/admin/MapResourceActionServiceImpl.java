package com.hotel.webapp.service.admin;

import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.admin.request.MapRADTO;
import com.hotel.webapp.dto.admin.request.properties.MapRAPropertiesDTO;
import com.hotel.webapp.entity.MapResourcesAction;
import com.hotel.webapp.entity.Permissions;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.ActionRepository;
import com.hotel.webapp.repository.MapResourceActionRepository;
import com.hotel.webapp.repository.PermissionsRepository;
import com.hotel.webapp.repository.ResourcesRepository;
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
public class MapResourceActionServiceImpl extends BaseServiceImpl<MapResourcesAction, Integer, MapRADTO,
      MapResourceActionRepository> {
  MapResourceActionRepository mapRARepository;
  ResourcesRepository resourcesRepository;
  ActionRepository actionRepository;
  PermissionsRepository permissionsRepository;
  AuthService authService;

  public MapResourceActionServiceImpl(
        MapResourceActionRepository mapRARepository,
        ResourcesRepository resourcesRepository,
        ActionRepository actionRepository,
        PermissionsRepository permissionsRepository,
        AuthService authService
  ) {
    super(mapRARepository, authService);
    this.mapRARepository = mapRARepository;
    this.resourcesRepository = resourcesRepository;
    this.actionRepository = actionRepository;
    this.permissionsRepository = permissionsRepository;
    this.authService = authService;
  }

  @Override
  @Transactional
  public List<MapResourcesAction> createCollectionBulk(MapRADTO createDto) {
    for (MapRAPropertiesDTO prop : createDto.getProperties()) {
      for (Integer actionId : prop.getActionId()) {
        if (!actionRepository.existsByIdAndDeletedAtIsNull(actionId))
          throw new AppException(ErrorCode.ACTION_NOT_ACTIVE);
      }
    }

    for (MapRAPropertiesDTO prop : createDto.getProperties()) {
      if (!resourcesRepository.existsByIdAndDeletedAtIsNull(prop.getResourceId()))
        throw new AppException(ErrorCode.RESOURCE_NOT_ACTIVE);
    }

    // create new resource-action mapping
    List<MapResourcesAction> listRAs = new ArrayList<>();
    for (MapRAPropertiesDTO prop : createDto.getProperties()) {
      for (Integer actionIds : prop.getActionId()) {
        var mapRA = new MapResourcesAction();
        mapRA.setResourceId(prop.getResourceId());
        mapRA.setActionId(actionIds);
        mapRA.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        mapRA.setCreatedBy(getAuthId());
        listRAs.add(mapRA);
      }
    }

    var saveMappings = mapRARepository.saveAll(listRAs);
    if (saveMappings.isEmpty()) {
      throw new AppException(ErrorCode.CREATION_FAILED);
    }
    return saveMappings;
  }

  @Override
  @Transactional
  public List<MapResourcesAction> updateCollectionBulk(Integer id, MapRADTO updateDto) {
    getById(id);
    // Collect resourceIds and actionIds
    Set<Integer> resourceIds = updateDto.getProperties()
                                        .stream()
                                        .map(MapRAPropertiesDTO::getResourceId)
                                        .collect(Collectors.toSet());

    Set<Integer> actionIds = updateDto.getProperties().stream()
                                      .flatMap(prop -> prop.getActionId().stream())
                                      .collect(Collectors.toSet());

    //  Valid resource
    for (Integer resourceId : resourceIds) {
      if (!resourcesRepository.existsByIdAndDeletedAtIsNull(resourceId))
        throw new AppException(ErrorCode.RESOURCE_NOT_ACTIVE);
    }

    for (Integer actionId : actionIds) {
      if (!actionRepository.existsByIdAndDeletedAtIsNull(actionId)) {
        throw new AppException(ErrorCode.ACTION_NOT_ACTIVE);
      }
    }

    // find all old mapping
    List<MapResourcesAction> oldMappings = mapRARepository.findAllByResourceIdInAndDeletedAtIsNull(resourceIds);

    // Delete at existing old mappings
    for (MapResourcesAction oldMapping : oldMappings) {
      oldMapping.setDeletedAt(LocalDateTime.now());
      updatePermissionIfMapRAUpdate(oldMapping.getId());
    }

    mapRARepository.saveAll(oldMappings);

    // Create new mapping
    List<MapResourcesAction> newMapping = new ArrayList<>();
    for (MapRAPropertiesDTO prop : updateDto.getProperties()) {
      for (Integer actionId : prop.getActionId()) {
        MapResourcesAction mapRA = MapResourcesAction.builder()
                                                     .resourceId(prop.getResourceId())
                                                     .actionId(actionId)
                                                     .updatedAt(new Timestamp(System.currentTimeMillis()))
                                                     .updatedBy(getAuthId())
                                                     .build();
        newMapping.add(mapRA);
      }
    }

    List<MapResourcesAction> savedMappings = mapRARepository.saveAll(newMapping);

    if (savedMappings.isEmpty()) {
      throw new AppException(ErrorCode.UPDATED_FAILED);
    }

    return savedMappings;
  }

  @Override
  protected void validateCreate(MapRADTO create) {

  }

  @Override
  protected void validateUpdate(Integer id, MapRADTO update) {

  }

  @Override
  protected void validateDelete(Integer integer) {

  }

  @Override
  protected RuntimeException createNotFoundException(Integer integer) {
    return new AppException(ErrorCode.MAPPING_RA_NOTFOUND);
  }

  private void updatePermissionIfMapRAUpdate(int oldMapRAId) {
    List<Permissions> oldPermissions = permissionsRepository.findByMapResourcesActionId(oldMapRAId);

    for (Permissions oldPermission : oldPermissions) {
      oldPermission.setDeletedAt(LocalDateTime.now());
      permissionsRepository.save(oldPermission);
    }
  }


}
