package com.minio.storage.utils.compression;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;
import de.schlichtherle.truezip.file.TFileOutputStream;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class TarUtils {

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

                        // Open new output streams for the next part
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

    public static void main(String[] args) throws IOException {
        // Example usage:
        // List<File> files = Arrays.asList(new File("file1.txt"), new File("file2.txt"), ...);
        // byte[] compressedData = compressToTar(files, 10 * 1024 * 1024); // Max file size 10MB
    }

}
