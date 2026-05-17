package com.vendo.product_service.adapter.product.out.persistence;

import com.vendo.product_service.adapter.product.out.mapper.MongoProductMapper;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.out.product.InternalProductQueryPort;
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
        log.info("Logging input: {}, {}.", cursor, limit);
        if (Objects.isNull(cursor)) {
            List<Product> products = mapper.toProducts(repository.findAllByOrderByIdDesc(Limit.of(limit)));
            log.info(products.toString());
            return products;
        }

        List<MongoProduct> cursorProducts = repository.findByIdLessThanOrderByIdDesc(cursor, Limit.of(limit));
        log.info(cursorProducts.toString());
        return mapper.toProducts(cursorProducts);
    }
}
