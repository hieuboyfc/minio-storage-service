package com.minio.storage.api;

import com.minio.storage.payload.request.InputFileRequest;
import com.minio.storage.service.MinioDownloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
@RestController
@RequestMapping("/api/download")
@Tag(name = "MinioDownloadAPI", description = "Minio Download API")
public class MinioDownloadAPI {

    private final MinioDownloadService service;

    public MinioDownloadAPI(MinioDownloadService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "MinIO - Download File", description = "MinIO - Download File", tags = {"MinioDownloadAPI"})
    public CompletableFuture<ResponseEntity<byte[]>> downloadFile(
            @Parameter(description = "Payload Request") @RequestBody InputFileRequest request
    ) {
        try {
            return getService().downloadFile(request)
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(throwable -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    @PostMapping("/multiple")
    @Operation(summary = "MinIO - Download Multiple Files", description = "MinIO - Download Multiple Files", tags = {"MinioDownloadAPI"})
    public CompletableFuture<ResponseEntity<List<byte[]>>> downloadMultipleFiles(
            @Parameter(description = "Payload List Request") @RequestBody List<InputFileRequest> requests
    ) {
        try {
            return getService().downloadMultipleFiles(requests)
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(throwable -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

}
