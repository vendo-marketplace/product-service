package com.vendo.product_service.adapter.product.out.persistence;

import com.vendo.product_service.domain.attribute.model.AttributeValue;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@Document
public class MongoProduct {

    @Id
    private String id;
    private String title;
    private String description;
    private Integer quantity;
    private Boolean isNew;
    private BigDecimal price;
    private String ownerId;
    private String categoryId;
    private List<AttributeValue> attributes;
    private List<String> imageKeys;
    private Boolean active;

    @Version
    private long version;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

}
