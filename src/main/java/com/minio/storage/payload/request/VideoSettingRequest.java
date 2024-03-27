package com.minio.storage.payload.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoSettingRequest {

    int startX;

    int startY;

    int width;

    int height;

    int frameRate;

    String formatFile;

    int audioChannels;

    Boolean cropVideo;

    long timeInSeconds;

}