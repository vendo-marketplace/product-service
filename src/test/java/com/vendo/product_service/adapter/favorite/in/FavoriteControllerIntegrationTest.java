package com.vendo.product_service.adapter.favorite.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.core_lib.utils.AssertionUtils;
import com.vendo.product_service.adapter.favorite.in.dto.FavoriteResponse;
import com.vendo.product_service.domain.favorite.model.Favorite;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.out.favorite.FavoriteCommandPort;
import com.vendo.product_service.port.out.favorite.FavoriteQueryPort;
import com.vendo.product_service.port.out.product.ProductQueryPort;
import com.vendo.product_service.test_utils.builder.ProductDataBuilder;
import com.vendo.product_service.test_utils.builder.UserDataBuilder;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.response.ExceptionResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

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
    private ProductQueryPort productQueryPort;
    @MockitoBean
    private FavoriteCommandPort favoriteCommandPort;
    @MockitoBean
    private FavoriteQueryPort favoriteQueryPort;

    private final User user = UserDataBuilder.withAllFields().build();

    private ResultActions performAdd(String productId) throws Exception {
        return mockMvc.perform(post("/favorites/{productId}", productId)
                .with(authentication(SecurityContextService.initializeAuth(user)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performRemove(String productId) throws Exception {
        return mockMvc.perform(delete("/favorites/{productId}", productId)
                .with(authentication(SecurityContextService.initializeAuth(user)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performGetAll() throws Exception {
        return mockMvc.perform(get("/favorites")
                .with(authentication(SecurityContextService.initializeAuth(user)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Nested
    class AddFavoriteTests {

        @Test
        void add_shouldAddFavorite() throws Exception {
            String productId = "product_id";
            ArgumentCaptor<Favorite> argumentCaptor = ArgumentCaptor.forClass(Favorite.class);

            when(productQueryPort.existsById(productId)).thenReturn(true);

            performAdd(productId).andExpect(status().isOk());

            verify(productQueryPort).existsById(productId);
            verify(favoriteCommandPort).save(argumentCaptor.capture());
        }

        @Test
        void add_shouldReturnNotFound_whenProductNotFound() throws Exception {
            String productId = "product_id";

            when(productQueryPort.existsById(productId)).thenReturn(false);

            String content = performAdd(productId).andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Product not found.");
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getPath()).isEqualTo("/favorites/" + productId);

            verify(productQueryPort).existsById(productId);
            verifyNoInteractions(favoriteCommandPort);
        }

        @Test
        void add_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
            mockMvc.perform(post("/favorites/{productId}", "product_id")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(productQueryPort, favoriteCommandPort);
        }
    }

    @Nested
    class RemoveFavoriteTests {

        @Test
        void remove_shouldRemoveFavorite() throws Exception {
            String productId = "product_id";

            performRemove(productId).andExpect(status().isOk());

            verify(favoriteCommandPort).delete(user.id(), productId);
        }

        @Test
        void remove_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
            mockMvc.perform(delete("/favorites/{productId}", "product_id")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(favoriteCommandPort);
        }
    }

    @Nested
    class GetFavoritesTests {

        @Test
        void getAll_shouldReturnFavorites() throws Exception {
            Product product = ProductDataBuilder.withAllFields().build();
            Favorite favorite = Favorite.builder().id("id").userId(user.id()).productId(product.getId()).build();

            when(favoriteQueryPort.findAllBy(user.id())).thenReturn(List.of(favorite));
            when(productQueryPort.findAllByIds(List.of(favorite.getProductId()))).thenReturn(List.of(product));

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
            AssertionUtils.assertFrom(actual, product, "ownerId", "attributes", "createdAt", "description", "categoryId");

            verify(favoriteQueryPort).findAllBy(user.id());
            verify(productQueryPort).findAllByIds(List.of(favorite.getProductId()));
        }

        @Test
        void getAll_shouldReturnEmptyList_whenNoFavorites() throws Exception {
            when(favoriteQueryPort.findAllBy(user.id())).thenReturn(List.of());

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

            verify(favoriteQueryPort).findAllBy(user.id());
        }

        @Test
        void getAll_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/favorites")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(favoriteQueryPort);
        }
    }
}