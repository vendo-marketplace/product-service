package com.vendo.product_service.adapter.product.out;

import com.vendo.product_service.adapter.product.out.mapper.MongoProductMapper;
import com.vendo.product_service.adapter.product.out.persistence.MongoProduct;
import com.vendo.product_service.adapter.product.out.persistence.ProductCommandAdapter;
import com.vendo.product_service.adapter.product.out.persistence.ProductRepository;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.port.category.CategoryQueryPort;
import com.vendo.product_service.domain.port.security.CurrentUserPort;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.security.common.exception.AccessDeniedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCommandAdapterTest {

    @InjectMocks
    private ProductCommandAdapter productCommandAdapter;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MongoProductMapper mongoProductMapper;

    @Mock
    private CategoryQueryPort categoryQueryPort;

    @Mock
    private CurrentUserPort currentUserPort;

    private Product buildDomainProduct() {
        return Product.builder()
                .id("product-1")
                .title("Test product")
                .categoryId("cat-1")
                .ownerId("user-1")
                .build();
    }

    private MongoProduct buildMongoProduct() {
        return MongoProduct.builder()
                .id("product-1")
                .title("Test product")
                .categoryId("cat-1")
                .ownerId("user-1")
                .build();
    }

    @Test
    void save_shouldMapAndSaveProductEntity() {
        Product product = buildDomainProduct();
        MongoProduct entity = buildMongoProduct();

        when(mongoProductMapper.toMongoProduct(product)).thenReturn(entity);

        productCommandAdapter.save(product);

        verify(mongoProductMapper).toMongoProduct(product);

        ArgumentCaptor<MongoProduct> captor = ArgumentCaptor.forClass(MongoProduct.class);
        verify(productRepository).save(captor.capture());

        assertThat(captor.getValue()).isEqualTo(entity);
    }

    @Test
    void update_shouldSaveProduct_whenOwnerMatchesAndCategoryExists() {
        Product existing = buildDomainProduct();
        Product updated = buildDomainProduct();
        updated.setTitle("Updated title");

        MongoProduct mongoExisting = buildMongoProduct();
        MongoProduct mongoUpdated = buildMongoProduct();
        mongoUpdated.setTitle("Updated title");

        when(productRepository.findById(existing.getId())).thenReturn(Optional.of(mongoExisting));
        when(mongoProductMapper.toProduct(mongoExisting)).thenReturn(existing);
        when(currentUserPort.getCurrentUserId()).thenReturn(existing.getOwnerId());
        when(categoryQueryPort.existsById(updated.getCategoryId())).thenReturn(true);
        when(mongoProductMapper.toMongoProduct(updated)).thenReturn(mongoUpdated);

        productCommandAdapter.update(existing.getId(), updated);

        ArgumentCaptor<MongoProduct> captor = ArgumentCaptor.forClass(MongoProduct.class);
        verify(productRepository).save(captor.capture());
        MongoProduct saved = captor.getValue();

        assertThat(saved.getId()).isEqualTo(existing.getId());
        assertThat(saved.getTitle()).isEqualTo("Updated title");
        assertThat(saved.getOwnerId()).isEqualTo(existing.getOwnerId());
    }

    @Test
    void update_shouldThrowAccessDeniedException_whenOwnerDoesNotMatch() {
        Product existing = buildDomainProduct();
        Product updated = buildDomainProduct();

        MongoProduct mongoExisting = buildMongoProduct();
        when(productRepository.findById(existing.getId())).thenReturn(Optional.of(mongoExisting));
        when(mongoProductMapper.toProduct(mongoExisting)).thenReturn(existing);
        when(currentUserPort.getCurrentUserId()).thenReturn("otherUser");

        assertThatThrownBy(() ->
                productCommandAdapter.update(existing.getId(), updated))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Only owner can edit its product.");

        verify(productRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowCategoryNotFoundException_whenCategoryDoesNotExist() {
        Product existing = buildDomainProduct();
        Product updated = buildDomainProduct();

        MongoProduct mongoExisting = buildMongoProduct();
        when(productRepository.findById(existing.getId())).thenReturn(Optional.of(mongoExisting));
        when(mongoProductMapper.toProduct(mongoExisting)).thenReturn(existing);
        when(currentUserPort.getCurrentUserId()).thenReturn(existing.getOwnerId());
        when(categoryQueryPort.existsById(updated.getCategoryId())).thenReturn(false);

        assertThatThrownBy(() ->
                productCommandAdapter.update(existing.getId(), updated))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Category not found.");

        verify(productRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowProductNotFoundException_whenProductDoesNotExist() {
        Product updated = buildDomainProduct();

        when(productRepository.findById("missing-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productCommandAdapter.update("missing-id", updated))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found.");

        verify(productRepository, never()).save(any());
    }
}