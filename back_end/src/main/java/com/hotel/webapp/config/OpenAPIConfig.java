package com.hotel.webapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

  @Bean
  public OpenAPI openAPI() {
    Server server = new Server();
    server.setUrl("http://localhost:9899/hotel_owner");
    server.description("Phoebe Dev");

    Contact contact = new Contact();
    contact.setName("Phoebe");

    Info info = new Info()
          .title("API service documentation")
          .version("1.0")
          .description("This Api exposes endpoints to manage hotel")
          .contact(contact);

    SecurityScheme bearerAuth =
          new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT");

    SecurityRequirement securityRequirement =new SecurityRequirement().addList("bearerAuth");

    return new OpenAPI()
          .info(info)
          .servers(List.of(server))
          .components(new Components().addSecuritySchemes("bearerAuth", bearerAuth))
          .addSecurityItem(securityRequirement);
  }
}
