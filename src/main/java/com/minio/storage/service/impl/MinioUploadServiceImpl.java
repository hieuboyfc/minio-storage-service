package com.minio.storage.service.impl;

import com.minio.storage.entities.FileInfo;
import com.minio.storage.payload.request.InputFileRequest;
import com.minio.storage.service.MinioAdapterService;
import com.minio.storage.service.MinioUploadService;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class MinioUploadServiceImpl implements MinioUploadService {

    private final MinioClient minioClient;
    private final MinioAdapterService minioAdapterService;

    @Override
    public void uploadFile(MultipartFile multipartFile, InputFileRequest request) {
        try {
            InputStream inputStream = multipartFile.getInputStream();
            ObjectWriteResponse writeResponse = minioClient.putObject(PutObjectArgs.builder()
                    .bucket(request.getBucketName())
                    .object(multipartFile.getOriginalFilename())
                    .stream(inputStream, inputStream.available(), -1)
                    .build());

            if (ObjectUtils.isNotEmpty(writeResponse)) {
                log.info("File: {} uploaded successfully.", multipartFile.getOriginalFilename());
            } else {
                log.error("File: {} upload failed.", multipartFile.getOriginalFilename());
            }
        } catch (MinioException e) {
            throw new RuntimeException("Error occurred while pushing file: " + multipartFile.getOriginalFilename(), e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CompletableFuture<List<FileInfo>> uploadMultipleFiles(List<MultipartFile> multipartFiles,
                                                                 InputFileRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Kiểm tra nếu thư mục không tồn tại
                minioAdapterService.makeBucket(request.getBucketName());

                // Upload từng file và lưu thông tin vào danh sách
                List<FileInfo> uploadedFileInfos = multipartFiles.stream()
                        .map(multipartFile -> uploadFileAndSave(multipartFile, request))
                        .collect(Collectors.toList());

                // Lưu danh sách thông tin vào cơ sở dữ liệu
                if (ObjectUtils.isNotEmpty(uploadedFileInfos)) {

                }

                return uploadedFileInfos;
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload files to MinIO: " + e.getMessage());
            }
        });
    }

    @Override
    public CompletableFuture<Void> uploadFileToMinIOAsync(InputStream inputStream, InputFileRequest request) {
        return CompletableFuture.runAsync(() -> {
            try {
                String uniqueFileName = request.getFolderName() + "/" + request.getOutputFilePath();

                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(request.getBucketName())
                        .object(uniqueFileName)
                        .stream(inputStream, inputStream.available(), -1)
                        .build());
            } catch (MinioException e) {
                throw new RuntimeException("Error occurred while pushing file: " + request.getOutputFilePath(), e);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    private FileInfo uploadFileAndSave(MultipartFile multipartFile, InputFileRequest request) {
        try {
            String objectName = multipartFile.getOriginalFilename();
            ObjectWriteResponse writeResponse = minioClient.putObject(PutObjectArgs.builder()
                    .bucket(request.getBucketName())
                    .object(objectName)
                    .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                    .build());

            // Tạo và trả về đối tượng UploadedFile
            FileInfo fileInfo = new FileInfo();
            fileInfo.setName(multipartFile.getOriginalFilename());
            return fileInfo;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file: " + multipartFile.getOriginalFilename(), e);
        }
    }

}
