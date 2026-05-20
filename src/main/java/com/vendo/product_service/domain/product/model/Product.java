package com.vendo.product_service.domain.product.model;

import com.vendo.product_service.domain.attribute.model.AttributeValue;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Product {

    private String id;
    private String title;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private String ownerId;
    private String categoryId;
    private List<AttributeValue> attributes;
    private Boolean active;
    private LocalDateTime createdAt;

}
