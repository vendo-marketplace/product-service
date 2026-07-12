package com.vendo.product_service.application.product;

import com.vendo.product_service.application.product.model.ProductComparison;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.product.ProductCompareUseCase;
import com.vendo.product_service.port.attribute.AttributeQueryPort;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductCompareService implements ProductCompareUseCase {

    private final CategoryQueryPort categoryQueryPort;
    private final AttributeQueryPort attributeQueryPort;
    private final ProductQueryPort productQueryPort;

    @Override
    public List<ProductComparison> compare(String categoryId, List<String> productIds) {
        Category category = categoryQueryPort.findById(categoryId);
        List<Product> products = productQueryPort.requireAllByIds(productIds);
        requireProductsInCategory(categoryId, products);

        if (CollectionUtils.isEmpty(category.getAttributes())) return List.of();
        List<Attribute> attributes = attributeQueryPort.findAllByIds(category.getAttributes());

        return attributes.stream()
                .map(attribute -> buildComparison(attribute, products))
                .toList();
    }

    private ProductComparison buildComparison(Attribute attribute, List<Product> products) {
        List<List<String>> values = products.stream()
                .map(product -> getAttributeValues(attribute.id(), product))
                .toList();

        return new ProductComparison(attribute.id(), attribute.title(), isSameAttributeValues(values), values);
    }

    private boolean isSameAttributeValues(List<List<String>> values) {
        return values.stream().distinct().count() <= 1;
    }

    private List<String> getAttributeValues(String attributeId, Product product) {
        if (CollectionUtils.isEmpty(product.getAttributes())) return List.of();

        return product.getAttributes().stream()
                .filter(av -> av.id().equals(attributeId))
                .map(AttributeValue::values)
                .findFirst()
                .orElse(List.of());
    }

    private void requireProductsInCategory(String categoryId, List<Product> products) {
        boolean allBelongToCategory = products.stream().allMatch(product -> categoryId.equals(product.getCategoryId()));
        if (!allBelongToCategory) throw new ProductNotFoundException("Some products do not belong to the specified category.");
    }

}
