package com.vendo.product_service.domain.category.model;

import com.vendo.product_service.domain.category.model.embedded.AttributeValue;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder
@Document
public class Category {

    @Id
    private String id;

    @Indexed(unique = true)
    private String title;

    private String parentId;

    private Map<String, AttributeValue> attributes;

}
