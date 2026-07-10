package com.vendo.product_service.adapter.product.out.persistence;

import com.vendo.product_service.adapter.product.out.mapper.MongoProductMapper;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.product.InternalProductQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class InternalProductQueryAdapter implements InternalProductQueryPort {

    private final ProductRepository repository;
    private final MongoProductMapper mapper;

    @Override
    public List<Product> getAll(String cursor, int limit) {
        if (Objects.isNull(cursor)) {
            return mapper.toProducts(repository.findAllByOrderByIdDesc(Limit.of(limit)));
        }

        List<MongoProduct> cursorProducts = repository.findByIdLessThanOrderByIdDesc(cursor, Limit.of(limit));
        return mapper.toProducts(cursorProducts);
    }
}
