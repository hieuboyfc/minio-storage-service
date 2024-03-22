/*
package com.minio.storage.provider;

import com.minio.storage.dto.FileAttributeDTO;
import com.minio.storage.entities.BucketConfig;
import com.minio.storage.entities.File;
import com.minio.storage.utils.enums.QuotaUnit;
import io.minio.errors.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface StorageProvider {

    FileAttributeDTO uploadFile(FileAttributeDTO fileAttributeDTO) throws IOException;

    FileAttributeDTO downloadFile(File file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    boolean checkExist(File file, FileAttributeDTO fileAttributeDTO) throws IOException;

    Boolean deleteFile(File file) throws IOException;

    FileAttributeDTO downloadFile(File file, String resize) throws Exception;

    InputStream downloadFile(File file, Long version);

    FileAttributeDTO downloadFile(String bucketName, File file, String resize) throws Exception;

    FileAttributeDTO downloadFile(String bucketName, File file) throws Exception;

    String getLocation();

    void initConnectFromConfig();

    void setBucketKafkaNotification(String providerCode, String bucketName);

    void makeBucket(String bucketName);

    void removeBucket(String bucketName);

    boolean bucketIsEmpty(String bucketName);

    void setBucketQuota(String bucketName, Long size, QuotaUnit unit);

    void removeObject(String bucketName, String objectName);

    void copyObject(String bucketName, String originalObject, String replacedObject);

    void uploadFile(String bucketName, String pathName, String path, String fileId);

    void moveFolder(String bucketName, String sourcePathName, String dictPathName);

    Long computeSizeOfObject(String bucketName, String objectName);

    BucketConfig getBucketAvailable(Long companyId, Long minSize);

    Long getBucketFreeSpace(String bucketName);

    void setBucketLifeCycle(String bucketName, Integer days, String ruleFilter);

    void setBucketLifeCycle(String bucketName, Integer days);

    void delBucketLifeCycle(String bucketName);

    void setProviderCode(String providerCode);

    String getProviderCode();

    FileAttributeDTO writeToDisk(FileAttributeDTO fileAttributeDTO) throws IOException;

    FileAttributeDTO readFromDisk(String bucketName, FileAttributeDTO fileAttributeDTO, String fileName);

    void addCannedPolicy(String name, String policy);

    void removeCannedPolicy(String name);

}
*/
