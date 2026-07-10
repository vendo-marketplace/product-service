package com.vendo.product_service.application.product_image;

import com.vendo.product_service.domain.product_image.model.ImageStatus;
import com.vendo.product_service.domain.product_image.model.ProductImage;
import com.vendo.product_service.port.product_image.ProductImageUseCase;
import com.vendo.product_service.port.product_image.ProductImageCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductImageService implements ProductImageUseCase {

    private final ProductImageCommandPort productImageCommandPort;

    @Override
    public void save(ProductImage productImage) {
        productImageCommandPort.save(productImage.withStatus(ImageStatus.PENDING));
    }

}
