package com.vendo.product_service.adapter.favorite.out;

import com.vendo.product_service.adapter.favorite.out.mapper.FavoriteMapper;
import com.vendo.product_service.adapter.favorite.out.persistence.FavoriteQueryAdapter;
import com.vendo.product_service.adapter.favorite.out.persistence.FavoriteRepository;
import com.vendo.product_service.adapter.favorite.out.persistence.MongoFavorite;
import com.vendo.product_service.domain.favorite.model.Favorite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteQueryAdapterTest {

    @InjectMocks
    private FavoriteQueryAdapter favoriteQueryAdapter;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private FavoriteMapper favoriteMapper;

    @Test
    void findAllBy_shouldReturnMappedFavorites() {
        String userId = "user-id";

        MongoFavorite favoriteEntity1 = MongoFavorite.builder()
                .userId(userId)
                .productId("product-1")
                .build();
        MongoFavorite favoriteEntity2 = MongoFavorite.builder()
                .userId(userId)
                .productId("product-2")
                .build();

        Favorite favorite1 = Favorite.builder()
                .userId(userId)
                .productId("product-1")
                .build();
        Favorite favorite2 = Favorite.builder()
                .userId(userId)
                .productId("product-2")
                .build();

        when(favoriteRepository.findAllByUserId(userId))
                .thenReturn(List.of(favoriteEntity1, favoriteEntity2));

        when(favoriteMapper.toFavorites(List.of(favoriteEntity1, favoriteEntity2)))
                .thenReturn(List.of(favorite1, favorite2));

        List<Favorite> result = favoriteQueryAdapter.findAllBy(userId);

        assertThat(result)
                .containsExactly(favorite1, favorite2);

        verify(favoriteRepository).findAllByUserId(userId);
        verify(favoriteMapper).toFavorites(List.of(favoriteEntity1, favoriteEntity2));

        verifyNoMoreInteractions(favoriteRepository, favoriteMapper);
    }

    @Test
    void findAllBy_shouldReturnEmptyList_whenNoFavorites() {
        String userId = "user-id";

        when(favoriteRepository.findAllByUserId(userId)).thenReturn(List.of());
        when(favoriteMapper.toFavorites(List.of())).thenReturn(List.of());

        List<Favorite> result = favoriteQueryAdapter.findAllBy(userId);

        assertThat(result).isEmpty();

        verify(favoriteRepository).findAllByUserId(userId);
        verify(favoriteMapper).toFavorites(List.of());
        verifyNoMoreInteractions(favoriteRepository, favoriteMapper);
    }

    @Test
    void existsBy_shouldReturnTrue_whenFavoriteExists() {
        String userId = "user-id";
        String productId = "product-1";

        when(favoriteRepository.existsByUserIdAndProductId(userId, productId)).thenReturn(true);

        boolean result = favoriteQueryAdapter.existsBy(userId, productId);

        assertThat(result).isTrue();

        verify(favoriteRepository).existsByUserIdAndProductId(userId, productId);
        verifyNoMoreInteractions(favoriteRepository, favoriteMapper);
    }

    @Test
    void existsBy_shouldReturnFalse_whenFavoriteDoesNotExist() {
        String userId = "user-id";
        String productId = "product-1";

        when(favoriteRepository.existsByUserIdAndProductId(userId, productId)).thenReturn(false);

        boolean result = favoriteQueryAdapter.existsBy(userId, productId);

        assertThat(result).isFalse();

        verify(favoriteRepository).existsByUserIdAndProductId(userId, productId);
        verifyNoMoreInteractions(favoriteRepository, favoriteMapper);
    }
}