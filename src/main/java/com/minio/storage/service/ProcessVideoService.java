package com.minio.storage.service;

import com.minio.storage.request.VideoSettingRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface ProcessVideoService {

    CompletableFuture<String> processVideo(MultipartFile file, VideoSettingRequest request);

    CompletableFuture<String> convertVideoFormat(MultipartFile file, VideoSettingRequest request);

    CompletableFuture<String> createVideoThumbnail(String videoPath, long timeInSeconds);

}
