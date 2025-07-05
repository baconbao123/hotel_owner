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
public class TypeHotel implements AuditEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;
  String name;
  @Column(unique = true)
  String colName;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
  Integer createdBy;
  Integer updatedBy;
  LocalDateTime deletedAt;
}
