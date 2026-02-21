package com.vendo.product_service.adapter.in.product.adapter;

import com.vendo.product_service.adapter.in.product.mapper.ProductDtoMapper;
import com.vendo.product_service.application.ProductUseCase;

import com.vendo.product_service.domain.category.validation.attribute.CategoryAttributeValidator;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.adapter.in.product.dto.CreateProductRequest;
import com.vendo.product_service.adapter.in.product.dto.ProductResponse;
import com.vendo.product_service.adapter.in.product.dto.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.vendo.product_service.security.common.helper.SecurityContextHelper.getUserIdFromContext;

@Component
@RequiredArgsConstructor
public class ProductAdapter {

    private final ProductUseCase productUseCase;
    private final ProductDtoMapper productDtoMapper;
    private final CategoryAttributeValidator categoryAttributeValidator;

    public void save(CreateProductRequest request) {
        categoryAttributeValidator.validateCategoryAttributes(request.categoryId(), request.attributes());

        Product product = productDtoMapper.toProductDomainFromCreateRequest(request);
        product.setOwnerId(getUserIdFromContext());
        product.setActive(true);

        productUseCase.save(product);
    }

    public void update(String id, UpdateProductRequest request) {
        Product product = productDtoMapper.toProductDomainFromUpdateRequest(request);
        productUseCase.update(id, product);
    }

    public ProductResponse findById(String id) {
        Product product = productUseCase.findById(id);
        return productDtoMapper.toProductResponseFromDomain(product);
    }
}