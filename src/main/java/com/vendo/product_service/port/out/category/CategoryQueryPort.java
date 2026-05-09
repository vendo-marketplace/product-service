package com.vendo.product_service.port.out.category;

import com.vendo.product_service.domain.category.model.Category;

import java.util.List;

public interface CategoryQueryPort {

    Category findById(String id, String message);

    boolean existsById(String id);

    boolean existsByCode(String code);

    List<Category> findAll();

}