package com.vendo.product_service.adapter.favorite.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.core_lib.utils.AssertionUtils;
import com.vendo.product_service.adapter.product.in.dto.ProductResponse;
import com.vendo.product_service.adapter.product.in.dto.ProductsResponse;
import com.vendo.product_service.domain.favorite.model.Favorite;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.favorite.FavoriteCommandPort;
import com.vendo.product_service.port.favorite.FavoriteQueryPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.test_utils.builder.ProductDataBuilder;
import com.vendo.product_service.test_utils.builder.UserDataBuilder;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.ExceptionResponse;
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

            Favorite savedFavorite = argumentCaptor.getValue();
            assertThat(savedFavorite.userId()).isEqualTo(user.id());
            assertThat(savedFavorite.productId()).isEqualTo(productId);
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
            String productId = "product_id";

            String content = mockMvc.perform(post("/favorites/{productId}", productId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Unauthorized.");
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
            assertThat(exceptionResponse.getPath()).isEqualTo("/favorites/" + productId);

            verifyNoInteractions(productQueryPort, favoriteCommandPort);
        }
    }

    @Nested
    class RemoveFavoriteTests {

        @Test
        void remove_shouldRemoveFavorite() throws Exception {
            String productId = "product_id";

            when(favoriteQueryPort.existsBy(user.id(), productId)).thenReturn(true);

            performRemove(productId).andExpect(status().isOk());

            verify(favoriteQueryPort).existsBy(user.id(), productId);
            verify(favoriteCommandPort).delete(user.id(), productId);
        }

        @Test
        void remove_shouldReturnNotFound_whenFavoriteNotFound() throws Exception {
            String productId = "product_id";

            when(favoriteQueryPort.existsBy(user.id(), productId)).thenReturn(false);

            String content = performRemove(productId).andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Favorite not found.");
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getPath()).isEqualTo("/favorites/" + productId);

            verify(favoriteQueryPort).existsBy(user.id(), productId);
            verifyNoInteractions(favoriteCommandPort);
        }

        @Test
        void remove_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
            String productId = "product_id";

            String content = mockMvc.perform(delete("/favorites/{productId}", productId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Unauthorized.");
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
            assertThat(exceptionResponse.getPath()).isEqualTo("/favorites/" + productId);

            verifyNoInteractions(favoriteCommandPort, favoriteQueryPort);
        }
    }

    @Nested
    class GetFavoritesTests {

        @Test
        void getAll_shouldReturnFavorites() throws Exception {
            Product product = ProductDataBuilder.withAllFields().build();
            Favorite favorite = Favorite.builder().id("id").userId(user.id()).productId(product.getId()).build();

            when(favoriteQueryPort.findAllBy(user.id())).thenReturn(List.of(favorite));
            when(productQueryPort.findAllByIds(List.of(favorite.productId()))).thenReturn(List.of(product));

            String content = performGetAll()
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ProductsResponse productsResponse = objectMapper.readValue(content, ProductsResponse.class);
            assertThat(productsResponse).isNotNull();
            assertThat(productsResponse.data()).isNotNull();
            assertThat(productsResponse.data().size()).isEqualTo(1);


            ProductResponse actual = productsResponse.data().get(0);
            AssertionUtils.assertFrom(actual, product);

            verify(favoriteQueryPort).findAllBy(user.id());
            verify(productQueryPort).findAllByIds(List.of(favorite.productId()));
        }

        @Test
        void getAll_shouldReturnEmptyList_whenNoFavorites() throws Exception {
            when(favoriteQueryPort.findAllBy(user.id())).thenReturn(List.of());

            String content = performGetAll()
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ProductsResponse productsResponse = objectMapper.readValue(content, ProductsResponse.class);
            assertThat(productsResponse).isNotNull();
            assertThat(productsResponse.data()).isNotNull();
            assertThat(productsResponse.data().size()).isEqualTo(0);

            verify(favoriteQueryPort).findAllBy(user.id());
        }

        @Test
        void getAll_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
            String content = mockMvc.perform(get("/favorites")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Unauthorized.");
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
            assertThat(exceptionResponse.getPath()).isEqualTo("/favorites");
        }
    }
}