package com.vendo.product_service.port.in.category;

import com.vendo.product_service.domain.category.model.Category;

public interface CategoryCommandUseCase {

    void save(Category category);

}
