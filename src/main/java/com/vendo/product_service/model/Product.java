package com.vendo.product_service.model;

import com.vendo.product_service.common.dto.AuditingEntity;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Title is required.")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters.")
    private String title;

    @NotBlank(message = "Description is required.")
    @Size(min = 5, max = 250, message = "Description must be between 5 and 250 characters.")
    private String description;

    @Min(value = 0, message = "Minimal quantity is one.")
    private int quantity;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0", inclusive = false, message = "Price must be greater or equal to 0.")
    @Digits(integer = 8, fraction = 2, message = "Price must have up to 8 digits before the decimal point and 2 after.")
    private BigDecimal price;

    private String sellerId;

    private String categoryId;

    private Map<String, Object> attributes;

    private boolean active;

}
