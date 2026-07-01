package com.vendo.product_service.adapter.category.in.dto;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.category.type.CategoryType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryTreeResponse {

    private List<CategoryTree> data;

    @Builder
    public record CategoryTree(
            String id,
            String title,
            String slug,
            CategoryType type,
            List<Attribute> attributes,
            List<String> path,
            List<CategoryTree> children
    ) {
    }
}