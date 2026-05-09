package com.vendo.product_service.adapter.category.in.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryTreeResponse {

    private String id;
    private String title;
    private String code;
    private List<String> attributes;
    private List<String> path;
}