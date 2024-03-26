package com.minio.storage.utils.file;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public final static String TEMP_DIR = File.separator + "tmp" + File.separator;

    public static byte[] combineFiles(List<byte[]> filesData) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            for (int i = 0; i < filesData.size(); i++) {
                ZipEntry zipEntry = new ZipEntry("file" + i + ".bin");
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(filesData.get(i));
                zipOutputStream.closeEntry();
            }
        }
        return outputStream.toByteArray();
    }

    public static File convertBytesToFile(byte[] fileData) throws IOException {
        File tempFile = File.createTempFile("temp", null);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(fileData);
        }
        return tempFile;
    }

    public static List<File> convertBytesToFiles(List<byte[]> filesData) throws IOException {
        List<File> fileList = new ArrayList<>();

        for (int i = 0; i < filesData.size(); i++) {
            byte[] fileData = filesData.get(i);
            File tempFile = File.createTempFile("temp" + i, null);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(fileData);
            }
            fileList.add(tempFile);
        }

        return fileList;
    }

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
