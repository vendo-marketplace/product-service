package com.vendo.product_service.application.category.model;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.category.model.Category;

import java.util.List;
import java.util.Map;

public record CategoryView(
        String id,
        String title,
        String code,
        List<Attribute> attributes,
        List<String> path
) {

    public static CategoryView from(Category category, Map<String, Attribute> attributesById) {
        List<Attribute> attributes = category.getAttributes().stream()
                .map(attributesById::get)
                .toList();

        return new CategoryView(
                category.getId(),
                category.getTitle(),
                category.getCode(),
                attributes,
                category.getPath()
        );
    }
}