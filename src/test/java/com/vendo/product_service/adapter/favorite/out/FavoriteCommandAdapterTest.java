package com.vendo.product_service.adapter.favorite.out;

import com.vendo.product_service.adapter.favorite.out.mapper.FavoriteMapper;
import com.vendo.product_service.adapter.favorite.out.persistence.FavoriteCommandAdapter;
import com.vendo.product_service.adapter.favorite.out.persistence.FavoriteRepository;
import com.vendo.product_service.adapter.favorite.out.persistence.MongoFavorite;
import com.vendo.product_service.domain.favorite.exception.FavoriteAlreadyExistsException;
import com.vendo.product_service.domain.favorite.model.Favorite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteCommandAdapterTest {

    @InjectMocks
    private FavoriteCommandAdapter favoriteCommandAdapter;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private FavoriteMapper favoriteMapper;

    @Test
    void save_shouldMapAndSaveFavorite() {
        Favorite favorite = Favorite.builder()
                .userId("user-id")
                .productId("product-id")
                .build();

        MongoFavorite entity = MongoFavorite.builder()
                .userId("user-id")
                .productId("product-id")
                .build();

        when(favoriteMapper.toEntity(favorite)).thenReturn(entity);

        favoriteCommandAdapter.save(favorite);

        verify(favoriteMapper).toEntity(favorite);
        verify(favoriteRepository).save(entity);
        verifyNoMoreInteractions(favoriteRepository, favoriteMapper);
    }

    @Test
    void save_shouldThrowException_whenFavoriteAlreadyExists() {
        Favorite favorite = Favorite.builder()
                .userId("user-id")
                .productId("product-id")
                .build();

        MongoFavorite entity = MongoFavorite.builder()
                .userId("user-id")
                .productId("product-id")
                .build();

        when(favoriteMapper.toEntity(favorite)).thenReturn(entity);
        doThrow(new DuplicateKeyException("duplicate"))
                .when(favoriteRepository)
                .save(entity);

        assertThatThrownBy(() -> favoriteCommandAdapter.save(favorite))
                .isInstanceOf(FavoriteAlreadyExistsException.class)
                .hasMessage("Product is already in favorites");

        verify(favoriteMapper).toEntity(favorite);
        verify(favoriteRepository).save(entity);
        verifyNoMoreInteractions(favoriteRepository, favoriteMapper);
    }

    @Test
    void delete_shouldDeleteFavorite() {
        String userId = "user-id";
        String productId = "product-id";

        favoriteCommandAdapter.delete(userId, productId);

        verify(favoriteRepository)
                .deleteByUserIdAndProductId(userId, productId);

        verifyNoMoreInteractions(favoriteRepository);
        verifyNoInteractions(favoriteMapper);
    }
}