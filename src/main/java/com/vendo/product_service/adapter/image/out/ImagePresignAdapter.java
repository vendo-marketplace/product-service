package com.vendo.product_service.adapter.image.out;

import com.vendo.product_service.adapter.image.out.aws.AwsClient;
import com.vendo.product_service.adapter.image.out.aws.dto.PresignRequest;
import com.vendo.product_service.adapter.image.out.aws.dto.PresignResponse;
import com.vendo.product_service.adapter.image.out.aws.dto.nested.PresignBody;
import com.vendo.product_service.adapter.image.out.aws.dto.nested.PresignType;
import com.vendo.product_service.adapter.image.out.mapper.PresignMapper;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.domain.image.model.PresignImage;
import com.vendo.product_service.port.image.ImagePresignPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ImagePresignAdapter implements ImagePresignPort {

    private final PresignMapper mapper;
    private final AwsClient awsClient;

    @Override
    public List<PresignImage> generate(List<Image> images) {
        List<PresignBody> presignBodies = mapper.toPresignBodies(images);
        PresignResponse response = awsClient.presign(new PresignRequest(PresignType.PRODUCT, presignBodies));
        return response.data();
    }

}
