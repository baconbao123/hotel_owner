package com.hotel.webapp.service.admin.interfaces;

import com.hotel.webapp.dto.admin.request.AuthReq;
import com.hotel.webapp.dto.admin.response.AuthResponse;

public interface AuthService {
  AuthResponse authenticate(AuthReq authReq);

  int getAuthLogin();
}
