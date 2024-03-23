package com.minio.storage.service.impl;

import com.minio.storage.request.InputFileRequest;
import com.minio.storage.request.ResizeRequest;
import com.minio.storage.request.ThumbnailRequest;
import com.minio.storage.service.MinioUploadService;
import com.minio.storage.service.ProcessImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ProcessImageServiceImpl implements ProcessImageService {

    private final MinioUploadService minioUploadService;

    @Override
    public CompletableFuture<Void> convertImageFormatAsync(MultipartFile file, InputFileRequest request) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Đọc hình ảnh gốc
                BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));

                // Ghi hình ảnh convert ra file
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(inputImage, request.getOutputFilePath(), outputStream);

                // Upload file
                request.setFolderName("convert");
                minioUploadService
                        .uploadFileToMinIOAsync(new ByteArrayInputStream(outputStream.toByteArray()), request)
                        .join();
            } catch (IOException e) {
                throw new RuntimeException("Failed to convert image format: " + e.getMessage());
            }
        });
    }

    @Override
    public CompletableFuture<Void> resizeImageAsync(MultipartFile file, ResizeRequest request) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Đọc hình ảnh gốc
                BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));

                Image resultingImage = inputImage.getScaledInstance(
                        request.getScaledWidth(), request.getScaledHeight(), Image.SCALE_SMOOTH
                );

                BufferedImage outputImage = new BufferedImage(
                        request.getScaledWidth(), request.getScaledHeight(), BufferedImage.TYPE_INT_RGB
                );

                outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);

                // Ghi hình ảnh resize ra file
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(outputImage, request.getOutputFilePath(), outputStream);

                // Upload file
                request.setFolderName("resize");
                minioUploadService
                        .uploadFileToMinIOAsync(new ByteArrayInputStream(outputStream.toByteArray()), request)
                        .join();
            } catch (IOException e) {
                throw new RuntimeException("Failed to resize image: " + e.getMessage());
            }
        });
    }

    @Override
    public CompletableFuture<Void> createThumbnailAsync(MultipartFile file, ThumbnailRequest request) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Đọc hình ảnh gốc
                BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));

                // Tạo hình ảnh Thumbnail mới
                BufferedImage outputImage = new BufferedImage(
                        request.getThumbnailWidth(), request.getThumbnailHeight(), BufferedImage.TYPE_INT_RGB
                );
                Graphics2D g2d = outputImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(inputImage.getScaledInstance(
                        request.getThumbnailWidth(), request.getThumbnailHeight(), Image.SCALE_SMOOTH
                ), 0, 0, request.getThumbnailWidth(), request.getThumbnailHeight(), null);
                g2d.dispose();

                // Ghi hình ảnh thumbnail ra file
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(outputImage, request.getOutputFilePath(), outputStream);

                // Upload file
                request.setFolderName("thumbnail");
                minioUploadService
                        .uploadFileToMinIOAsync(new ByteArrayInputStream(outputStream.toByteArray()), request)
                        .join();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create thumbnail: " + e.getMessage());
            }
        });
    }

}