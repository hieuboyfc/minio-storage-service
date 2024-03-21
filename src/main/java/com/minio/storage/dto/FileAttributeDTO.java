package com.minio.storage.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minio.storage.utils.*;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class FileAttributeDTO {

    private String id;

    private String name;

    private String contentType;

    private Long length;

    @JsonIgnore
    private Long companyId;

    private String createBy;

    @JsonIgnore
    private String hash;

    private Date createDate;

    @JsonIgnore
    private String folder;

    private Long version;

    @JsonIgnore
    private byte[] content;

    @JsonIgnore
    private InputStream inputStream;

    @JsonIgnore
    private OutputStream outputStream;

    private String accessType;

    private Integer status;

    private String bucket;

    private String uploadState;

    private Date modifiedDate;

    private String updateBy;

    private String accessType2;

    private String ref;

    public static FileAttributeDTO createAttribute(AttachmentDTO attachmentDTO, MultipartFile file) throws IOException, NoSuchAlgorithmException {
        return createAttribute(attachmentDTO.getCompanyId(), attachmentDTO.getRequester(), file);
    }

    public static FileAttributeDTO createAttribute(Long companyId, String createBy, byte[] content) throws NoSuchAlgorithmException, IOException {
        FileAttributeDTOBuilder builder = FileAttributeDTO.createAttribute(companyId, createBy);
        return createAttribute(builder, content).build();
    }

    public static FileAttributeDTOBuilder createAttribute(FileAttributeDTOBuilder builder, byte[] content) throws NoSuchAlgorithmException, IOException {
        String hash = FileChecksumUtils.getByteChecksum(content);
        builder.hash(hash);
        builder.content(content);
        builder.contentType(FileUtils.getContentType(content));
        builder.length((long) content.length);
        return builder;
    }

    public static FileAttributeDTOBuilder createAttribute(Long companyId, String createBy) {
        FileAttributeDTOBuilder builder = FileAttributeDTO.builder();
        builder.companyId(companyId);
        builder.createBy(createBy);
        builder.id(StringGeneratorUtils.getUUID());
        builder.companyId(companyId);
        builder.createDate(new Date());
        return builder;
    }

    public static FileAttributeDTO createAttribute(Long companyId, String createBy, MultipartFile file) throws IOException, NoSuchAlgorithmException {
        FileAttributeDTOBuilder builder = createAttribute(companyId, createBy);
        builder.content(file.getBytes());
        String hash = FileChecksumUtils.getByteChecksum(builder.content);
        builder.hash(hash);
        builder.name(getLastName(file.getOriginalFilename()));
        builder.contentType(file.getContentType());
        builder.length(file.getSize());
        return builder.build();
    }

    private static String getLastName(String fullPathName) {
        if (StringUtils.isEmpty(fullPathName)) {
            return fullPathName;
        }
        return StringUtils.substringAfterLast(fullPathName, "/");
    }

    public void writeTo(File file) throws IOException {
        if (content != null && content.length > 0) {
            writeToFile(content, file);
        } else {
            if (inputStream != null) {
                org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream, file);
            }
        }
    }

    @JsonIgnore
    public InputStream getStream() {
        try {
            return getInputStream();
        } catch (Exception e) {
            log.error("Error getting InputStream: ", e);
        }
        return new ByteArrayInputStream(new byte[0]);
    }

    public InputStream getInputStream() {
        try {
            if (inputStream == null) {
                String path = "test";
                File file = new File(path);
                if (file.length() > 0) {
                    this.length = file.length();
                    return new FileInputStream(file);
                } else {
                    return new ByteArrayInputStream(new byte[0]);
                }
            } else {
                return inputStream;
            }
        } catch (Exception ex) {
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setInputStream(Long length, InputStream inputStream) {
        this.length = length;
        this.inputStream = inputStream;
    }


    public InputStream read(File file) throws IOException {
        if (file.length() > 0) {
            FileInputStream inputStream = new FileInputStream(file);
            this.length = file.length();
            this.content = IOUtils.toByteArray(inputStream);
            this.inputStream = inputStream;
            inputStream.close();
            return inputStream;
        } else {
            log.error("file-not-found: " + file.getAbsolutePath());
            throw new IOException("file-not-found");
        }
    }


    private void writeToFile(byte[] data, File target) throws IOException {
        try (ReadableByteChannel src = Channels.newChannel(new ByteArrayInputStream(data));
             FileOutputStream fo = new FileOutputStream(target);
             FileChannel out = fo.getChannel()) {
            out.transferFrom(src, 0, data.length);
        }
    }

}
