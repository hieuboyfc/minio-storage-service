package com.minio.storage.configuration.minio;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix = "minio")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MinioProperties {

    Bucket bucket = new Bucket();

    DefaultConfig defaultConfig = new DefaultConfig();

    Access access = new Access();

    EndPoint endPoint = new EndPoint();

    private Long fileSize;

    public MinioProperties() {
    }

    public MinioProperties(Bucket bucket, DefaultConfig defaultConfig, Access access, EndPoint endPoint, Long fileSize) {
        this.bucket = bucket;
        this.defaultConfig = defaultConfig;
        this.access = access;
        this.endPoint = endPoint;
        this.fileSize = fileSize;
    }

    public static class Bucket {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class DefaultConfig {
        private String folder;

        public String getFolder() {
            return folder;
        }

        public void setFolder(String folder) {
            this.folder = folder;
        }
    }

    public static class Access {
        private String key;
        private String secret;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }

    public static class EndPoint {
        private String url;
        private Integer port;
        private boolean secure;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public boolean isSecure() {
            return secure;
        }

        public void setSecure(boolean secure) {
            this.secure = secure;
        }
    }

    public Bucket getBucket() {
        return bucket;
    }

    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(DefaultConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public EndPoint getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(EndPoint endPoint) {
        this.endPoint = endPoint;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

}
