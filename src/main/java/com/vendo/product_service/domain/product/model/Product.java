package com.vendo.product_service.domain.product.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Product {

    private String id;
    private String title;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private String ownerId;
    private String categoryId;
    private Map<String, List<String>> attributes;
    private Boolean active;

    public Product(String id, String title, String description, Integer quantity, BigDecimal price, String ownerId, String categoryId, Map<String, List<String>> attributes, Boolean active) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.ownerId = ownerId;
        this.categoryId = categoryId;
        this.attributes = attributes;
        this.active = active;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(title, product.title) && Objects.equals(description, product.description) && Objects.equals(quantity, product.quantity) && Objects.equals(price, product.price) && Objects.equals(ownerId, product.ownerId) && Objects.equals(categoryId, product.categoryId) && Objects.equals(attributes, product.attributes) && Objects.equals(active, product.active);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, quantity, price, ownerId, categoryId, attributes, active);
    }

    @Override
    public String toString() {
        return "Product{" + "id='" + id + '\'' + ", title='" + title + '\'' + ", description='" + description + '\'' + ", quantity=" + quantity + ", price=" + price + ", ownerId='" + ownerId + '\'' + ", categoryId='" + categoryId + '\'' + ", attributes=" + attributes + ", active=" + active + '}';
    }

    public static class Builder {
        private String id;
        private String title;
        private String description;
        private Integer quantity;
        private BigDecimal price;
        private String ownerId;
        private String categoryId;
        private Map<String, List<String>> attributes;
        private Boolean active;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder ownerId(String ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Builder categoryId(String categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder attributes(Map<String, List<String>> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public Product build() {
            return new Product(id, title, description, quantity, price, ownerId, categoryId, attributes, active);
        }
    }
}
