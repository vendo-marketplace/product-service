package com.vendo.product_service.adapter.product_image.controller;

import com.vendo.product_service.adapter.product_image.controller.dto.ProductImageConfirmRequest;
import com.vendo.product_service.port.product_image.ProductImageUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product-images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageUseCase productImageUseCase;

    @PostMapping("/confirm")
    public void confirm(@Valid ProductImageConfirmRequest request) {
        productImageUseCase.confirm(request.keys());
    }

}
