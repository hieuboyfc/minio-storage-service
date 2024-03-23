package com.minio.storage.repo;

import com.minio.storage.entities.BucketConfig;
import org.springframework.stereotype.Repository;

@Repository
public interface BucketConfigRepo extends XBaseRepo<BucketConfig, String> {
}
