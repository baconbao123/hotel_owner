package com.hotel.webapp.service.admin;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.admin.request.NameDTO;
import com.hotel.webapp.entity.HotelActions;
import com.hotel.webapp.entity.HotelMapResourcesAction;
import com.hotel.webapp.entity.HotelPermissions;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.HotelActionRepository;
import com.hotel.webapp.repository.HotelMapResourceActionRepository;
import com.hotel.webapp.repository.HotelPermissionsRepository;
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
public class HotelActionServiceImpl extends BaseServiceImpl<HotelActions, Integer, NameDTO, HotelActionRepository> {
  ValidateDataInput validateDataInput;
  HotelMapResourceActionRepository actionResourceRepository;
  HotelPermissionsRepository hotelPermissionsRepository;

  public HotelActionServiceImpl(
        HotelActionRepository repository,
        BaseMapper<HotelActions, NameDTO> mapper,
        ValidateDataInput validateDataInput,
        HotelMapResourceActionRepository actionResourceRepository,
        HotelPermissionsRepository hotelPermissionsRepository,
        AuthService authService
  ) {
    super(repository, mapper, authService);
    this.validateDataInput = validateDataInput;
    this.actionResourceRepository = actionResourceRepository;
    this.hotelPermissionsRepository = hotelPermissionsRepository;
  }

  @Override
  protected void validateCreate(NameDTO create) {
    if (repository.existsByNameAndDeletedAtIsNull(create.getName())) {
      throw new AppException(ErrorCode.ACTION_EXISTED);
    }
    create.setName(validateDataInput.lowercaseFirstLetter(create.getName()));
  }

  @Override
  protected void validateUpdate(Integer id, NameDTO update) {
    if (repository.existsByNameAndIdNotAndDeletedAtIsNull(update.getName(), id)) {
      throw new AppException(ErrorCode.ACTION_EXISTED);
    }
    update.setName(validateDataInput.lowercaseFirstLetter(update.getName()));
  }

  @Override
  protected void validateDelete(Integer id) {
    updateMapRAIfActionDelete(id, getAuthId());
  }

  @Override
  protected RuntimeException createNotFoundException(Integer integer) {
    return new AppException(ErrorCode.ACTION_NOTFOUND);
  }


  private void updateMapRAIfActionDelete(int actionId, int authId) {
    List<HotelMapResourcesAction> mapRAList = actionResourceRepository.findAllByActionId(actionId);

    List<Integer> mapRAIds = mapRAList.stream()
                                      .map(HotelMapResourcesAction::getId)
                                      .toList();

    updatePermissionIfActionDelete(mapRAIds, authId);

    for (HotelMapResourcesAction mapRA : mapRAList) {
      mapRA.setDeletedAt(LocalDateTime.now());
      mapRA.setUpdatedBy(authId);
      actionResourceRepository.save(mapRA);
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