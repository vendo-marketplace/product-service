package com.vendo.product_service.service;

import com.vendo.product_service.common.mapper.ProductMapper;
import com.vendo.product_service.db.command.ProductCommandService;
import com.vendo.product_service.db.model.Product;
import com.vendo.product_service.db.query.ProductQueryService;
import com.vendo.product_service.domain.category.common.exception.CategoryValidationException;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.db.query.CategoryQueryService;
import com.vendo.product_service.web.dto.CreateProductRequest;
import com.vendo.product_service.web.dto.ProductResponse;
import com.vendo.product_service.web.dto.UpdateProductRequest;
import com.vendo.security.common.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.vendo.product_service.security.common.helper.SecurityContextHelper.getUserIdFromContext;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    private final ProductQueryService productQueryService;

    private final CategoryQueryService categoryQueryService;

    private final ProductCommandService productCommandService;

    public void save(CreateProductRequest createProductRequest) {
        validateProductCategory(createProductRequest.categoryId());
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

    private void validateProductCategory(String categoryId) {
        Category category = categoryQueryService.findByIdOrThrow(categoryId);
        if (category.getCategoryType() != CategoryType.CHILD) {
            // TODO change exception because it's not validation
            throw new CategoryValidationException("Product should have only child categories.");
        }
    }
}
