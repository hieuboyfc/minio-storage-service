/*
package com.minio.storage.process;

import com.minio.storage.dto.FileAttributeDTO;
import com.minio.storage.entities.File;
import com.minio.storage.provider.StorageProvider;
import com.minio.storage.utils.DateTimeUtils;
import com.minio.storage.utils.StringUtils;
import com.minio.storage.utils.constant.Constants;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
public class VideoProcess {

    public static String MEDIA_KEY = "media";
    public static String THUMBNAILS_KEY = "thumbs";
    public static String IMAGE_MIME_TYPE = ".jpg";

    public static void upload(FileAttributeDTO fileAttributeDTO, String location) throws IOException {
        java.io.File storageFile = createRealPath(location, fileAttributeDTO);
        fileAttributeDTO.writeTo(storageFile);
        createSimpleLink(storageFile, fileAttributeDTO, location);
        createThumbnail(storageFile, location, fileAttributeDTO.getId());
    }

    public static void upload(File file, FileAttributeDTO fileAttributeDTO, String location) throws IOException {
        java.io.File storageFile = createRealPath(location, file.toAttribute());
        fileAttributeDTO.writeTo(storageFile);
        createSimpleLink(storageFile, fileAttributeDTO, location);
        createThumbnail(storageFile, location, fileAttributeDTO.getId());
    }

    public static void upload(FileAttributeDTO fileAttributeDTO, StorageProvider provider) throws IOException {
        fileAttributeDTO = provider.writeToDisk(fileAttributeDTO);

        // Create temple
        java.io.File storageFile = createTempFile(fileAttributeDTO, MEDIA_KEY);
        fileAttributeDTO.writeTo(storageFile);

        // Create createSymbolicLink --> Cho việc ánh xạ Link tới File gốc, nên theo Provider
        createSimpleLink(storageFile, fileAttributeDTO, provider);

        // Create thumbnail --> Cho việc tạo ảnh cho Video
        createThumbnail(storageFile, provider.getLocation(), fileAttributeDTO.getId());

        // Remove temple
        storageFile.deleteOnExit();
    }

    private static void createSimpleLink(java.io.File source, FileAttributeDTO fileAttributeDTO, StorageProvider provider) throws IOException {
        try {
            StringBuilder buffer = new StringBuilder(provider.getLocation());
            buffer.append(java.io.File.separator).append(MEDIA_KEY);

            Path path = Paths.get(buffer.toString());
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            buffer.append(java.io.File.separator).append(fileAttributeDTO.getId());

            // Tạo link ảo trỏ tới file gốc, đường dẫn ảo : \\data\\upload\\media\\<fileId>
            Files.createSymbolicLink(Paths.get(buffer.toString()), source.toPath());
        } catch (Exception ex) {
            log.error(ex);
        }
    }

    private static void createSimpleLink(java.io.File source, FileAttributeDTO fileAttribute, String location) throws IOException {
        try {
            StringBuilder buffer = new StringBuilder(location);
            buffer.append(java.io.File.separator).append(MEDIA_KEY);

            Path path = Paths.get(buffer.toString());
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            buffer.append(java.io.File.separator).append(fileAttribute.getId());
            // Tạo link ảo trỏ tới file gốc, đường dẫn ảo : \\data\\upload\\media\\<fileId>
            Files.createSymbolicLink(Paths.get(buffer.toString()), source.toPath());
        } catch (Exception e) {
            log.error(e);
        }
    }

    private static void createThumbnail(java.io.File file, String location, String id) {
        try {
            StringBuilder buffer = new StringBuilder("ffmpeg -ss 00:00:02 -i ");
            // Link File gốc của Video
            buffer.append(file.getAbsolutePath());
            buffer.append(" -vframes 1 ");
            // Link tới File Thumbnail theo ID
            buffer.append(createThumbnailFile(id, location));
            String[] cmd = buffer.toString().split(" ");
            log.info("run command thumbs: " + buffer.toString());
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            log.error(e);
        }
    }

    private static String createThumbnailFile(String id, String location) throws IOException {
        StringBuilder buffer = new StringBuilder(location);
        buffer.append(java.io.File.separator).append(MEDIA_KEY);
        buffer.append(java.io.File.separator).append(THUMBNAILS_KEY);

        Path path = Paths.get(buffer.toString());
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        buffer.append(java.io.File.separator).append(id);
        buffer.append(IMAGE_MIME_TYPE);
        return buffer.toString();
    }

    private static java.io.File createRealPath(String location, FileAttributeDTO fileAttributeDTO) throws IOException {
        return new java.io.File(createRealPath(new StringBuffer(location), fileAttributeDTO).toString());
    }

    private static java.io.File createRealPath(String location, File file) throws IOException {
        return new java.io.File(createRealPath(new StringBuffer(location), file).toString());
    }

    private static StringBuffer createRealPath(StringBuffer realPath, File file) throws IOException {
        realPath.append(java.io.File.separator).append(file.getCompanyId());
        realPath.append(java.io.File.separator).append(DateTimeUtils.format(file.getCreateDate(), Constants.DATE_DEFAULT));
        realPath.append(java.io.File.separator).append(file.getCreateBy());

        Path path = Paths.get(realPath.toString());
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        String id = file.getId();
        if (ObjectUtils.isNotEmpty(file.getRef())) {
            id = file.getRef();
        }

        realPath.append(java.io.File.separator).append(id);
        return realPath;
    }

    private static StringBuffer createRealPath(StringBuffer realPath, FileAttributeDTO fileAttributeDTO) throws IOException {
        realPath.append(java.io.File.separator).append(fileAttributeDTO.getCompanyId());
        realPath.append(java.io.File.separator).append(DateTimeUtils.format(fileAttributeDTO.getCreateDate(), Constants.DATE_DEFAULT));
        realPath.append(java.io.File.separator).append(fileAttributeDTO.getCreateBy());

        Path path = Paths.get(realPath.toString());
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        realPath.append(java.io.File.separator).append(fileAttributeDTO.getId());
        return realPath;
    }

    private static java.io.File createTempFile(FileAttributeDTO fileAttributeDTO, String prefix) throws IOException {
        String tmpdir = System.getProperty("java.io.tmpdir");
        if (ObjectUtils.isNotEmpty(prefix)) {
            tmpdir = StringUtils.concatCateNate(tmpdir, prefix);
        }
        StringBuffer realPath = createRealPath(new StringBuffer(tmpdir), fileAttributeDTO);
        return new java.io.File(realPath.toString());
    }

}
*/
