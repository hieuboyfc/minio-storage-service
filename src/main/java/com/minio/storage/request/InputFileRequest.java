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
public class InputFileRequest {

    private String inputFilePath;

    private String outputFilePath;

    private String formatName;

    private String outputFormatFile;

    private String folderName;

    private String bucketName;

}