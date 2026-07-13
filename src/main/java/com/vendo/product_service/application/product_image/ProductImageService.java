package com.vendo.product_service.application.product_image;

import com.vendo.product_service.domain.product_image.exception.ProductImageAlreadyConfirmedException;
import com.vendo.product_service.domain.product_image.model.ProductImageStatus;
import com.vendo.product_service.domain.product_image.model.ProductImage;
import com.vendo.product_service.port.product_image.ProductImageQueryPort;
import com.vendo.product_service.port.product_image.ProductImageUseCase;
import com.vendo.product_service.port.product_image.ProductImageCommandPort;
import com.vendo.product_service.port.product_image.ProductImageValidationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageService implements ProductImageUseCase {

    private final ProductImageCommandPort productImageCommandPort;
    private final ProductImageQueryPort productImageQueryPort;
    private final ProductImageValidationPort productImageValidationPort;

    @Override
    public void save(ProductImage productImage) {
        productImageCommandPort.save(productImage.withStatus(ProductImageStatus.PENDING));
    }

    @Override
    public void confirm(List<String> keys) {
        List<ProductImage> productImages = productImageQueryPort.findAllBy(keys);
        validateNotConfirmedImages(productImages);

        productImageValidationPort.validate(productImages);

        productImageCommandPort.updateAllBy(productImages.stream().map(ProductImage::key).toList(), ProductImageStatus.CONFIRMED);
    }

    private void validateNotConfirmedImages(List<ProductImage> productImages) {
        for (ProductImage productImage : productImages) {
            if (productImage.status() == ProductImageStatus.CONFIRMED) {
                throw new ProductImageAlreadyConfirmedException("Product image %s already confirmed.".formatted(productImage.key()));
            }
        }
    }

}
