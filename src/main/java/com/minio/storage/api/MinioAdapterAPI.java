package com.minio.storage.api;

import com.minio.storage.service.MinioAdapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/adapter")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Tag(name = "MinioAdapterAPI", description = "Minio Adapter API")
public class MinioAdapterAPI {

    final MinioAdapterService minioAdapterService;

    @Value("${server.port}")
    int portNumber;

    @PostMapping("/upload-file")
    @Operation(summary = "MinIO - Upload File", description = "MinIO - Upload File", tags = {"MinioAdapterAPI"})
    public ResponseEntity<?> uploadFile(
            @Parameter(description = "Request File") @RequestParam MultipartFile multipartFile,
            @Parameter(description = "String Bucket Name") @RequestParam String bucketName
    ) {
        return ResponseEntity.ok(minioAdapterService.putObject(multipartFile, bucketName));
    }

    @PostMapping("/add-bucket/{bucketName}")
    @Operation(summary = "MinIO - Add Bucket", description = "MinIO - Add Bucket", tags = {"MinioAdapterAPI"})
    public void addBucket(@Parameter(description = "Path Variable Bucket Name") @PathVariable String bucketName) {
        minioAdapterService.makeBucket(bucketName);
    }

    @GetMapping("/show-object/{bucketName}")
    @Operation(summary = "MinIO - Show Object", description = "MinIO - Show Object", tags = {"MinioAdapterAPI"})
    public ResponseEntity<?> showObject(@Parameter(description = "Path Variable Bucket Name") @PathVariable String bucketName) {
        return ResponseEntity.ok(minioAdapterService.listObjectNames(bucketName));
    }

    @GetMapping("/show-bucket-name")
    @Operation(summary = "MinIO - Show Bucket Name", description = "MinIO - Show Bucket Name", tags = {"MinioAdapterAPI"})
    public ResponseEntity<?> showBucketName() {
        return ResponseEntity.ok(minioAdapterService.listBucketName());
    }

    @DeleteMapping("/remove-bucket/{bucketName}")
    @Operation(summary = "MinIO - Remove Bucket", description = "MinIO - Remove Bucket", tags = {"MinioAdapterAPI"})
    public ResponseEntity<?> removeBucket(@Parameter(description = "Path Variable Bucket Name") @PathVariable String bucketName) {
        return ResponseEntity.ok(minioAdapterService.removeBucket(bucketName));
    }

    @DeleteMapping("/remove-object/{bucketName}/{objectName}")
    @Operation(summary = "MinIO - Remove Object", description = "MinIO - Remove Object", tags = {"MinioAdapterAPI"})
    public ResponseEntity<?> removeObject(
            @Parameter(description = "Path Variable Bucket Name") @PathVariable String bucketName,
            @Parameter(description = "Path Variable Object Name") @PathVariable String objectName
    ) {
        return ResponseEntity.ok(minioAdapterService.removeObject(bucketName, objectName));
    }

    @DeleteMapping("/remove-list-object/{bucketName}")
    @Operation(summary = "MinIO - Remove List Object", description = "MinIO - Remove List Object", tags = {"MinioAdapterAPI"})
    public ResponseEntity<?> removeListObject(
            @Parameter(description = "Path Variable Bucket Name") @PathVariable String bucketName,
            @Parameter(description = "Request List String") @RequestBody List<String> listObjectNames
    ) {
        return ResponseEntity.ok(minioAdapterService.removeListObject(bucketName, listObjectNames));
    }

    @GetMapping("/show-list-and-download-url/{bucketName}")
    @Operation(summary = "MinIO - Show List And Download Url", description = "MinIO - Show List And Download Url", tags = {"MinioAdapterAPI"})
    public ResponseEntity<?> showListObjectNameAndDownloadUrl(
            @Parameter(description = "Path Variable Bucket Name") @PathVariable String bucketName
    ) {
        Map<String, String> resultMap = new HashMap<>();
        List<String> listObjectNames = minioAdapterService.listObjectNames(bucketName);

        String url = "http://localhost:" + portNumber + "/minio/download/" + bucketName + "/";
        for (String listObjectName : listObjectNames) {
            resultMap.put(listObjectName, url + listObjectName);
        }

        return ResponseEntity.ok(resultMap);
    }

    @GetMapping("/download-file/{bucketName}/{objectName}")
    @Operation(summary = "MinIO - Download File", description = "MinIO - Download File", tags = {"MinioAdapterAPI"})
    public void downloadFile(
            @Parameter(description = "Http Servlet Response") HttpServletResponse response,
            @Parameter(description = "Path Variable Bucket Name") @PathVariable String bucketName,
            @Parameter(description = "Path Variable Object Name") @PathVariable String objectName
    ) {
        minioAdapterService.downloadObject(response, bucketName, objectName);
    }

}
