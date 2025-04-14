package com.hotel.webapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {
  private static final Logger logger = LoggerFactory.getLogger(StartupListener.class);

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    logger.info("Swagger UI is available at http://localhost:9898/hotel/swagger-ui/index.html");
    logger.info("Email: sa@gmail.com || Raw password: 123");
  }
}
