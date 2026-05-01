package com.vendo.product_service.application;

import com.vendo.product_service.application.category.CategoryReadService;
import com.vendo.product_service.application.category.model.CategoryNode;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryReadServiceTest {

    @Mock
    private CategoryQueryPort categoryQueryPort;

    @InjectMocks
    private CategoryReadService categoryReadService;


    private Category build(String id, String parentId) {
        return Category.builder()
                .id(id)
                .title("title-" + id)
                .code("code-" + id)
                .parentId(parentId)
                .attributes(null)
                .build();
    }

    @Test
    void shouldBuildTree() {

        when(categoryQueryPort.findAll()).thenReturn(List.of(
                build("1", null),
                build("2", "1"),
                build("3", "1")
        ));

        List<CategoryNode> result = categoryReadService.getTree();

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals(2, result.get(0).getChildren().size());
    }

    @Test
    void shouldIgnoreOrphanNodes() {

        when(categoryQueryPort.findAll()).thenReturn(List.of(
                build("2", "999")
        ));

        List<CategoryNode> result = categoryReadService.getTree();

        assertEquals(0, result.size());
    }

    @Test
    void shouldReturnMultipleRoots() {

        when(categoryQueryPort.findAll()).thenReturn(List.of(
                build("1", null),
                build("2", null),
                build("3", "1")
        ));

        List<CategoryNode> result = categoryReadService.getTree();

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnSingleBreadcrumbForRoot() {

        when(categoryQueryPort.findAll()).thenReturn(List.of(
                build("1", null)
        ));

        List<CategoryNode> result = categoryReadService.getBreadcrumbs("1");

        assertEquals(1, result.size());
    }

    @Test
    void shouldBuildBreadcrumbs() {

        when(categoryQueryPort.findAll()).thenReturn(List.of(
                build("1", null),
                build("2", "1"),
                build("3", "2")
        ));

        List<CategoryNode> result = categoryReadService.getBreadcrumbs("3");

        assertEquals(List.of("1", "2", "3"),
                result.stream().map(CategoryNode::getId).toList());
    }

    @Test
    void shouldReturnChildren() {

        when(categoryQueryPort.findByParentId("1")).thenReturn(List.of(
                build("2", "1")
        ));

        List<CategoryNode> result = categoryReadService.getChildren("1");

        assertEquals(1, result.size());
        assertEquals("2", result.get(0).getId());
    }

    @Test
    void shouldFilterByType() {

        when(categoryQueryPort.findAll()).thenReturn(List.of(
                build("1", null),
                build("2", "1")
        ));

        List<CategoryNode> result = categoryReadService.getByType(CategoryType.PARENT);

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
    }
}