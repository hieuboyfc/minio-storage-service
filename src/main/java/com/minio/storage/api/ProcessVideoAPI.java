package com.minio.storage.api;

import com.minio.storage.payload.request.VideoSettingRequest;
import com.minio.storage.service.ProcessVideoService;
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
@RequestMapping("/api/videos")
@Tag(name = "ProcessVideoAPI", description = "Process Video API")
public class ProcessVideoAPI {

    private final ProcessVideoService service;

    public ProcessVideoAPI(ProcessVideoService service) {
        this.service = service;
    }

    @PostMapping("/process")
    @Operation(summary = "MinIO - Process Video", description = "MinIO - Process Video", tags = {"ProcessVideoAPI"})
    public CompletableFuture<ResponseEntity<String>> processVideo(
            @Parameter(description = "Request File") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Payload Request") @RequestBody VideoSettingRequest request
    ) {
        try {
            return getService().processVideo(file, request)
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(throwable -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (Exception e) {
            // return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(e.getMessage()));
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    @PostMapping("/convert")
    @Operation(summary = "MinIO - Convert Video Format", description = "MinIO - Convert Video Format", tags = {"ProcessVideoAPI"})
    public CompletableFuture<ResponseEntity<String>> convertVideoFormat(
            @Parameter(description = "Request File") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Payload Request") @RequestBody VideoSettingRequest request
    ) {
        try {
            return getService().convertVideoFormat(file, request)
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(throwable -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    @PostMapping("/thumbnail")
    @Operation(summary = "MinIO - Create Thumbnail", description = "MinIO - Create Thumbnail", tags = {"ProcessVideoAPI"})
    public CompletableFuture<ResponseEntity<String>> createVideoThumbnail(
            @Parameter(description = "Request Video Path") @RequestParam("videoPath") String videoPath,
            @Parameter(description = "Request Time In Seconds") @RequestParam("timeInSeconds") long timeInSeconds
    ) {
        try {
            return getService().createVideoThumbnail(videoPath, timeInSeconds)
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(throwable -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

}