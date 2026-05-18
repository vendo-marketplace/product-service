package com.vendo.product_service.domain.category.model;

import com.vendo.product_service.domain.category.exception.CategoryTypeException;
import com.vendo.product_service.domain.category.exception.CategoryValidationException;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.utils_lib.StringUtils;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
public class Category {

    private String id;
    private String title;
    private String code;
    private String parentId;
    private List<String> attributes;
    private List<String> path;

    public static List<String> extractAttributes(List<Category> categories) {
        return categories.stream()
                .flatMap(category -> category.getAttributes().stream())
                .distinct()
                .toList();
    }

    public static List<Category> filterByType(List<Category> categories, CategoryType type) {
        return categories.stream()
                .filter(category -> category.getType() == type)
                .toList();
    }

    public static Map<String, List<Category>> groupByParentId(List<Category> categories) {
        return categories.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Category::getParentId));
    }

    public CategoryType getType() {
        if (isParent(parentId, attributes)) {
            return CategoryType.PARENT;
        }
        if (isSub(parentId, attributes)) {
            return CategoryType.SUB;
        }
        if (isChild(parentId, attributes)) {
            return CategoryType.CHILD;
        }

        throw new CategoryValidationException("Invalid category structure.");
    }

    public void throwIfNotDesiredType(CategoryType desiredType, String message) {
        if (getType() != desiredType) {
            throw new CategoryTypeException(message);
        }
    }

    public List<String> buildPath(List<String> parentPath) {
        if (id == null) throw new IllegalStateException("Id is empty.");

        if (parentPath.isEmpty()) {
            return List.of(id);
        }

        List<String> path = new ArrayList<>(parentPath);
        path.add(id);

        return path;
    }

    private boolean isParent(String parentId, List<String> attributes) {
        return StringUtils.isEmpty(parentId) && CollectionUtils.isEmpty(attributes);
    }

    private boolean isSub(String parentId, List<String> attributes) {
        return !StringUtils.isEmpty(parentId) && CollectionUtils.isEmpty(attributes);
    }

    private boolean isChild(String parentId, List<String> attributes) {
        return !StringUtils.isEmpty(parentId) && !CollectionUtils.isEmpty(attributes);
    }
}
