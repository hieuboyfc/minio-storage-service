package com.minio.storage.api;

import com.minio.storage.request.VideoSettingRequest;
import com.minio.storage.service.ProcessVideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/videos")
@Tag(name = "ProcessVideoAPI", description = "Process Video API")
public class ProcessVideoAPI {

    private final ProcessVideoService processVideoService;

    public ProcessVideoAPI(ProcessVideoService processVideoService) {
        this.processVideoService = processVideoService;
    }

    @PostMapping("/process")
    @Operation(summary = "Process video")
    public CompletableFuture<ResponseEntity<?>> processVideo(
            @Parameter(description = "Request File") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Payload Request") @RequestBody VideoSettingRequest request
    ) {
        try {
            return processVideoService.processVideo(file, request).thenApply(ResponseEntity::ok);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(e.getMessage()));
        }
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert video format")
    public CompletableFuture<ResponseEntity<?>> convertVideoFormat(
            @Parameter(description = "Request File") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Payload Request") @RequestBody VideoSettingRequest request
    ) {
        try {
            return processVideoService.convertVideoFormat(file, request).thenApply(ResponseEntity::ok);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(e.getMessage()));
        }
    }

    @PostMapping("/thumbnail")
    @Operation(summary = "Create thumbnail")
    public CompletableFuture<ResponseEntity<?>> createVideoThumbnail(
            @Parameter(description = "Request Video Path") @RequestParam("videoPath") String videoPath,
            @Parameter(description = "Request Time In Seconds") @RequestParam("timeInSeconds") long timeInSeconds
    ) {
        try {
            return processVideoService.createVideoThumbnail(videoPath, timeInSeconds).thenApply(ResponseEntity::ok);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(e.getMessage()));
        }
    }

}