package com.vendo.product_service.adapter.attribute.out.persistence;

import com.vendo.product_service.adapter.attribute.out.mapper.MongoAttributeMapper;
import com.vendo.product_service.domain.attribute.exception.AttributeNotFoundException;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.port.attribute.AttributeQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class AttributeQueryAdapter implements AttributeQueryPort {

    private final MongoAttributeMapper mapper;
    private final AttributeRepository attributeRepository;

    @Override
    public Attribute findById(String id) {
        MongoAttribute entity = attributeRepository.findById(id)
                .orElseThrow(() -> new AttributeNotFoundException("Attribute not found by id: %s.".formatted(id)));
        return mapper.toAttribute(entity);
    }

    @Override
    public List<Attribute> findAllByIds(List<String> ids) {
        List<MongoAttribute> attributes = attributeRepository.findAllByIdIsIn(ids);
        throwIfAttributeNotFound(ids, attributes);
        return mapper.toAttributes(attributes);
    }

    private void throwIfAttributeNotFound(List<String> attributeIds, List<MongoAttribute> attributes) {
        if (attributeIds.size() == attributes.size()) return;

        Set<String> foundIds = attributes.stream().map(MongoAttribute::getId).collect(Collectors.toSet());
        for (String attributeId : attributeIds) {
            if (!foundIds.contains(attributeId)) {
                throw new AttributeNotFoundException("Attribute not found by id: %s.".formatted(attributeId));
            }
        }
    }

}
