package com.vendo.product_service.domain.product.model;

import com.vendo.core_lib.utils.CollectionUtils;
import com.vendo.core_lib.utils.StringUtils;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.domain.image.exception.ImageKeyNotFoundException;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
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

    public List<String> mergeImageKeys(List<String> newImageKeys) {
        List<String> result = new ArrayList<>();

        if (!CollectionUtils.isEmpty(imageKeys)) {
            result.addAll(imageKeys);
        }

        if (!CollectionUtils.isEmpty(newImageKeys)) {
            result.addAll(newImageKeys);
        }

        return result;
    }

    public void throwIfMissingId() {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalStateException("Id is missing.");
        }
    }

    public List<String> filterImageKeys(String excludedKey) {
        return imageKeys.stream().filter(ik -> !ik.equals(excludedKey)).toList();
    }

    public void throwIfImageKeysNotContain(String imageKey) {
        if (CollectionUtils.isEmpty(imageKeys) || !imageKeys.contains(imageKey)) {
            throw new ImageKeyNotFoundException("%s does not exist in product.".formatted(imageKey));
        }
    }
}
