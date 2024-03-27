package com.minio.storage.configuration.minio;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
@Configuration
@ConfigurationProperties(prefix = "minio")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MinioProperties {

    OpenAPI openAPI = new OpenAPI();

    Bucket bucket = new Bucket();

    DefaultConfig defaultConfig = new DefaultConfig();

    Access access = new Access();

    EndPoint endPoint = new EndPoint();

    private Long fileSize;

    @Setter
    @Getter
    public static class OpenAPI {
        private String devUrl;
        private String prodUrl;
    }

    @Setter
    @Getter
    public static class Bucket {
        private String name;
    }

    @Setter
    @Getter
    public static class DefaultConfig {
        private String folder;
    }

    @Setter
    @Getter
    public static class Access {
        private String key;
        private String secret;
    }

    @Setter
    @Getter
    public static class EndPoint {
        private String url;
        private Integer port;
        private boolean secure;
    }

}
