package com.minio.storage.configuration.openapi;

import com.minio.storage.configuration.minio.MinioProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OpenAPIConfig {

    MinioProperties minioProperties;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(minioProperties.getOpenAPI().getDevUrl());
        devServer.setDescription("URL máy chủ trong môi trường Development");

        Server prodServer = new Server();
        prodServer.setUrl(minioProperties.getOpenAPI().getProdUrl());
        prodServer.setDescription("URL máy chủ trong môi trường Production");

        Contact contact = new Contact();
        contact.setEmail("hieuboyfc@gmail.com");
        contact.setName("ZimJi");
        contact.setUrl("https://github.com/hieuboyfc");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("MinIO Storage Service - Management API")
                .version("1.9.7")
                .contact(contact)
                .description("This API exposes endpoints to manage tutorials.")
                .termsOfService("https://github.com/hieuboyfc")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }

}