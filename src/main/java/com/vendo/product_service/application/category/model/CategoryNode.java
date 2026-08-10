package com.vendo.product_service.application.category.model;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.model.ImageBody;
import com.vendo.product_service.domain.category.type.CategoryType;

import java.util.List;

public record CategoryNode(
        String id,
        String title,
        String slug,
        CategoryType type,
        ImageBody image,

        List<Attribute> attributes,
        List<String> path,
        List<CategoryNode> children
) {

    public static CategoryNode from(Category category, List<Attribute> attributes, List<CategoryNode> children) {
        return new CategoryNode(
                category.getId(),
                category.getTitle(),
                category.getSlug(),
                category.getType(),
                category.getImage(),
                attributes,
                category.getPath(),
                children
        );
    }
}