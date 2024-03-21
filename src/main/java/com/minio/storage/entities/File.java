package com.minio.storage.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minio.storage.dto.FileAttributeDTO;
import com.minio.storage.utils.MapperUtils;
import com.minio.storage.utils.constant.Constants;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "file", indexes = {
        @Index(name = "hash_file_idx", columnList = "hash"),
        @Index(name = "folder_file_idx", columnList = "folder")
})
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class File extends XPersistableEntity {

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

    public static File of(Long cid, String uid, String folder, String provider,
                          Object dto, String type, String publishType) {
        File file = MapperUtils.map(dto, File.class);
        file.setCompanyId(cid);
        file.setFolder(folder);
        file.setProvider(provider);
        file.setCreateBy(uid);
        file.setUpdateBy(uid);
        file.setLabel(file.getName());
        file.setStatus(Constants.STATUS_ACTIVE);
        file.setType(type);
        file.setPublishType(publishType);
        file.setUserId(uid);
        return file;
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
