package com.vendo.product_service.adapter.category.out.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.product_service.application.category.model.CategoryNode;
import com.vendo.product_service.domain.category.exception.CategoryCacheException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryTreeSerializer {

    private final ObjectMapper objectMapper;

    public String serialize(List<CategoryNode> tree) {
        try {
            return objectMapper.writeValueAsString(tree);
        } catch (JsonProcessingException e) {
            throw new CategoryCacheException("Failed to serialize category tree.");
        }
    }

    public List<CategoryNode> deserialize(String json) {
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, CategoryNode.class));
        } catch (JsonProcessingException e) {
            throw new CategoryCacheException("Failed to deserialize category tree.");
        }
    }
}
