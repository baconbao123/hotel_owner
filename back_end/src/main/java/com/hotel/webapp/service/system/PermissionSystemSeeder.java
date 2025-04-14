package com.hotel.webapp.service.system;

import com.hotel.webapp.repository.*;
import com.nimbusds.jose.util.Pair;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionSystemSeeder {
  ResourcesRepository resourcesRepository;
  PermissionsRepository permissionsRepository;
  ActionRepository actionRepository;
  MapResourceActionRepository mapResourceActionRepository;
  MapUserRoleRepository mapUserRoleRepository;

  List<String> DEFAULT_RESOURCE = List.of("Hotel", "User", "Booking", "Permissions", "Resources", "Actions");

  List<String> DEFAULT_ACTION = List.of("view", "create", "update", "delete");

  @Transactional
  public void seeder(int adminId, int roleId) {
    Map<String, Integer> resourceIds = seederResources();

    Map<String, Integer> actionIds = seederActions();

    // create resource-actions mapping
    Map<Pair<Integer, Integer>, Integer> resourceActionMappings = seederResourceActionMapping(resourceIds, actionIds);

    // create permission for sa
    seederAdminPermission(adminId, roleId, resourceActionMappings);
  }

  //  seederResources
  private Map<String, Integer> seederResources() {
    Map<String, Integer> resourceIds = new HashMap<>();
    Timestamp now = new Timestamp(System.currentTimeMillis());

    for (String resourceName : DEFAULT_RESOURCE) {
      int resourceId = resourcesRepository.findIdByName(resourceName)
                                          .orElseGet(() -> {
                                            resourcesRepository.insertResources(resourceName, now, 0);
                                            return resourcesRepository.findIdByName(resourceName)
                                                                      .orElseThrow(() -> new RuntimeException(
                                                                            "Failed to insert resource "
                                                                                  + resourceName));
                                          });
      resourceIds.put(resourceName, resourceId);
    }

    return resourceIds;
  }

  //  seederActions
  private Map<String, Integer> seederActions() {
    Map<String, Integer> actionIds = new HashMap<>();
    Timestamp now = new Timestamp(System.currentTimeMillis());

    for (String actionName : DEFAULT_ACTION) {
      int actionId = actionRepository.findIdByName(actionName)
                                     .orElseGet(() -> {
                                       actionRepository.insetActions(actionName, now, 0);
                                       return actionRepository.findIdByName(actionName)
                                                              .orElseThrow(() -> new RuntimeException(
                                                                    "Failed to insert action " + actionName));
                                     });
      actionIds.put(actionName, actionId);
    }
    return actionIds;
  }


  //  seederResourceActionMapping
  private Map<Pair<Integer, Integer>, Integer> seederResourceActionMapping(Map<String, Integer> resourceIds,
        Map<String, Integer> actionIds) {
    Map<Pair<Integer, Integer>, Integer> mappings = new HashMap<>();
    Timestamp now = new Timestamp(System.currentTimeMillis());

    for (String resourceName : DEFAULT_RESOURCE) {
      int resourceId = resourceIds.get(resourceName);

      for (String actionName : DEFAULT_ACTION) {
        int actionId = actionIds.get(actionName);

        int mappingId = mapResourceActionRepository.findIdByResourceIdAndActionId(resourceId, actionId)
                                                   .orElseGet(() -> {
                                                     mapResourceActionRepository.insertMapping(resourceId, actionId,
                                                           now, 0);
                                                     return mapResourceActionRepository
                                                           .findIdByResourceIdAndActionId(resourceId, actionId)
                                                           .orElseThrow(() -> new RuntimeException(
                                                                 "Failed create mapping for resource " + resourceName + " and action " + actionName
                                                           ));

                                                   });
        mappings.put(Pair.of(resourceId, actionId), mappingId);
      }
    }
    return mappings;
  }

  //  seederAdminPermission
  private void seederAdminPermission(int adminId, int roleId,
        Map<Pair<Integer, Integer>, Integer> resourceActionMappings) {
    int mapUserRoleId = mapUserRoleRepository.findIdByRoleIdAndUserId(roleId, adminId);

    Timestamp now = new Timestamp(System.currentTimeMillis());

    for (int resourceActionId : resourceActionMappings.values()) {
      if (permissionsRepository.countPermissionsByMapResourcesActionIdAndMapUserRolesId(roleId,
            resourceActionId) == 0) {
        permissionsRepository.insertPermissions(resourceActionId, mapUserRoleId, now, 0);
      }
    }
  }

}
