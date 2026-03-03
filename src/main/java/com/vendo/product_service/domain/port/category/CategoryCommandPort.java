package com.vendo.product_service.domain.port.category;

import com.vendo.product_service.domain.category.model.Category;

public interface CategoryCommandPort {
    void save(Category category);
}
