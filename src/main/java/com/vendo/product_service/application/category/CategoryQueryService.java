package com.vendo.product_service.application.category;

import com.vendo.product_service.application.category.model.CategoryNode;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.port.category.usecase.CategoryQueryUseCase;
import com.vendo.product_service.port.attribute.AttributeQueryPort;
import com.vendo.product_service.port.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class CategoryQueryService implements CategoryQueryUseCase {

    private final CategoryQueryPort categoryQueryPort;
    private final AttributeQueryPort attributeQueryPort;

    @Override
    public Category findById(String id) {
        return categoryQueryPort.findById(id, "Category not found.");
    }

    @Override
    @Cacheable("category-tree")
    public List<CategoryNode> getTree() {
        List<Category> categories = categoryQueryPort.findAll();
        Map<String, Attribute> attributesById = loadAttributesById(categories);
        return buildTree(categories, attributesById);
    }

    private Map<String, Attribute> loadAttributesById(List<Category> categories) {
        return attributeQueryPort.findAllByIds(Category.extractAttributes(categories)).stream()
                .collect(Collectors.toMap(Attribute::id, Function.identity()));
    }

    private List<CategoryNode> buildTree(List<Category> categories, Map<String, Attribute> attributesById) {
        List<Category> parents = Category.filterByType(categories, CategoryType.PARENT);
        Map<String, List<Category>> childrenByParentId = Category.groupByParentId(categories);
        return parents.stream().map(parent -> buildBranch(parent, attributesById, childrenByParentId)).toList();
    }

    private CategoryNode buildBranch(
            Category parent,
            Map<String, Attribute> attributesById,
            Map<String, List<Category>> childrenByParentId
    ) {
        List<Category> children = childrenByParentId.getOrDefault(parent.getId(), List.of());

        List<CategoryNode> childrenNodes = children.stream()
                .map(category -> buildBranch(category, attributesById, childrenByParentId))
                .toList();

        return CategoryNode.from(parent, Attribute.extractAll(parent.getAttributes(), attributesById), childrenNodes);
    }

}