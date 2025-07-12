package com.hotel.webapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  public static final String[] PUBLIC_URLS = {
        "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/api/auth/**", "/public/**", "/upload/**", "/user/**"
  };

  public static final String[] AUTHENTICATED_PUBLIC_URLS = {
        "/api/permission/resources", "/api/common-data", "/api/user/profile", "/api/user/profile/**"
  };

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  private final CustomJwtDecoder jwtDecoder;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CustomJwtDecoder jwtDecoder) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.jwtDecoder = jwtDecoder;
  }

  @Bean
  @Order(1)
  SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
    RequestMatcher[] matchers = Arrays.stream(PUBLIC_URLS)
                                      .map(AntPathRequestMatcher::new)
                                      .toArray(RequestMatcher[]::new);

    http.securityMatcher(new OrRequestMatcher(matchers))
        .authorizeHttpRequests(req -> req.anyRequest().permitAll())
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()));
    return http.build();
  }

  @Bean
  @Order(2)
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(req -> req.anyRequest().authenticated())
        .oauth2ResourceServer(oauth ->
              oauth.jwt(jwtConfigurer -> jwtConfigurer
                    .decoder(jwtDecoder)
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .addFilterAfter(jwtAuthenticationFilter, BearerTokenAuthenticationFilter.class)
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()));
    return http.build();
  }


  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:9899", "http://localhost:5173", "http://localhost:5174"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter jwtGrantedAuthorities = new JwtGrantedAuthoritiesConverter();
    jwtGrantedAuthorities.setAuthorityPrefix("ROLE_");

    JwtAuthenticationConverter jwtAuthentication = new JwtAuthenticationConverter();
    jwtAuthentication.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthorities);
    return jwtAuthentication;
  }
}
