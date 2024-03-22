package com.minio.storage.api;

import com.minio.storage.request.InputFileRequest;
import com.minio.storage.service.MinioUploadService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "MinioUploadFileAPI", description = "Minio Upload File API")
public class MinioUploadFileAPI {

    private final MinioUploadService minioUploadService;

    public MinioUploadFileAPI(MinioUploadService minioUploadService) {
        this.minioUploadService = minioUploadService;
    }

    @PostMapping
    public ResponseEntity<?> uploadFile(
            @Parameter(description = "Request File") @RequestParam("file") MultipartFile multipartFile,
            @Parameter(description = "Payload Request") @RequestBody InputFileRequest request
    ) {
        try {
            minioUploadService.uploadFile(multipartFile, request);
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while uploading file: " + e.getMessage());
        }
    }

    @PostMapping("/multiple")
    public CompletableFuture<ResponseEntity<List<?>>> uploadMultipleFiles(
            @Parameter(description = "Request Files") @RequestParam("files") List<MultipartFile> multipartFiles,
            @Parameter(description = "Payload Request") @RequestBody InputFileRequest request
    ) {
        try {
            return minioUploadService.uploadMultipleFiles(multipartFiles, request).thenApply(ResponseEntity::ok);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList(new File("Error occurred: " + e.getMessage()))));
        }
    }

    @PostMapping("/async")
    public CompletableFuture<ResponseEntity<?>> uploadFileToMinIOAsync(
            @Parameter(description = "Request File") @RequestParam("file") MultipartFile multipartFile,
            @Parameter(description = "Payload Request") @RequestBody InputFileRequest request
    ) {
        try {
            return minioUploadService.uploadFileToMinIOAsync(multipartFile.getInputStream(), request)
                    .thenApply(response -> ResponseEntity.ok("File uploaded asynchronously successfully."));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while uploading file asynchronously: " + e.getMessage()));
        }
    }

}
