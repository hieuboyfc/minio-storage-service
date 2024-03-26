package com.minio.storage.configuration.minio;

import io.minio.MinioClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MinioClientConfig {

    MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(
                        minioProperties.getEndPoint().getUrl(),
                        minioProperties.getEndPoint().getPort(),
                        minioProperties.getEndPoint().isSecure()
                )
                .credentials(
                        minioProperties.getAccess().getKey(),
                        minioProperties.getAccess().getSecret()
                )
                .build();
    }

}