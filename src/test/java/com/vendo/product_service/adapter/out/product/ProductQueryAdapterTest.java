package com.vendo.product_service.adapter.out.product;

import com.vendo.product_service.adapter.model.product.MongoProduct;
import com.vendo.product_service.adapter.out.product.mapper.ProductMapper;
import com.vendo.product_service.adapter.out.product.repository.ProductRepository;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.product.ProductQueryPort;
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
    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productQueryAdapter = new ProductQueryAdapter(productRepository, productMapper);
    }

    @Test
    void findById_shouldReturnMappedProduct_whenEntityExists() {
        String productId = "123";
        MongoProduct entity = MongoProduct.builder().id(productId).title("Test").build();
        Product product = Product.builder().id(productId).title("Test").build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(entity));
        when(productMapper.toProduct(entity)).thenReturn(product);

        Product result = productQueryAdapter.findById(productId);

        assertThat(result).isEqualTo(product);
        verify(productRepository).findById(productId);
        verify(productMapper).toProduct(entity);
    }

    @Test
    void findById_shouldThrowException_whenEntityDoesNotExist() {
        String productId = "123";
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productQueryAdapter.findById(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found.");

        verify(productRepository).findById(productId);
        verifyNoInteractions(productMapper);
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