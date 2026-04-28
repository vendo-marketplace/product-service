package com.vendo.product_service.domain.category.model;

import com.vendo.product_service.domain.category.exception.CategoryTypeException;
import com.vendo.product_service.domain.category.exception.CategoryValidationException;
import com.vendo.product_service.domain.category.type.CategoryType;

import java.util.List;
import java.util.Objects;

public class Category {

    private String id;
    private String title;
    private String code;
    private String parentId;
    private List<String> attributes;
    private List<String> path;

    public Category(String id, String title, String code, String parentId, List<String> attributes, List<String> path) {
        this.id = id;
        this.title = title;
        this.code = code;
        this.parentId = parentId;
        this.attributes = attributes;
        this.path = path;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public List<String> getPath() {
        return path;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Category{" + "id='" + id + '\'' + ", title='" + title + '\'' + ", code='" + code + '\'' + ", parentId='" + parentId + '\'' + ", attributes=" + attributes + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) && Objects.equals(title, category.title) && Objects.equals(code, category.code) && Objects.equals(parentId, category.parentId) && Objects.equals(attributes, category.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, code, parentId, attributes);
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

    private boolean isParent(String parentId, List<String> attributes) {
        return parentId == null && attributes == null;
    }

    private boolean isSub(String parentId, List<String> attributes) {
        return (parentId != null && !parentId.isEmpty()) && attributes == null;
    }

    private boolean isChild(String parentId, List<String> attributes) {
        return (parentId != null && !parentId.isEmpty()) && attributes != null && !attributes.isEmpty();
    }

    public static class Builder {
        private String id;
        private String title;
        private String code;
        private String parentId;
        private List<String> attributes;
        private List<String> path;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder parentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder attributes(List<String> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder path(List<String> path) {
            this.path = path;
            return this;
        }

        public Category build() {
            return new Category(id, title, code, parentId, attributes, path);
        }
    }
}
