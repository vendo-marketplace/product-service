package com.vendo.product_service.adapter.attribute.in;

import com.vendo.product_service.adapter.attribute.in.dto.CreateAttributeRequest;
import com.vendo.product_service.adapter.attribute.out.mapper.DtoAttributeMapper;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.port.in.attribute.AttributeUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attributes")
class AttributeController {

    private final AttributeUseCase attributeUseCase;

    private final DtoAttributeMapper mapper;

    @PostMapping
    void save(@Valid @RequestBody CreateAttributeRequest request) {
        Attribute attribute = mapper.toAttribute(request);
        attributeUseCase.save(attribute);
    }

}
