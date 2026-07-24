package com.vendo.product_service.application.image;

import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.domain.image.model.PresignImage;
import com.vendo.product_service.domain.product.exception.NotProductOwnerException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.id.IdGenerationPort;
import com.vendo.product_service.port.image.ImageUploadPort;
import com.vendo.product_service.port.image.ImageUseCase;
import com.vendo.product_service.port.image.PresignPort;
import com.vendo.product_service.port.product.ProductCommandPort;
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

    private final ProductCommandPort productCommandPort;
    private final ProductQueryPort productQueryPort;

    private final PresignPort presignPort;
    private final ImageUploadPort imageUploadPort;

    private final IdGenerationPort idGenerationPort;
    private final AuthUserPort authUserPort;

    @Value("${product.image.max-size}")
    private int maxSize;

    @Override
    public void upload(String productId, List<Image> images) {
        images.forEach(image -> image.validate(maxSize));
        final List<Image> withIds = withIds(images);

        Product product = productQueryPort.findById(productId);
        validateOwner(product.getOwnerId());

        List<PresignImage> presigns = presignPort.generate(withIds);
        imageUploadPort.upload(mapToImagesByUrl(withIds, presigns));

        productCommandPort.update(productId, Product.builder().imageKeys(getKeys(presigns)).build());
    }

    private List<Image> withIds(List<Image> images) {
        return images.stream().map(image -> image.withId(idGenerationPort.generate())).toList();
    }

    private void validateOwner(String ownerId) {
        User authUser = authUserPort.getAuthUser();
        if (!authUser.id().equals(ownerId)) throw new NotProductOwnerException("You're not product's owner.");
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
}
