package com.minio.storage.service.impl;

import com.minio.storage.service.MinioBucketService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import io.minio.messages.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class MinioBucketServiceImpl implements MinioBucketService {

    private final MinioClient minioClient;

    @Override
    public List<Bucket> getAllBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void createBucketIfNotExists(String bucketName) {
        try {
            BucketExistsArgs existsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            Boolean bucketExist = minioClient.bucketExists(existsArgs);
            if (Boolean.FALSE.equals(bucketExist)) {
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(bucketName).build();
                minioClient.makeBucket(makeBucketArgs);
                log.info("Bucket: {} created successfully.", bucketName);
            } else {
                log.warn("Bucket: {} already exists.", bucketName);
            }
        } catch (MinioException e) {
            log.error("Error occurred while checking/creating Bucket: {}", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
