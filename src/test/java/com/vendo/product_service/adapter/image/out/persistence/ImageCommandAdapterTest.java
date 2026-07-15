package com.vendo.product_service.adapter.image.out.persistence;

import com.vendo.product_service.adapter.image.out.mapper.ProductImageMapper;
import com.vendo.product_service.domain.image.exception.ImageAlreadyExists;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.domain.image.model.ProductImageStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageCommandAdapterTest {

    @InjectMocks
    private ImageCommandAdapter productImageCommandAdapter;

    @Mock
    private ProductImageRepository repository;
    @Mock
    private ProductImageMapper mapper;

    @Test
    void save_shouldSaveEntity() {
        Image image = new Image("key", "image/png", 1, ProductImageStatus.PENDING);
        ProductImageMongo imageMongo = mock(ProductImageMongo.class);

        when(mapper.toEntity(eq((image)))).thenReturn(imageMongo);
        when(repository.save(any())).thenReturn(imageMongo);

        productImageCommandAdapter.save(image);

        verify(mapper).toEntity(image);
        verify(repository).save(any());
    }

    @Test
    void save_shouldThrowProductImageAlreadyExists_whenDuplicateEntityFound() {
        Image image = new Image("key", "image/png", 1, ProductImageStatus.PENDING);
        ProductImageMongo imageMongo = mock(ProductImageMongo.class);

        when(mapper.toEntity(eq((image)))).thenReturn(imageMongo);
        when(repository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThatThrownBy(() -> productImageCommandAdapter.save(image))
                .isInstanceOf(ImageAlreadyExists.class)
                .hasMessage("Product image already exists by key: %s.".formatted(image.key()));

        verify(mapper).toEntity(image);
        verify(repository).save(any());
    }
}
