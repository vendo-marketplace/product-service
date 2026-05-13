package com.vendo.product_service.port.in.category;

import com.vendo.product_service.application.category.model.CategoryView;
import com.vendo.product_service.domain.category.model.Category;

import java.util.List;

public interface CategoryQueryUseCase {

    Category findById(String id);

    List<CategoryView> getTree();
}
