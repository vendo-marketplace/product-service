package com.vendo.product_service.application;

import com.vendo.product_service.application.product.ProductService;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.port.product.ProductCommandPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.port.user.CurrentUserPort;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.security.common.exception.AccessDeniedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductCommandPort commandPort;

    @Mock
    private ProductQueryPort queryPort;

    @Mock
    private CategoryQueryPort categoryQueryPort;

    @Mock
    private CurrentUserPort currentUserPort;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Product buildDomainProduct() {
        return Product.builder()
                .id("product-1")
                .title("Test product")
                .categoryId("cat-1")
                .ownerId("user-1")
                .price(BigDecimal.TEN)
                .build();
    }

    @Test
    void save_shouldCallCommandPort_whenCategoryExists() {
        Product product = buildDomainProduct();

        when(categoryQueryPort.existsById(product.getCategoryId())).thenReturn(true);

        productService.save(product);

        verify(commandPort, times(1)).save(product);
    }

    @Test
    void save_shouldThrowCategoryNotFoundException_whenCategoryDoesNotExist() {
        Product product = buildDomainProduct();

        when(categoryQueryPort.existsById(product.getCategoryId())).thenReturn(false);

        assertThatThrownBy(() -> productService.save(product))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Category not found.");

        verify(commandPort, never()).save(any());
    }

    @Test
    void update_shouldUpdateFieldsAndCallCommandPort_whenOwnerMatches() {
        Product existingProduct = buildDomainProduct();
        existingProduct.setOwnerId("user123");

        Product updatedProduct = buildDomainProduct();
        updatedProduct.setTitle("Updated title");
        updatedProduct.setCategoryId(existingProduct.getCategoryId());

        when(currentUserPort.getCurrentUserId())
                .thenReturn("user123");
        when(queryPort.findById(existingProduct.getId())).thenReturn(existingProduct);
        when(categoryQueryPort.existsById(existingProduct.getCategoryId())).thenReturn(true);

        productService.update(existingProduct.getId(), updatedProduct);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(commandPort).save(captor.capture());

        Product saved = captor.getValue();

        assert saved.getId().equals(existingProduct.getId());
        assert saved.getTitle().equals("Updated title");
        assert saved.getOwnerId().equals("user123");
    }

    @Test
    void update_shouldThrowAccessDeniedException_whenOwnerDoesNotMatch() {
        Product existingProduct = buildDomainProduct();
        existingProduct.setOwnerId("ownerX");

        Product updatedProduct = buildDomainProduct();

        when(currentUserPort.getCurrentUserId())
                .thenReturn("otherUser");

        when(queryPort.findById(existingProduct.getId())).thenReturn(existingProduct);

        assertThatThrownBy(() -> productService.update(existingProduct.getId(), updatedProduct))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Only owner can edit its product.");

        verify(commandPort, never()).save(any());
    }

    @Test
    void findById_shouldReturnProductFromQueryPort() {
        Product product = buildDomainProduct();

        when(queryPort.findById(product.getId())).thenReturn(product);

        Product result = productService.findById(product.getId());

        assert result != null;
        verify(queryPort, times(1)).findById(product.getId());
    }
}