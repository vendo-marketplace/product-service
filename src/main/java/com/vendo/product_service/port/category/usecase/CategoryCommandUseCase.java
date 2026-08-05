package com.vendo.product_service.port.category.usecase;

import com.vendo.product_service.domain.category.model.Category;

public interface CategoryCommandUseCase {

    void save(Category category);

    void update(String id, Category category);

}
