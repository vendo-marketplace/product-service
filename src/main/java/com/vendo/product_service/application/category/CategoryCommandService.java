package com.vendo.product_service.application.category;

import com.vendo.core_lib.utils.CollectionUtils;
import com.vendo.core_lib.utils.ObjectUtils;
import com.vendo.product_service.domain.image.model.PresignType;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.model.ImageBody;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.port.attribute.AttributeQueryPort;
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

import java.util.List;

@Component
@RequiredArgsConstructor
class CategoryCommandService implements CategoryCommandUseCase {

    private final TypeValidationPort typeValidationPort;
    private final IdGenerationPort idGenerationPort;

    private final AttributeQueryPort attributeQueryPort;

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

        categoryCommandPort.save(category);
    }

    @CacheEvict(value = "category-tree", allEntries = true)
    public void update(String id, Category category) {
        validateAttributes(category.getAttributes());
        categoryCommandPort.update(id, category);
    }

    @Override
    @CacheEvict(value = "category-tree", allEntries = true)
    public void uploadImage(String id, Image image) {
        Category category = categoryQueryPort.findById(id);
        String key = imageUseCase.upload(PresignType.CATEGORY, image);

        Category updateCategory = Category.builder().image(new ImageBody(key, baseUrl.concat(key))).build();
        categoryCommandPort.update(id, updateCategory);

        if (ObjectUtils.isNotNull(category.getImage())) imageEventSenderPort.delete(category.getImage().key());
    }

    @Override
    @CacheEvict(value = "category-tree", allEntries = true)
    public void removeImage(String id) {
        Category category = categoryQueryPort.findById(id);
        category.throwIfHasNoImage();

        imageEventSenderPort.delete(category.getImage().key());
        categoryCommandPort.removeImage(id);
    }

    private void validateAttributes(List<String> attributes) {
        if (CollectionUtils.isEmpty(attributes)) return;
        attributeQueryPort.findAllByIds(attributes);
    }
}