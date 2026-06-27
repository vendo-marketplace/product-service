package com.vendo.product_service.adapter.category.out.persistence;

import lombok.*;
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
public class MongoCategory {

    @Id
    private String id;

    @Indexed(unique = true)
    private String slug;

    private String title;
    private String parentId;
    private List<String> attributes;
    private List<String> path;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
