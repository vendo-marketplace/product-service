package com.vendo.product_service.adapter.attribute.out.persistence;

import com.vendo.product_service.adapter.attribute.out.mapper.MongoAttributeMapper;
import com.vendo.product_service.domain.attribute.exception.AttributeAlreadyExistsException;
import com.vendo.product_service.domain.attribute.exception.AttributeNotFoundException;
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
    public Attribute save(Attribute attribute) {
        return persist(mapper.toEntity(attribute));
    }

    @Override
    public Attribute update(Attribute attribute) {
        MongoAttribute entity = findOrThrow(attribute.id());
        mapper.updateEntity(entity, attribute);
        return persist(entity);
    }

    private MongoAttribute findOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new AttributeNotFoundException("Attribute not found by id: %s.".formatted(id)));
    }

    private Attribute persist(MongoAttribute entity) {
        try {
            return mapper.toAttribute(repository.save(entity));
        } catch (DuplicateKeyException e) {
            throw new AttributeAlreadyExistsException("Attribute already exists by slug.");
        }
    }

}
