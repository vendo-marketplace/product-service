package com.vendo.product_service.application.product;

import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.in.product.InternalProductUseCase;
import com.vendo.product_service.port.out.product.InternalProductQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternalProductService implements InternalProductUseCase {

    private final InternalProductQueryPort internalProductQueryPort;

    @Override
    public List<Product> getAll(Instant cursor, int limit) {
        return internalProductQueryPort.getAll(cursor, limit);
    }

}
