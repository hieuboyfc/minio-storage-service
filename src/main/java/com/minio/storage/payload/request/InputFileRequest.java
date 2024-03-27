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
public class InputFileRequest {

    String inputFilePath;

    String outputFilePath;

    String formatName;

    String outputFormatFile;

    String folderName;

    String bucketName;

    String objectName;

    Boolean isCompressFile;

    String typeCompressFile;

}