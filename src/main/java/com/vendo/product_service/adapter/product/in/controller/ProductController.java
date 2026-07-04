package com.vendo.product_service.adapter.product.in.controller;

import com.vendo.product_service.adapter.product.in.dto.CompareAttributeResponse;
import com.vendo.product_service.adapter.product.in.dto.CreateProductRequest;
import com.vendo.product_service.adapter.product.in.dto.ProductResponse;
import com.vendo.product_service.adapter.product.in.dto.UpdateProductRequest;
import com.vendo.product_service.adapter.product.out.mapper.CompareProductMapper;
import com.vendo.product_service.adapter.product.out.mapper.DtoProductMapper;
import com.vendo.product_service.application.product.model.ProductComparison;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.in.product.ProductCompareUseCase;
import com.vendo.product_service.port.in.product.ProductUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
class ProductController {

    private final ProductUseCase productUseCase;
    private final ProductCompareUseCase productCompareUseCase;
    private final DtoProductMapper mapper;
    private final CompareProductMapper compareMapper;

    @PostMapping
    void save(@Valid @RequestBody CreateProductRequest request) {
        productUseCase.save(mapper.toEntity(request));
    }

    @PutMapping("/{id}")
    void update(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        productUseCase.update(id, mapper.toEntity(request));
    }

    @GetMapping("/{id}")
    ResponseEntity<ProductResponse> find(@PathVariable String id) {
        Product product = productUseCase.findById(id);
        return ResponseEntity.ok(mapper.toResponse(product));
    }

    @GetMapping("/compare")
    ResponseEntity<List<CompareAttributeResponse>> compare(
            @RequestParam @NotBlank(message = "Category ID is required.") String categoryId,
            @RequestParam @NotEmpty(message = "Product IDs are required.") List<String> productIds
    ) {
        List<ProductComparison> comparisons = productCompareUseCase.compare(categoryId, productIds);
        return ResponseEntity.ok(compareMapper.toResponses(comparisons));
    }
}