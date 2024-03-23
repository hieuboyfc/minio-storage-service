package com.minio.storage.utils.compression;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;
import de.schlichtherle.truezip.file.TFileOutputStream;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class TarUtils {

    private static final int BUFFER_SIZE = 8192;

    public static byte[] compressToTar(byte[] fileData) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(outputStream)) {
            TarArchiveEntry entry = new TarArchiveEntry("file"); // Tên tệp tin có thể được thay đổi
            entry.setSize(fileData.length);
            tarArchiveOutputStream.putArchiveEntry(entry);
            tarArchiveOutputStream.write(fileData);
            tarArchiveOutputStream.closeArchiveEntry();
            tarArchiveOutputStream.finish();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Failed to compress files to TAR: " + e.getMessage(), e);
        }
    }

    public byte[] compressToTar(List<File> files) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             TFileOutputStream tFileOutputStream = new TFileOutputStream(new TFile(String.valueOf(outputStream)))) {
            for (File file : files) {
                TFile entry = new TFile(file);
                try (TFileInputStream tFileInputStream = new TFileInputStream(entry)) {
                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = tFileInputStream.read(buffer)) != -1) {
                        tFileOutputStream.write(buffer, 0, length);
                    }
                }
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Failed to compress files to TAR: " + e.getMessage(), e);
        }
    }

    public static byte[] compressToTar(List<File> files, long maxFileSize) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (TarArchiveOutputStream tarOutputStream = new TarArchiveOutputStream(new BufferedOutputStream(outputStream))) {
            int fileIndex = 1;
            CompletableFuture<?>[] futures = new CompletableFuture<?>[files.size()];
            for (int i = 0; i < files.size(); i++) {
                final int index = i;
                futures[i] = CompletableFuture.runAsync(() -> {
                    try {
                        compressFileToTar(files.get(index), tarOutputStream, maxFileSize, fileIndex);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                });
            }
            CompletableFuture.allOf(futures).join();
        }

        return outputStream.toByteArray();
    }

    private static void compressFileToTar(File file, TarArchiveOutputStream tarOutputStream, long maxFileSize, int fileIndex) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytesRead = 0;

            while ((bytesRead = fis.read(buffer)) != -1) {
                if (totalBytesRead + bytesRead > maxFileSize) {
                    synchronized (TarUtils.class) {
                        tarOutputStream.close();
                        totalBytesRead = 0;

                        // Mở luồng đầu ra mới cho phần tiếp theo
                        tarOutputStream = new TarArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(file.getName() + "_part" + fileIndex++)));
                    }
                }

                TarArchiveEntry entry = new TarArchiveEntry(file, file.getName() + "_part" + fileIndex);
                entry.setSize(bytesRead);
                tarOutputStream.putArchiveEntry(entry);
                tarOutputStream.write(buffer, 0, bytesRead);
                tarOutputStream.closeArchiveEntry();

                totalBytesRead += bytesRead;
            }
        }
    }

    public static List<File> splitFilesToTar(File file, long maxFileSize) throws IOException {
        List<File> parts = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            int fileIndex = 1;
            long totalBytesRead = 0;

            TarArchiveOutputStream tarOutputStream = null;
            TarArchiveEntry entry;

            while ((bytesRead = bis.read(buffer)) != -1) {
                if (totalBytesRead + bytesRead > maxFileSize || tarOutputStream == null) {
                    if (tarOutputStream != null) {
                        tarOutputStream.close();
                    }
                    totalBytesRead = 0;

                    // Mở các luồng đầu ra mới cho phần tiếp theo
                    File partFile = new File(file.getParent(), file.getName() + "_part" + fileIndex + ".tar");
                    parts.add(partFile);
                    tarOutputStream = new TarArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(partFile)));
                    fileIndex++;
                }

                entry = new TarArchiveEntry(file, file.getName() + "_part" + (fileIndex - 1));
                entry.setSize(bytesRead);
                tarOutputStream.putArchiveEntry(entry);
                tarOutputStream.write(buffer, 0, bytesRead);
                tarOutputStream.closeArchiveEntry();

                totalBytesRead += bytesRead;
            }

            if (tarOutputStream != null) {
                tarOutputStream.close();
            }
        }
        return parts;
    }

    public static List<byte[]> splitFilesToTar(byte[] file, long maxFileSize) throws IOException {
        List<byte[]> parts = new ArrayList<>();

        try (ByteArrayInputStream bis = new ByteArrayInputStream(file);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(bis)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            int fileIndex = 1;
            long totalBytesRead = 0;

            ByteArrayOutputStream outputStream = null;
            TarArchiveOutputStream tarOutputStream = null;
            TarArchiveEntry entry;

            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                if (totalBytesRead + bytesRead > maxFileSize || tarOutputStream == null) {
                    if (tarOutputStream != null) {
                        tarOutputStream.close();
                        parts.add(outputStream.toByteArray());
                    }
                    totalBytesRead = 0;

                    // Mở các luồng đầu ra mới cho phần tiếp theo
                    outputStream = new ByteArrayOutputStream();
                    tarOutputStream = new TarArchiveOutputStream(outputStream);
                    fileIndex++;
                }

                entry = new TarArchiveEntry("part" + (fileIndex - 1));
                entry.setSize(bytesRead);
                tarOutputStream.putArchiveEntry(entry);
                tarOutputStream.write(buffer, 0, bytesRead);
                tarOutputStream.closeArchiveEntry();

                totalBytesRead += bytesRead;
            }

            if (tarOutputStream != null) {
                tarOutputStream.close();
                parts.add(outputStream.toByteArray());
            }
        }
        return parts;
    }

    /*public static void main(String[] args) {
        File inputFile = new File("input_file.txt");
        long maxFileSize = 1024 * 1024; // 1 MB

        try {
            List<File> parts = splitFilesToTar(inputFile, maxFileSize);
            System.out.println("File has been split into " + parts.size() + " parts:");
            for (File part : parts) {
                System.out.println(part.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public static void main(String[] args) throws IOException {
        // Example usage:
        // List<File> files = Arrays.asList(new File("file1.txt"), new File("file2.txt"), ...);
        // byte[] compressedData = compressToTar(files, 10 * 1024 * 1024); // Max file size 10MB
    }

}
