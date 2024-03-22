package com.minio.storage.service.impl;

import com.minio.storage.request.InputFileRequest;
import com.minio.storage.service.MinioDownloadService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MinioDownloadServiceImpl implements MinioDownloadService {

    private final MinioClient minioClient;

    @Override
    public CompletableFuture<byte[]> downloadFile(InputFileRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Download file từ MinIO
                InputStream inputStream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(request.getBucketName())
                                .object(request.getObjectName())
                                .build());

                // Chuyển đổi InputStream thành mảng byte
                return IOUtils.toByteArray(inputStream);
            } catch (Exception e) {
                throw new RuntimeException("Failed to download file: " + e.getMessage());
            }
        });
    }

}
