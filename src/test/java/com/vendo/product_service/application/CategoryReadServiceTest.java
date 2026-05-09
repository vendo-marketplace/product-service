package com.vendo.product_service.application;

import com.vendo.product_service.application.category.CategoryReadService;
import com.vendo.product_service.application.category.model.CategoryView;
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

    }
}