package com.minio.storage.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentDTO {

    @Valid
    @NotBlank(message = "attachment.application_is_not_blank")
    private String application;

    private String description;

    private String name;

    private String refName;

    private Long companyId;

    private String requester;

    @Valid
    @NotBlank(message = "attachment.ref_type_is_not_blank")
    private String refType;

    @Valid
    @NotBlank(message = "attachment.ref_id_is_not_blank")
    private String refId;

    private String businessType;

    private Map<String, Object> attributes;

    private String versionNo;

    private String templateId;

}
