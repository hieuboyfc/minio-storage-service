package com.minio.storage.utils.file;

import cn.hutool.core.io.FileTypeUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileTypeUtils {

    static final Logger LOGGER = LoggerFactory.getLogger(FileTypeUtils.class);

    static final String IMAGE_TYPE = "image/";
    static final String AUDIO_TYPE = "audio/";
    static final String VIDEO_TYPE = "video/";
    static final String APPLICATION_TYPE = "application/";
    static final String TXT_TYPE = "txt/";

    static final Map<String, String> fileTypes = new HashMap<>();

    static {
        // Image types
        fileTypes.put("jpg", IMAGE_TYPE);
        fileTypes.put("jpeg", IMAGE_TYPE);
        fileTypes.put("gif", IMAGE_TYPE);
        fileTypes.put("png", IMAGE_TYPE);
        fileTypes.put("bmp", IMAGE_TYPE);
        fileTypes.put("pcx", IMAGE_TYPE);
        fileTypes.put("tga", IMAGE_TYPE);
        fileTypes.put("psd", IMAGE_TYPE);
        fileTypes.put("tiff", IMAGE_TYPE);

        // Audio types
        fileTypes.put("mp3", AUDIO_TYPE);
        fileTypes.put("ogg", AUDIO_TYPE);
        fileTypes.put("wav", AUDIO_TYPE);
        fileTypes.put("real", AUDIO_TYPE);
        fileTypes.put("ape", AUDIO_TYPE);
        fileTypes.put("module", AUDIO_TYPE);
        fileTypes.put("midi", AUDIO_TYPE);
        fileTypes.put("vqf", AUDIO_TYPE);
        fileTypes.put("cd", AUDIO_TYPE);

        // Video types
        fileTypes.put("mp4", VIDEO_TYPE);
        fileTypes.put("avi", VIDEO_TYPE);
        fileTypes.put("mpeg-1", VIDEO_TYPE);
        fileTypes.put("rm", VIDEO_TYPE);
        fileTypes.put("asf", VIDEO_TYPE);
        fileTypes.put("wmv", VIDEO_TYPE);
        fileTypes.put("qlv", VIDEO_TYPE);
        fileTypes.put("mpeg-2", VIDEO_TYPE);
        fileTypes.put("mpeg4", VIDEO_TYPE);
        fileTypes.put("mov", VIDEO_TYPE);
        fileTypes.put("3gp", VIDEO_TYPE);

        // Application types
        fileTypes.put("doc", APPLICATION_TYPE);
        fileTypes.put("docx", APPLICATION_TYPE);
        fileTypes.put("ppt", APPLICATION_TYPE);
        fileTypes.put("pptx", APPLICATION_TYPE);
        fileTypes.put("xls", APPLICATION_TYPE);
        fileTypes.put("xlsx", APPLICATION_TYPE);
        fileTypes.put("zip", APPLICATION_TYPE);
        fileTypes.put("jar", APPLICATION_TYPE);

        // Text types
        fileTypes.put("txt", TXT_TYPE);
    }

    public static String getFileType(MultipartFile multipartFile) {
        try {
            InputStream inputStream = multipartFile.getInputStream();
            String type = FileTypeUtil.getType(inputStream);
            LOGGER.info("FileTypeUtils | getFileType | type : " + type);

            String fileTypePrefix = fileTypes.get(type.toLowerCase());
            if (fileTypePrefix != null) {
                LOGGER.info("---> FileTypeUtils | getFileType | " + fileTypePrefix + type);
                return fileTypePrefix + type;
            }
        } catch (IOException e) {
            LOGGER.info("FileTypeUtils | getFileType | IOException : " + e.getMessage());
        }
        return null;
    }

}
