package com.hotel.webapp.entity;

import com.hotel.webapp.base.AuditEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelUser implements AuditEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;
  Integer hotelId;
  String fullName;
  String email;
  String phoneNumber;
  @Lob
  String password;
  @Nullable
  String avatarUrl;
  @Nullable
  Integer addressId;
  @Nullable
  @Lob
  String refreshToken;
  Boolean isActive;
  Timestamp createdAt;
  @Nullable
  Timestamp updatedAt;
  Integer createdBy;
  @Nullable
  Integer updatedBy;
  @Nullable
  LocalDateTime deletedAt;
}
