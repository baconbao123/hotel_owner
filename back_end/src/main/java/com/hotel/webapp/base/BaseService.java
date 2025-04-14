package com.hotel.webapp.base;

import java.util.List;

public interface BaseService<E, ID, DTO> {
  E create(DTO create);

  E update(ID id, DTO update);

  void delete(ID id);

  E getById(ID id);

  List<E> getAll();
}
