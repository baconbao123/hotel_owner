package com.hotel.webapp.entity.shared;

import com.hotel.webapp.base.AuditEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class Hotels implements AuditEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;
  Integer ownerId;
  String name;
  @Nullable
  String description;
  Integer avatarId;
  Integer addressId;
  Integer policyId;
  Boolean status;
  Timestamp createdAt;
  Timestamp updatedAt;
  Integer createdBy;
  Integer updatedBy;
  LocalDateTime deletedAt;
}

