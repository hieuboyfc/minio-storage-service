package com.minio.storage.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Entity
@Table(name = "bucket_config")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BucketConfig extends XPersistableEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(length = 50)
    private String id;

    private String bucketName;

    private String region;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ssZ",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ssZ",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date endDate;

    private String providerCode;

    private Long priority;

    private String quota;

}
