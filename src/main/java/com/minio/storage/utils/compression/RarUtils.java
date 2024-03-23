package com.minio.storage.utils.compression;

import com.minio.storage.utils.enums.FileExtension;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Log4j2
public class RarUtils {

    public static byte[] compressToRar(byte[] fileData) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ArchiveOutputStream archiveOutputStream = new ArchiveStreamFactory()
                     .createArchiveOutputStream(FileExtension.RAR.getName(), outputStream)) {

            ZipArchiveEntry entry = new ZipArchiveEntry("");
            entry.setSize(fileData.length);
            archiveOutputStream.putArchiveEntry(entry);
            archiveOutputStream.write(fileData);
            archiveOutputStream.closeArchiveEntry();

            return outputStream.toByteArray();
        } catch (ArchiveException e) {
            throw new IOException("Failed to compress files to RAR: " + e.getMessage(), e);
        }
    }

    public byte[] compressToRar(List<File> files) throws IOException {
        try (java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
             ArchiveOutputStream archiveOutputStream = new ArchiveStreamFactory()
                     .createArchiveOutputStream("rar", outputStream)) {
            for (File file : files) {
                ArchiveEntry entry = archiveOutputStream.createArchiveEntry(file, file.getName());
                archiveOutputStream.putArchiveEntry(entry);
                Files.copy(file.toPath(), archiveOutputStream);
                archiveOutputStream.closeArchiveEntry();
            }
            return outputStream.toByteArray();
        } catch (ArchiveException e) {
            throw new IOException("Failed to compress files to RAR: " + e.getMessage(), e);
        }
    }

    public static byte[] compressToRar(List<File> files, long maxFileSize) throws IOException, ExecutionException, InterruptedException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ProcessBuilder builder = new ProcessBuilder(FileExtension.RAR.getName(), "a", "-m5", "-", "-");
        Process process = builder.start();

        OutputStream stdin = process.getOutputStream();
        InputStream stderr = process.getErrorStream();
        InputStream stdout = process.getInputStream();

        // Tạo CompletableFuture cho mỗi tệp nén
        var compressionTasks = new CompletableFuture[files.size()];
        for (int i = 0; i < files.size(); i++) {
            final int index = i;
            compressionTasks[i] = CompletableFuture.runAsync(() -> {
                try {
                    if (files.get(index).length() <= maxFileSize) {
                        addFileToRar(files.get(index), stdin);
                    } else {
                        splitAndAddFileToRar(files.get(index), maxFileSize, stdin);
                    }
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            });
        }

        // Chờ cho tất cả các nhiệm vụ nén hoàn thành
        CompletableFuture<Void> allCompressionTasks = CompletableFuture.allOf(compressionTasks);
        allCompressionTasks.get(); // Chờ cho tất cả các nhiệm vụ hoàn thành

        // Đóng quá trình RAR
        stdin.close();

        // Đọc đầu ra của quá trình rar
        byte[] buffer = new byte[1024];
        int length;
        while ((length = stdout.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }

        // Kiểm tra lỗi
        BufferedReader reader = new BufferedReader(new InputStreamReader(stderr));
        String line;
        while ((line = reader.readLine()) != null) {
            System.err.println(line);
        }

        return outputStream.toByteArray();
    }

    private static void addFileToRar(File file, OutputStream outputStream) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        }
    }

    private static void splitAndAddFileToRar(File file, long maxFileSize, OutputStream outputStream) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;
            int partNumber = 1;

            while ((bytesRead = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                if (totalBytesRead >= maxFileSize) {
                    // Đã đạt đến kích thước tệp tối đa, bắt đầu một phần mới
                    outputStream.flush(); // Đảm bảo tất cả dữ liệu được ghi vào luồng
                    outputStream.close(); // Tạm thời đóng luồng

                    // Mở một luồng đầu ra mới cho phần tiếp theo
                    outputStream = openNewPartOutputStream(file, partNumber);
                    partNumber++;

                    totalBytesRead = 0; // Đặt lại tổng số byte đã đọc cho phần mới
                }
            }
        }
    }

    private static OutputStream openNewPartOutputStream(File file, int partNumber) throws IOException {
        // Tạo một luồng đầu ra mới cho phần tiếp theo
        String partFileName = file.getName() + ".part" + partNumber;
        File partFile = new File(file.getParent(), partFileName);
        return new FileOutputStream(partFile);
    }

    public static List<File> splitFilesToRar(File file, long maxFileSize) throws IOException {
        List<File> parts = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(FileExtension.RAR.getName(), bis)) {

            ArchiveEntry entry;
            long bytesWritten = 0;
            int partNumber = 1;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ZipArchiveOutputStream aos = new ZipArchiveOutputStream(outputStream);

            while ((entry = ais.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }

                ZipArchiveEntry zipEntry = new ZipArchiveEntry(entry.getName());
                aos.putArchiveEntry(zipEntry);
                IOUtils.copy(ais, aos);
                aos.closeArchiveEntry();

                bytesWritten += entry.getSize();
                if (bytesWritten >= maxFileSize) {
                    File partFile = new File(file.getParent(), file.getName() + "_part" + partNumber + ".rar");
                    try (FileOutputStream fos = new FileOutputStream(partFile)) {
                        outputStream.writeTo(fos);
                    }
                    parts.add(partFile);
                    outputStream.reset();
                    aos.close();
                    outputStream = new ByteArrayOutputStream();
                    aos = new ZipArchiveOutputStream(outputStream);
                    partNumber++;
                    bytesWritten = 0;
                }
            }

            // Thêm phần còn lại
            if (bytesWritten > 0) {
                File partFile = new File(file.getParent(), file.getName() + "_part" + partNumber + ".rar");
                try (FileOutputStream fos = new FileOutputStream(partFile)) {
                    outputStream.writeTo(fos);
                }
                parts.add(partFile);
            }

            return parts;
        } catch (ArchiveException e) {
            throw new IOException("Failed to split files to RAR: " + e.getMessage(), e);
        }
    }

    public static List<byte[]> splitFilesToRar(byte[] fileData, long maxFileSize) throws IOException {
        List<byte[]> parts = new ArrayList<>();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(fileData);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(bis);
             ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(FileExtension.RAR.getName(), bufferedInputStream)) {

            ArchiveEntry entry;
            long bytesWritten = 0;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ZipArchiveOutputStream aos = new ZipArchiveOutputStream(outputStream);

            while ((entry = ais.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }

                ZipArchiveEntry zipEntry = new ZipArchiveEntry(entry.getName());
                aos.putArchiveEntry(zipEntry);
                IOUtils.copy(ais, aos);
                aos.closeArchiveEntry();

                bytesWritten += entry.getSize();
                if (bytesWritten >= maxFileSize) {
                    parts.add(outputStream.toByteArray());
                    outputStream.reset();
                    aos.close();
                    outputStream = new ByteArrayOutputStream();
                    aos = new ZipArchiveOutputStream(outputStream);
                    bytesWritten = 0;
                }
            }

            // Thêm phần còn lại
            if (bytesWritten > 0) {
                parts.add(outputStream.toByteArray());
            }

            return parts;
        } catch (ArchiveException e) {
            throw new IOException("Failed to split files to RAR: " + e.getMessage(), e);
        }
    }

    /*public static void main(String[] args) {
        File inputFile = new File("input_file.txt");
        long maxFileSize = 1024 * 1024; // 1 MB

        try {
            List<File> parts = splitFilesToRar(inputFile, maxFileSize);
            System.out.println("File has been split into " + parts.size() + " parts:");
            for (File part : parts) {
                System.out.println(part.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        // Example usage:
        // List<File> files = Arrays.asList(new File("file1.txt"), new File("file2.txt"), ...);
        // byte[] compressedData = compressToRar(files, 10 * 1024 * 1024); // Max file size 10MB
    }

}
