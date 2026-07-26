package com.vendo.product_service.domain.product.model;

import com.vendo.core_lib.utils.StringUtils;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
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
    private List<String> imageKeys;
    private Boolean active;
    private Instant createdAt;

    public void throwIfMissingId() {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalStateException("Id is missing.");
        }
    }

}
