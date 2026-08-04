package com.vendo.product_service.application.product;

import com.vendo.core_lib.utils.CollectionUtils;
import com.vendo.product_service.domain.image.exception.ImagesLimitExceededException;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.domain.image.model.PresignImage;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.IdGenerationPort;
import com.vendo.product_service.port.image.ImageEventSenderPort;
import com.vendo.product_service.port.image.ImageUploadPort;
import com.vendo.product_service.port.image.ImagePresignPort;
import com.vendo.product_service.port.product.ProductCommandPort;
import com.vendo.product_service.port.product.ProductEventSenderPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.port.product.usecase.ProductImageUseCase;
import com.vendo.product_service.port.user.AuthUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
class ProductImageService implements ProductImageUseCase {

    private final int imagesMaxLimit;

    private final ProductCommandPort productCommandPort;
    private final ProductQueryPort productQueryPort;
    private final ProductEventSenderPort productEventSenderPort;

    private final ImagePresignPort imagePresignPort;
    private final ImageUploadPort imageUploadPort;
    private final ImageEventSenderPort imageEventSenderPort;

    private final IdGenerationPort idGenerationPort;
    private final AuthUserPort authUserPort;

    @Override
    public void upload(String productId, List<Image> images) {
        final List<Image> withIds = withIds(images);
        Product product = productQueryPort.findById(productId);

        User authUser = authUserPort.getAuthUser();
        authUser.throwIfNotOwner(product.getOwnerId());

        validateImagesLimit(product, images);

        List<PresignImage> presigns = imagePresignPort.generate(withIds);
        imageUploadPort.upload(toImagesByUrl(withIds, presigns));
        Product updateProduct = Product.builder().id(productId).imageKeys(product.mergeImageKeys(PresignImage.getKeys(presigns))).build();

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

    private List<Image> withIds(List<Image> images) {
        return images.stream().map(image -> image.withId(idGenerationPort.generate())).toList();
    }

    private void validateImagesLimit(Product product, List<Image> images) {
        int currentImagesCount = CollectionUtils.isEmpty(product.getImageKeys()) ? 0 : product.getImageKeys().size();

        if (images.size() > imagesMaxLimit || images.size() + currentImagesCount > imagesMaxLimit) {
            throw new ImagesLimitExceededException("The maximum number of images is %d.".formatted(imagesMaxLimit));
        }
    }

    private Map<String, Image> toImagesByUrl(List<Image> images, List<PresignImage> presigns) {
        Map<String, Image> imagesByUrl = new HashMap<>();

        for (PresignImage presign : presigns) {
            Image imageById = Image.findById(presign.id(), images);
            imagesByUrl.put(presign.uploadUrl(), imageById);
        }

        return imagesByUrl;
    }
}
