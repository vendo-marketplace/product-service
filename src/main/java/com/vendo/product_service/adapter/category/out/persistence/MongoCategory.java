package com.vendo.product_service.adapter.category.out.persistence;

import com.vendo.product_service.domain.category.model.AttributeDefinition;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@Document
public class MongoCategory {

    @Id
    private String id;

    private String title;

    @Indexed(unique = true)
    private String code;

    private String parentId;

    private Map<String, AttributeDefinition> attributes;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
