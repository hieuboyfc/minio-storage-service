/*
package com.minio.storage.configuration.ffmpeg;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FFmpegExecutorConfig {

    @Bean
    public FFmpegExecutor ffmpegExecutor() throws IOException {
        // Tạo một đối tượng FFmpegExecutor
        FFmpeg ffmpeg = new FFmpeg("/usr/local/bin/ffmpeg");
        FFprobe ffprobe = new FFprobe("/usr/local/bin/ffprobe");

        return new FFmpegExecutor(ffmpeg, ffprobe);
    }

}*/
