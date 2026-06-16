package com.vendo.product_service.application.favorite;

import com.vendo.product_service.domain.favorite.model.Favorite;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.out.favorite.FavoriteCommandPort;
import com.vendo.product_service.port.out.favorite.FavoriteQueryPort;
import com.vendo.product_service.port.out.product.ProductQueryPort;
import com.vendo.product_service.port.out.user.CurrentUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @InjectMocks
    private FavoriteService favoriteService;

    @Mock
    private FavoriteCommandPort favoriteCommandPort;

    @Mock
    private FavoriteQueryPort favoriteQueryPort;

    @Mock
    private ProductQueryPort productQueryPort;

    @Mock
    private CurrentUserPort currentUserPort;


    @Test
    void add_shouldSaveFavorite_whenProductExists() {
        String userId = "user-id";
        String productId = "product-id";

        when(currentUserPort.getCurrentUserId()).thenReturn(userId);
        when(productQueryPort.existsById(productId)).thenReturn(true);

        favoriteService.add(productId);

        ArgumentCaptor<Favorite> favoriteCaptor =
                ArgumentCaptor.forClass(Favorite.class);

        verify(currentUserPort).getCurrentUserId();
        verify(productQueryPort).existsById(productId);
        verify(favoriteCommandPort).save(favoriteCaptor.capture());

        Favorite favorite = favoriteCaptor.getValue();

        assertThat(favorite.getUserId()).isEqualTo(userId);
        assertThat(favorite.getProductId()).isEqualTo(productId);
    }

    @Test
    void add_shouldThrowException_whenProductNotExists() {
        String userId = "user-id";
        String productId = "product-id";

        when(currentUserPort.getCurrentUserId()).thenReturn(userId);
        when(productQueryPort.existsById(productId)).thenReturn(false);

        assertThatThrownBy(() -> favoriteService.add(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining(productId);

        verify(currentUserPort).getCurrentUserId();
        verify(productQueryPort).existsById(productId);

        verifyNoInteractions(favoriteCommandPort);
    }

    @Test
    void remove_shouldDeleteFavorite() {
        String userId = "user-id";
        String productId = "product-id";

        when(currentUserPort.getCurrentUserId()).thenReturn(userId);

        favoriteService.remove(productId);

        verify(currentUserPort).getCurrentUserId();
        verify(favoriteCommandPort)
                .delete(userId, productId);

        verifyNoMoreInteractions(favoriteCommandPort);
    }

    @Test
    void getAll_shouldReturnProducts() {
        String userId = "user-id";

        Favorite favorite1 = Favorite.builder()
                .userId(userId)
                .productId("product-1")
                .build();

        Favorite favorite2 = Favorite.builder()
                .userId(userId)
                .productId("product-2")
                .build();

        Product product1 = Product.builder()
                .id("product-1")
                .build();

        Product product2 = Product.builder()
                .id("product-2")
                .build();

        when(currentUserPort.getCurrentUserId()).thenReturn(userId);
        when(favoriteQueryPort.findAllBy(userId))
                .thenReturn(List.of(favorite1, favorite2));

        when(productQueryPort.findAllByIds(List.of("product-1", "product-2")))
                .thenReturn(List.of(product1, product2));

        List<Product> result = favoriteService.getAll();

        assertThat(result)
                .containsExactly(product1, product2);

        verify(currentUserPort).getCurrentUserId();
        verify(favoriteQueryPort).findAllBy(userId);
        verify(productQueryPort)
                .findAllByIds(List.of("product-1", "product-2"));
    }

    @Test
    void getAll_shouldReturnEmptyList_whenFavoritesEmpty() {
        String userId = "user-id";

        when(currentUserPort.getCurrentUserId()).thenReturn(userId);
        when(favoriteQueryPort.findAllBy(userId))
                .thenReturn(List.of());

        when(productQueryPort.findAllByIds(List.of()))
                .thenReturn(List.of());

        List<Product> result = favoriteService.getAll();

        assertThat(result).isEmpty();

        verify(currentUserPort).getCurrentUserId();
        verify(favoriteQueryPort).findAllBy(userId);
        verify(productQueryPort).findAllByIds(List.of());
    }
}