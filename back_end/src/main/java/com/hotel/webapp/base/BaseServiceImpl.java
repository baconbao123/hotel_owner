package com.hotel.webapp.base;

import com.hotel.webapp.service.admin.interfaces.AuthService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Transactional
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class BaseServiceImpl<E, ID, DTO, R extends BaseRepository<E, ID>> implements BaseService<E, ID, DTO> {
  R repository;
  BaseMapper<E, DTO> mapper;
  AuthService authService; // get authId

  public BaseServiceImpl(R repository, BaseMapper<E, DTO> mapper, AuthService authService) {
    this.repository = repository;
    this.mapper = mapper;
    this.authService = authService;
  }

  public BaseServiceImpl(R repository, AuthService authService) {
    this(repository, null, authService);
  }

  @Override
  public E create(DTO create) {
    validateCreate(create);
    E entity = mapper.toCreate(create);

    if (entity instanceof AuditEntity audit) {
      audit.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
      audit.setCreatedBy(authService.getAuthLogin());
    }

    return repository.save(entity);
  }

  @Override
  public E update(ID id, DTO update) {
    E entity = getById(id);
    validateUpdate((Integer) id, update);
    mapper.toUpdate(entity, update);

    if (entity instanceof AuditEntity audit) {
      audit.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
      audit.setUpdatedBy(authService.getAuthLogin());
    }

    return repository.save(entity);
  }

  @Override
  public void delete(ID id) {
    E entity = getById(id);
    validateDelete(id);

    if (entity instanceof AuditEntity audit) {
      audit.setDeletedAt(LocalDateTime.now());
      repository.save(entity);
    }
  }

  @Override
  public E getById(ID id) {
    return repository.findById(id)
                     .filter(e -> !(e instanceof AuditEntity audit) || audit.getDeletedAt() == null)
                     .orElseThrow(() -> createNotFoundException(id));
  }

  @Override
  public List<E> getAll() {
    return repository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                     .stream()
                     .filter(e -> !(e instanceof AuditEntity audit) || audit.getDeletedAt() == null)
                     .toList();
  }


  protected Integer getAuthId() {
    return authService.getAuthLogin();
  }

  public Collection<E> createCollectionBulk(DTO dto) {
    throw new RuntimeException("Not implemented yet");
  }

  public Collection<E> updateCollectionBulk(ID id, DTO dto) {
    throw new RuntimeException("Not implemented yet");
  }

  protected abstract void validateCreate(DTO create);

  protected abstract void validateUpdate(Integer id, DTO update);

  protected abstract void validateDelete(ID id);

  protected abstract RuntimeException createNotFoundException(ID id);
}