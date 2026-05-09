package com.vendo.product_service.application.category.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryView {

    private String id;
    private String title;
    private String code;
    private List<String> attributes;
    private List<String> path;
}