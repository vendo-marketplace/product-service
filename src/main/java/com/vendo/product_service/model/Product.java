package com.vendo.product_service.model;

import com.vendo.product_service.common.dto.AuditingEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@Document
@EqualsAndHashCode(callSuper = true)
public class Product extends AuditingEntity {

    @Id
    private String id;

    private String title;

    private String description;

    private int quantity;

    private BigDecimal price;

    private String sellerId;

    private String categoryId;

    private Map<String, Object> attributes;

    private boolean active;

}
