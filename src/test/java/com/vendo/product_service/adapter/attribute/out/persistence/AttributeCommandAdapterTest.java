package com.vendo.product_service.adapter.attribute.out.persistence;

import com.vendo.product_service.adapter.attribute.out.mapper.MongoAttributeMapper;
import com.vendo.product_service.domain.attribute.exception.AttributeAlreadyExistsException;
import com.vendo.product_service.domain.attribute.model.Attribute;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class AttributeCommandAdapterTest {

    @Mock
    private MongoAttributeMapper mapper;
    @Mock
    private AttributeRepository repository;

    @InjectMocks
    private AttributeCommandAdapter attributeCommandAdapter;

    @Test
    void save_shouldMapAndSaveAttribute() {
        Attribute attribute = Attribute.builder().id("cat123").slug("SLUG").title("Title").build();
        MongoAttribute attributeEntity = MongoAttribute.builder()
                .id("cat123")
                .slug("SLUG")
                .title("Title")
                .build();

        when(mapper.toEntity(attribute)).thenReturn(attributeEntity);

        attributeCommandAdapter.save(attribute);

        verify(mapper).toEntity(attribute);
        verify(repository).save(attributeEntity);
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void save_shouldThrowException_whenAttributeAlreadyExistsBySlug() {
        Attribute attribute = Attribute.builder().id("cat123").slug("SLUG").title("Title").build();
        MongoAttribute attributeEntity = MongoAttribute.builder()
                .id("cat123")
                .slug("SLUG")
                .title("Title")
                .build();

        when(mapper.toEntity(attribute)).thenReturn(attributeEntity);
        when(repository.save(attributeEntity)).thenThrow(new DuplicateKeyException("exception message"));

        assertThrows(AttributeAlreadyExistsException.class, () -> attributeCommandAdapter.save(attribute));

        verify(mapper).toEntity(attribute);
        verify(repository).save(attributeEntity);

        verifyNoMoreInteractions(mapper, repository);
    }

}
