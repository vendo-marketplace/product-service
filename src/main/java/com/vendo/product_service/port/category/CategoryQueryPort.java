package com.vendo.product_service.port.category;

import com.vendo.product_service.domain.category.model.Category;

public interface CategoryQueryPort {

    Category findById(String id, String message);

    boolean existsById(String id);

    boolean existsByCode(String code);

}