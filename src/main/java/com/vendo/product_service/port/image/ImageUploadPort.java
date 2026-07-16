package com.vendo.product_service.port.image;

import java.util.Map;

public interface ImageUploadPort {

    void upload(Map<String, byte[]> images);

}
