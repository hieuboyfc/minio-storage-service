package com.minio.storage.api;

import com.minio.storage.request.InputFileRequest;
import com.minio.storage.service.MinioFolderService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/folder")
@Tag(name = "MinioFolderAPI", description = "Minio Folder API")
public class MinioFolderAPI {

    private final MinioFolderService service;

    public MinioFolderAPI(MinioFolderService service) {
        this.service = service;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<?>> createFolder(
            @Parameter(description = "Payload Request") @RequestBody InputFileRequest request
    ) {
        try {
            return service.createFolder(request).thenApply(folder -> ResponseEntity.ok().body(folder));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create folder on MinIO: " + e.getMessage()));
        }
    }

}
