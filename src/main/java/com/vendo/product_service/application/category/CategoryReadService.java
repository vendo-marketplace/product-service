package com.vendo.product_service.application.category;

import com.vendo.product_service.application.category.model.CategoryNode;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryReadService {

    private final CategoryQueryPort categoryQueryPort;

    @Cacheable(value = "categoryTree", key = "'tree'")
    public List<CategoryNode> getTree() {

        List<Category> all = categoryQueryPort.findAll();

        Map<String, CategoryNode> map = new HashMap<>();
        List<CategoryNode> roots = new ArrayList<>();

        for (Category c : all) {
            map.put(c.getId(), toNode(c));
        }

        for (Category c : all) {
            CategoryNode node = map.get(c.getId());

            if (c.getParentId() == null) {
                roots.add(node);
            } else {
                CategoryNode parent = map.get(c.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }

        return roots;
    }

    public List<CategoryNode> getChildren(String parentId) {
        return categoryQueryPort.findByParentId(parentId)
                .stream()
                .map(this::toNode)
                .toList();
    }

    public List<CategoryNode> getBreadcrumbs(String id) {

        Map<String, Category> all = categoryQueryPort.findAll()
                .stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        List<CategoryNode> result = new ArrayList<>();

        Category current = all.get(id);

        if (current == null) {
            throw new CategoryNotFoundException("Category not found");
        }

        while (current != null) {
            result.add(0, toNode(current));

            current = current.getParentId() != null
                    ? all.get(current.getParentId())
                    : null;
        }

        return result;
    }

    public List<CategoryNode> getByType(CategoryType type) {
        return categoryQueryPort.findAll().stream()
                .filter(c -> c.getType() == type)
                .map(this::toNode)
                .toList();
    }

    private CategoryNode toNode(Category c) {
        return CategoryNode.builder()
                .id(c.getId())
                .title(c.getTitle())
                .code(c.getCode())
                .build();
    }
}