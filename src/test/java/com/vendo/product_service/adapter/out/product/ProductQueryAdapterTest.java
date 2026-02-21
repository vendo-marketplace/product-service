package com.vendo.product_service.adapter.out.product;

import com.vendo.product_service.adapter.model.product.ProductEntity;
import com.vendo.product_service.adapter.out.product.mapper.ProductEntityMapper;
import com.vendo.product_service.adapter.out.product.repository.ProductRepository;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.domain.product.port.ProductQueryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ProductQueryAdapterTest {

    private ProductQueryPort productQueryAdapter;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductEntityMapper productEntityMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productQueryAdapter = new ProductQueryAdapter(productRepository, productEntityMapper);
    }

    @Test
    void findById_shouldReturnMappedProduct_whenEntityExists() {
        String productId = "123";
        ProductEntity entity = ProductEntity.builder().id(productId).title("Test").build();
        Product product = Product.builder().id(productId).title("Test").build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(entity));
        when(productEntityMapper.toProductDomainFromProductEntity(entity)).thenReturn(product);

        Product result = productQueryAdapter.findById(productId);

        assertThat(result).isEqualTo(product);
        verify(productRepository).findById(productId);
        verify(productEntityMapper).toProductDomainFromProductEntity(entity);
    }

    @Test
    void findById_shouldThrowException_whenEntityDoesNotExist() {
        String productId = "123";
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productQueryAdapter.findById(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found.");

        verify(productRepository).findById(productId);
        verifyNoInteractions(productEntityMapper);
    }

    @Test
    void existsById_shouldReturnTrueOrFalse() {
        String productId = "123";
        when(productRepository.existsById(productId)).thenReturn(true);

        boolean exists = productQueryAdapter.existsById(productId);

        assertThat(exists).isTrue();
        verify(productRepository).existsById(productId);
    }
}