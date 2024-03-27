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
public class ThumbnailRequest extends InputFileRequest {

    int thumbnailWidth;

    int thumbnailHeight;

}