package com.vendo.product_service.adapter.attribute.out.persistence;

import com.vendo.product_service.adapter.attribute.out.mapper.MongoAttributeMapper;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.port.out.attribute.AttributeCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttributeCommandAdapter implements AttributeCommandPort {

    private final MongoAttributeMapper mapper;
    private final AttributeRepository repository;

    @Override
    public void save(Attribute attribute) {
        MongoAttribute entity = mapper.toEntity(attribute);
        repository.save(entity);
    }

}
