package com.minio.storage.service.impl;

import com.minio.storage.request.InputFileRequest;
import com.minio.storage.service.MinioDownloadService;
import com.minio.storage.utils.FileRequestUtils;
import com.minio.storage.utils.FileUtils;
import com.minio.storage.utils.compression.RarUtils;
import com.minio.storage.utils.compression.TarUtils;
import com.minio.storage.utils.compression.ZipUtils;
import com.minio.storage.utils.enums.FileExtension;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MinioDownloadServiceImpl implements MinioDownloadService {

    private static final long MAX_PART_SIZE = 500 * 1024 * 1024;

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

    @Async
    @Override
    public CompletableFuture<List<byte[]>> downloadMultipleFiles(List<InputFileRequest> requests) {
        try {
            List<byte[]> filesData = new ArrayList<>();

            for (InputFileRequest request : requests) {
                GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                        .bucket(request.getBucketName())
                        .object(request.getObjectName())
                        .build();

                byte[] fileData = minioClient.getObject(getObjectArgs).readAllBytes();
                filesData.add(fileData);
            }

            boolean isCompressFile = FileRequestUtils.findFirstField(
                    requests, InputFileRequest::getIsCompressFile,
                    InputFileRequest::getIsCompressFile, false
            );

            String typeCompressFile = FileRequestUtils.findFirstField(
                    requests, o -> ObjectUtils.isNotEmpty(o.getTypeCompressFile()),
                    InputFileRequest::getTypeCompressFile, "rar"
            );

            if (Boolean.TRUE.equals(isCompressFile)) {
                byte[] combinedFileData = FileUtils.combineFiles(filesData);

                // Kiểm tra kích thước vượt quá 500MB
                if (combinedFileData.length > MAX_PART_SIZE) {
                    filesData.addAll(splitFiles(combinedFileData, typeCompressFile));
                } else {
                    combinedFileData = compressFile(combinedFileData, typeCompressFile);
                    filesData.add(combinedFileData);
                }
            }
            return CompletableFuture.completedFuture(filesData);
        } catch (Exception e) {
            CompletableFuture<List<byte[]>> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    private byte[] compressFile(byte[] fileData, String compressionType) throws IOException {
        FileExtension fileType = FileExtension.valueOf(compressionType.toUpperCase());
        return switch (fileType) {
            case RAR -> RarUtils.compressToRar(fileData);
            case TAR -> TarUtils.compressToTar(fileData);
            case ZIP -> ZipUtils.compressToZip(fileData);
            default -> throw new IllegalArgumentException("Unsupported compression format: " + compressionType);
        };
    }

    private List<byte[]> splitFiles(byte[] fileData, String compressionType) throws IOException {
        FileExtension fileType = FileExtension.valueOf(compressionType.toUpperCase());
        return switch (fileType) {
            case RAR -> RarUtils.splitFilesToRar(fileData, MAX_PART_SIZE);
            case TAR -> TarUtils.splitFilesToTar(fileData, MAX_PART_SIZE);
            case ZIP -> ZipUtils.splitFilesToZip(fileData, MAX_PART_SIZE);
            default -> throw new IllegalArgumentException("Unsupported compression format: " + compressionType);
        };
    }

}
