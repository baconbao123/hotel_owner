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
@Table(name = "hotel_map_resources_action")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelMapResourcesAction implements AuditEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;
  Integer resourceId;
  Integer actionId;
  Integer hotelId;
  Timestamp createdAt;
  Timestamp updatedAt;
  Integer createdBy;
  Integer updatedBy;
  LocalDateTime deletedAt;
}
