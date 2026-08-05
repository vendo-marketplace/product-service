package com.vendo.product_service.application.category;

import com.vendo.core_lib.utils.StringUtils;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryImageType;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.domain.image.model.PresignImage;
import com.vendo.product_service.port.category.usecase.CategoryCommandUseCase;
import com.vendo.product_service.port.category.TypeValidationPort;
import com.vendo.product_service.port.IdGenerationPort;
import com.vendo.product_service.port.category.CategoryCommandPort;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.port.image.ImageEventSenderPort;
import com.vendo.product_service.port.image.ImagePresignPort;
import com.vendo.product_service.port.image.ImageUploadPort;
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

    @Override
    @CacheEvict(value = "category-tree", allEntries = true)
    public void update(String id, Category category) {
        throwIfNotExistsBy(id);

        typeValidationPort.validate(category);
        updatePathIfPresent(category);

        categoryCommandPort.update(id, category);
    }

    @Override
    public void uploadImage(String id, CategoryImageType type, Image image) {
        Category category = categoryQueryPort.findById(id);
        String key = imageUseCase.upload(image);


    }

    @Override
    public void removeImage(String id, CategoryImageType type) {
        Category category = categoryQueryPort.findById(id);
        sendImageDeletionEvent(type, category);
        categoryCommandPort.removeImage(id, type);
    }

    private void sendImageDeletionEvent(CategoryImageType type, Category category) {
        if (type == CategoryImageType.ICON) {
            imageEventSenderPort.delete(category.getIcon().key());
            return;
        }

        imageEventSenderPort.delete(category.getPreview().key());
    }

    private void updatePathIfPresent(Category category) {
        if (!StringUtils.isEmpty(category.getParentId())) {
            category.setPath(category.buildPath(getParentPath(category)));
        }
    }

    private void throwIfNotExistsBy(String id) {
        if (!categoryQueryPort.existsById(id)) {
            throw new CategoryNotFoundException("Category not found.");
        }
    }

    private List<String> getParentPath(Category category) {
        if (StringUtils.isEmpty(category.getParentId())) {
            return Collections.emptyList();
        }

        Category parent = categoryQueryPort.findById(category.getParentId(), "Parent category not found.");
        return parent.getPath();
    }
}