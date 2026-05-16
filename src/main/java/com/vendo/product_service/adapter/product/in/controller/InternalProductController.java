package com.vendo.product_service.adapter.product.in.controller;

import com.vendo.product_service.adapter.product.in.dto.ProductResponse;
import com.vendo.product_service.adapter.product.out.mapper.DtoProductMapper;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.in.product.InternalProductUseCase;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

import static com.vendo.product_service.adapter.product.in.constants.InternalProductConstants.MAX_LIMIT;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/products")
public class InternalProductController {

    private final DtoProductMapper mapper;
    private final InternalProductUseCase useCase;

    @GetMapping
    List<ProductResponse> getAll(
            @RequestParam(required = false, name = "cursor") Instant cursor,
            @RequestParam("limit") @Valid @Max(value = MAX_LIMIT, message = "Max limit is " + MAX_LIMIT + ".") int limit
    ) {
        List<Product> products = useCase.getAll(cursor, limit);
        return mapper.toResponses(products);
    }

}
