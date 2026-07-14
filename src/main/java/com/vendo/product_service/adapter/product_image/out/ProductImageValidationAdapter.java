package com.vendo.product_service.adapter.product_image.out;

import com.vendo.product_service.adapter.aws.out.AwsClient;
import com.vendo.product_service.adapter.aws.out.dto.ImageValidationRequest;
import com.vendo.product_service.adapter.aws.out.mapper.ImageAwsMapper;
import com.vendo.product_service.domain.product_image.model.ProductImage;
import com.vendo.product_service.port.product_image.ProductImageValidationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class ProductImageValidationAdapter implements ProductImageValidationPort {

    private final AwsClient client;

    private final ImageAwsMapper mapper;

    @Override
    public void validate(List<ProductImage> productImages) {
        ImageValidationRequest request = new ImageValidationRequest(mapper.toRequest(productImages));
        client.validate(request);
    }

}
