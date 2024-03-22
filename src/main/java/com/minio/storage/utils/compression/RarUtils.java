package com.minio.storage.utils.compression;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Log4j2
public class RarUtils {

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
        ProcessBuilder builder = new ProcessBuilder("rar", "a", "-m5", "-", "-");
        Process process = builder.start();

        OutputStream stdin = process.getOutputStream();
        InputStream stderr = process.getErrorStream();
        InputStream stdout = process.getInputStream();

        // Create CompletableFuture for each file compression
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

        // Wait for all compression tasks to complete
        CompletableFuture<Void> allCompressionTasks = CompletableFuture.allOf(compressionTasks);
        allCompressionTasks.get(); // Wait for all tasks to complete

        // Close the rar process
        stdin.close();

        // Read the output of the rar process
        byte[] buffer = new byte[1024];
        int length;
        while ((length = stdout.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }

        // Check for errors
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
                    // Reached the maximum file size, start a new part
                    outputStream.flush(); // Ensure all data is written to the stream
                    outputStream.close(); // Close the stream temporarily

                    // Open a new output stream for the next part
                    outputStream = openNewPartOutputStream(file, partNumber);
                    partNumber++;

                    totalBytesRead = 0; // Reset total bytes read for the new part
                }
            }
        }
    }

    private static OutputStream openNewPartOutputStream(File file, int partNumber) throws IOException {
        // Create a new output stream for the next part
        String partFileName = file.getName() + ".part" + partNumber;
        File partFile = new File(file.getParent(), partFileName);
        return new FileOutputStream(partFile);
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        // Example usage:
        // List<File> files = Arrays.asList(new File("file1.txt"), new File("file2.txt"), ...);
        // byte[] compressedData = compressToRar(files, 10 * 1024 * 1024); // Max file size 10MB
    }

}
