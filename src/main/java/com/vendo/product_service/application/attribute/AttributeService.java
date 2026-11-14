package com.vendo.product_service.application.attribute;

import com.vendo.product_service.application.attribute.validation.AttributeDefinitionValidationFactory;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeFilter;
import com.vendo.product_service.port.attribute.AttributeCommandPort;
import com.vendo.product_service.port.attribute.AttributeQueryPort;
import com.vendo.product_service.port.attribute.AttributeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class AttributeService implements AttributeUseCase {

    private final AttributeDefinitionValidationFactory attributeValidationFactory;

    private final AttributeCommandPort attributeCommandPort;
    private final AttributeQueryPort attributeQueryPort;

    @Override
    public Attribute save(Attribute attribute) {
        validateDefinition(attribute);
        return attributeCommandPort.save(attribute);
    }

    @Override
    @CacheEvict(value = {"category-tree", "category-attributes"}, allEntries = true)
    public Attribute update(String id, Attribute attribute) {
        Attribute existing = attributeQueryPort.findById(id);
        Attribute updated = existing.withChanges(attribute);

        validateDefinition(updated);

        return attributeCommandPort.update(updated);
    }

    @Override
    public List<Attribute> findAll(AttributeFilter filter) {
        return attributeQueryPort.findAll(filter);
    }

    private void validateDefinition(Attribute attribute) {
        attributeValidationFactory.findValidator(attribute.type()).ifPresent(validator -> validator.validate(attribute));
    }

}
