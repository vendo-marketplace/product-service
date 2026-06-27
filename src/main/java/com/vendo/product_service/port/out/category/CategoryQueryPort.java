package com.vendo.product_service.port.out.category;

import com.vendo.product_service.domain.category.model.Category;

import java.util.List;

public interface CategoryQueryPort {

    Category findById(String id, String message);
    Category findById(String id);

    boolean existsById(String id);

    List<Category> findAll();

}