package com.vendo.product_service.port.category.usecase;

import com.vendo.product_service.application.category.model.CategoryNode;
import com.vendo.product_service.domain.category.model.Category;

import java.util.List;

public interface CategoryQueryUseCase {

    Category findById(String id);

    List<CategoryNode> getTree();
}
