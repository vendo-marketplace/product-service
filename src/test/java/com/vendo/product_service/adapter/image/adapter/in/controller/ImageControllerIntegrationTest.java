package com.vendo.product_service.adapter.image.adapter.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.product_service.domain.image.model.PresignImage;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.image.ImageUploadPort;
import com.vendo.product_service.port.image.PresignPort;
import com.vendo.product_service.port.product.ProductCommandPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.test_utils.builder.ProductDataBuilder;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.ExceptionResponse;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ImageControllerIntegrationTest {

    private static final String DEFAULT_USER_ID = "123456";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductQueryPort productQueryPort;
    @MockitoBean
    private PresignPort presignPort;
    @MockitoBean
    private ProductCommandPort productCommandPort;
    @MockitoBean
    private ImageUploadPort imageUploadPort;

    private ResultActions performUpload(String userId, String productId, List<MultipartFile> files) throws Exception {
        User user = new User(userId, "email", UserStatus.ACTIVE, Set.of(UserRole.USER), true);

        MockMultipartHttpServletRequestBuilder request = multipart("/images/upload?productId=" + productId);
        request.with(authentication(SecurityContextService.initializeAuth(user)));

        for (MultipartFile file : files) {
            request.file(new MockMultipartFile(
                    "images",
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            ));
        }

        return mockMvc.perform(request);
    }

    private ResultActions performUploadWithoutProductId(String userId, List<MultipartFile> files) throws Exception {
        User user = new User(userId, "email", UserStatus.ACTIVE, Set.of(UserRole.USER), true);

        MockMultipartHttpServletRequestBuilder request = multipart("/images/upload");
        request.with(authentication(SecurityContextService.initializeAuth(user)));

        for (MultipartFile file : files) {
            request.file(new MockMultipartFile(
                    "images",
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            ));
        }

        return mockMvc.perform(request);
    }

    private MockMultipartFile validImage(String filename) {
        return new MockMultipartFile("images", filename, "image/png", new byte[]{1, 2, 3});
    }

    @Test
    void upload_shouldReturnBadRequest_whenProductIdParameterIsMissing() throws Exception {
        String content = performUploadWithoutProductId(DEFAULT_USER_ID, List.of(validImage("photo.png")))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
        assertThat(exceptionResponse.getErrors()).containsEntry("productId", "Required parameter 'productId' is not present.");

        verifyNoInteractions(productQueryPort, presignPort, productCommandPort, imageUploadPort);
    }

    @Test
    void upload_shouldReturnBadRequest_whenProductIdParameterIsBlank() throws Exception {
        String content = performUpload(DEFAULT_USER_ID, "", List.of(validImage("photo.png")))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
        assertThat(exceptionResponse.getErrors()).containsEntry("productId", "Product ID is required.");

        verifyNoInteractions(productQueryPort, presignPort, productCommandPort, imageUploadPort);
    }

    @Test
    void upload_shouldReturnBadRequest_whenImagesParameterIsMissing() throws Exception {
        String content = performUpload(DEFAULT_USER_ID, "product_id", List.of())
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
        assertThat(exceptionResponse.getErrors()).containsEntry("images", "Required part 'images' is not present.");

        verifyNoInteractions(productQueryPort, presignPort, productCommandPort, imageUploadPort);
    }

    @Test
    void upload_shouldReturnBadRequest_whenImagesParameterIsEmpty() throws Exception {
        String content = performUpload(DEFAULT_USER_ID, "product_id", List.of())
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
        assertThat(exceptionResponse.getErrors()).containsEntry("images", "Required part 'images' is not present.");

        verifyNoInteractions(productQueryPort, presignPort, productCommandPort, imageUploadPort);
    }

    @Test
    void upload_shouldReturnBadRequest_whenMultipartImageIsEmpty() throws Exception {
        MockMultipartFile emptyImage = new MockMultipartFile("images", "empty.png", "image/png", new byte[0]);

        String content = performUpload(DEFAULT_USER_ID, "product_id", List.of(emptyImage))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/images/upload");
        assertThat(exceptionResponse.getErrors()).isNotEmpty();
        assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
        assertThat(exceptionResponse.getErrors().get("size")).isEqualTo("%s is empty.".formatted(emptyImage.getOriginalFilename()));

        verifyNoInteractions(productQueryPort, presignPort, productCommandPort, imageUploadPort);
    }

    @Test
    void upload_shouldReturnBadRequest_whenFileTypeIsNotImage() throws Exception {
        MockMultipartFile notImage = new MockMultipartFile("images", "document.txt", "text/plain", "content".getBytes());

        String content = performUpload(DEFAULT_USER_ID, "product_id", List.of(notImage))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
        assertThat(exceptionResponse.getErrors()).containsEntry("contentType", "document.txt has invalid image content type text/plain.");

        verifyNoInteractions(productQueryPort, presignPort, productCommandPort, imageUploadPort);
    }

    @Test
    void upload_shouldReturnNotFound_whenProductNotFound() throws Exception {
        String productId = "product_id";
        when(productQueryPort.findById(productId)).thenThrow(new ProductNotFoundException("Product not found."));

        String content = performUpload(DEFAULT_USER_ID, productId, List.of(validImage("photo.png")))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(exceptionResponse.getMessage()).isEqualTo("Product not found.");
        assertThat(exceptionResponse.getPath()).isEqualTo("/images/upload");

        verify(productQueryPort).findById(productId);
        verifyNoInteractions(presignPort, productCommandPort, imageUploadPort);
    }

    @Test
    void upload_shouldReturnForbidden_whenNotProductOwner() throws Exception {
        Product product = ProductDataBuilder.withAllFields().ownerId("owner_id").build();
        when(productQueryPort.findById(product.getId())).thenReturn(product);

        String content = performUpload("not_owner_id", product.getId(), List.of(validImage("photo.png")))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(exceptionResponse.getMessage()).isEqualTo("You're not product's owner.");
        assertThat(exceptionResponse.getPath()).isEqualTo("/images/upload");

        verify(productQueryPort).findById(product.getId());
        verifyNoInteractions(presignPort, productCommandPort, imageUploadPort);
    }

    @Test
    void upload_shouldReturnInternalError_whenImageNotFoundById_whileMappingByUrl() throws Exception {
        Product product = ProductDataBuilder.withAllFields().ownerId(DEFAULT_USER_ID).build();
        when(productQueryPort.findById(product.getId())).thenReturn(product);
        when(presignPort.generate(any())).thenReturn(List.of(new PresignImage("unknown_id", "upload_url", "key")));

        String content = performUpload(DEFAULT_USER_ID, product.getId(), List.of(validImage("photo.png")))
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/images/upload");

        verify(productQueryPort).findById(product.getId());
        verify(presignPort).generate(any());
        verifyNoInteractions(productCommandPort, imageUploadPort);
    }

}
