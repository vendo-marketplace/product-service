package com.vendo.product_service.adapter.model.product;

import com.vendo.product_service.adapter.common.dto.AuditingEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@Document
@EqualsAndHashCode(callSuper = true)
public class ProductEntity extends AuditingEntity {

    @Id
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
