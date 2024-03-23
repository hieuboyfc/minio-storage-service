package com.minio.storage.utils.enums;

import lombok.Getter;

@Getter
public enum FileExtension {

    TXT("txt"),
    PDF("pdf"),
    DOC("doc"),
    DOCX("docx"),
    JPEG("jpeg"),
    JPG("jpg"),
    PNG("png"),
    RAR("rar"),
    TAR("tar"),
    ZIP("zip");
    // Thêm các phần mở rộng tệp khác nếu cần

    private final String name;

    FileExtension(String name) {
        this.name = name;
    }

}
