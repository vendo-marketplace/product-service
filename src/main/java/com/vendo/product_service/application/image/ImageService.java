package com.vendo.product_service.application.image;

import com.vendo.core_lib.utils.CollectionUtils;
import com.vendo.product_service.domain.image.exception.ImageKeyNotFoundException;
import com.vendo.product_service.domain.image.exception.ImagesLimitExceededException;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.domain.image.model.PresignImage;
import com.vendo.product_service.domain.product.exception.NotProductOwnerException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.IdGenerationPort;
import com.vendo.product_service.port.image.ImageEventSenderPort;
import com.vendo.product_service.port.image.ImageUploadPort;
import com.vendo.product_service.port.image.usecase.ImageUseCase;
import com.vendo.product_service.port.image.ImagePresignPort;
import com.vendo.product_service.port.product.ProductCommandPort;
import com.vendo.product_service.port.product.ProductEventSenderPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.port.user.AuthUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
class ImageService implements ImageUseCase {

    @Value("${product.images.max-limit}")
    private int IMAGES_MAX_LIMIT;

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

        validateOwner(product.getOwnerId());
        validateImagesLimit(product, images);

        List<PresignImage> presigns = imagePresignPort.generate(withIds);
        imageUploadPort.upload(mapToImagesByUrl(withIds, presigns));
        Product updateProduct = Product.builder().id(productId).imageKeys(addAllKeys(product, getKeys(presigns))).build();

        productCommandPort.update(productId, updateProduct);
        productEventSenderPort.sendUpdated(updateProduct);
    }

    @Override
    public void delete(String productId, String imageKey) {
        Product product = productQueryPort.findById(productId);

        validateOwner(product.getOwnerId());
        throwIfImageKeyNotContains(imageKey, product.getImageKeys());

        Product updateProduct = Product.builder().id(productId).imageKeys(filterImageKeys(imageKey, product.getImageKeys())).build();
        productCommandPort.update(productId, updateProduct);

        imageEventSenderPort.delete(imageKey);
        productEventSenderPort.sendUpdated(updateProduct);
    }

    private List<String> addAllKeys(Product product, List<String> requestImageKeys) {
        List<String> imageKeys = product.getImageKeys();

        if (CollectionUtils.isEmpty(imageKeys)) {
            return requestImageKeys;
        }

        imageKeys.addAll(requestImageKeys);
        return imageKeys;
    }

    private List<Image> withIds(List<Image> images) {
        return images.stream().map(image -> image.withId(idGenerationPort.generate())).toList();
    }

    private void validateOwner(String ownerId) {
        User authUser = authUserPort.getAuthUser();
        if (!authUser.id().equals(ownerId)) throw new NotProductOwnerException("You're not product's owner.");
    }

    private void validateImagesLimit(Product product, List<Image> images) {
        int currentImagesCount = CollectionUtils.isEmpty(product.getImageKeys()) ? 0 : product.getImageKeys().size();

        if (images.size() > IMAGES_MAX_LIMIT || images.size() + currentImagesCount > IMAGES_MAX_LIMIT) {
            throw new ImagesLimitExceededException("The maximum number of images is %d.".formatted(IMAGES_MAX_LIMIT));
        }
    }

    private Map<String, Image> mapToImagesByUrl(List<Image> images, List<PresignImage> presigns) {
        Map<String, Image> imagesByUrl = new HashMap<>();

        for (PresignImage presign : presigns) {
            Image imageById = Image.findById(presign.id(), images);
            imagesByUrl.put(presign.uploadUrl(), imageById);
        }

        return imagesByUrl;
    }

    private List<String> getKeys(List<PresignImage> presigns) {
        return presigns.stream().map(PresignImage::key).toList();
    }

    private void throwIfImageKeyNotContains(String imageKey, List<String> imageKeys) {
        if (CollectionUtils.isEmpty(imageKeys) || !imageKeys.contains(imageKey)) {
            throw new ImageKeyNotFoundException("%s does not exist in product.".formatted(imageKey));
        }
    }

    private List<String> filterImageKeys(String imageKey, List<String> imageKeys) {
        return imageKeys.stream().filter(ik -> !ik.equals(imageKey)).toList();
    }
}
