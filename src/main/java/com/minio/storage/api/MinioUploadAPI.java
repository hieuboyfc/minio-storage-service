package com.minio.storage.api;

import com.minio.storage.entities.FileInfo;
import com.minio.storage.request.InputFileRequest;
import com.minio.storage.service.MinioUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
@RestController
@RequestMapping("/api/upload")
@Tag(name = "MinioUploadAPI", description = "Minio Upload API")
public class MinioUploadAPI {

    private final MinioUploadService service;

    public MinioUploadAPI(MinioUploadService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "MinIO - Upload File", description = "MinIO - Upload File", tags = {"MinioUploadAPI"})
    public ResponseEntity<?> uploadFile(
            @Parameter(description = "Request File") @RequestParam("file") MultipartFile multipartFile,
            @Parameter(description = "Payload Request") @RequestBody InputFileRequest request
    ) {
        try {
            getService().uploadFile(multipartFile, request);
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while uploading file: " + e.getMessage());
        }
    }

    @PostMapping("/multiple")
    @Operation(summary = "MinIO - Upload Multiple Files", description = "MinIO - Upload Multiple Files", tags = {"MinioUploadAPI"})
    public CompletableFuture<ResponseEntity<List<FileInfo>>> uploadMultipleFiles(
            @Parameter(description = "Request Files") @RequestParam("files") List<MultipartFile> multipartFiles,
            @Parameter(description = "Payload Request") @RequestBody InputFileRequest request
    ) {
        try {
            return getService().uploadMultipleFiles(multipartFiles, request)
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(throwable -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    @PostMapping("/async")
    @Operation(summary = "MinIO - Upload File To Async", description = "MinIO - Upload File To Async", tags = {"MinioUploadAPI"})
    public CompletableFuture<ResponseEntity<Void>> uploadFileToMinIOAsync(
            @Parameter(description = "Request File") @RequestParam("file") MultipartFile multipartFile,
            @Parameter(description = "Payload Request") @RequestBody InputFileRequest request
    ) {
        try {
            return getService().uploadFileToMinIOAsync(multipartFile.getInputStream(), request)
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(throwable -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

}
