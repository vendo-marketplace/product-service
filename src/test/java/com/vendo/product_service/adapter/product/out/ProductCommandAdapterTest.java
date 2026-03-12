package com.vendo.product_service.adapter.product.out;

import com.vendo.product_service.adapter.product.out.mapper.MongoProductMapper;
import com.vendo.product_service.adapter.product.out.persistence.MongoProduct;
import com.vendo.product_service.adapter.product.out.persistence.ProductCommandAdapter;
import com.vendo.product_service.adapter.product.out.persistence.ProductRepository;
import com.vendo.product_service.domain.product.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCommandAdapterTest {

    @InjectMocks
    private ProductCommandAdapter productCommandAdapter;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MongoProductMapper mongoProductMapper;


    @Test
    void save_shouldMapAndSaveProductEntity() {
        Product product = Product.builder().title("Test").build();
        MongoProduct entity = MongoProduct.builder().title("Test").build();

        when(mongoProductMapper.toEntity(product)).thenReturn(entity);

        productCommandAdapter.save(product);

        verify(mongoProductMapper).toEntity(product);
        verify(productRepository).save(entity);
    }

    @Test
    void update_shouldSetIdAndSaveEntity() {
        Product product = Product.builder().title("Updated").build();

        MongoProduct entity = MongoProduct.builder().title("Updated").build();

        when(mongoProductMapper.toEntity(product)).thenReturn(entity);

        productCommandAdapter.update("product-1", product);

        assertThat(product.getId()).isEqualTo("product-1");

        verify(productRepository).save(entity);
    }
}