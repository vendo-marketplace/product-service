package com.vendo.product_service.adapter.product.out;

import com.vendo.product_service.adapter.product.out.mapper.MongoProductMapper;
import com.vendo.product_service.adapter.product.out.persistence.MongoProduct;
import com.vendo.product_service.adapter.product.out.persistence.ProductRepository;
import com.vendo.product_service.domain.port.product.ProductQueryPort;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductQueryAdapterTest {
    @InjectMocks
    private ProductQueryPort productQueryAdapter;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MongoProductMapper mongoProductMapper;

    @Test
    void findById_shouldReturnMappedProduct_whenEntityExists() {
        String productId = "123";
        MongoProduct entity = MongoProduct.builder().id(productId).title("Test").build();
        Product product = Product.builder().id(productId).title("Test").build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(entity));
        when(mongoProductMapper.toProduct(entity)).thenReturn(product);

        Product result = productQueryAdapter.findById(productId);

        assertThat(result).isEqualTo(product);
        verify(productRepository).findById(productId);
        verify(mongoProductMapper).toProduct(entity);
    }

    @Test
    void findById_shouldThrowException_whenEntityDoesNotExist() {
        String productId = "123";
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productQueryAdapter.findById(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found.");

        verify(productRepository).findById(productId);
        verifyNoInteractions(mongoProductMapper);
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