package com.minio.storage.utils.compression;

import lombok.extern.log4j.Log4j2;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Log4j2
public class ZipUtils {

    private static final int BUFFER_SIZE = 1024;

    public static byte[] compressToZip(byte[] fileData) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            ZipEntry entry = new ZipEntry("file"); // Tên tệp tin trong ZIP, có thể thay đổi
            zipOutputStream.putNextEntry(entry);
            zipOutputStream.write(fileData);
            zipOutputStream.closeEntry();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Failed to compress files to ZIP: " + e.getMessage(), e);
        }
    }

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
            // Nếu kích thước tệp vượt quá kích thước tối đa, chia tệp thành các phần
            long numParts = (fileSize + maxFileSize - 1) / maxFileSize; // Phép chia lên
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

    public static List<File> splitFilesToZip(File file, long maxFileSize) {
        List<File> zipParts = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            long fileSize = file.length();

            if (fileSize <= maxFileSize) {
                // Nếu kích thước của tệp nhỏ hơn hoặc bằng kích thước tối đa, thêm toàn bộ tệp vào danh sách
                zipParts.add(file);
            } else {
                // Nếu kích thước của tệp vượt quá kích thước tối đa, chia tệp thành các phần
                byte[] buffer = new byte[BUFFER_SIZE];
                int partIndex = 1;
                long bytesWritten = 0;

                while (true) {
                    File partFile = new File(file.getParent(), file.getName() + ".part" + partIndex);
                    try (FileOutputStream fos = new FileOutputStream(partFile);
                         BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                        int bytesRead = bis.read(buffer);
                        if (bytesRead == -1) {
                            // Nếu không còn dữ liệu nào để đọc nữa, thoát khỏi vòng lặp
                            break;
                        }
                        bos.write(buffer, 0, bytesRead);
                        zipParts.add(partFile);
                        bytesWritten += bytesRead;

                        if (bytesWritten >= maxFileSize) {
                            partIndex++;
                            bytesWritten = 0;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return zipParts;
    }

    public static List<byte[]> splitFilesToZip(byte[] file, long maxFileSize) {
        List<byte[]> zipParts = new ArrayList<>();

        try (ByteArrayInputStream bis = new ByteArrayInputStream(file);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(bis)) {

            long dataSize = file.length;

            if (dataSize <= maxFileSize) {
                // Nếu kích thước của dữ liệu nhỏ hơn hoặc bằng kích thước tối đa, thêm toàn bộ dữ liệu vào danh sách
                zipParts.add(file.clone());
            } else {
                // Nếu kích thước của dữ liệu vượt quá kích thước tối đa, chia dữ liệu thành các phần
                byte[] buffer = new byte[BUFFER_SIZE];
                long bytesWritten = 0;

                int bytesRead;
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        bytesWritten += bytesRead;

                        if (bytesWritten >= maxFileSize) {
                            zipParts.add(outputStream.toByteArray());
                            outputStream.reset();
                            bytesWritten = 0;
                        }
                    }

                    // Thêm phần cuối cùng nếu còn dữ liệu
                    if (outputStream.size() > 0) {
                        zipParts.add(outputStream.toByteArray());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return zipParts;
    }

    /*public static void main(String[] args) {
        // Example usage
        try {
            // Splitting a byte array
            byte[] data = "This is a test string to split into ZIP parts".getBytes();
            long maxFileSize = 20; // Example max file size
            List<byte[]> zipPartsByteArray = splitFilesToZip(data, maxFileSize);
            for (int i = 0; i < zipPartsByteArray.size(); i++) {
                byte[] part = zipPartsByteArray.get(i);
                // Do something with each part (e.g., save to disk, send over network, etc.)
                System.out.println("Byte array part " + (i + 1) + ": " + new String(part));
            }

            // Splitting a file
            File inputFile = new File("input.txt"); // Example input file
            long maxFileSize2 = 50; // Example max file size
            List<File> zipPartsFiles = splitFilesToZip(inputFile, maxFileSize2);
            for (int i = 0; i < zipPartsFiles.size(); i++) {
                File partFile = zipPartsFiles.get(i);
                // Do something with each part file (e.g., save to disk, send over network, etc.)
                System.out.println("File part " + (i + 1) + ": " + partFile.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static void main(String[] args) throws IOException {
        // Example usage:
        // List<File> files = Arrays.asList(new File("file1.txt"), new File("file2.txt"), ...);
        // byte[] compressedData = compressToZip(files, 10 * 1024 * 1024); // Max file size 10MB
    }

}
