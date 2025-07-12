package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.DocumentType;
import com.hotel.webapp.entity.DocumentsHotel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentsHotelRepository extends BaseRepository<DocumentsHotel, Integer> {
  List<DocumentsHotel> findByHotelId(Integer hotelId);

  // document type
  @Query("select t from DocumentType t where t.deletedAt is null")
  List<DocumentType> findDocumentTypeAndDeletedAtIsNull();

  // document type - seeder
  //  insert
  @Query("select d from DocumentType d where d.name = :name and d.deletedAt is null")
  Optional<DocumentType> findDocumentTypeByName(String name);

  @Transactional
  @Modifying
  @Query("insert into DocumentType (name, colName, createdAt, createdBy) " +
        "values (:name, :colName, :createdAt, :createdBy)")
  void insertDocumentType(String name, String colName, LocalDateTime createdAt, Integer createdBy);
}
