package com.minio.storage.utils;

import org.apache.coyote.BadRequestException;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public final static String PDF_EXTENSION = "pdf";
    public final static String DOCX_EXTENSION = "docx";
    public final static String DOC_EXTENSION = "doc";
    public final static String TEMP_DIR = File.separator + "tmp" + File.separator;

    public static String getExtension(byte[] content) throws IOException {
        try {
            TikaConfig tikaConfig = new TikaConfig();
            MimeType mimeType = tikaConfig.getMimeRepository().forName(getContentType(tikaConfig, content));
            return mimeType.getExtension().split("\\.")[1];
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public static String getContentType(byte[] content) throws IOException {
        try {
            return getContentType(new TikaConfig(), content);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public static String getContentType(TikaConfig tikaConfig, byte[] content) throws IOException {
        try {
            Detector detector = tikaConfig.getDetector();
            TikaInputStream stream = TikaInputStream.get(content);
            Metadata metadata = new Metadata();
            MediaType mediaType = detector.detect(stream, metadata);
            return mediaType.toString();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

}
