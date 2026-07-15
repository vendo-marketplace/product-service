package com.vendo.product_service.adapter.aws.out;

import com.vendo.product_service.adapter.aws.out.config.AwsFeignConfig;
import com.vendo.product_service.adapter.aws.out.dto.PresignRequest;
import com.vendo.product_service.adapter.aws.out.dto.PresignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
@FeignClient(
        name = "aws-service",
        path = "/internal",
        configuration = AwsFeignConfig.class)
public interface AwsClient {

    @PostMapping("/presigned")
    PresignResponse presign(@RequestBody List<PresignRequest> files);

}
