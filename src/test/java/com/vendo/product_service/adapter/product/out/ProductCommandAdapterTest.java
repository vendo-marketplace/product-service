package com.vendo.product_service.adapter.product.out;


import com.vendo.product_service.adapter.product.out.mapper.MongoProductMapper;
import com.vendo.product_service.adapter.product.out.persistence.MongoProduct;
import com.vendo.product_service.adapter.product.out.persistence.ProductCommandAdapter;
import com.vendo.product_service.adapter.product.out.persistence.ProductRepository;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.port.user.CurrentUserPort;
import com.vendo.product_service.domain.product.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductCommandAdapterTest {

    private ProductCommandAdapter productCommandAdapter;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MongoProductMapper mongoProductMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        CategoryQueryPort categoryQueryPort = mock(CategoryQueryPort.class);
        CurrentUserPort currentUserPort = mock(CurrentUserPort.class);

        productCommandAdapter = new ProductCommandAdapter(
                productRepository,
                mongoProductMapper,
                categoryQueryPort,
                currentUserPort
        );
    }

    @Test
    void save_shouldMapAndSaveProductEntity() {
        Product product = Product.builder().title("Test").build();
        MongoProduct entity = MongoProduct.builder().title("Test").build();

        when(mongoProductMapper.toMongoProduct(product)).thenReturn(entity);

        productCommandAdapter.save(product);

        verify(mongoProductMapper).toMongoProduct(product);
        ArgumentCaptor<MongoProduct> captor = ArgumentCaptor.forClass(MongoProduct.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(entity);
    }
}