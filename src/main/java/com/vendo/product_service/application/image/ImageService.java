package com.vendo.product_service.application.image;

import com.vendo.product_service.domain.image.model.PresignType;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.domain.image.model.PresignImage;
import com.vendo.product_service.port.IdGenerationPort;
import com.vendo.product_service.port.image.ImagePresignPort;
import com.vendo.product_service.port.image.ImageUploadPort;
import com.vendo.product_service.port.image.usecase.ImageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
class ImageService implements ImageUseCase {

    private final IdGenerationPort idGenerationPort;

    private final ImagePresignPort imagePresignPort;
    private final ImageUploadPort imageUploadPort;

    @Override
    public List<String> upload(PresignType type, List<Image> images) {
        final List<Image> withIds = withIds(images);

        List<PresignImage> presigns = imagePresignPort.generate(type, withIds);
        imageUploadPort.upload(toImagesByUrl(withIds, presigns));

        return PresignImage.getKeys(presigns);
    }

    private List<Image> withIds(List<Image> images) {
        return images.stream().map(image -> image.withId(idGenerationPort.generate())).toList();
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
