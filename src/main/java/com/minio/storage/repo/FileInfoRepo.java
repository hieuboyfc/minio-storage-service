package com.minio.storage.repo;

import com.minio.storage.entities.FileInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface FileInfoRepo extends XBaseRepo<FileInfo, String> {
}
