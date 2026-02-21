package com.vendo.product_service.adapter.model.category;

import com.vendo.product_service.adapter.model.category.embedded.AttributeDefinition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Document
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode()
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
