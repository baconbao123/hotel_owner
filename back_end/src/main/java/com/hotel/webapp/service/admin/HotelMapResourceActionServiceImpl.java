package com.hotel.webapp.service.admin;

import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.admin.request.MapRADTO;
import com.hotel.webapp.dto.admin.request.properties.MapRAPropertiesDTO;
import com.hotel.webapp.entity.HotelMapResourcesAction;
import com.hotel.webapp.entity.HotelPermissions;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.HotelActionRepository;
import com.hotel.webapp.repository.HotelMapResourceActionRepository;
import com.hotel.webapp.repository.HotelPermissionsRepository;
import com.hotel.webapp.repository.HotelResourcesRepository;
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
public class HotelMapResourceActionServiceImpl extends BaseServiceImpl<HotelMapResourcesAction, Integer, MapRADTO,
      HotelMapResourceActionRepository> {
  HotelResourcesRepository hotelResourcesRepository;
  HotelActionRepository hotelActionRepository;
  HotelPermissionsRepository hotelPermissionsRepository;

  public HotelMapResourceActionServiceImpl(
        HotelMapResourceActionRepository repository,
        HotelResourcesRepository hotelResourcesRepository,
        HotelActionRepository hotelActionRepository,
        HotelPermissionsRepository hotelPermissionsRepository,
        AuthService authService
  ) {
    super(repository, authService);
    this.hotelResourcesRepository = hotelResourcesRepository;
    this.hotelActionRepository = hotelActionRepository;
    this.hotelPermissionsRepository = hotelPermissionsRepository;
  }

  @Override
  @Transactional
  public List<HotelMapResourcesAction> createCollectionBulk(MapRADTO createDto) {
    for (MapRAPropertiesDTO prop : createDto.getProperties()) {
      for (Integer actionId : prop.getActionId()) {
        if (!hotelActionRepository.existsByIdAndDeletedAtIsNull(actionId))
          throw new AppException(ErrorCode.ACTION_NOT_ACTIVE);
      }
    }

    for (MapRAPropertiesDTO prop : createDto.getProperties()) {
      if (!hotelResourcesRepository.existsByIdAndDeletedAtIsNull(prop.getResourceId()))
        throw new AppException(ErrorCode.RESOURCE_NOT_ACTIVE);
    }

    // create new resource-action mapping
    List<HotelMapResourcesAction> listRAs = new ArrayList<>();
    for (MapRAPropertiesDTO prop : createDto.getProperties()) {
      for (Integer actionIds : prop.getActionId()) {
        var mapRA = new HotelMapResourcesAction();
        mapRA.setResourceId(prop.getResourceId());
        mapRA.setActionId(actionIds);
        mapRA.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        mapRA.setCreatedBy(getAuthId());
        listRAs.add(mapRA);
      }
    }

    var saveMappings = repository.saveAll(listRAs);
    if (saveMappings.isEmpty()) {
      throw new AppException(ErrorCode.CREATION_FAILED);
    }
    return saveMappings;
  }

  @Override
  @Transactional
  public List<HotelMapResourcesAction> updateCollectionBulk(Integer id, MapRADTO updateDto) {
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
      if (!hotelResourcesRepository.existsByIdAndDeletedAtIsNull(resourceId))
        throw new AppException(ErrorCode.RESOURCE_NOT_ACTIVE);
    }

    for (Integer actionId : actionIds) {
      if (!hotelActionRepository.existsByIdAndDeletedAtIsNull(actionId)) {
        throw new AppException(ErrorCode.ACTION_NOT_ACTIVE);
      }
    }

    // find all old mapping
    List<HotelMapResourcesAction> oldMappings = repository.findAllByResourceIdInAndDeletedAtIsNull(resourceIds);

    // Delete at existing old mappings
    for (HotelMapResourcesAction oldMapping : oldMappings) {
      oldMapping.setDeletedAt(LocalDateTime.now());
      updatePermissionIfMapRAUpdate(oldMapping.getId());
    }

    repository.saveAll(oldMappings);

    // Create new mapping
    List<HotelMapResourcesAction> newMapping = new ArrayList<>();
    for (MapRAPropertiesDTO prop : updateDto.getProperties()) {
      for (Integer actionId : prop.getActionId()) {
        HotelMapResourcesAction mapRA = HotelMapResourcesAction.builder()
                                                               .resourceId(prop.getResourceId())
                                                               .actionId(actionId)
                                                               .updatedAt(new Timestamp(System.currentTimeMillis()))
                                                               .updatedBy(getAuthId())
                                                               .build();
        newMapping.add(mapRA);
      }
    }

    List<HotelMapResourcesAction> savedMappings = repository.saveAll(newMapping);

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
    List<HotelPermissions> oldPermissions = hotelPermissionsRepository.findByMapResourcesActionId(oldMapRAId);

    for (HotelPermissions oldPermission : oldPermissions) {
      oldPermission.setDeletedAt(LocalDateTime.now());
      hotelPermissionsRepository.save(oldPermission);
    }
  }


}
