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
public class Booking implements AuditEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;
  @Column(nullable = false)
  Integer userId;
  @Column(nullable = false)
  Integer roomId;
  Integer paymentId;
  @Column(nullable = false)
  LocalDateTime checkInTime;
  @Column(nullable = false)
  LocalDateTime checkOutTime;
  LocalDateTime actualCheckInTime;
  LocalDateTime actualCheckOutTime;
  @Lob
  String note;
  Boolean status;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
  Integer createdBy;
  Integer updatedBy;
  LocalDateTime deletedAt;
}
