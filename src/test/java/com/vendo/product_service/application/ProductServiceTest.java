package com.vendo.product_service.application;

import com.vendo.product_service.application.category.validation.attribute.AttributesValidator;
import com.vendo.product_service.application.product.ProductService;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.port.product.ProductCommandPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.port.user.CurrentUserPort;
import com.vendo.product_service.test_utils.builder.CategoryDataBuilder;
import com.vendo.product_service.test_utils.builder.ProductDataBuilder;
import com.vendo.security_lib.exception.AccessDeniedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductCommandPort commandPort;

    @Mock
    private ProductQueryPort queryPort;

    @Mock
    private CategoryQueryPort categoryQueryPort;

    @Mock
    private AttributesValidator attributesValidator;

    @Mock
    private CurrentUserPort currentUserPort;

    @InjectMocks
    private ProductService productService;

    @Test
    void save_shouldSetOwnerAndActiveAndSave_whenCategoryExists() {
        Product product = ProductDataBuilder.withAllFields().build();
        Category category = CategoryDataBuilder.withAllFields().build();
        String currentUserId = "user-1";

        when(categoryQueryPort.findById(product.getCategoryId(), "Parent category not found.")).thenReturn(category);
        when(currentUserPort.getCurrentUserId()).thenReturn(currentUserId);

        productService.save(product);

        verify(attributesValidator, times(1)).validate(category, product.getAttributes());
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(commandPort, times(1)).save(captor.capture());

        Product savedProduct = captor.getValue();

        assertThat(savedProduct.getOwnerId()).isEqualTo(currentUserId);
        assertThat(savedProduct.getActive()).isTrue();
        assertThat(savedProduct.getTitle()).isEqualTo(product.getTitle());
        assertThat(savedProduct.getCategoryId()).isEqualTo(product.getCategoryId());
        verifyNoMoreInteractions(commandPort, categoryQueryPort, attributesValidator, currentUserPort);
    }

    @Test
    void save_shouldThrowCategoryNotFoundException_whenCategoryDoesNotExist() {
        Product product = ProductDataBuilder.withAllFields().build();

        when(categoryQueryPort.findById(any(), any()))
                .thenThrow(new CategoryNotFoundException("Parent category not found."));

        assertThatThrownBy(() -> productService.save(product))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Parent category not found.");

        verify(categoryQueryPort)
                .findById(eq(product.getCategoryId()), anyString());
        verifyNoInteractions(attributesValidator, currentUserPort, commandPort);
    }

    @Test
    void update_shouldUpdateFieldsAndCallCommandPort_whenOwnerMatchesAndCategoryExists() {
        String currentUserId = "user123";
        Product existingProduct = ProductDataBuilder.withAllFields()
                .ownerId(currentUserId)
                .build();
        Product updatedProduct = ProductDataBuilder.withAllFields()
                .categoryId("new-cat-id")
                .title("Updated title")
                .build();

        when(queryPort.findById(existingProduct.getId())).thenReturn(existingProduct);
        when(currentUserPort.getCurrentUserId()).thenReturn(currentUserId);
        when(categoryQueryPort.existsById(updatedProduct.getCategoryId())).thenReturn(true);

        productService.update(existingProduct.getId(), updatedProduct);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(commandPort).update(eq(existingProduct.getId()), captor.capture());

        Product passedForUpdate = captor.getValue();
        assertThat(passedForUpdate.getTitle()).isEqualTo("Updated title");
        assertThat(passedForUpdate.getCategoryId()).isEqualTo("new-cat-id");
    }

    @Test
    void update_shouldThrowAccessDeniedException_whenOwnerDoesNotMatch() {
        Product existingProduct = ProductDataBuilder.withAllFields().ownerId("ownerX").build();
        Product updatedProduct = ProductDataBuilder.withAllFields().build();

        when(queryPort.findById(existingProduct.getId())).thenReturn(existingProduct);
        when(currentUserPort.getCurrentUserId()).thenReturn("intruderUser");

        assertThatThrownBy(() -> productService.update(existingProduct.getId(), updatedProduct))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You're not product's owner.");

        verify(queryPort).findById(existingProduct.getId());
        verify(currentUserPort).getCurrentUserId();

        verifyNoInteractions(categoryQueryPort, commandPort);
    }

    @Test
    void findById_shouldReturnProduct_whenProductExists() {
        Product product = ProductDataBuilder.withAllFields().build();

        when(queryPort.findById(product.getId())).thenReturn(product);

        Product result = productService.findById(product.getId());

        assertThat(result).isEqualTo(product);
        verify(queryPort).findById(product.getId());
        verifyNoMoreInteractions(queryPort);
    }
}