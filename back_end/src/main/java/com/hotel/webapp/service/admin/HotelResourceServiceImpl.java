package com.hotel.webapp.service.admin;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.admin.request.NameDTO;
import com.hotel.webapp.entity.HotelMapResourcesAction;
import com.hotel.webapp.entity.HotelPermissions;
import com.hotel.webapp.entity.HotelResources;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.HotelMapResourceActionRepository;
import com.hotel.webapp.repository.HotelPermissionsRepository;
import com.hotel.webapp.repository.HotelResourcesRepository;
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
public class HotelResourceServiceImpl extends BaseServiceImpl<HotelResources, Integer, NameDTO, HotelResourcesRepository> {
  ValidateDataInput validateDataInput;
  HotelMapResourceActionRepository hotelMapResourceActionRepository;
  HotelPermissionsRepository hotelPermissionsRepository;

  public HotelResourceServiceImpl(
        BaseMapper<HotelResources, NameDTO> mapper,
        AuthService authService,
        ValidateDataInput validateDataInput,
        HotelResourcesRepository repository,
        HotelMapResourceActionRepository hotelMapResourceActionRepository,
        HotelPermissionsRepository hotelPermissionsRepository
  ) {
    super(repository, mapper, authService);
    this.validateDataInput = validateDataInput;
    this.hotelMapResourceActionRepository = hotelMapResourceActionRepository;
    this.hotelPermissionsRepository = hotelPermissionsRepository;
  }

  @Override
  protected void validateCreate(NameDTO create) {
    if (repository.existsByNameAndDeletedAtIsNull(create.getName()))
      throw new AppException(ErrorCode.RESOURCE_EXISTED);
    create.setName(validateDataInput.capitalizeFirstLetter(create.getName()));
  }

  @Override
  protected void validateUpdate(Integer id, NameDTO update) {
    if (repository.existsByNameAndIdNotAndDeletedAtIsNull(update.getName(), id))
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
    List<HotelMapResourcesAction> mapRAList = hotelMapResourceActionRepository.findAllByResourceId(resourceId);

    List<Integer> mapRAIds = mapRAList.stream()
                                      .map(HotelMapResourcesAction::getId)
                                      .toList();

    updatePermissionIfActionDelete(mapRAIds, authId);

    for (HotelMapResourcesAction mapRA : mapRAList) {
      mapRA.setDeletedAt(LocalDateTime.now());
      mapRA.setUpdatedBy(authId);
      hotelMapResourceActionRepository.save(mapRA);
    }
  }

  private void updatePermissionIfActionDelete(Collection<Integer> mapRAs, int authId) {
    List<HotelPermissions> findAllPermissions = hotelPermissionsRepository.findAllByMapRAId(mapRAs);

    for (HotelPermissions permission : findAllPermissions) {
      permission.setDeletedAt(LocalDateTime.now());
      permission.setUpdatedBy(authId);
      hotelPermissionsRepository.save(permission);
    }
  }

}
