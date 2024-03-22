package com.minio.storage.service;

import io.minio.messages.Bucket;

import java.util.List;

public interface MinioBucketService {

    List<Bucket> getAllBuckets();

    void createBucketIfNotExists(String bucketName);

}
