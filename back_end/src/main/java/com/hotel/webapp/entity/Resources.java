package com.hotel.webapp.entity;

import com.hotel.webapp.base.AuditEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Resources implements AuditEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;
  String name;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
  Integer createdBy;
  Integer updatedBy;
  LocalDateTime deletedAt;
}
