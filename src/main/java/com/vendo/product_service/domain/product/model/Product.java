package com.vendo.product_service.domain.product.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Version;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Builder
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

    @Version
    private long version;
}
