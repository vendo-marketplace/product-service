package com.vendo.product_service.adapter.product.out.persistence;

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
import java.util.Map;

@Data
@Builder
@Document
public class MongoProduct  {

    @Id
    private String id;
    private String title;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private String ownerId;
    private String categoryId;
    private Map<String, List<String>> attributes;
    private Boolean active;

    @Version
    private long version;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

}
