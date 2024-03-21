package com.minio.storage.service;

import com.minio.storage.config.MinioProperties;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class MinioAdapterService {

    private static final Logger log = LoggerFactory.getLogger(MinioAdapterService.class);

    private final MinioProperties minioProperties;
    private final MinioClient minioClient;

    public MinioAdapterService(MinioProperties minioProperties, MinioClient minioClient) {
        this.minioProperties = minioProperties;
        this.minioClient = minioClient;
    }

    @PostConstruct
    public void init() {
    }

    public List<Bucket> getAllBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void uploadFile(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucket().getName())
                    .object(file.getOriginalFilename())
                    .stream(inputStream, inputStream.available(), -1)
                    .build());

            if (ObjectUtils.isNotEmpty(response)) {
                log.info("File: {} uploaded successfully.", file.getOriginalFilename());
            } else {
                log.error("File: {} upload failed.", file.getOriginalFilename());
            }
        } catch (MinioException e) {
            throw new RuntimeException("Error occurred while pushing file: " + file.getOriginalFilename(), e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public InputStream downloadFile(String fileName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucket().getName())
                            .object(fileName)
                            .build()
            );
        } catch (MinioException e) {
            throw new RuntimeException("Error occurred while downloading file: " + fileName, e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getTempUrl(String objectName)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException, MinioException {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("response-content-type", "application/json");
        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(minioProperties.getBucket().getName())
                        .object(objectName)
                        .expiry(2, TimeUnit.HOURS)
                        .extraQueryParams(requestParams)
                        .build());
        log.info(url);
        return url;
    }

}