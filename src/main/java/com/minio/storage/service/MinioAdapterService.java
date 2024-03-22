package com.minio.storage.service;

import com.minio.storage.configuration.minio.MinioProperties;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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