package com.vendo.product_service.port.out.category;

import com.vendo.product_service.application.category.model.CategoryNode;

import java.util.List;
import java.util.Optional;

public interface CategoryCachePort {
    void saveTree(List<CategoryNode> tree);
    Optional<List<CategoryNode>> getTree();
    void evictTree();
}
