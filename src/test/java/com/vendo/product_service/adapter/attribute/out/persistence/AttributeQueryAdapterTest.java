package com.vendo.product_service.adapter.attribute.out.persistence;

import com.vendo.product_service.adapter.attribute.out.mapper.MongoAttributeMapper;
import com.vendo.product_service.domain.attribute.exception.AttributeNotFoundException;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeFilter;
import com.vendo.product_service.domain.attribute.model.AttributeType;
import com.vendo.product_service.test_utils.builder.AttributeDataBuilder;
import com.vendo.product_service.test_utils.builder.MongoAttributeDataBuilder;
import org.bson.Document;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class AttributeQueryAdapterTest {

    @Mock
    private MongoAttributeMapper mapper;
    @Mock
    private AttributeRepository repository;
    @Mock
    private MongoTemplate mongoTemplate;

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

    @Nested
    class FindAllTests {

        private Query captureQuery(AttributeFilter filter) {
            ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);

            when(mongoTemplate.find(captor.capture(), eq(MongoAttribute.class))).thenReturn(List.of());
            when(mapper.toAttributes(List.of())).thenReturn(List.of());

            adapter.findAll(filter);

            return captor.getValue();
        }

        private List<Pattern> extractSearchPatterns(Query query, String... fields) {
            List<?> or = query.getQueryObject().getList("$or", Object.class);
            assertThat(or).hasSize(fields.length);

            List<Pattern> patterns = new ArrayList<>();
            for (int i = 0; i < fields.length; i++) {
                Document criteria = (Document) or.get(i);
                assertThat(criteria.keySet()).containsExactly(fields[i]);
                patterns.add((Pattern) criteria.get(fields[i]));
            }
            return patterns;
        }

        @Test
        void findAll_shouldReturnMappedAttributes_sortedByTitle_whenFilterIsEmpty() {
            MongoAttribute entity = MongoAttributeDataBuilder.withAllFields().build();
            Attribute attribute = AttributeDataBuilder.withAllFields().build();
            ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);

            when(mongoTemplate.find(captor.capture(), eq(MongoAttribute.class))).thenReturn(List.of(entity));
            when(mapper.toAttributes(List.of(entity))).thenReturn(List.of(attribute));

            List<Attribute> result = adapter.findAll(new AttributeFilter(null, null));

            assertThat(result).containsExactly(attribute);
            assertThat(captor.getValue().getQueryObject()).isEmpty();
            assertThat(captor.getValue().getSortObject()).isEqualTo(new Document("title", 1));
        }

        @Test
        void findAll_shouldNotAddSearchCriteria_whenSearchIsEmptyString() {
            Query query = captureQuery(new AttributeFilter("", null));

            assertThat(query.getQueryObject()).isEmpty();
        }

        @Test
        void findAll_shouldSearchByTitleOrSlug_whenOnlySearchIsPresent() {
            Query query = captureQuery(new AttributeFilter("color", null));

            assertThat(query.getQueryObject().keySet()).containsExactly("$or");
            List<Pattern> patterns = extractSearchPatterns(query, "title", "slug");
            assertThat(patterns).allSatisfy(pattern -> assertThat(pattern.pattern()).isEqualTo(Pattern.quote("color")));
        }

        @Test
        void findAll_shouldSearchCaseInsensitive() {
            Query query = captureQuery(new AttributeFilter("color", null));

            List<Pattern> patterns = extractSearchPatterns(query, "title", "slug");
            assertThat(patterns).allSatisfy(pattern -> {
                assertThat(pattern.flags() & Pattern.CASE_INSENSITIVE).isNotZero();
                assertThat(pattern.matcher("Main COLOR").find()).isTrue();
            });
        }

        @Test
        void findAll_shouldTrimSearch() {
            Query query = captureQuery(new AttributeFilter("  color  ", null));

            List<Pattern> patterns = extractSearchPatterns(query, "title", "slug");
            assertThat(patterns).allSatisfy(pattern -> assertThat(pattern.pattern()).isEqualTo(Pattern.quote("color")));
        }

        @Test
        void findAll_shouldEscapeRegexSpecialCharacters_whenSearchContainsThem() {
            Query query = captureQuery(new AttributeFilter("co.lor(", null));

            List<Pattern> patterns = extractSearchPatterns(query, "title", "slug");
            assertThat(patterns).allSatisfy(pattern -> {
                assertThat(pattern.matcher("Co.lor(s)").find()).isTrue();
                assertThat(pattern.matcher("coXlor(").find()).isFalse();
            });
        }

        @Test
        void findAll_shouldFilterByTypeName_whenOnlyTypeIsPresent() {
            Query query = captureQuery(new AttributeFilter(null, AttributeType.SELECT));

            assertThat(query.getQueryObject()).isEqualTo(new Document("type", "SELECT"));
        }

        @Test
        void findAll_shouldCombineSearchAndType_whenBothArePresent() {
            Query query = captureQuery(new AttributeFilter("color", AttributeType.ENUM));

            assertThat(query.getQueryObject().keySet()).containsExactlyInAnyOrder("$or", "type");
            assertThat(query.getQueryObject().get("type")).isEqualTo("ENUM");
            extractSearchPatterns(query, "title", "slug");
        }
    }
}
