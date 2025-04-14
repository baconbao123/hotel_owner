package com.hotel.webapp.config;

import com.hotel.webapp.service.system.PermissionSystemSeeder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
//@DependsOn("entityManagerFactory")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionSeederRunner implements CommandLineRunner {
  PermissionSystemSeeder permissionSystemSeeder;

  @Override
  public void run(String... args) throws Exception {
    if(args.length > 0 && "seeder-permission".equals(args[0])) {
      permissionSystemSeeder.seeder(1, 1);
      log.info("Permission system seeding completed");

    }
  }
}
