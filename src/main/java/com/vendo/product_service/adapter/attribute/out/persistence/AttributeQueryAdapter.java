package com.vendo.product_service.adapter.attribute.out.persistence;

import com.vendo.core_lib.utils.ClassFields;
import com.vendo.core_lib.utils.ObjectUtils;
import com.vendo.core_lib.utils.StringUtils;
import com.vendo.product_service.adapter.attribute.out.mapper.MongoAttributeMapper;
import com.vendo.product_service.domain.attribute.exception.AttributeNotFoundException;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeFilter;
import com.vendo.product_service.port.attribute.AttributeQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class AttributeQueryAdapter implements AttributeQueryPort {

    private static final String REGISTRY_DISABLED_OPTION = "i";

    private final MongoAttributeMapper mapper;
    private final MongoTemplate mongoTemplate;
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

    @Override
    public List<Attribute> findAll(AttributeFilter filter) {
        Query query = new Query().with(Sort.by(Sort.Direction.ASC, Attribute.TITLE_FIELD));

        if (!StringUtils.isEmpty(filter.search())) {
            withSearch(filter, query);
        }

        if (ObjectUtils.isNotNull(filter.type())) {
            withType(filter, query);
        }

        return mapper.toAttributes(mongoTemplate.find(query, MongoAttribute.class));
    }

    private void withSearch(AttributeFilter filter, Query query) {
        String regex = Pattern.quote(filter.search().trim());
        query.addCriteria(new Criteria().orOperator(
                Criteria.where(ClassFields.nameOf(Attribute.TITLE_FIELD, Attribute.class)).regex(regex, REGISTRY_DISABLED_OPTION),
                Criteria.where(ClassFields.nameOf(Attribute.SLUG_FIELD, Attribute.class)).regex(regex, REGISTRY_DISABLED_OPTION))
        );
    }

    private void withType(AttributeFilter filter, Query query) {
        query.addCriteria(Criteria.where(ClassFields.nameOf(Attribute.TYPE_FIELD, Attribute.class)).is(filter.type().name()));
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
