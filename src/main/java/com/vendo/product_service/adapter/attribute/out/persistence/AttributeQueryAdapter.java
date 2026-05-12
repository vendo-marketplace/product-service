package com.vendo.product_service.adapter.attribute.out.persistence;

import com.vendo.product_service.adapter.attribute.out.mapper.MongoAttributeMapper;
import com.vendo.product_service.domain.attribute.exception.AttributeNotFoundException;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.port.out.attribute.AttributeQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AttributeQueryAdapter implements AttributeQueryPort {

    private final MongoAttributeMapper mapper;

    private final AttributeRepository attributeRepository;

    @Override
    public Attribute findById(String id) {
        MongoAttribute entity = attributeRepository.findById(id)
                .orElseThrow(() -> new AttributeNotFoundException("Attribute not found by id: %s.".formatted(id)));
        return mapper.toAttribute(entity);
    }

    @Override
    public List<Attribute> findAllByIdsOrThrow(List<String> ids) {
        return ids.stream().map(this::findById).toList();
    }

    @Override
    public List<Attribute> findAllByIds(List<String> ids) {
        return attributeRepository.findAllById(ids).stream()
                .map(mapper::toAttribute)
                .toList();
    }

}
