package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.model.ImageBody;

import java.util.List;
import java.util.UUID;

public class CategoryDataBuilder {

    public static Category.CategoryBuilder withChild() {
        return Category.builder()
                .id(String.valueOf(UUID.randomUUID()))
                .title("Category")
                .parentId(String.valueOf(UUID.randomUUID()))
                .slug("slug")
                .path(List.of("id1", "id2", "id3"))
                .attributes(List.of("id_1"));
    }

    public static Category.CategoryBuilder withSub() {
        String id = String.valueOf(UUID.randomUUID()), parentId = String.valueOf(UUID.randomUUID());

        return Category.builder()
                .id(id)
                .title("Category")
                .parentId(parentId)
                .slug("slug")
                .path(List.of(parentId, id));
    }

    public static Category.CategoryBuilder withParent() {
        String id = String.valueOf(UUID.randomUUID());

        return Category.builder()
                .id(id)
                .title("Category")
                .image(new ImageBody("key", "key-url"))
                .slug("slug")
                .path(List.of(id));
    }

}
