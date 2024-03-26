package com.minio.storage.api;

import com.minio.storage.payload.request.InputFileRequest;
import com.minio.storage.service.MinioFolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Getter
@RestController
@RequestMapping("/api/folder")
@Tag(name = "MinioFolderAPI", description = "Minio Folder API")
public class MinioFolderAPI {

    private final MinioFolderService service;

    public MinioFolderAPI(MinioFolderService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "MinIO - Create Folder", description = "MinIO - Create Folder", tags = {"MinioFolderAPI"})
    public CompletableFuture<ResponseEntity<String>> createFolder(
            @Parameter(description = "Payload Request") @RequestBody InputFileRequest request
    ) {
        try {
            return getService().createFolder(request)
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(throwable -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

}
