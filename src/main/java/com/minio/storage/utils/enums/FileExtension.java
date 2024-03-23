package com.minio.storage.utils.enums;

import lombok.Getter;

@Getter
public enum FileExtension {

    TXT("TXT"),
    PDF("PDF"),
    DOC("DOC"),
    DOCX("DOCX"),
    JPEG("JPEG"),
    JPG("JPG"),
    PNG("PNG"),
    RAR("RAR"),
    TAR("TAR"),
    ZIP("ZIP");
    // Thêm các phần mở rộng tệp khác nếu cần

    private final String name;

    FileExtension(String name) {
        this.name = name;
    }

}
