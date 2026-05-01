package com.vendo.product_service.application.category.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CategoryNode {

    private String id;
    private String title;
    private String code;

    @Builder.Default
    private List<CategoryNode> children = new ArrayList<>();
}