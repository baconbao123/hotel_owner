package com.hotel.webapp.service.owner.interfaces;

import com.hotel.webapp.dto.request.AuthReq;
import com.hotel.webapp.dto.response.AuthResponse;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;

public interface AuthService {
  AuthResponse authenticate(AuthReq authReq);

  AuthResponse refreshToken(AuthReq.TokenRefreshReq tokenRefreshReq);

  Integer getAuthLogin();

  AuthResponse.IntrospectRes introspect(AuthReq.IntrospectRequest request);

  String generatePasswordResetToken(String email);

  void resetPassword(String token, String newPassword) throws ParseException, JOSEException;

  SignedJWT verifyToken(String token) throws JOSEException, ParseException;

}
