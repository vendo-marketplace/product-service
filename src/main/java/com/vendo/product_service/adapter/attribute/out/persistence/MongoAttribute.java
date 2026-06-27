package com.vendo.product_service.adapter.attribute.out.persistence;

import com.vendo.product_service.domain.attribute.model.AttributeType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@Document
public class MongoAttribute {

    @Id
    private String id;

    @Indexed(unique = true)
    private String slug;

    private String title;
    private AttributeType type;
    private boolean required;
    private List<String> allowedValues;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

}
