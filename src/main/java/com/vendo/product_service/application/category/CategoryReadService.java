package com.vendo.product_service.application.category;

import com.vendo.product_service.application.category.model.CategoryNode;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.port.out.category.CategoryCachePort;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryReadService {

    private final CategoryQueryPort categoryQueryPort;
    private final CategoryCachePort categoryCachePort;

    public List<CategoryNode> getTree() {
        Optional<List<CategoryNode>> cached = categoryCachePort.getTree();
        if (cached.isPresent()) return cached.get();

        List<CategoryNode> tree = buildTree();
        categoryCachePort.saveTree(tree);
        return tree;
    }

    public List<CategoryNode> getChildren(String parentId) {
        return categoryQueryPort.findByParentId(parentId)
                .stream()
                .map(this::toNode)
                .toList();
    }

    public List<CategoryNode> getBreadcrumbs(String id) {

        Category current = categoryQueryPort.findById(id, "Category not found");

        Map<String, Category> byId = categoryQueryPort.findAllByIds(current.getPath()).stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        return current.getPath().stream()
                .map(byId::get)
                .map(this::toNode)
                .toList();
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

    private List<CategoryNode> buildTree() {
        List<Category> all = categoryQueryPort.findAll();
        Map<String, CategoryNode> nodeMap = new HashMap<>();
        all.forEach(c -> nodeMap.put(c.getId(), toNode(c)));
        linkChildren(all, nodeMap);
        return extractRoots(all, nodeMap);
    }

    private void linkChildren(List<Category> categories, Map<String, CategoryNode> nodeMap) {
        categories.stream()
                .filter(c -> c.getParentId() != null)
                .forEach(c -> nodeMap.get(c.getParentId()).getChildren().add(nodeMap.get(c.getId())));
    }

    private List<CategoryNode> extractRoots(List<Category> categories, Map<String, CategoryNode> nodeMap) {
        return categories.stream()
                .filter(c -> c.getParentId() == null)
                .map(c -> nodeMap.get(c.getId()))
                .toList();
    }
}