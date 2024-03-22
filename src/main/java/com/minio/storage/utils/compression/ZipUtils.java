package com.minio.storage.utils.compression;

import lombok.extern.log4j.Log4j2;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Log4j2
public class ZipUtils {

    public static byte[] compressToZip(List<File> files) throws IOException {
        try (java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream()) {
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(CompressionMethod.DEFLATE);
            parameters.setCompressionLevel(CompressionLevel.NORMAL);

            ZipFile zipFile = new ZipFile(String.valueOf(outputStream));
            for (File file : files) {
                zipFile.addFile(file, parameters);
            }

            return outputStream.toByteArray();
        } catch (ZipException e) {
            throw new IOException("Failed to compress files to ZIP: " + e.getMessage(), e);
        }
    }

    // Phương thức để nén các tệp vào một tệp ZIP với việc chia nhỏ động
    public static byte[] compressToZip(List<File> files, long maxFileSize) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
            List<CompletableFuture<Void>> compressionTasks = new ArrayList<>();

            for (File file : files) {
                compressionTasks.add(CompletableFuture.runAsync(() -> {
                    try {
                        compressFileToZip(file, zipOutputStream, maxFileSize);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                }));
            }

            CompletableFuture<Void> allCompressionTasks = CompletableFuture
                    .allOf(compressionTasks.toArray(new CompletableFuture[0]));
            allCompressionTasks.join();

            zipOutputStream.close();

            return outputStream.toByteArray();
        }
    }

    // Phương thức để nén một tệp đơn và ghi vào luồng đầu ra, chia nhỏ nếu cần thiết.
    private static void compressFileToZip(File file, ZipOutputStream zipOutputStream, long maxFileSize) throws IOException {
        long fileSize = file.length();

        if (fileSize <= maxFileSize) {
            // Nếu kích thước của tệp nhỏ hơn hoặc bằng kích thước tối đa, ta sẽ nén tệp như bình thường.
            ZipEntry entry = new ZipEntry(file.getName());
            zipOutputStream.putNextEntry(entry);
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, length);
                }
            }
            zipOutputStream.closeEntry();
        } else {
            // If file size exceeds the max size, split the file into parts
            long numParts = (fileSize + maxFileSize - 1) / maxFileSize; // Ceiling division
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int length;
                for (int i = 0; i < numParts; i++) {
                    ZipEntry entry = new ZipEntry(file.getName() + ".part" + (i + 1));
                    zipOutputStream.putNextEntry(entry);
                    long bytesWritten = 0;
                    while (bytesWritten < maxFileSize && (length = fis.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, length);
                        bytesWritten += length;
                    }
                    zipOutputStream.closeEntry();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // Example usage:
        // List<File> files = Arrays.asList(new File("file1.txt"), new File("file2.txt"), ...);
        // byte[] compressedData = compressToZip(files, 10 * 1024 * 1024); // Max file size 10MB
    }

}
