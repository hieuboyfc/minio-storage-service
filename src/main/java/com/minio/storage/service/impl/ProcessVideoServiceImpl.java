package com.minio.storage.service.impl;

import com.minio.storage.utils.factory.FFmpegBuilderFactory;
import com.minio.storage.payload.request.VideoSettingRequest;
import com.minio.storage.service.ProcessVideoService;
import com.minio.storage.utils.StringGeneratorUtils;
import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ProcessVideoServiceImpl implements ProcessVideoService {

    private final FFmpegExecutor ffmpegExecutor;

    public ProcessVideoServiceImpl() throws IOException {
        this.ffmpegExecutor = new FFmpegExecutor(new FFmpeg());
    }

    @Override
    public CompletableFuture<String> processVideo(MultipartFile file, VideoSettingRequest request) {
        return convertVideoFormat(file, request).thenCompose(videoPath -> createVideoThumbnail((String) videoPath, request.getTimeInSeconds()));
    }

    @Override
    public CompletableFuture<String> convertVideoFormat(MultipartFile file, VideoSettingRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Tạo file trung gian
                File sourceFile = File.createTempFile(StringGeneratorUtils.getRandomString(), ".tmp");

                // Lưu dữ liệu từ MultipartFile vào file trung gian
                file.transferTo(sourceFile);

                // Tạo file đích với định dạng mới và kích thước tự động
                File outputFile = File.createTempFile(StringGeneratorUtils.getRandomString(), "." + request.getFormatFile());

                // Xác định độ phân giải và tỷ lệ khung hình dựa trên kích thước video gốc
                request.setWidth(1280); // Độ rộng mặc định
                request.setHeight(720); // Độ cao mặc định
                request.setFrameRate(24); // Tốc độ khung hình mặc định

                // Xây dựng lệnh FFmpeg để chuyển đổi định dạng và thu nhỏ kích thước
                FFmpegBuilder builder = FFmpegBuilderFactory.createFFmpegBuilder(sourceFile, outputFile, request);

                // Thực hiện lệnh FFmpeg
                ffmpegExecutor.createJob(builder).run();

                // Trả về đường dẫn của file đích (video)
                return outputFile.getAbsolutePath();
            } catch (Exception e) {
                throw new RuntimeException("Failed to convert video format: " + e.getMessage());
            }
        });
    }

    @Override
    public CompletableFuture<String> createVideoThumbnail(String videoPath, long timeInSeconds) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Tạo thumbnail từ video
                File thumbFile = File.createTempFile(StringGeneratorUtils.getRandomString(), ".jpg");

                CompletableFuture<Double> videoDuration = getVideoDuration(videoPath);
                if (timeInSeconds > videoDuration.get()) {
                    throw new RuntimeException("The Thumbnail extraction time exceeds the duration of the Video.");
                }

                // Xây dựng lệnh FFmpeg để tạo thumbnail
                FFmpegBuilder thumbnailBuilder = new FFmpegBuilder()
                        .setInput(videoPath) // Đầu vào là video đã xử lý
                        .addOutput(thumbFile.getAbsolutePath()) // Đầu ra là thumbnail
                        .setFrames(1) // Chỉ cần lấy một khung hình
                        .setDuration(timeInSeconds, TimeUnit.SECONDS) // Thiết lập thời gian cụ thể trong video (đơn vị giây)
                        .done();

                // Thực hiện lệnh FFmpeg để tạo Thumbnail
                ffmpegExecutor.createJob(thumbnailBuilder).run();

                // Trả về đường dẫn của thumbnail
                return thumbFile.getAbsolutePath();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create video thumbnail: " + e.getMessage());
            }
        });
    }

    private CompletableFuture<Double> getVideoDuration(String videoPath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Tạo đối tượng FFprobe
                FFprobe ffprobe = new FFprobe();

                // Thực hiện lệnh FFprobe để lấy thông tin về video
                FFmpegProbeResult probeResult = ffprobe.probe(videoPath);

                // Lấy đối tượng FFmpegFormat từ kết quả FFprobe
                FFmpegFormat format = probeResult.getFormat();

                // Trả về thời lượng video
                return format.duration;
            } catch (IOException e) {
                throw new RuntimeException("Failed to get video duration: " + e.getMessage());
            }
        });
    }

}