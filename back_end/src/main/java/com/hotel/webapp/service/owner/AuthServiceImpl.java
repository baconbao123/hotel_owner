package com.hotel.webapp.service.owner;

import com.hotel.webapp.dto.request.AuthReq;
import com.hotel.webapp.dto.response.AuthResponse;
import com.hotel.webapp.entity.Role;
import com.hotel.webapp.entity.User;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.MapUserRoleRepository;
import com.hotel.webapp.repository.RoleRepository;
import com.hotel.webapp.repository.UserRepository;
import com.hotel.webapp.service.owner.interfaces.AuthService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthServiceImpl implements AuthService {
  UserRepository userRepository;
  MapUserRoleRepository userRoleRepository;
  RoleRepository roleRepository;
  PasswordEncoder passwordEncoder;

  @NonFinal
  @Value("${jwt.signerKey}")
  protected String SIGNER_KEY;

  @NonFinal
  @Value("${jwt.valid-duration}")
  protected int VALIDATION_DURATION;

  @NonFinal
  @Value("${jwt.refreshable-duration}")
  protected int REFRESHABLE_DURATION;

  @Override
  public AuthResponse authenticate(AuthReq authReq) {
    var user = userRepository.findByEmail(authReq.getEmail())
                             .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "User"));
    boolean authenticated = passwordEncoder.matches(authReq.getPassword(), user.getPassword());

    if (!authenticated) {
      throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
    }

    var token = generateToken(user);

    String refreshToken = authReq.getRemember() ? UUID.randomUUID().toString() : "";

    if (refreshToken != null && !refreshToken.isEmpty()) {
      user.setRefreshToken(refreshToken);
      user.setExpired(LocalDateTime.now().plusSeconds(REFRESHABLE_DURATION));
    }

    userRepository.save(user);

    return AuthResponse.builder().token(token).refreshToken(refreshToken).build();
  }

  @Override
  public AuthResponse refreshToken(AuthReq.TokenRefreshReq tokenRefreshReq) {
    var refreshToken = tokenRefreshReq.getRefreshToken();

    var user = userRepository.findByRefreshToken(refreshToken)
                             .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "User"));

    if (user.getExpired() != null && user.getExpired().isBefore(LocalDateTime.now())) {
      throw new AppException(ErrorCode.EXPIRED_TOKEN);
    }

    String newToken = generateToken(user);
    String newRefreshToken = UUID.randomUUID().toString();

    user.setRefreshToken(newRefreshToken);
    user.setExpired(LocalDateTime.now().plusSeconds(REFRESHABLE_DURATION));
    userRepository.save(user);
    return AuthResponse.builder().token(newToken).refreshToken(newRefreshToken).build();
  }

  @Override
  public AuthResponse.IntrospectRes introspect(AuthReq.IntrospectRequest request) {
    var token = request.getToken();
    boolean isValid = true;

    try {
      verifyToken(token);
    } catch (ParseException | JOSEException e) {
      isValid = false;
    }

    return AuthResponse.IntrospectRes.builder().isValid(isValid).build();
  }

  @Override
  public SignedJWT verifyToken(String token) throws JOSEException, ParseException {
    JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

    SignedJWT signedJWT = SignedJWT.parse(token);

    Date expTime = signedJWT.getJWTClaimsSet().getExpirationTime();

    boolean verified = signedJWT.verify(verifier);

    if (expTime == null || !verified || expTime.before(new Date())) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    return signedJWT;
  }

  @Override
  public Integer getAuthLogin() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    var username = auth.getName();
    User user = userRepository.findByEmail(username)
                              .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Auth login"));
    return user.getId();
  }

  private String generateToken(User user) {
    var userRoles = userRoleRepository.findAllByUserIdAndDeletedAtIsNull(user.getId());

    if (userRoles.isEmpty()) throw new AppException(ErrorCode.COMMON_400,
          "Something went wrong with account");

    List<String> roles = userRoles.stream()
                                  .map(ur -> roleRepository.findRolesByDeletedAtIsNull(ur.getRoleId())
                                                           .orElseThrow(() -> new AppException(ErrorCode.COMMON_400,
                                                                 "Something went wrong with account")))
                                  .map(Role::getName)
                                  .toList();

    String roleString = String.join(",", roles);
    JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
          .subject(user.getEmail())
          .issuer("Phoebe dev")
          .issueTime(new Date())
          .expirationTime(new Date(Instant.now().plus(VALIDATION_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
          .claim("userId", user.getId())
          .claim("scope", roleString)
          .build();

    Payload payload = new Payload(jwtClaimsSet.toJSONObject());

    JWSObject jwsObject = new JWSObject(header, payload);

    try {
      jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
      return jwsObject.serialize();
    } catch (JOSEException e) {
      log.error("Cannot create token");
      throw new RuntimeException(e);
    }
  }

  public String generatePasswordResetToken(String email) {
    JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
          .subject(email)
          .issuer("Phoebe dev")
          .issueTime(new Date())
          .expirationTime(new Date(Instant.now().plus(30, ChronoUnit.MINUTES).toEpochMilli()))
          .build();

    Payload payload = new Payload(jwtClaimsSet.toJSONObject());

    JWSObject jwsObject = new JWSObject(header, payload);

    try {
      jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
      return jwsObject.serialize();
    } catch (JOSEException e) {
      log.error("Cannot create token");
      throw new RuntimeException(e);
    }
  }

  public void resetPassword(String token, String newPassword) throws ParseException, JOSEException {
    SignedJWT verify = verifyToken(token);

    String email = verify.getJWTClaimsSet().getSubject();

    User user = userRepository.findByEmail(email)
                              .orElseThrow(() -> new RuntimeException("User not found"));

    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }
}
