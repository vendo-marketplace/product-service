package com.vendo.product_service.adapter.product_image.out.persistence;

import com.vendo.product_service.adapter.product_image.out.mapper.ProductImageMapper;
import com.vendo.product_service.domain.product_image.exception.ProductImageAlreadyExists;
import com.vendo.product_service.domain.product_image.model.ImageStatus;
import com.vendo.product_service.domain.product_image.model.ProductImage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductImageCommandAdapterTest {

    @InjectMocks
    private ProductImageCommandAdapter productImageCommandAdapter;

    @Mock
    private ProductImageRepository repository;
    @Mock
    private ProductImageMapper mapper;

    @Test
    void save_shouldSaveEntity() {
        ProductImage productImage = new ProductImage("key", "image/png", 1, ImageStatus.PENDING);
        ProductImageMongo imageMongo = mock(ProductImageMongo.class);

        when(mapper.toEntity(eq((productImage)))).thenReturn(imageMongo);
        when(repository.save(any())).thenReturn(imageMongo);

        productImageCommandAdapter.save(productImage);

        verify(mapper).toEntity(productImage);
        verify(repository).save(any());
    }

    @Test
    void save_shouldThrowProductImageAlreadyExists_whenDuplicateEntityFound() {
        ProductImage productImage = new ProductImage("key", "image/png", 1, ImageStatus.PENDING);
        ProductImageMongo imageMongo = mock(ProductImageMongo.class);

        when(mapper.toEntity(eq((productImage)))).thenReturn(imageMongo);
        when(repository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThatThrownBy(() -> productImageCommandAdapter.save(productImage))
                .isInstanceOf(ProductImageAlreadyExists.class)
                .hasMessage("Product image already exists by key: %s.".formatted(productImage.key()));

        verify(mapper).toEntity(productImage);
        verify(repository).save(any());
    }
}
