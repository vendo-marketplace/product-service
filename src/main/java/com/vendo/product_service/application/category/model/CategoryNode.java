package com.vendo.product_service.application.category.model;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.category.model.Category;

import java.util.List;

public record CategoryNode(
        String id,
        String title,
        String code,
        List<Attribute> attributes,
        List<String> path,
        List<CategoryNode> children
) {

    public static CategoryNode from(Category category, List<Attribute> attributes, List<CategoryNode> children) {
        return new CategoryNode(
                category.getId(),
                category.getTitle(),
                category.getCode(),
                attributes,
                category.getPath(),
                children
        );
    }
}