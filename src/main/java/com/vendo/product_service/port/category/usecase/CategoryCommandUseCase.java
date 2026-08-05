package com.vendo.product_service.port.category.usecase;

import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.image.model.Image;

public interface CategoryCommandUseCase {

    void save(Category category);

    void update(String id, Category category);

    void uploadImage(String id, Image image);
    void removeImage(String id);

}
