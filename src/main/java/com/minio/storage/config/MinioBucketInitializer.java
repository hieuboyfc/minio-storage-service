package com.minio.storage.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MinioBucketInitializer {

    private static final Logger log = LoggerFactory.getLogger(MinioBucketInitializer.class);

    private final MinioProperties minioProperties;
    private final MinioClient minioClient;

    public MinioBucketInitializer(MinioProperties minioProperties, MinioClient minioClient) {
        this.minioProperties = minioProperties;
        this.minioClient = minioClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeBucket() {
        try {
            BucketExistsArgs existsArgs = BucketExistsArgs.builder().bucket(minioProperties.getBucket().getName()).build();
            Boolean bucketExist = minioClient.bucketExists(existsArgs);
            if (Boolean.FALSE.equals(bucketExist)) {
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(minioProperties.getBucket().getName()).build();
                minioClient.makeBucket(makeBucketArgs);
                log.info("Bucket: {} created successfully.", minioProperties.getBucket().getName());
            } else {
                log.warn("Bucket: {} already exists.", minioProperties.getBucket().getName());
            }
        } catch (MinioException e) {
            log.error("Error occurred while checking/creating Bucket: {}", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
