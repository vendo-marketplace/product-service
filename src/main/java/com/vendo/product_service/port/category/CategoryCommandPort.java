package com.vendo.product_service.port.category;

import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryImageType;

public interface CategoryCommandPort {

    void save(Category category);
    void update(String id, Category category);

    void removeImage(String id, CategoryImageType type);

}
