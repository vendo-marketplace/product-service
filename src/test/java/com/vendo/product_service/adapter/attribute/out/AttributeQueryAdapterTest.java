package com.vendo.product_service.adapter.attribute.out;

import com.vendo.product_service.adapter.attribute.out.mapper.MongoAttributeMapper;
import com.vendo.product_service.adapter.attribute.out.persistence.AttributeQueryAdapter;
import com.vendo.product_service.adapter.attribute.out.persistence.AttributeRepository;
import com.vendo.product_service.adapter.attribute.out.persistence.MongoAttribute;
import com.vendo.product_service.domain.attribute.exception.AttributeNotFoundException;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.test_utils.builder.MongoAttributeDataBuilder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class AttributeQueryAdapterTest {

    @Mock
    private MongoAttributeMapper mapper;
    @Mock
    private AttributeRepository repository;

    @InjectMocks
    private AttributeQueryAdapter adapter;

    @Nested
    class FindByIdTests {

        @Test
        void findById_shouldReturnAttribute() {
            MongoAttribute mongoAttribute = MongoAttributeDataBuilder.withAllFields().build();

            when(repository.findById(mongoAttribute.getId())).thenReturn(Optional.of(mongoAttribute));
            when(mapper.toAttribute(any(MongoAttribute.class))).thenReturn(any(Attribute.class));

            adapter.findById(mongoAttribute.getId());

            verify(repository).findById(mongoAttribute.getId());
            verify(mapper).toAttribute(any());
        }

        @Test
        void findById_shouldThrowException_whenNotFound() {
            MongoAttribute mongoAttribute = MongoAttributeDataBuilder.withAllFields().build();
            String exception = "Attribute not found by id: %s.".formatted(mongoAttribute.getId());

            when(repository.findById(mongoAttribute.getId())).thenThrow(new AttributeNotFoundException(exception));

            assertThatThrownBy(() -> adapter.findById(mongoAttribute.getId())).isInstanceOf(AttributeNotFoundException.class).hasMessage(exception);

            verify(repository).findById(mongoAttribute.getId());
            verifyNoInteractions(mapper);
        }
    }

    @Nested
    class FindAllByIds {

        @Test
        void findAllByIds_shouldReturnAttributes() {
            MongoAttribute mongoAttribute = MongoAttributeDataBuilder.withAllFields().build();

            when(repository.findAllByIdIsIn(List.of(mongoAttribute.getId()))).thenReturn(List.of(mongoAttribute));
            when(mapper.toAttributes(anyList())).thenReturn(anyList());

            adapter.findAllByIds(List.of(mongoAttribute.getId()));

            verify(repository).findAllByIdIsIn(List.of(mongoAttribute.getId()));
            verify(mapper).toAttributes(anyList());
        }

        @Test
        void findAllByIds_shouldThrowException_whenAtLeastOneNotFound() {
            MongoAttribute mongoAttribute = MongoAttributeDataBuilder.withAllFields().build();
            MongoAttribute mongoAttribute1 = MongoAttributeDataBuilder.withAllFields().build();

            when(repository.findAllByIdIsIn(List.of(mongoAttribute.getId(), mongoAttribute1.getId()))).thenReturn(List.of(mongoAttribute));

            assertThatThrownBy(() -> adapter.findAllByIds(List.of(mongoAttribute.getId(), mongoAttribute1.getId()))).isInstanceOf(AttributeNotFoundException.class).hasMessage("Attribute not found by id: %s.".formatted(mongoAttribute1.getId()));

            verify(repository).findAllByIdIsIn(List.of(mongoAttribute.getId(), mongoAttribute1.getId()));
            verify(mapper, never()).toAttributes(anyList());
        }
    }
}
