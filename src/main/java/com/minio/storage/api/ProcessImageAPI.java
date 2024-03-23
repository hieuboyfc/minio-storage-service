package com.minio.storage.api;

import com.minio.storage.request.InputFileRequest;
import com.minio.storage.request.ResizeRequest;
import com.minio.storage.request.ThumbnailRequest;
import com.minio.storage.service.ProcessImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@Getter
@RestController
@RequestMapping("/api/images")
@Tag(name = "ProcessImageAPI", description = "Process Image API")
public class ProcessImageAPI {

    private final ProcessImageService service;

    public ProcessImageAPI(ProcessImageService service) {
        this.service = service;
    }

    @PostMapping("/convert")
    @Operation(summary = "MinIO - Convert Image Format", description = "MinIO - Convert Image Format", tags = {"ProcessImageAPI"})
    public CompletableFuture<ResponseEntity<Void>> convertImageFormat(
            @Parameter(description = "Request File") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Payload Request") @RequestBody InputFileRequest request
    ) {
        try {
            return getService().convertImageFormatAsync(file, request)
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(throwable -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    @PostMapping("/resize")
    @Operation(summary = "MinIO - Resize Image", description = "MinIO - Resize Image", tags = {"ProcessImageAPI"})
    public CompletableFuture<ResponseEntity<Void>> resizeImage(
            @Parameter(description = "Request File") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Payload Request") @RequestBody ResizeRequest request
    ) {
        try {
            return getService().resizeImageAsync(file, request)
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(throwable -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    @PostMapping("/thumbnail")
    @Operation(summary = "MinIO - Create Thumbnail", description = "MinIO - Create Thumbnail", tags = {"ProcessImageAPI"})
    public CompletableFuture<ResponseEntity<Void>> createThumbnail(
            @Parameter(description = "Request File") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Payload Request") @RequestBody ThumbnailRequest request
    ) {
        try {
            return getService().createThumbnailAsync(file, request)
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(throwable -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

}