package com.zimji.storage.service;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${minio.bucket.name}")
    String defaultBucketName;

    @Value("${minio.default.folder}")
    String defaultBaseFolder;

    private final MinioClient minioClient;

    public MinioAdapterService(MinioClient minioClient) {
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
                    .bucket(defaultBucketName)
                    .object(file.getOriginalFilename())
                    .stream(inputStream, inputStream.available(), -1)
                    .build());

            if (response != null) {
                System.out.println("File " + file.getOriginalFilename() + " uploaded successfully.");
            } else {
                System.out.println("Failed to upload file " + file.getOriginalFilename());
            }
        } catch (MinioException e) {
            throw new RuntimeException("Failed to upload file " + file.getOriginalFilename(), e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InputStream downloadFile(String fileName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(defaultBucketName)
                            .object(fileName)
                            .build()
            );
        } catch (MinioException e) {
            throw new RuntimeException("Failed to download file " + fileName, e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTempUrl(String objectName)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException, MinioException {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("response-content-type", "application/json");
        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(defaultBucketName)
                        .object(objectName)
                        .expiry(2, TimeUnit.HOURS)
                        .extraQueryParams(requestParams)
                        .build());
        System.out.println(url);
        return url;
    }

}