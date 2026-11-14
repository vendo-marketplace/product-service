package com.vendo.product_service.application.attribute;

import com.vendo.product_service.application.attribute.validation.AttributeDefinitionValidationFactory;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.port.attribute.AttributeUseCase;
import com.vendo.product_service.port.attribute.AttributeCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class AttributeService implements AttributeUseCase {

    private final AttributeCommandPort attributeCommandPort;
    private final AttributeDefinitionValidationFactory attributeValidationFactory;

    @Override
    public void save(Attribute attribute) {
        attributeValidationFactory.findValidator(attribute.type()).ifPresent(validator -> validator.validate(attribute));
        attributeCommandPort.save(attribute);
    }

}
