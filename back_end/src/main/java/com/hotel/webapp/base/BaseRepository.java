package com.hotel.webapp.base;

import com.hotel.webapp.dto.response.CommonRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<E, ID> extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {
  @Query("SELECT new com.hotel.webapp.dto.response.CommonRes(e, u1.fullName, u2.fullName) " +
        "FROM #{#entityName} e " +
        "LEFT JOIN com.hotel.webapp.entity.User u1 ON e.createdBy = u1.id " +
        "LEFT JOIN com.hotel.webapp.entity.User u2 ON e.updatedBy = u2.id " +
        "WHERE e.id = :id AND (e.deletedAt IS NULL)")
  Optional<CommonRes<E>> findByIdWithFullname(@Param("id") ID id);
}
