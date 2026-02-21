package com.vendo.product_service.domain.category.port;

import com.vendo.product_service.domain.category.model.Category;

public interface CategoryCommandPort {
    void save(Category category);
}
