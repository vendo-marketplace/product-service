package com.vendo.product_service.application.image;

import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.domain.image.model.PresignedImage;
import com.vendo.product_service.domain.product.exception.NotProductOwnerException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.image.ImageUploadPort;
import com.vendo.product_service.port.image.ImageUseCase;
import com.vendo.product_service.port.image.PresignImagePort;
import com.vendo.product_service.port.product.ProductCommandPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.port.user.AuthUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
class ImageService implements ImageUseCase {

    private final AuthUserPort authUserPort;
    private final ProductQueryPort productQueryPort;
    private final PresignImagePort presignImagePort;
    private final ImageUploadPort imageUploadPort;
    private final ProductCommandPort productCommandPort;

    @Override
    public void upload(String productId, List<Image> images) {
        Product product = productQueryPort.findById(productId);
        throwIfNotOwner(product.getOwnerId());

        List<PresignedImage> presignedImages = presignImagePort.generate(images);
        imageUploadPort.upload(mapToImagesByUrl(images, presignedImages));

        List<String> keys = presignedImages.stream().map(PresignedImage::key).toList();
        productCommandPort.update(productId, Product.builder().imageKeys(keys).build());
    }

    private void throwIfNotOwner(String ownerId) {
        User authUser = authUserPort.getAuthUser();
        if (!authUser.id().equals(ownerId)) throw new NotProductOwnerException("You're not product's owner.");
    }

    private Map<String, byte[]> mapToImagesByUrl(List<Image> images, List<PresignedImage> presignedImages) {
        Map<String, byte[]> imagesByUrl = new HashMap<>();

        for (PresignedImage presignedImage : presignedImages) {
            Image imageById = findImageById(presignedImage.id(), images);
            imagesByUrl.put(presignedImage.uploadUrl(), imageById.bytes());
        }

        return imagesByUrl;
    }

    private Image findImageById(String id, List<Image> images) {
        return images.stream()
                .filter(image -> image.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Image not found by id: %s.".formatted(id)));
    }

}
