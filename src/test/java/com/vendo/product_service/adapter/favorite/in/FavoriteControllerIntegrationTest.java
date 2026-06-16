package com.vendo.product_service.adapter.favorite.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.product_service.adapter.favorite.in.dto.FavoriteResponse;
import com.vendo.product_service.adapter.favorite.out.mapper.FavoriteMapper;
import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaims;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.in.favorite.FavoriteUseCase;
import com.vendo.product_service.port.out.product.ProductQueryPort;
import com.vendo.product_service.test_utils.builder.ProductDataBuilder;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FavoriteControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoriteUseCase favoriteUseCase;
    @MockitoBean
    private FavoriteMapper favoriteMapper;
    @MockitoBean
    private ProductQueryPort productQueryPort;


    private TokenClaims defaultClaims() {
        return new TokenClaims("user_id", UserStatus.ACTIVE, List.of(UserRole.USER.name()), true);
    }

    private FavoriteResponse buildFavoriteResponse(String id, String title, BigDecimal price, Integer quantity, Boolean active) {
        return new FavoriteResponse(id, title, price, quantity, active);
    }

    private ResultActions performAdd(String productId) throws Exception {
        return mockMvc.perform(post("/favorites/{productId}", productId)
                .with(authentication(SecurityContextService.initializeAuth(defaultClaims())))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performRemove(String productId) throws Exception {
        return mockMvc.perform(delete("/favorites/{productId}", productId)
                .with(authentication(SecurityContextService.initializeAuth(defaultClaims())))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performGetAll() throws Exception {
        return mockMvc.perform(get("/favorites")
                .with(authentication(SecurityContextService.initializeAuth(defaultClaims())))
                .contentType(MediaType.APPLICATION_JSON));
    }


    @Nested
    class AddFavoriteTests {

        @Test
        void add_shouldAddFavorite() throws Exception {
            String productId = "product_id";

            doNothing().when(favoriteUseCase).add(productId);

            performAdd(productId).andExpect(status().isOk());

            verify(favoriteUseCase).add(productId);
        }

        @Test
        void add_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
            mockMvc.perform(post("/favorites/{productId}", "product_id")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(favoriteUseCase);
        }
    }

    @Nested
    class RemoveFavoriteTests {

        @Test
        void remove_shouldRemoveFavorite() throws Exception {
            String productId = "product_id";

            doNothing().when(favoriteUseCase).remove(productId);

            performRemove(productId).andExpect(status().isOk());

            verify(favoriteUseCase).remove(productId);
        }

        @Test
        void remove_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
            mockMvc.perform(delete("/favorites/{productId}", "product_id")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(favoriteUseCase);
        }
    }

    @Nested
    class GetFavoritesTests {

        @Test
        void getAll_shouldReturnFavorites() throws Exception {
            Product product = ProductDataBuilder.withAllFields().build();
            FavoriteResponse favoriteResponse = buildFavoriteResponse("id", "Title", BigDecimal.valueOf(99.99), 5, true);

            when(favoriteUseCase.getAll()).thenReturn(List.of(product));
            when(favoriteMapper.toResponse(product)).thenReturn(favoriteResponse);

            String content = performGetAll()
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            List<FavoriteResponse> responses = objectMapper.readValue(
                    content,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, FavoriteResponse.class)
            );

            assertThat(responses).isNotNull();
            assertThat(responses.size()).isEqualTo(1);

            FavoriteResponse actual = responses.get(0);
            assertThat(actual.id()).isEqualTo(favoriteResponse.id());
            assertThat(actual.title()).isEqualTo(favoriteResponse.title());
            assertThat(actual.price()).isEqualByComparingTo(favoriteResponse.price());
            assertThat(actual.quantity()).isEqualTo(favoriteResponse.quantity());
            assertThat(actual.active()).isEqualTo(favoriteResponse.active());

            verify(favoriteUseCase).getAll();
            verify(favoriteMapper).toResponse(product);
        }

        @Test
        void getAll_shouldReturnEmptyList_whenNoFavorites() throws Exception {
            when(favoriteUseCase.getAll()).thenReturn(List.of());

            String content = performGetAll()
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            List<FavoriteResponse> responses = objectMapper.readValue(
                    content,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, FavoriteResponse.class)
            );

            assertThat(responses).isNotNull();
            assertThat(responses.size()).isEqualTo(0);

            verify(favoriteUseCase).getAll();
            verifyNoInteractions(favoriteMapper);
        }

        @Test
        void getAll_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/favorites")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(favoriteUseCase, favoriteMapper);
        }
    }
}