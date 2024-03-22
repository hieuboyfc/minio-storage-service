package com.minio.storage.api;

import com.minio.storage.service.MinioAdapterService;
import com.minio.storage.service.MinioBucketService;
import io.minio.messages.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MinioStorageAPI {

    private final MinioAdapterService minioAdapterService;
    private final MinioBucketService minioBucketService;

    @GetMapping(path = "/buckets")
    public List<Bucket> listBuckets() {
        return minioBucketService.getAllBuckets();
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