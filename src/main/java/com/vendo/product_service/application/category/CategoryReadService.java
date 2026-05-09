package com.vendo.product_service.application.category;

import com.vendo.product_service.application.category.model.CategoryView;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryReadService {

    private final CategoryQueryPort categoryQueryPort;

    @Cacheable("categoryTree")
    public List<CategoryView> getTree() {
        return categoryQueryPort.findAll().stream()
                .map(this::toNode)
                .toList();
    }

    private CategoryView toNode(Category c) {
        return CategoryView.builder()
                .id(c.getId())
                .title(c.getTitle())
                .code(c.getCode())
                .attributes(c.getAttributes())
                .path(c.getPath())
                .build();
    }
}