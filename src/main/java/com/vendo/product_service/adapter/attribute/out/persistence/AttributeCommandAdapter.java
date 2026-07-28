package com.vendo.product_service.adapter.attribute.out.persistence;

import com.vendo.product_service.adapter.attribute.out.mapper.MongoAttributeMapper;
import com.vendo.product_service.domain.attribute.exception.AttributeAlreadyExistsException;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.port.attribute.AttributeCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class AttributeCommandAdapter implements AttributeCommandPort {

    private final MongoAttributeMapper mapper;
    private final AttributeRepository repository;

    @Override
    public void save(Attribute attribute) {
        try {
            repository.save(mapper.toEntity(attribute));
        } catch (DuplicateKeyException e) {
            throw new AttributeAlreadyExistsException("Attribute already exists by slug.");
        }
    }

}
