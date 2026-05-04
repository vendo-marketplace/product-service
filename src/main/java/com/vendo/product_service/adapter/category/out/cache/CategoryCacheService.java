package com.vendo.product_service.adapter.category.out.cache;

import com.vendo.product_service.application.category.model.CategoryNode;
import com.vendo.product_service.port.out.category.CategoryCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryCacheService implements CategoryCachePort {

    private final RedisTemplate<String, String> redisTemplate;
    private final CategoryTreeSerializer serializer;
    private final CacheCategoryNamespace categoryNamespace;

    @Override
    public void saveTree(List<CategoryNode> tree) {
        try {
            redisTemplate.opsForValue().set(
                    categoryNamespace.getTree().buildPrefix("all"),
                    serializer.serialize(tree),
                    Duration.ofSeconds(categoryNamespace.getTree().ttl())
            );
        } catch (Exception e) {
            log.warn("Failed to save category tree to cache: {}", e.getMessage());
        }
    }

    @Override
    public Optional<List<CategoryNode>> getTree() {
        try {
            String json = redisTemplate.opsForValue().get(categoryNamespace.getTree().buildPrefix("all"));

            if (json == null) return Optional.empty();

            return Optional.of(serializer.deserialize(json));
        } catch (Exception e) {
            log.warn("Redis unavailable, falling back to DB: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void evictTree() {
        try {
            redisTemplate.delete(categoryNamespace.getTree().buildPrefix("all"));
        } catch (Exception e) {
            log.warn("Failed to evict category tree from cache: {}", e.getMessage());
        }
    }
}