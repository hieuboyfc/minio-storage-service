package com.minio.storage.service;

import com.minio.storage.request.InputFileRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MinioDownloadService {

    CompletableFuture<byte[]> downloadFile(InputFileRequest request);

    CompletableFuture<List<byte[]>> downloadMultipleFiles(List<InputFileRequest> requests);

}
