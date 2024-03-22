package com.minio.storage.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class VideoSettingRequest {

    private int startX;

    private int startY;

    private int width;

    private int height;

    private int frameRate;

    private String formatFile;

    private int audioChannels = 2;

    private Boolean cropVideo;

    private long timeInSeconds;

}