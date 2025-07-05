package com.hotel.webapp.config;

import com.hotel.webapp.validation.Permission;
import com.hotel.webapp.validation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.Arrays;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  //  PermissionService permissionService;
  ApplicationContext applicationContext;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
    AntPathMatcher pathMatcher = new AntPathMatcher();
    String contextPath = request.getContextPath();
    String path = request.getRequestURI();

    if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
      path = path.substring(contextPath.length());
    }

    String finalPath = path;
    boolean isPublic = Arrays.stream(SecurityConfig.PUBLIC_URLS)
                             .anyMatch(url -> pathMatcher.match(url, finalPath));

    if (isPublic) {
      filterChain.doFilter(request, response);
      return;
    }

    boolean isAuthenticatedPublic = Arrays.stream(SecurityConfig.AUTHENTICATED_PUBLIC_URLS)
                                          .anyMatch(url -> pathMatcher.match(url, finalPath));

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
      return;
    }

    Jwt jwt = (Jwt) auth.getPrincipal();
    String userId = jwt.getClaimAsString("userId");
    String email = jwt.getClaimAsString("sub");

    if (userId == null) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid JWT token: userId not found");
      return;
    }

    // Bypass permission check for super admin
    if ("sa@gmail.com".equals(email)) {
      filterChain.doFilter(request, response);
      return;
    }

    if (isAuthenticatedPublic) {
      filterChain.doFilter(request, response);
      return;
    }

    // Get handler information
    Object handler = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);

    if (handler == null) {
      // Try alternative ways to get the handler
      for (HandlerMapping hm : applicationContext.getBeansOfType(HandlerMapping.class).values()) {
        try {
          HandlerExecutionChain hec = hm.getHandler(request);
          if (hec != null) {
            handler = hec;
            break;
          }
        } catch (Exception e) {
          throw new ServletException(e);
        }
      }
    }

    if (!(handler instanceof HandlerExecutionChain)) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
      return;
    }

//    HandlerMethod handlerMethod = null;
//    Object handlerObject = ((HandlerExecutionChain) handler).getHandler();
//
//    if (handlerObject instanceof HandlerMethod method) {
//      handlerMethod = method;
//    } else {
//      log.warn("Handler is not a HandlerMethod: {}", handlerObject);
//      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid handler type");
//      return;
//    }

    // Get resource from annotation on controller class
//    Resource resourceAnnotation = handlerMethod.getBeanType().getAnnotation(Resource.class);

    // Get action from annotation on controller method
//    Permission permissionAnnotation = handlerMethod.getMethodAnnotation(Permission.class);

//    if (resourceAnnotation == null || permissionAnnotation == null) {
//      log.warn("Resource or Permission annotation not found for {}#{}",
//            handlerMethod.getBeanType().getSimpleName(),
//            handlerMethod.getMethod().getName());
//      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Resource or Permission not defined");
//      return;
//    }

//    String resourceName = resourceAnnotation.name().toLowerCase();
//    String permissionName = permissionAnnotation.name().toLowerCase();


//    boolean hasPermission = permissionService.checkPermission(Integer.valueOf(userId), resourceName, permissionName);
//    log.info("Permission check for userId={}, resource={}, permission={}: {}",
//          userId, resourceName, permissionName, hasPermission);
//
//    if (!hasPermission) {
//      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Permission Denied");
//      return;
//    }

    filterChain.doFilter(request, response);
  }
}
