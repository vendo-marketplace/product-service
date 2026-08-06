package com.vendo.product_service.application.product;

import com.vendo.core_lib.utils.CollectionUtils;
import com.vendo.product_service.adapter.image.out.aws.dto.nested.PresignType;
import com.vendo.product_service.domain.image.exception.ImagesLimitExceededException;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.image.ImageEventSenderPort;
import com.vendo.product_service.port.image.usecase.ImageUseCase;
import com.vendo.product_service.port.product.ProductCommandPort;
import com.vendo.product_service.port.product.ProductEventSenderPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.port.product.usecase.ProductImageUseCase;
import com.vendo.product_service.port.user.AuthUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class ProductImageService implements ProductImageUseCase {

    private final int imagesMaxLimit;

    private final ProductCommandPort productCommandPort;
    private final ProductQueryPort productQueryPort;
    private final ProductEventSenderPort productEventSenderPort;

    private final ImageEventSenderPort imageEventSenderPort;
    private final ImageUseCase imageUseCase;

    private final AuthUserPort authUserPort;

    @Override
    public void upload(String productId, List<Image> images) {
        Product product = productQueryPort.findById(productId);

        User authUser = authUserPort.getAuthUser();
        authUser.throwIfNotOwner(product.getOwnerId());

        validateImagesLimit(product, images);

        List<String> keys = imageUseCase.upload(PresignType.PRODUCT, images);
        Product updateProduct = Product.builder().id(productId).imageKeys(product.mergeImageKeys(keys)).build();

        productCommandPort.update(productId, updateProduct);
        productEventSenderPort.sendUpdated(updateProduct);
    }

    @Override
    public void delete(String productId, String imageKey) {
        Product product = productQueryPort.findById(productId);

        User authUser = authUserPort.getAuthUser();
        authUser.throwIfNotOwner(product.getOwnerId());

        product.throwIfImageKeysNotContain(imageKey);

        Product updateProduct = Product.builder().id(productId).imageKeys(product.filterImageKeys(imageKey)).build();
        productCommandPort.update(productId, updateProduct);

        imageEventSenderPort.delete(imageKey);
        productEventSenderPort.sendUpdated(updateProduct);
    }

    private void validateImagesLimit(Product product, List<Image> images) {
        int currentImagesCount = CollectionUtils.isEmpty(product.getImageKeys()) ? 0 : product.getImageKeys().size();

        if (images.size() > imagesMaxLimit || images.size() + currentImagesCount > imagesMaxLimit) {
            throw new ImagesLimitExceededException("The maximum number of images is %d.".formatted(imagesMaxLimit));
        }
    }
}
