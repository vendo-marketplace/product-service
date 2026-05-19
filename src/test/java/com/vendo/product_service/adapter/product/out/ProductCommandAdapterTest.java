package com.vendo.product_service.adapter.product.out;

import com.vendo.product_service.adapter.product.out.mapper.MongoProductMapper;
import com.vendo.product_service.adapter.product.out.persistence.MongoProduct;
import com.vendo.product_service.adapter.product.out.persistence.ProductCommandAdapter;
import com.vendo.product_service.adapter.product.out.persistence.ProductRepository;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

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
        MongoProduct entity = MongoProduct.builder().id(String.valueOf(UUID.randomUUID())).title("Test").build();

        when(mongoProductMapper.toEntity(product)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(entity);
        when(mongoProductMapper.toProduct(entity)).thenReturn(product);

        productCommandAdapter.save(product);

        verify(mongoProductMapper).toEntity(product);
        verify(productRepository).save(entity);
        verify(mongoProductMapper).toProduct(entity);
    }

    @Test
    void update_shouldFindUpdateAndSaveEntity_whenProductExists() {
        String id = "product-1";
        Product productToUpdate = Product.builder().title("Updated").build();
        MongoProduct existingEntity = MongoProduct.builder().id(id).title("Old").build();

        when(productRepository.findById(id)).thenReturn(Optional.of(existingEntity));

        productCommandAdapter.update(id, productToUpdate);

        verify(productRepository).findById(id);
        verify(mongoProductMapper).updateEntity(existingEntity, productToUpdate);
        verify(productRepository).save(existingEntity);
        verifyNoMoreInteractions(productRepository, mongoProductMapper);
    }

    @Test
    void update_shouldThrowException_whenProductDoesNotExist() {
        String id = "product-1";
        Product productToUpdate = Product.builder().title("Updated").build();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productCommandAdapter.update(id, productToUpdate))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found.");

        verify(productRepository).findById(id);
        verifyNoInteractions(mongoProductMapper);
        verifyNoMoreInteractions(productRepository);
    }
}