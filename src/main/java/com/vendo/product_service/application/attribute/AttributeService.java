package com.vendo.product_service.application.attribute;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.port.attribute.AttributeUseCase;
import com.vendo.product_service.port.attribute.AttributeCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttributeService implements AttributeUseCase {

    private final AttributeCommandPort commandPort;

    @Override
    public void save(Attribute attribute) {
        commandPort.save(attribute);
    }

}
