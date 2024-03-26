package com.minio.storage.service;

import com.minio.storage.payload.response.FileResponse;
import io.minio.messages.Bucket;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface MinioAdapterService {

    // Kiểm tra xem Bucket đã tồn tại chưa?
    boolean bucketExists(String bucketName);

    // Tạo một Bucket
    void makeBucket(String bucketName);

    // Liệt kê tất cả các tên bucket
    List<String> listBucketName();

    // Liệt kê tất cả các Bucket
    List<Bucket> listBuckets();

    // Xóa Bucket bằng Tên
    boolean removeBucket(String bucketName);

    // Liệt kê tất cả các tên đối tượng trong Bucket
    List<String> listObjectNames(String bucketName);

    // Tải lên các tệp vào Bucket
    FileResponse putObject(MultipartFile multipartFile, String bucketName);

    // Tải xuống tệp từ Bucket
    InputStream downloadObject(String bucketName, String objectName);

    // Xóa tệp trong Bucket
    boolean removeObject(String bucketName, String objectName);

    // Xóa tất cả các tệp trong Bucket
    boolean removeListObject(String bucketName, List<String> objectNameList);

    // Lấy đường dẫn tệp từ Bucket
    String getObjectUrl(String bucketName, String objectName);

}
