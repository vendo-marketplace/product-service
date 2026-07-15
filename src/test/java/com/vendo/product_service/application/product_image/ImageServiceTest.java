package com.vendo.product_service.application.product_image;

import com.vendo.core_lib.utils.AssertionUtils;
import com.vendo.product_service.application.image.ImageService;
import com.vendo.product_service.domain.image.model.ProductImageStatus;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.port.image.ImageCommandPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private ImageCommandPort imageCommandPort;

    @Test
    void save_shouldSaveProductImage_withPendingStatus() {
        Image image = new Image("key", "image/png", 1, null);
        ArgumentCaptor<Image> captor = ArgumentCaptor.forClass(Image.class);

        doNothing().when(imageCommandPort).save(captor.capture());

        imageService.save(image);

        Image captorValue = captor.getValue();
        assertThat(captorValue).isNotNull();
        assertThat(captorValue.status()).isEqualTo(ProductImageStatus.PENDING);
        AssertionUtils.assertFrom(image, captorValue, "status");

        verify(imageCommandPort).save(captorValue);
    }

}
