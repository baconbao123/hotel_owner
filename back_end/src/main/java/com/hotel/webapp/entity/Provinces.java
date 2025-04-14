package com.hotel.webapp.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Provinces {
  @Id
  String code;
  String name;
  String nameEn;
  String fullName;
  String fullNameEn;
  String codeName;
  Integer administrativeUnitId;
  Integer administrativeRegionId;
  Timestamp createdAt;
  @Nullable
  Timestamp updatedAt;
  Integer createdBy;
  @Nullable
  Integer updatedBy;
  @Nullable
  LocalDateTime deletedAt;
}
