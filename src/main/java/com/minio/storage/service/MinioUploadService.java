package com.minio.storage.service;

import com.minio.storage.entities.FileInfo;
import com.minio.storage.payload.request.InputFileRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MinioUploadService {

    void uploadFile(MultipartFile multipartFile, InputFileRequest request);

    CompletableFuture<List<FileInfo>> uploadMultipleFiles(List<MultipartFile> files, InputFileRequest request);

    CompletableFuture<Void> uploadFileToMinIOAsync(InputStream inputStream, InputFileRequest request);

}
