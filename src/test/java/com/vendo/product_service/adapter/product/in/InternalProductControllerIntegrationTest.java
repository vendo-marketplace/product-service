package com.vendo.product_service.adapter.product.in;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.core_lib.utils.AssertionUtils;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.out.product.InternalProductQueryPort;
import com.vendo.product_service.test_utils.builder.TokenClaimsDataBuilder;
import com.vendo.product_service.test_utils.builder.ProductDataBuilder;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.response.ExceptionResponse;
import com.vendo.security_starter.jwt.parser.TokenClaims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test" )
public class InternalProductControllerIntegrationTest {

    private final int LIMIT = 1;
    private final String CURSOR = String.valueOf(UUID.randomUUID());
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private InternalProductQueryPort internalProductQueryPort;

    private ResultActions performGetAll(String cursor, int limit) throws Exception {
        TokenClaims claims = TokenClaimsDataBuilder.buildWithAllFields().build();
        Map<String, String> params = Map.of("cursor", cursor, "limit", String.valueOf(limit));

        return mockMvc.perform(get("/internal/products" ).params(MultiValueMap.fromSingleValue(params))
                .with(authentication(SecurityContextService.initializeAuth(claims)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performGetAll(int limit) throws Exception {
        TokenClaims claims = TokenClaimsDataBuilder.buildWithAllFields().build();

        return mockMvc.perform(get("/internal/products" ).param("limit", String.valueOf(limit))
                .with(authentication(SecurityContextService.initializeAuth(claims)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAll_shouldReturnAllProducts() throws Exception {
        List<Product> products = List.of(ProductDataBuilder.withAllFields().build());

        when(internalProductQueryPort.getAll(null, LIMIT)).thenReturn(products);

        String response = performGetAll(LIMIT)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response).isNotNull();
        List<Product> responseProducts = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertThat(responseProducts).isNotNull();
        assertThat(responseProducts.size()).isEqualTo(1);
        AssertionUtils.assertFrom(products.get(0), responseProducts.get(0));
    }

    @Test
    void getAll_shouldReturnAll_whenNoCursorParameter() throws Exception {
        List<Product> products = List.of(ProductDataBuilder.withAllFields().build());

        when(internalProductQueryPort.getAll(CURSOR, LIMIT)).thenReturn(products);

        String response = performGetAll(String.valueOf(CURSOR), LIMIT)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response).isNotNull();
        List<Product> responseProducts = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertThat(responseProducts).isNotNull();
        assertThat(responseProducts.size()).isEqualTo(1);
        AssertionUtils.assertFrom(products.get(0), responseProducts.get(0));
    }

    @Test
    void getAll_shouldReturnBadRequest_whenMaxLimitParameterReached() throws Exception {
        List<Product> products = List.of(ProductDataBuilder.withAllFields().build());

        when(internalProductQueryPort.getAll(null, LIMIT)).thenReturn(products);

        String response = performGetAll(10_000)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response).isNotNull();
        ExceptionResponse exceptionResponse = objectMapper.readValue(response, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed." );
        assertThat(exceptionResponse.getErrors()).isNotNull();
        assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
        assertThat(exceptionResponse.getErrors().get("limit" )).isEqualTo("Max limit is 5000." );
        assertThat(exceptionResponse.getPath()).isEqualTo("/internal/products" );
    }

}
