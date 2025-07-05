package com.hotel.webapp.base;

import com.hotel.webapp.dto.response.CommonRes;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.service.owner.interfaces.AuthService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class BaseServiceImpl<E, ID, DTO, R extends BaseRepository<E, ID>> implements BaseService<E, ID, DTO> {
  R repository;
  BaseMapper<E, DTO> mapper;
  AuthService authService;

  public BaseServiceImpl(R repository, BaseMapper<E, DTO> mapper, AuthService authService) {
    this.repository = repository;
    this.mapper = mapper;
    this.authService = authService;
  }

  public BaseServiceImpl(R repository, AuthService authService) {
    this(repository, null, authService);
  }

  private static String convertDateToString(Object value) {
    if (value instanceof LocalDate localDate) {
      return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    return value.toString();
  }

  private static Object parseNumber(String value, Class<?> targetType) {
    if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
      return Integer.parseInt(value);
    } else if (targetType.equals(Long.class) || targetType.equals(long.class)) {
      return Long.parseLong(value);
    }
    return value;
  }

  @Override
  public Page<E> getAll(Map<String, String> filters, Map<String, String> sort, int size, int page) {
    Map<String, Object> filterMap = removedFiltersKey(filters);
    Map<String, Object> sortMap = removedSortedKey(sort);

    Specification<E> spec = buildSpecification(filterMap);
    Pageable defaultPage = buildPageable(sortMap, page, size);
    return repository.findAll(spec, defaultPage);
  }

  public <E> Specification<E> buildSpecification(Map<String, Object> filters) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.isNull(root.get("deletedAt")));

      // Loại trừ email sa@gmail.com nếu field email tồn tại
      boolean hasEmailField = true;
      try {
        root.get("email");
        predicates.add(cb.notEqual(root.get("email"), "sa@gmail.com"));
      } catch (IllegalArgumentException e) {
        hasEmailField = false;
      }

      if (filters == null) return cb.conjunction();

      for (var entry : filters.entrySet()) {
        String field = entry.getKey();
        Object value = entry.getValue();

        if (field == null || field.isBlank() || value == null || value.toString().isBlank()) {
          continue;
        }

        // Bỏ qua filter email nếu là sa@gmail.com
        if (field.equals("email") && hasEmailField && value.toString().trim().equalsIgnoreCase("sa@gmail.com")) {
          continue;
        }

        try {
          String searchValue = convertDateToString(value).toLowerCase();
          Class<?> type = root.get(field).getJavaType();

          if (type.equals(LocalDate.class)) {
            predicates.add(cb.equal(root.get(field), LocalDate.parse(searchValue, DateTimeFormatter.ISO_LOCAL_DATE)));
          } else if (type.equals(Boolean.class)) {
            predicates.add(cb.equal(root.get(field), Boolean.parseBoolean(searchValue)));
          } else if (Number.class.isAssignableFrom(type) || type.equals(int.class) || type.equals(long.class)) {
            predicates.add(cb.equal(root.get(field), parseNumber(searchValue, type)));
          } else {
            predicates.add(cb.like(cb.lower(root.get(field).as(String.class)), "%" + searchValue + "%"));
          }
        } catch (IllegalArgumentException e) {
        }
      }

      return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  public Pageable buildPageable(Map<String, Object> sort, int page, int size) {
    if (sort == null || sort.isEmpty()) {
      return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
    }

    List<Sort.Order> orders = new ArrayList<>();

    for (Map.Entry<String, Object> entry : sort.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      // Kiểm tra key có đúng định dạng sort[fieldName]
      if (key != null && key.startsWith("sort[") && key.endsWith("]") &&
            value != null && !value.toString().trim().isEmpty()) {
        // Lấy tên trường, ví dụ: "id" từ "sort[id]"
        String fieldName = key.replaceAll("sort\\[(.*)\\]", "$1");
        if (!fieldName.isEmpty()) {
          String directionStr = value.toString().trim().toUpperCase();
          if (directionStr.equals("ASC") || directionStr.equals("DESC")) {
            Sort.Direction direction = Sort.Direction.fromString(directionStr);
            orders.add(new Sort.Order(direction, fieldName));
          }
        }
      }
    }

    Sort sortResult = orders.isEmpty() ? Sort.by(Sort.Direction.DESC, "id") : Sort.by(orders);
    return PageRequest.of(page, size, sortResult);
  }

  @Override
  public E create(DTO create) {
    validateDTOCommon(create);
    validateCreate(create);
    E entity = mapper.toCreate(create);

    beforeCommon(entity, create);
    beforeCreate(entity, create);

    if (entity instanceof AuditEntity audit) {
      audit.setCreatedAt(LocalDateTime.now());
      audit.setCreatedBy(getAuthId());
    }

    entity = repository.save(entity);

    afterCreate(entity, create);
    afterCommon(entity, create);

    return entity;
  }

  @Override
  public E update(ID id, DTO update) {

    validateDTOCommon(update);

    E entity = findById(id);
    validateUpdate((Integer) id, update);
    mapper.toUpdate(entity, update);

    beforeCommon(entity, update);
    beforeUpdate(entity, update);

    if (entity instanceof AuditEntity audit) {
      audit.setUpdatedAt(LocalDateTime.now());
      audit.setUpdatedBy(getAuthId());
    }

    afterUpdate(entity, update);
    afterCommon(entity, update);

    return repository.save(entity);
  }

  @Override
  public void delete(ID id) {
    E entity = findById(id);
    validateDelete(id);

    beforeDelete(id);

    if (entity instanceof AuditEntity audit) {
      audit.setDeletedAt(LocalDateTime.now());
      repository.save(entity);
    }
  }

  @Override
  public CommonRes<E> getEById(ID id) {
    return repository.findByIdWithFullname(id)
                     .filter(e -> !(e instanceof AuditEntity audit) || audit.getDeletedAt() == null)
                     .orElseThrow(() -> createNotFoundException(id));
  }

  @Override
  public E findById(ID id) {
    return repository.findById(id)
                     .filter(e -> !(e instanceof AuditEntity audit) || audit.getDeletedAt() == null)
                     .orElseThrow(() -> createNotFoundException(id));
  }

  protected Integer getAuthId() {
    Integer authId = authService.getAuthLogin();
    if (authId == null) {
      throw new AppException(ErrorCode.ACCESS_DENIED);
    }
    return authId;
  }

  protected Map<String, Object> removedFiltersKey(Map<String, String> filters) {
    Map<String, Object> filterMap = filters != null ? new HashMap<>(filters) : new HashMap<>();
    filterMap.remove("size");
    filterMap.remove("page");
    filterMap.keySet().removeIf(key -> key.startsWith("sort["));

    return filterMap;
  }

  protected Map<String, Object> removedSortedKey(Map<String, String> sort) {
    Map<String, Object> sortMap = sort != null ? new HashMap<>(sort) : new HashMap<>();
    sortMap.remove("size");
    sortMap.remove("page");
    sortMap.keySet().removeIf(key -> key.startsWith("filters["));

    return sortMap;
  }

  protected void beforeCommon(E entity, DTO dto) {
  }

  protected void beforeCreate(E e, DTO create) {
  }

  protected void beforeUpdate(E e, DTO update) {
  }

  protected void beforeDelete(ID id) {
  }

  protected void validateCreate(DTO create) {
  }

  protected void validateUpdate(Integer id, DTO update) {
  }

  protected void validateDelete(ID id) {
  }

  protected void validateDTOCommon(DTO dto) {
  }

  protected abstract RuntimeException createNotFoundException(ID id);


  protected void afterCreate(E entity, DTO dto) {
  }

  protected void afterUpdate(E entity, DTO dto) {
  }

  protected void afterCommon(E entity, DTO dto) {
  }


}