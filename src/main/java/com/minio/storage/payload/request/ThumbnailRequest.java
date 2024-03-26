package com.minio.storage.payload.request;

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
public class ThumbnailRequest extends InputFileRequest {

    private int thumbnailWidth;
    private int thumbnailHeight;

}