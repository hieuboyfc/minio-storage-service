package com.minio.storage.service.impl;

import com.minio.storage.payload.request.InputFileRequest;
import com.minio.storage.service.MinioAdapterService;
import com.minio.storage.service.MinioFolderService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MinioFolderServiceImpl implements MinioFolderService {

    private final MinioClient minioClient;
    private final MinioAdapterService minioAdapterService;

    @Override
    public CompletableFuture<String> createFolder(InputFileRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String folderPath = request.getFolderName() + "/";

                // Kiểm tra nếu thư mục không tồn tại
                minioAdapterService.makeBucket(request.getBucketName());

                // Tạo một object với nội dung trống để tạo thư mục trên MinIO
                minioClient.putObject(PutObjectArgs
                        .builder()
                        .bucket(request.getBucketName())
                        .object(folderPath)
                        .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                        .build());
                return "Folder created successfully: " + request.getFolderName();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create folder on MinIO: " + e.getMessage());
            }
        });
    }

}
