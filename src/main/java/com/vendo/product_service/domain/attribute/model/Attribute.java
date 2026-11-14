package com.vendo.product_service.domain.attribute.model;

import com.vendo.core_lib.utils.CollectionUtils;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder(toBuilder = true)
public record Attribute(
        String id,
        String title,
        String slug,
        AttributeType type,
        boolean required,
        List<String> allowedValues
) {

    public static final String TITLE_FIELD = "title", SLUG_FIELD = "slug", TYPE_FIELD = "type";


    public static List<Attribute> extractAll(List<String> ids, Map<String, Attribute> attributesById) {
        if (CollectionUtils.isEmpty(ids)) return List.of();
        return ids.stream().map(attributesById::get).toList();
    }

    public Attribute withChanges(Attribute changes) {
        return toBuilder()
                .title(changes.title())
                .slug(changes.slug())
                .required(changes.required())
                .allowedValues(changes.allowedValues())
                .build();
    }

}
