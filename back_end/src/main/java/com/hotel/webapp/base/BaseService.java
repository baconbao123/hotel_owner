package com.hotel.webapp.base;

import com.hotel.webapp.dto.response.CommonRes;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface BaseService<E, ID, DTO> {
  E create(DTO create);

  E update(ID id, DTO update);

  void delete(ID id);

  CommonRes<E> getEById(ID id);

  E findById(ID id);

  Page<E> getAll(Map<String, String> filters, Map<String, String> sort, int size, int page);
}
