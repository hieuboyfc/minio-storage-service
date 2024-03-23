package com.minio.storage.service;

import com.minio.storage.request.InputFileRequest;

import java.util.concurrent.CompletableFuture;

public interface MinioFolderService {

    CompletableFuture<String> createFolder(InputFileRequest request);

}
