package com.vendo.product_service.adapter.attribute.in;

import com.vendo.product_service.adapter.attribute.in.dto.AttributeResponse;
import com.vendo.product_service.adapter.attribute.in.dto.CreateAttributeRequest;
import com.vendo.product_service.adapter.attribute.in.dto.UpdateAttributeRequest;
import com.vendo.product_service.adapter.attribute.out.mapper.DtoAttributeMapper;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeFilter;
import com.vendo.product_service.domain.attribute.model.AttributeType;
import com.vendo.product_service.port.attribute.AttributeUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attributes")
@PreAuthorize("hasAuthority('ADMIN')")
public class AttributeController {

    private final AttributeUseCase attributeUseCase;
    private final DtoAttributeMapper mapper;

    @GetMapping
    public ResponseEntity<List<AttributeResponse>> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) AttributeType type) {
        List<Attribute> attributes = attributeUseCase.findAll(new AttributeFilter(search, type));
        return ResponseEntity.ok(mapper.toResponses(attributes));
    }

    @PostMapping
    public ResponseEntity<AttributeResponse> save(@Valid @RequestBody CreateAttributeRequest request) {
        Attribute saved = attributeUseCase.save(mapper.toAttribute(request));
        return ResponseEntity.ok(mapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttributeResponse> update(@PathVariable String id, @Valid @RequestBody UpdateAttributeRequest request) {
        Attribute updated = attributeUseCase.update(id, mapper.toAttribute(request));
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

}
