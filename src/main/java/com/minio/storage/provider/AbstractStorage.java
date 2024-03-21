package com.minio.storage.provider;

import com.google.gson.JsonObject;
import com.minio.storage.dto.FileAttributeDTO;
import com.minio.storage.entities.BucketConfig;
import com.minio.storage.entities.File;
import com.minio.storage.process.MediaProcess;
import com.minio.storage.utils.enums.QuotaUnit;
import io.minio.errors.*;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.BadRequestException;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Log4j2
public abstract class AbstractStorage implements StorageProvider {

    protected final static String LOCATION_KEY = "storage.dir";
    protected final static String LOCATION_DEFAULT = "/data/upload";
    protected final static String MEDIA_PATH = "media";

    protected MediaProcess mediaProcess;

    protected String providerCode;
    protected String location;
    protected Path basePath;
    protected Path videoPath;
    protected JsonObject config;

    public AbstractStorage(Environment env, MediaProcess mediaProcess, JsonObject config) throws IOException {
        try {
            this.location = env.getProperty(LOCATION_KEY, LOCATION_DEFAULT);
            this.basePath = Paths.get(location);
            this.videoPath = Paths.get(location, MEDIA_PATH);
            this.mediaProcess = mediaProcess;
            this.config = config;
            Files.createDirectories(basePath);
            Files.createDirectories(videoPath);
            log.info("===> Listener root path: " + this.basePath);
            initConnectFromConfig();
        } catch (IOException e) {
            throw new BadRequestException("Init: unable to create base storage directory: " + e.getMessage());
        }
    }

    @Override
    public FileAttributeDTO uploadFile(FileAttributeDTO fileAttributeDTO) throws IOException {
        return null;
    }

    @Override
    public FileAttributeDTO downloadFile(File file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return null;
    }

    @Override
    public boolean checkExist(File file, FileAttributeDTO fileAttributeDTO) throws IOException {
        return false;
    }

    @Override
    public Boolean deleteFile(File file) throws IOException {
        return null;
    }

    @Override
    public FileAttributeDTO downloadFile(File file, String resize) throws Exception {
        return null;
    }

    @Override
    public InputStream downloadFile(File file, Long version) {
        return null;
    }

    @Override
    public FileAttributeDTO downloadFile(String bucketName, File file, String resize) throws Exception {
        return null;
    }

    @Override
    public FileAttributeDTO downloadFile(String bucketName, File file) throws Exception {
        return null;
    }

    @Override
    public String getLocation() {
        return null;
    }

    @Override
    public void initConnectFromConfig() {

    }

    @Override
    public void setBucketKafkaNotification(String providerCode, String bucketName) {

    }

    @Override
    public void makeBucket(String bucketName) {

    }

    @Override
    public void removeBucket(String bucketName) {

    }

    @Override
    public boolean bucketIsEmpty(String bucketName) {
        return false;
    }

    @Override
    public void setBucketQuota(String bucketName, Long size, QuotaUnit unit) {

    }

    @Override
    public void removeObject(String bucketName, String objectName) {

    }

    @Override
    public void copyObject(String bucketName, String originalObject, String replacedObject) {

    }

    @Override
    public void uploadFile(String bucketName, String pathName, String path, String fileId) {

    }

    @Override
    public void moveFolder(String bucketName, String sourcePathName, String dictPathName) {

    }

    @Override
    public Long computeSizeOfObject(String bucketName, String objectName) {
        return null;
    }

    @Override
    public BucketConfig getBucketAvailable(Long companyId, Long minSize) {
        return null;
    }

    @Override
    public Long getBucketFreeSpace(String bucketName) {
        return null;
    }

    @Override
    public void setBucketLifeCycle(String bucketName, Integer days, String ruleFilter) {

    }

    @Override
    public void setBucketLifeCycle(String bucketName, Integer days) {

    }

    @Override
    public void delBucketLifeCycle(String bucketName) {

    }

    @Override
    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    @Override
    public String getProviderCode() {
        return this.providerCode;
    }

    @Override
    public FileAttributeDTO writeToDisk(FileAttributeDTO fileAttributeDTO) throws IOException {
        return fileAttributeDTO;
    }

    @Override
    public FileAttributeDTO readFromDisk(String bucketName, FileAttributeDTO fileAttributeDTO, String fileName) {
        return null;
    }

    @Override
    public void addCannedPolicy(String name, String policy) {

    }

    @Override
    public void removeCannedPolicy(String name) {

    }
}
