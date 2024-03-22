package com.minio.storage.service;

import com.minio.storage.request.InputFileRequest;

import java.util.concurrent.CompletableFuture;

public interface MinioDownloadService {

    CompletableFuture<byte[]> downloadFile(InputFileRequest request);

}
