package com.hotel.webapp.entity;

import com.hotel.webapp.base.AuditEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
  @Lob
  String description;
  String avatar;
  Integer addressId;
  Integer policyId;
  Boolean status;
  Integer approveId;
  String note;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
  Integer createdBy;
  Integer updatedBy;
  LocalDateTime deletedAt;
}
