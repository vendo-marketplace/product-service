package com.vendo.product_service.application;

import com.vendo.product_service.adapter.model.product.ProductEntity;
import com.vendo.product_service.adapter.out.product.mapper.ProductEntityMapper;
import com.vendo.product_service.common.builder.ProductDataBuilder;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.port.CategoryQueryPort;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.domain.product.port.ProductCommandPort;
import com.vendo.product_service.domain.product.port.ProductQueryPort;
import com.vendo.product_service.security.common.helper.SecurityContextHelper;
import com.vendo.security.common.exception.AccessDeniedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ProductUseCaseTest {

    @Mock
    private ProductCommandPort commandPort;

    @Mock
    private ProductQueryPort queryPort;

    @Mock
    private CategoryQueryPort categoryQueryPort;

    @InjectMocks
    private ProductUseCase productUseCase;

    @InjectMocks
    private ProductEntityMapper productEntityMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Product buildDomainProduct() {
        ProductEntity entity = ProductDataBuilder.buildProductWithRequiredFields().build();
        return productEntityMapper.toProductDomainFromProductEntity(entity);
    }

    @Test
    void save_shouldCallCommandPort_whenCategoryExists() {
        Product product = buildDomainProduct();

        when(categoryQueryPort.existsById(product.getCategoryId())).thenReturn(true);

        productUseCase.save(product);

        verify(commandPort, times(1)).save(product);
    }

    @Test
    void save_shouldThrowCategoryNotFoundException_whenCategoryDoesNotExist() {
        Product product = buildDomainProduct();

        when(categoryQueryPort.existsById(product.getCategoryId())).thenReturn(false);

        assertThatThrownBy(() -> productUseCase.save(product))
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

        try (MockedStatic<com.vendo.product_service.security.common.helper.SecurityContextHelper> securityHelperMock =
                     Mockito.mockStatic(com.vendo.product_service.security.common.helper.SecurityContextHelper.class)) {

            securityHelperMock.when(SecurityContextHelper::getUserIdFromContext)
                    .thenReturn("user123");

            when(queryPort.findById(existingProduct.getId())).thenReturn(existingProduct);
            when(categoryQueryPort.existsById(existingProduct.getCategoryId())).thenReturn(true);

            productUseCase.update(existingProduct.getId(), updatedProduct);

            verify(commandPort, times(1)).save(existingProduct);
        }
    }

    @Test
    void update_shouldThrowAccessDeniedException_whenOwnerDoesNotMatch() {
        Product existingProduct = buildDomainProduct();
        existingProduct.setOwnerId("ownerX");

        Product updatedProduct = buildDomainProduct();

        try (MockedStatic<com.vendo.product_service.security.common.helper.SecurityContextHelper> securityHelperMock =
                     Mockito.mockStatic(com.vendo.product_service.security.common.helper.SecurityContextHelper.class)) {

            securityHelperMock.when(SecurityContextHelper::getUserIdFromContext)
                    .thenReturn("otherUser");

            when(queryPort.findById(existingProduct.getId())).thenReturn(existingProduct);

            assertThatThrownBy(() -> productUseCase.update(existingProduct.getId(), updatedProduct))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessage("Only owner can edit product");

            verify(commandPort, never()).save(any());
        }
    }

    @Test
    void findById_shouldReturnProductFromQueryPort() {
        Product product = buildDomainProduct();

        when(queryPort.findById(product.getId())).thenReturn(product);

        Product result = productUseCase.findById(product.getId());

        assert result != null;
        verify(queryPort, times(1)).findById(product.getId());
    }
}