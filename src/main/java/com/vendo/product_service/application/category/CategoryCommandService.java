package com.vendo.product_service.application.category;

import com.vendo.core_lib.utils.StringUtils;
import com.vendo.product_service.domain.image.model.PresignType;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.model.ImageBody;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.port.category.usecase.CategoryCommandUseCase;
import com.vendo.product_service.port.category.TypeValidationPort;
import com.vendo.product_service.port.IdGenerationPort;
import com.vendo.product_service.port.category.CategoryCommandPort;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.port.image.ImageEventSenderPort;
import com.vendo.product_service.port.image.usecase.ImageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
class CategoryCommandService implements CategoryCommandUseCase {

    private final TypeValidationPort typeValidationPort;
    private final IdGenerationPort idGenerationPort;

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryQueryPort categoryQueryPort;

    private final String baseUrl;
    private final ImageEventSenderPort imageEventSenderPort;
    private final ImageUseCase imageUseCase;

    @Override
    @CacheEvict(value = "category-tree", allEntries = true)
    public void save(Category category) {
        typeValidationPort.validate(category);

        category.setId(idGenerationPort.generate());
        category.setPath(category.buildPath(getParentPath(category)));

        categoryCommandPort.save(category);
    }

    @CacheEvict(value = "category-tree", allEntries = true)
    public void update(String id, Category request) {
        categoryCommandPort.update(id, request);
    }

    @Override
    @CacheEvict(value = "category-tree", allEntries = true)
    public void uploadImage(String id, Image image) {
        Category category = categoryQueryPort.findById(id);
        String key = imageUseCase.upload(PresignType.CATEGORY, image);

        Category updateCategory = Category.builder().image(new ImageBody(key, baseUrl.concat(key))).build();
        categoryCommandPort.update(id, updateCategory);

        if (category.getImage() != null) imageEventSenderPort.delete(category.getImage().key());;
    }

    @Override
    @CacheEvict(value = "category-tree", allEntries = true)
    public void removeImage(String id) {
        Category category = categoryQueryPort.findById(id);
        category.throwIfHasNoImage();

        imageEventSenderPort.delete(category.getImage().key());
        categoryCommandPort.removeImage(id);
    }

    private List<String> getParentPath(Category category) {
        if (StringUtils.isEmpty(category.getParentId())) {
            return Collections.emptyList();
        }

        return categoryQueryPort.findById(category.getParentId(), "Parent category not found.").getPath();
    }
}