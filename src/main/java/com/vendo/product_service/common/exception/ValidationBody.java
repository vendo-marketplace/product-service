package com.vendo.product_service.common.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ValidationBody {

    private boolean valid;

    private String fieldName;

    private String errorMessage;

}
