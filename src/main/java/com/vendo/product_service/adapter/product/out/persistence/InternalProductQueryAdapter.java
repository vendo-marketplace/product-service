package com.vendo.product_service.adapter.product.out.persistence;

import com.vendo.product_service.adapter.product.out.mapper.MongoProductMapper;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.out.product.InternalProductQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class InternalProductQueryAdapter implements InternalProductQueryPort {

    private final ProductRepository repository;
    private final MongoProductMapper mapper;

    @Override
    public List<Product> getAll(Instant cursor, int limit) {
        if (Objects.isNull(cursor)) {
            return mapper.toProducts(repository.findAllByOrderByCreatedAtDesc(Limit.of(limit)));
        }

        List<MongoProduct> cursorProducts = repository.getAllByCreatedAtOrderByCreatedAtDesc(cursor, Limit.of(limit));
        return mapper.toProducts(cursorProducts);
    }
}
