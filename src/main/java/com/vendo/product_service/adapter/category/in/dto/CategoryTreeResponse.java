package com.vendo.product_service.adapter.category.in.dto;

import com.vendo.product_service.domain.attribute.model.Attribute;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryTreeResponse {

    private String id;
    private String title;
    private String code;
    private List<Attribute> attributes;
    private List<String> path;
}