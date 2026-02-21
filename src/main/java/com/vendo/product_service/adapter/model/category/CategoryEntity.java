package com.vendo.product_service.adapter.model.category;

import com.vendo.product_service.adapter.common.dto.AuditingEntity;
import com.vendo.product_service.adapter.model.category.embedded.AttributeDefinition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CategoryEntity extends AuditingEntity {

    @Id
    private String id;

    private String title;

    @Indexed(unique = true)
    private String code;

    private String parentId;

    private Map<String, AttributeDefinition> attributes;
}
