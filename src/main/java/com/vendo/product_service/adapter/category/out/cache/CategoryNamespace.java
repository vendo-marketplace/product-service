package com.vendo.product_service.adapter.category.out.cache;

import com.vendo.redis_lib.config.PrefixProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CategoryNamespace {
    private PrefixProperties tree;
}
