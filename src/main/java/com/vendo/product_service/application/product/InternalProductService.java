package com.vendo.product_service.application.product;

import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.product.internal.InternalProductUseCase;
import com.vendo.product_service.port.product.internal.InternalProductQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class InternalProductService implements InternalProductUseCase {

    private final InternalProductQueryPort internalProductQueryPort;

    @Override
    public List<Product> getAll(String cursor, int limit) {
        return internalProductQueryPort.getAll(cursor, limit);
    }

}
