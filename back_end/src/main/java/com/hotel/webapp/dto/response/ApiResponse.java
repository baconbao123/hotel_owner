package com.hotel.webapp.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.Builder;

import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  @Builder.Default
  int code = 200;
  String message;
  T result;
  Map<String, String> errorMessages;
}
