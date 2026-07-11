package com.vendo.product_service.port.product;

import com.vendo.product_service.application.product.model.ProductComparison;

import java.util.List;

public interface ProductCompareUseCase {

    List<ProductComparison> compare(String categoryId, List<String> productIds);

}
