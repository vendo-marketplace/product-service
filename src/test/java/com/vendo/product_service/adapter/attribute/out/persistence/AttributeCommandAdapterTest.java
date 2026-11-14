package com.vendo.product_service.adapter.attribute.out.persistence;

import com.vendo.product_service.adapter.attribute.out.mapper.MongoAttributeMapper;
import com.vendo.product_service.domain.attribute.exception.AttributeAlreadyExistsException;
import com.vendo.product_service.domain.attribute.exception.AttributeNotFoundException;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.test_utils.builder.AttributeDataBuilder;
import com.vendo.product_service.test_utils.builder.MongoAttributeDataBuilder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class AttributeCommandAdapterTest {

    @Mock
    private MongoAttributeMapper mapper;
    @Mock
    private AttributeRepository repository;

    @InjectMocks
    private AttributeCommandAdapter attributeCommandAdapter;

    @Nested
    class SaveTests {

        @Test
        void save_shouldMapSaveAndReturnSavedAttribute() {
            Attribute attribute = AttributeDataBuilder.withAllFields().id(null).build();
            MongoAttribute entity = MongoAttributeDataBuilder.withAllFields().id(null).build();
            MongoAttribute saved = MongoAttributeDataBuilder.withAllFields().build();
            Attribute expected = AttributeDataBuilder.withAllFields().id(saved.getId()).build();

            when(mapper.toEntity(attribute)).thenReturn(entity);
            when(repository.save(entity)).thenReturn(saved);
            when(mapper.toAttribute(saved)).thenReturn(expected);

            Attribute result = attributeCommandAdapter.save(attribute);

            assertThat(result).isEqualTo(expected);
            verify(mapper).toEntity(attribute);
            verify(repository).save(entity);
            verify(mapper).toAttribute(saved);
            verifyNoMoreInteractions(mapper, repository);
        }

        @Test
        void save_shouldThrowException_whenAttributeAlreadyExistsBySlug() {
            Attribute attribute = AttributeDataBuilder.withAllFields().build();
            MongoAttribute entity = MongoAttributeDataBuilder.withAllFields().build();

            when(mapper.toEntity(attribute)).thenReturn(entity);
            when(repository.save(entity)).thenThrow(new DuplicateKeyException("exception message"));

            assertThatThrownBy(() -> attributeCommandAdapter.save(attribute))
                    .isInstanceOf(AttributeAlreadyExistsException.class)
                    .hasMessage("Attribute already exists by slug.");

            verify(mapper).toEntity(attribute);
            verify(repository).save(entity);
            verifyNoMoreInteractions(mapper, repository);
        }
    }

    @Nested
    class UpdateTests {

        @Test
        void update_shouldUpdateLoadedEntityAndReturnAttribute() {
            Attribute attribute = AttributeDataBuilder.withAllFields().build();
            MongoAttribute entity = MongoAttributeDataBuilder.withAllFields().id(attribute.id()).build();

            when(repository.findById(attribute.id())).thenReturn(Optional.of(entity));
            when(repository.save(entity)).thenReturn(entity);
            when(mapper.toAttribute(entity)).thenReturn(attribute);

            Attribute result = attributeCommandAdapter.update(attribute);

            assertThat(result).isEqualTo(attribute);
            verify(mapper).updateEntity(entity, attribute);
            verify(repository).save(entity);
        }

        @Test
        void update_shouldThrowException_whenAttributeNotFound() {
            Attribute attribute = AttributeDataBuilder.withAllFields().build();

            when(repository.findById(attribute.id())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> attributeCommandAdapter.update(attribute))
                    .isInstanceOf(AttributeNotFoundException.class)
                    .hasMessage("Attribute not found by id: %s.".formatted(attribute.id()));

            verify(repository, never()).save(any());
            verifyNoInteractions(mapper);
        }

        @Test
        void update_shouldThrowException_whenSlugAlreadyTaken() {
            Attribute attribute = AttributeDataBuilder.withAllFields().build();
            MongoAttribute entity = MongoAttributeDataBuilder.withAllFields().id(attribute.id()).build();

            when(repository.findById(attribute.id())).thenReturn(Optional.of(entity));
            when(repository.save(entity)).thenThrow(new DuplicateKeyException("exception message"));

            assertThatThrownBy(() -> attributeCommandAdapter.update(attribute))
                    .isInstanceOf(AttributeAlreadyExistsException.class)
                    .hasMessage("Attribute already exists by slug.");

            verify(mapper).updateEntity(entity, attribute);
            verify(mapper, never()).toAttribute(any());
        }
    }

}
