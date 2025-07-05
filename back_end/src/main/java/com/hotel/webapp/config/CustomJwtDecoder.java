package com.hotel.webapp.config;

import com.hotel.webapp.dto.request.AuthReq;
import com.hotel.webapp.service.owner.interfaces.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Component
public class CustomJwtDecoder implements JwtDecoder {
  private final AuthService authService;

  @Value("${jwt.signerKey}")
  private String SIGNER_KEY;

  private NimbusJwtDecoder nimbusJwtDecoder = null;

  public CustomJwtDecoder(AuthService authService) {
    this.authService = authService;
  }

  @Override
  public Jwt decode(String token) throws JwtException {
    var res = authService.introspect(AuthReq.IntrospectRequest.builder().token(token).build());
    if (!res.isValid()) throw new JwtException("invalid token");

    if (Objects.isNull(nimbusJwtDecoder)) {
      SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
      nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                                         .macAlgorithm(MacAlgorithm.HS512)
                                         .build();
    }

    return nimbusJwtDecoder.decode(token);
  }
}

