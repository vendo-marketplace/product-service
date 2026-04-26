package com.vendo.product_service.adapter.attribute.in;

import com.vendo.product_service.adapter.attribute.in.dto.CreateAttributeRequest;
import com.vendo.product_service.adapter.attribute.out.mapper.AttributeMapper;
import com.vendo.product_service.application.attribute.AttributeService;
import com.vendo.product_service.domain.attribute.model.Attribute;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attributes")
public class AttributeController {

    private final AttributeService attributeService;
    private final AttributeMapper attributeMapper;

    @PostMapping
    public void save(@Valid @RequestBody CreateAttributeRequest request) {
        Attribute attribute = attributeMapper.toAttribute(request);
        attributeService.save(attribute);
    }

}
