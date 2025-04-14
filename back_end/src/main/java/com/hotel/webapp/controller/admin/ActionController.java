package com.hotel.webapp.controller.admin;

import com.hotel.webapp.dto.admin.request.ActionResourceDTO;
import com.hotel.webapp.dto.admin.response.ApiResponse;
import com.hotel.webapp.entity.Actions;
import com.hotel.webapp.service.admin.ActionServiceImpl;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// rest full api
@RestController
@RequestMapping("/api/action")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActionController {
  ActionServiceImpl actionServiceImpl;

  @PostMapping("/create")
  public ApiResponse<Actions> create(@Valid ActionResourceDTO actionResourceDTO) {
    return ApiResponse.<Actions>builder()
                      .result(actionServiceImpl.create(actionResourceDTO))
                      .build();
  }

  @PutMapping("/update/{id}")
  public ApiResponse<Actions> update(@PathVariable int id, @Valid ActionResourceDTO updateReq) {
    return ApiResponse.<Actions>builder()
                      .result(actionServiceImpl.update(id, updateReq))
                      .build();
  }

  @GetMapping("/get-all")
  public ApiResponse<List<Actions>> getAll() {
    return ApiResponse.<List<Actions>>builder()
                      .result(actionServiceImpl.getAll())
                      .build();
  }

  @GetMapping("/find-by-id/{id}")
  public ApiResponse<Actions> findById(@PathVariable int id) {
    return ApiResponse.<Actions>builder()
                      .result(actionServiceImpl.getById(id))
                      .build();
  }

  @DeleteMapping("/delete/{id}")
  public ApiResponse<Void> deleteById(@PathVariable int id) {
    actionServiceImpl.delete(id);
    return ApiResponse.<Void>builder()
                      .message("Deleted action with id " + id + " successfully")
                      .build();
  }
}
