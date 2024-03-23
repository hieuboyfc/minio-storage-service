package com.minio.storage.service;

import com.minio.storage.request.InputFileRequest;
import com.minio.storage.request.ResizeRequest;
import com.minio.storage.request.ThumbnailRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface ProcessImageService {

    CompletableFuture<Void> convertImageFormatAsync(MultipartFile file, InputFileRequest request);

    CompletableFuture<Void> resizeImageAsync(MultipartFile file, ResizeRequest request);

    CompletableFuture<Void> createThumbnailAsync(MultipartFile file, ThumbnailRequest request);

}
