package com.vendo.product_service.application.product_image;

import com.vendo.core_lib.utils.AssertionUtils;
import com.vendo.product_service.domain.product_image.model.ImageStatus;
import com.vendo.product_service.domain.product_image.model.ProductImage;
import com.vendo.product_service.port.product_image.ProductImageCommandPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductImageServiceTest {

    @InjectMocks
    private ProductImageService productImageService;

    @Mock
    private ProductImageCommandPort productImageCommandPort;

    @Test
    void save_shouldSaveProductImage_withPendingStatus() {
        ProductImage productImage = new ProductImage("key", "image/png", 1, null);
        ArgumentCaptor<ProductImage> captor = ArgumentCaptor.forClass(ProductImage.class);

        doNothing().when(productImageCommandPort).save(captor.capture());

        productImageService.save(productImage);

        ProductImage captorValue = captor.getValue();
        assertThat(captorValue).isNotNull();
        assertThat(captorValue.status()).isEqualTo(ImageStatus.PENDING);
        AssertionUtils.assertFrom(productImage, captorValue, "status");

        verify(productImageCommandPort).save(captorValue);
    }

}
