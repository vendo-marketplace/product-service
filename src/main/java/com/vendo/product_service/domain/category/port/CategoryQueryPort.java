package com.vendo.product_service.domain.category.port;

import com.vendo.product_service.domain.category.model.Category;

public interface CategoryQueryPort {
    Category findById(String id, String s);
    boolean existsById(String id);
    boolean existsByCode(String code);
}