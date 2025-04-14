package com.hotel.webapp.service.admin;

import com.hotel.webapp.base.BaseServiceImpl;
import com.hotel.webapp.dto.admin.request.ActionResourceDTO;
import com.hotel.webapp.entity.Actions;
import com.hotel.webapp.entity.MapResourcesAction;
import com.hotel.webapp.entity.Permissions;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.mapper.admin.ActionMapper;
import com.hotel.webapp.repository.ActionRepository;
import com.hotel.webapp.repository.MapResourceActionRepository;
import com.hotel.webapp.repository.PermissionsRepository;
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
public class ActionServiceImpl extends BaseServiceImpl<Actions, Integer, ActionResourceDTO, ActionRepository> {
  AuthService authService;
  ValidateDataInput validateDataInput;
  ActionMapper actionMapper;
  ActionRepository actionRepository;
  MapResourceActionRepository actionResourceRepository;
  PermissionsRepository permissionsRepository;

  public ActionServiceImpl(
        ActionRepository actionRepository,
        ActionMapper actionMapper,
        ValidateDataInput validateDataInput,
        MapResourceActionRepository actionResourceRepository,
        PermissionsRepository permissionsRepository,
        AuthService authService
  ) {
    super(actionRepository, actionMapper, authService);
    this.validateDataInput = validateDataInput;
    this.actionMapper = actionMapper;
    this.actionRepository = actionRepository;
    this.actionResourceRepository = actionResourceRepository;
    this.permissionsRepository = permissionsRepository;
    this.authService = authService;
  }

  @Override
  protected void validateCreate(ActionResourceDTO create) {
    if (actionRepository.existsByNameAndDeletedAtIsNull(create.getName())) {
      throw new AppException(ErrorCode.ACTION_EXISTED);
    }
    create.setName(validateDataInput.lowercaseFirstLetter(create.getName()));
  }

  @Override
  protected void validateUpdate(Integer id, ActionResourceDTO update) {
    if (actionRepository.existsByNameAndIdNotAndDeletedAtIsNull(update.getName(), id)) {
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
    List<MapResourcesAction> mapRAList = actionResourceRepository.findAllByActionId(actionId);

    List<Integer> mapRAIds = mapRAList.stream()
                                      .map(MapResourcesAction::getId)
                                      .toList();

    updatePermissionIfActionDelete(mapRAIds, authId);

    for (MapResourcesAction mapRA : mapRAList) {
      mapRA.setDeletedAt(LocalDateTime.now());
      mapRA.setUpdatedBy(authId);
      actionResourceRepository.save(mapRA);
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