package com.minio.storage.api;

import com.minio.storage.service.MinioAdapterService;
import io.minio.messages.Bucket;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
public class MinioStorageAPI {

    private final MinioAdapterService minioAdapterService;

    public MinioStorageAPI(MinioAdapterService minioAdapterService) {
        this.minioAdapterService = minioAdapterService;
    }

    @GetMapping(path = "/buckets")
    public List<Bucket> listBuckets() {
        return minioAdapterService.getAllBuckets();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            minioAdapterService.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable("fileName") String fileName) {
        try {
            InputStream stream = minioAdapterService.downloadFile(fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(stream));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/temporary-url/{fileName}")
    public ResponseEntity<String> getTemporaryFileUrl(@PathVariable("fileName") String fileName) {
        try {
            return ResponseEntity.ok(minioAdapterService.getTempUrl(fileName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate temporary URL for file.");
        }
    }

}