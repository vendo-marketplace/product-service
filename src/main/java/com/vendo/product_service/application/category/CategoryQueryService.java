package com.vendo.product_service.application.category;

import com.vendo.product_service.application.category.model.CategoryView;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.in.category.CategoryQueryUseCase;
import com.vendo.product_service.port.out.attribute.AttributeQueryPort;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryQueryService implements CategoryQueryUseCase {

    private final CategoryQueryPort categoryQueryPort;
    private final AttributeQueryPort attributeQueryPort;

    @Override
    public Category findById(String id) {
        return categoryQueryPort.findById(id, "Category not found.");
    }

    @Override
    @Cacheable("category-tree")
    public List<CategoryView> getTree() {
        List<Category> categories = categoryQueryPort.findAll();

        Map<String, Attribute> attributesById = loadAttributesById(categories);
        return categories.stream()
                .map(category -> CategoryView.from(category, attributesById))
                .toList();
    }

    private Map<String, Attribute> loadAttributesById(List<Category> categories) {
        return attributeQueryPort.findAllByIds(Category.extractAttributes(categories)).stream()
                .collect(Collectors.toMap(Attribute::id, Function.identity()));
    }
}