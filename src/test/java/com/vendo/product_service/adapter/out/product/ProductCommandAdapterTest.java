package com.vendo.product_service.adapter.out.product;

import com.vendo.product_service.adapter.model.product.MongoProduct;
import com.vendo.product_service.adapter.out.product.mapper.ProductMapper;
import com.vendo.product_service.adapter.out.product.repository.ProductRepository;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.product.ProductCommandPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductCommandAdapterTest {

    private ProductCommandPort productCommandAdapter;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        productCommandAdapter = new ProductCommandAdapter(productRepository, productMapper);
//    }

    @Test
    void save_shouldMapAndSaveProductEntity() {
        Product product = Product.builder().title("Test").build();
        MongoProduct entity = MongoProduct.builder().title("Test").build();

        when(productMapper.toMongoProduct(product)).thenReturn(entity);

        productCommandAdapter.save(product);

        verify(productMapper).toMongoProduct(product);
        ArgumentCaptor<MongoProduct> captor = ArgumentCaptor.forClass(MongoProduct.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(entity);
    }
}