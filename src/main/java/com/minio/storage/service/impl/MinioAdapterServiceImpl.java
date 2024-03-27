package com.minio.storage.service.impl;

import com.minio.storage.configuration.minio.MinioProperties;
import com.minio.storage.payload.response.FileResponse;
import com.minio.storage.service.MinioAdapterService;
import com.minio.storage.utils.StringGeneratorUtils;
import com.minio.storage.utils.file.FileTypeUtils;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MinioAdapterServiceImpl implements MinioAdapterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioAdapterServiceImpl.class);

    MinioProperties minioProperties;
    MinioClient minioClient;

    @Override
    public boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void makeBucket(String bucketName) {
        try {
            boolean flag = this.bucketExists(bucketName);
            if (Boolean.FALSE.equals(flag)) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } else {
                LOGGER.warn("Bucket: {} already exists.", bucketName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<String> listBucketName() {
        try {
            return listBuckets().stream().map(Bucket::name).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Bucket> listBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean removeBucket(String bucketName) {
        try {
            boolean flag = this.bucketExists(bucketName);
            if (Boolean.TRUE.equals(flag)) {
                Iterable<Result<Item>> myObjects = listObjects(bucketName);

                if (ObjectUtils.isNotEmpty(myObjects) && myObjects.iterator().hasNext()) {
                    return false;
                }

                minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());

                flag = this.bucketExists(bucketName);
                return Boolean.FALSE.equals(flag);
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<String> listObjectNames(String bucketName) {
        try {
            List<String> listObjectNames = new ArrayList<>();
            boolean flag = this.bucketExists(bucketName);
            if (Boolean.TRUE.equals(flag)) {
                Iterable<Result<Item>> myObjects = listObjects(bucketName);
                if (ObjectUtils.isNotEmpty(myObjects)) {
                    for (Result<Item> result : myObjects) {
                        Item item = result.get();
                        listObjectNames.add(item.objectName());
                    }
                }
            }
            return listObjectNames;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public FileResponse putObject(MultipartFile multipartFile, String bucketName) {
        try {
            bucketName = StringUtils.isNotBlank(bucketName) ? bucketName : minioProperties.getBucket().getName();

            boolean flag = this.bucketExists(bucketName);
            if (Boolean.FALSE.equals(flag)) {
                this.makeBucket(bucketName);
            }

            String fileType = FileTypeUtils.getFileType(multipartFile);

            String fileName = multipartFile.getOriginalFilename();

            Long fileSize = multipartFile.getSize();

            assert fileName != null;
            String objectName = StringGeneratorUtils.getUUID().replaceAll("-", "") + fileName.substring(fileName.lastIndexOf("."));

            LocalDateTime createdTime = LocalDateTime.now();

            putObject(bucketName, multipartFile, objectName, fileType);

            LOGGER.info("---> MinioBucketServiceImpl | getFileType | url : {}/{}/{}", minioProperties.getEndPoint().getUrl(), bucketName, objectName);

            return FileResponse.builder()
                    .filename(objectName)
                    .fileSize(fileSize)
                    .contentType(fileType)
                    .createdTime(createdTime)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void downloadObject(HttpServletResponse response, String bucketName, String objectName) {
        try (InputStream inputStream = getObject(bucketName, objectName)) {
            if (ObjectUtils.isNotEmpty(inputStream)) {
                response.setHeader("Content-Disposition", "attachment;filename="
                        + URLEncoder.encode(objectName, StandardCharsets.UTF_8));
                response.setCharacterEncoding("UTF-8");
                IOUtils.copy(inputStream, response.getOutputStream());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean removeObject(String bucketName, String objectName) {
        try {
            boolean flag = this.bucketExists(bucketName);
            if (Boolean.TRUE.equals(flag)) {
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean removeListObject(String bucketName, List<String> listObjectNames) {
        try {
            boolean flag = this.bucketExists(bucketName);
            if (Boolean.TRUE.equals(flag)) {
                List<DeleteObject> objects = new LinkedList<>();
                for (String objectName : listObjectNames) {
                    objects.add(new DeleteObject(objectName));
                }
                Iterable<Result<DeleteError>> results =
                        minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build());

                for (Result<DeleteError> result : results) {
                    DeleteError error = result.get();
                    LOGGER.info("MinioBucketServiceImpl | removeObject | Error : " + error.objectName() + " - " + error.message());
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String getObjectUrl(String bucketName, String objectName) {
        try {
            boolean flag = this.bucketExists(bucketName);
            if (Boolean.TRUE.equals(flag)) {
                Map<String, String> requestParams = new HashMap<>();
                requestParams.put("response-content-type", "application/json");

                return minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.GET)
                                .bucket(bucketName)
                                .object(objectName)
                                .expiry(2, TimeUnit.MINUTES)
                                .extraQueryParams(requestParams)
                                .build()
                );
            }
            return StringUtils.EMPTY;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public StatObjectResponse statObject(String bucketName, String objectName) {
        try {
            boolean flag = this.bucketExists(bucketName);
            if (Boolean.TRUE.equals(flag)) {
                return minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void putObject(String bucketName, MultipartFile multipartFile, String filename, String fileType) {
        try (InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes())) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .stream(inputStream, -1, minioProperties.getFileSize())
                    .contentType(fileType)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public boolean putObject(String bucketName, String objectName, InputStream inputStream, String contentType) {
        try {
            boolean flag = this.bucketExists(bucketName);
            if (Boolean.TRUE.equals(flag)) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, inputStream.available(), -1)
                        .contentType(contentType)
                        .build());

                StatObjectResponse statObject = statObject(bucketName, objectName);
                return ObjectUtils.isNotEmpty(statObject);
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Iterable<Result<Item>> listObjects(String bucketName) {
        try {
            boolean flag = bucketExists(bucketName);
            if (Boolean.TRUE.equals(flag)) {
                return minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private InputStream getObject(String bucketName, String objectName) {
        try {
            boolean flag = this.bucketExists(bucketName);
            if (Boolean.TRUE.equals(flag)) {
                StatObjectResponse statObject = statObject(bucketName, objectName);
                if (ObjectUtils.isNotEmpty(statObject)) {
                    return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private InputStream getObject(String bucketName, String objectName, long offset, Long length) {
        try {
            boolean flag = this.bucketExists(bucketName);
            if (Boolean.TRUE.equals(flag)) {
                StatObjectResponse statObject = statObject(bucketName, objectName);
                if (ObjectUtils.isNotEmpty(statObject)) {
                    return minioClient.getObject(GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .offset(offset)
                            .length(length)
                            .build());
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
