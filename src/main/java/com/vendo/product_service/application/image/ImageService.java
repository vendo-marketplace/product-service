package com.vendo.product_service.application.image;

import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.domain.product.exception.NotProductOwnerException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.image.ImageUseCase;
import com.vendo.product_service.port.image.ImageCommandPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.port.user.AuthUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService implements ImageUseCase {

    private final AuthUserPort authUserPort;
    private final ProductQueryPort productQueryPort;
    private final ImageCommandPort imageCommandPort;

    @Override
    public void upload(String productId, List<Image> images) {
        // TODO validate if product exists and if product owner
        Product product = productQueryPort.findById(productId);
        throwIfNotOwner(product.getOwnerId());

        // TODO make http request to AWS for generating presigned urls
        // TODO save keys to product
    }

    private void throwIfNotOwner(String ownerId) {
        User authUser = authUserPort.getAuthUser();
        if (!authUser.id().equals(ownerId)) throw new NotProductOwnerException("You're not product's onwer.");
    }
}
