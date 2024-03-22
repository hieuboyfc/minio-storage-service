package com.minio.storage.factory;

import com.minio.storage.request.VideoSettingRequest;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.File;

public class FFmpegBuilderFactory {

    // Các hằng số đại diện cho các định dạng file đầu vào
    public static final String FORMAT_MP4 = "mp4";
    public static final String FORMAT_MOV = "mov";
    public static final String FORMAT_AVI = "avi";
    public static final String FORMAT_MKV = "mkv";
    public static final String FORMAT_WEBM = "webm";
    public static final String FORMAT_FLV = "flv";
    public static final String FORMAT_WMV = "wmv";

    // Hằng số đại diện cho codec video và audio mặc định
    public static final String DEFAULT_VIDEO_CODEC = "libx264";
    public static final String DEFAULT_AUDIO_CODEC = "aac";

    public static FFmpegBuilder createFFmpegBuilder(File sourceFile, File outputFile, VideoSettingRequest request) {
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(sourceFile.getAbsolutePath())
                .addOutput(outputFile.getAbsolutePath())
                .setFormat(request.getFormatFile())
                .setVideoFrameRate(request.getFrameRate())
                .setVideoResolution(request.getWidth(), request.getHeight())
                .setAudioChannels(request.getAudioChannels())
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Thiết lập cấp độ nghiêm ngặt của FFmpeg
                .setVideoCodec(DEFAULT_VIDEO_CODEC)
                .setAudioCodec(DEFAULT_AUDIO_CODEC)
                .done();

        // Tùy chỉnh các thiết lập dựa trên định dạng file đầu vào
        switch (request.getFormatFile()) {
            case FORMAT_MP4:
                builder = customizeForMP4(builder, request);
                break;
            case FORMAT_MOV:
                builder = customizeForMOV(builder, request);
                break;
            case FORMAT_AVI:
                builder = customizeForAVI(builder, request);
                break;
            case FORMAT_MKV:
                builder = customizeForMKV(builder, request);
                break;
            case FORMAT_WEBM:
                builder = customizeForWebM(builder, request);
                break;
            case FORMAT_FLV:
                builder = customizeForFLV(builder, request);
                break;
            case FORMAT_WMV:
                builder = customizeForWMV(builder, request);
                break;
            default:
                break;
        }

        // Thêm chức năng crop video vào
        cropVideo(builder, request);

        return builder;
    }

    private static void cropVideo(FFmpegBuilder builder, VideoSettingRequest request) {
        int startX = request.getStartX();
        int startY = request.getStartY();
        int width = request.getWidth();
        int height = request.getHeight();

        if (Boolean.TRUE.equals(request.getCropVideo())) {
            String cropFilter = String.format("crop=%d:%d:%d:%d", width, height, startX, startY);
            builder.addExtraArgs("-vf", cropFilter);
        }

    }

    // Hàm tùy chỉnh cho định dạng MP4
    private static FFmpegBuilder customizeForMP4(FFmpegBuilder builder, VideoSettingRequest request) {
        return builder
                .addExtraArgs("-b:v", "1500k") // Video Bitrate
                .addExtraArgs("-b:a", "128k"); // Audio Bitrate
    }

    // Hàm tùy chỉnh cho định dạng MOV
    private static FFmpegBuilder customizeForMOV(FFmpegBuilder builder, VideoSettingRequest request) {
        return builder
                .addExtraArgs("-b:v", "2000k") // Video Bitrate
                .addExtraArgs("-b:a", "192k"); // Audio Bitrate
    }

    // Hàm tùy chỉnh cho định dạng AVI
    private static FFmpegBuilder customizeForAVI(FFmpegBuilder builder, VideoSettingRequest request) {
        return builder
                .addExtraArgs("-b:v", "1200k")
                .addExtraArgs("-b:a", "96k");
    }


    // Hàm tùy chỉnh cho định dạng MKV
    private static FFmpegBuilder customizeForMKV(FFmpegBuilder builder, VideoSettingRequest request) {
        return builder
                .addExtraArgs("-b:v", "1800k")
                .addExtraArgs("-b:a", "160k");
    }

    // Hàm tùy chỉnh cho định dạng WebM
    private static FFmpegBuilder customizeForWebM(FFmpegBuilder builder, VideoSettingRequest request) {
        return builder
                .addExtraArgs("-b:v", "1600k")
                .addExtraArgs("-b:a", "128k");
    }

    // Hàm tùy chỉnh cho định dạng FLV
    private static FFmpegBuilder customizeForFLV(FFmpegBuilder builder, VideoSettingRequest request) {
        return builder
                .addExtraArgs("-b:v", "1400k")
                .addExtraArgs("-b:a", "96k");
    }

    // Hàm tùy chỉnh cho định dạng WMV
    private static FFmpegBuilder customizeForWMV(FFmpegBuilder builder, VideoSettingRequest request) {
        return builder
                .addExtraArgs("-b:v", "1600k")
                .addExtraArgs("-b:a", "128k");
    }

}