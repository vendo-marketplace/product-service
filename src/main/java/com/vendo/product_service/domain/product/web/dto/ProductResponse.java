package com.vendo.product_service.domain.product.web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ProductResponse {
    private String id;
    private String title;
    private String description;
    private int quantity;
    private BigDecimal price;
    private String ownerId;
    private String categoryId;
    private Map<String, Object> attributes;
    private boolean active;
}
