package com.vendo.product_service.adapter.product_image.out;

import com.vendo.product_service.domain.product_image.model.ProductImage;
import com.vendo.product_service.port.product_image.ProductImageValidationPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class ProductImageValidationAdapter implements ProductImageValidationPort {

    @Override
    public void validate(List<ProductImage> productImages) {

    }

}
