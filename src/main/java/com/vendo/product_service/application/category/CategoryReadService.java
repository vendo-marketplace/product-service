package com.vendo.product_service.application.category;

import com.vendo.product_service.application.category.model.CategoryView;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.out.attribute.AttributeQueryPort;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryReadService {

    private final CategoryQueryPort categoryQueryPort;
    private final AttributeQueryPort attributeQueryPort;

    @Cacheable("category-tree")
    public List<CategoryView> getTree() {
        List<Category> categories = categoryQueryPort.findAll();

        Map<String, Attribute> attributesById = loadAttributesById(categories);
        return categories.stream()
                .map(category -> toView(category, attributesById))
                .toList();
    }

    private Map<String, Attribute> loadAttributesById(List<Category> categories) {
        List<String> attributeIds = categories.stream()
                .flatMap(c -> getAttributeIds(c).stream())
                .distinct()
                .toList();

        return attributeQueryPort.findAllByIds(attributeIds).stream()
                .collect(Collectors.toMap(Attribute::id, Function.identity()));
    }

    private List<String> getAttributeIds(Category category) {
        return Optional.ofNullable(category.getAttributes()).orElse(List.of());
    }

    private CategoryView toView(Category category, Map<String, Attribute> attributesById) {
        List<Attribute> attributes = getAttributeIds(category).stream()
                .map(attributesById::get)
                .filter(Objects::nonNull)
                .toList();

        return CategoryView.builder()
                .id(category.getId())
                .title(category.getTitle())
                .code(category.getCode())
                .attributes(attributes)
                .path(category.getPath())
                .build();
    }
}