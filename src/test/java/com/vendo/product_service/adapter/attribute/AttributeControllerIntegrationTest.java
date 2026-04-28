package com.vendo.product_service.adapter.attribute;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.product_service.adapter.attribute.in.dto.CreateAttributeRequest;
import com.vendo.product_service.adapter.attribute.out.mapper.DtoAttributeMapper;
import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaims;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AttributeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DtoAttributeMapper mapper;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    private TokenClaims buildTokenClaims(UserRole role) {
        return new TokenClaims("id", UserStatus.ACTIVE, List.of(role.name()), true);
    }

    @Nested
    class SaveAttribute {

        @Test
        void save_shouldSuccessfullySave() throws Exception {
            TokenClaims claims = buildTokenClaims(UserRole.ADMIN);
            CreateAttributeRequest request = CreateAttributeRequestDataBuilder.withAllFields().build();

            when(mapper.toAttribute(request)).thenReturn()

            mockMvc.perform(post("/categories")
                    .with(authentication(SecurityContextService.initializeAuth(claims)))
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                     .andExpect(status().isOk());
        }

        @Test
        void save_shouldReturnBadRequest_whenAttributeTitleIsNotValid() {
        }

        @Test
        void save_shouldReturnUnauthorized_whenNotAdmin() {

        }
    }
}
