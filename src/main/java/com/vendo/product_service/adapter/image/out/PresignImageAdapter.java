package com.vendo.product_service.adapter.image.out;

import com.vendo.product_service.adapter.aws.out.AwsClient;
import com.vendo.product_service.adapter.aws.out.dto.PresignRequest;
import com.vendo.product_service.adapter.aws.out.dto.PresignResponse;
import com.vendo.product_service.adapter.aws.out.dto.PresignType;
import com.vendo.product_service.adapter.image.out.mapper.PresignMapper;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.domain.image.model.PresignedImage;
import com.vendo.product_service.port.image.PresignImagePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PresignImageAdapter implements PresignImagePort {

    private final PresignMapper mapper;
    private final AwsClient awsClient;

    @Override
    public List<PresignedImage> generate(List<Image> images) {
        List<PresignRequest.PresignBody> presignBodies = mapper.toPresignBodies(images);
        PresignResponse response = awsClient.presign(new PresignRequest(PresignType.PRODUCT, presignBodies));
        return response.data();
    }

}
