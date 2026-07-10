package com.vendo.product_service.adapter.product_image.out.persistence;

import com.vendo.product_service.domain.product_image.model.ImageStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document
public class ProductImageMongo {

    @Id
    private String id;

    @Indexed(unique = true)
    private String key;

    private String contentType;
    private long size;
    private ImageStatus status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

}
