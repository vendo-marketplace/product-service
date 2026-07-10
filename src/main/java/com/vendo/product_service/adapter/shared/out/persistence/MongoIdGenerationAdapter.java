package com.vendo.product_service.adapter.shared.out.persistence;

import com.vendo.product_service.port.IdGenerationPort;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class MongoIdGenerationAdapter implements IdGenerationPort {

    @Override
    public String generate() {
        return new ObjectId().toHexString();
    }
}
