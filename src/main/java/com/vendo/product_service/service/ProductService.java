package com.vendo.product_service.service;

import com.vendo.product_service.common.mapper.ProductMapper;
import com.vendo.product_service.db.command.ProductCommandService;
import com.vendo.product_service.db.model.Product;
import com.vendo.product_service.validation.CategoryTypeValidator;
import com.vendo.product_service.db.query.ProductQueryService;
import com.vendo.product_service.common.type.CategoryType;
import com.vendo.product_service.web.dto.CreateProductRequest;
import com.vendo.product_service.web.dto.ProductResponse;
import com.vendo.product_service.web.dto.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.vendo.product_service.security.common.helper.SecurityContextHelper.getUserIdFromContext;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    private final ProductQueryService productQueryService;

    private final ProductCommandService productCommandService;

    private final CategoryTypeValidator categoryTypeValidator;

    public void save(CreateProductRequest createProductRequest) {
        categoryTypeValidator.validateCategoryType(createProductRequest.categoryId(), CategoryType.CHILD);
        Product product = productMapper.toProductFromCreateProductRequest(createProductRequest);

        product.setActive(true);
        product.setOwnerId(getUserIdFromContext());

        productCommandService.save(product);
    }

    public void update(String id, UpdateProductRequest updateProductRequest) {
        Product product = productMapper.toProductFromUpdateProductRequest(updateProductRequest);
        productCommandService.update(id, product);
    }

    public ProductResponse findById(String id) {
        Product product = productQueryService.findById(id);
        return productMapper.toProductResponse(product);
    }
}
