package com.minio.storage.api;

import com.minio.storage.request.InputFileRequest;
import com.minio.storage.request.ResizeRequest;
import com.minio.storage.request.ThumbnailRequest;
import com.minio.storage.service.ProcessImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/images")
@Tag(name = "ProcessImageAPI", description = "Process Image API")
public class ProcessImageAPI {

    private final ProcessImageService processImageService;

    public ProcessImageAPI(ProcessImageService processImageService) {
        this.processImageService = processImageService;
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert image format")
    public ResponseEntity<String> convertImageFormat(
            @Parameter(description = "Request File") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Payload Request") @RequestBody InputFileRequest request
    ) {
        try {
            CompletableFuture<Void> future = processImageService.convertImageFormatAsync(file, request);
            future.join();
            return new ResponseEntity<>("Image converted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to convert image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/resize")
    @Operation(summary = "Resize image")
    public ResponseEntity<String> resizeImage(
            @Parameter(description = "Request File") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Payload Request") @RequestBody ResizeRequest request
    ) {
        try {
            CompletableFuture<Void> future = processImageService.resizeImageAsync(file, request);
            future.join();
            return new ResponseEntity<>("Image resized successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to resize image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/thumbnail")
    @Operation(summary = "Create thumbnail")
    public ResponseEntity<String> createThumbnail(
            @Parameter(description = "Request File") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Payload Request") @RequestBody ThumbnailRequest request
    ) {
        try {
            CompletableFuture<Void> future = processImageService.createThumbnailAsync(file, request);
            future.join();
            return new ResponseEntity<>("Thumbnail created successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create thumbnail: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}