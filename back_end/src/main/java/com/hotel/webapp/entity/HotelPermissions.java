package com.hotel.webapp.entity;

import com.hotel.webapp.base.AuditEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
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
@Table(name = "hotel_permissions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelPermissions implements AuditEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;
  Integer mapUserRolesId;
  Integer mapResourcesActionId;
  Timestamp createdAt;
  Timestamp updatedAt;
  Integer createdBy;
  Integer updatedBy;
  LocalDateTime deletedAt;
}
