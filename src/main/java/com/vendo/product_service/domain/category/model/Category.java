package com.vendo.product_service.domain.category.model;

import com.vendo.product_service.adapter.model.category.embedded.AttributeDefinition;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Data
@Getter
@Builder
public class Category {
    private String id;
    private String title;
    private String code;
    private String parentId;
    private Map<String, AttributeDefinition> attributes;
}
