package com.minio.storage.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minio.storage.dto.FileAttributeDTO;
import com.minio.storage.utils.MapperUtils;
import com.minio.storage.utils.constant.Constants;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "file_info", indexes = {
        @Index(name = "hash_file_idx", columnList = "hash"),
        @Index(name = "folder_file_idx", columnList = "folder")
})
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FileInfo extends XPersistableEntity {

    @Id
    @Column(length = 50)
    private String id;

    @Column(length = 255)
    private String name;

    private String userId;

    @JsonIgnore
    private String ref;

    private Long length;

    private String hash;

    private String contentType;

    private String label;

    private String folder;

    private String provider;

    // owner,temp,share
    private String type;

    private String publishType;

    private String bucket;

    private String uploadState;

    private Boolean deleted = false;

    private String openkmId;

    public static FileInfo of(Long cid, String uid, String folder, String provider,
                              Object dto, String type, String publishType) {
        FileInfo fileInfo = MapperUtils.map(dto, FileInfo.class);
        fileInfo.setCompanyId(cid);
        fileInfo.setFolder(folder);
        fileInfo.setProvider(provider);
        fileInfo.setCreateBy(uid);
        fileInfo.setUpdateBy(uid);
        fileInfo.setLabel(fileInfo.getName());
        fileInfo.setStatus(Constants.STATUS_ACTIVE);
        fileInfo.setType(type);
        fileInfo.setPublishType(publishType);
        fileInfo.setUserId(uid);
        return fileInfo;
    }

    public FileAttributeDTO toAttribute() {
        return FileAttributeDTO.builder().name(getName())
                .id(getId())
                .createBy(getCreateBy())
                .contentType(getContentType())
                .length(getLength())
                .createDate(getCreateDate())
                .hash(getHash())
                .modifiedDate(getModifiedDate())
                .updateBy(getUpdateBy())
                .uploadState(getUploadState())
                .bucket(getBucket())
                .companyId(getCompanyId())
                .folder(getFolder())
                .uploadState(getUploadState())
                .build();
    }

}
