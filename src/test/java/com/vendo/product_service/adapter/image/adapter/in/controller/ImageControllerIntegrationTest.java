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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EmbeddedKafka
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ImageControllerIntegrationTest {

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

    private static final String DEFAULT_USER_ID = "123456";

    public static User buildUser(String id) {
        return new User(id, "email", UserStatus.ACTIVE, Set.of(UserRole.USER), true);
    }

    private ResultActions performUpload(String userId, String productId, List<MultipartFile> files) throws Exception {
        MockMultipartHttpServletRequestBuilder request = multipart("/images?productId=" + productId);
        request.with(authentication(SecurityContextService.initializeAuth(buildUser(userId))));

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

    private ResultActions performDelete(String userId, String productId, String imageKey) throws Exception {
        MockMultipartHttpServletRequestBuilder request = multipart(HttpMethod.DELETE, "/images?productId={productId}&imageKey={imageKey}", productId, imageKey);
        request.with(authentication(SecurityContextService.initializeAuth(buildUser(userId))));
        return mockMvc.perform(request);
    }

    private ResultActions performUploadWithoutProductId(String userId, List<MultipartFile> files) throws Exception {
        User user = new User(userId, "email", UserStatus.ACTIVE, Set.of(UserRole.USER), true);

        MockMultipartHttpServletRequestBuilder request = multipart("/images");
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

    @Nested
    class UploadImageTests {

        @Test
        void upload_shouldReturnBadRequest_whenProductIdParameterIsMissing() throws Exception {
            String content = performUploadWithoutProductId(DEFAULT_USER_ID, List.of(validImage("photo.png")))
                    .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
            assertThat(exceptionResponse.getErrors()).containsEntry("productId", "Required parameter 'productId' is not present.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/images");

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
            assertThat(exceptionResponse.getPath()).isEqualTo("/images");

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
            assertThat(exceptionResponse.getPath()).isEqualTo("/images");

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
            assertThat(exceptionResponse.getPath()).isEqualTo("/images");

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
            assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/images");
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
            assertThat(exceptionResponse.getPath()).isEqualTo("/images");
            assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
            assertThat(exceptionResponse.getErrors()).containsEntry("contentType", "document.txt has invalid image content type text/plain.");

            verifyNoInteractions(productQueryPort, presignPort, productCommandPort, imageUploadPort);
        }

        @Test
        void upload_shouldReturnBadRequest_whenImagesLimitExceeded() throws Exception {
            MockMultipartFile image = new MockMultipartFile("images", "image.png", "image/png", "content".getBytes());
            Product product = ProductDataBuilder.withAllFields()
                    .ownerId(DEFAULT_USER_ID)
                    .imageKeys(IntStream.rangeClosed(1, 10).mapToObj(String::valueOf).toList())
                    .build();

            when(productQueryPort.findById(product.getId())).thenReturn(product);

            String content = performUpload(DEFAULT_USER_ID, product.getId(), List.of(image))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getPath()).isEqualTo("/images");
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("The maximum number of images is 10.");

            verify(productQueryPort).findById(product.getId());

            verifyNoInteractions(presignPort, productCommandPort, imageUploadPort);
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
            assertThat(exceptionResponse.getPath()).isEqualTo("/images");

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
            assertThat(exceptionResponse.getPath()).isEqualTo("/images");

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
            assertThat(exceptionResponse.getMessage()).isEqualTo("Internal server error.");
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
            assertThat(exceptionResponse.getPath()).isEqualTo("/images");

            verify(productQueryPort).findById(product.getId());
            verify(presignPort).generate(any());
            verifyNoInteractions(productCommandPort, imageUploadPort);
        }
    }

    @Nested
    class DeleteImageTests {

        @Test
        void delete_shouldDeleteImageFromProduct() throws Exception {
            String imageKey = "products/key.png";
            Product product = ProductDataBuilder.withAllFields().ownerId(DEFAULT_USER_ID).imageKeys(List.of(imageKey)).build();
            ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);

            when(productQueryPort.findById(product.getId())).thenReturn(product);
            doNothing().when(productCommandPort).update(eq(product.getId()), productArgumentCaptor.capture());

            performDelete(DEFAULT_USER_ID, product.getId(), imageKey).andExpect(status().isOk());

            Product captorValue = productArgumentCaptor.getValue();

            verify(productQueryPort).findById(product.getId());
            verify(productCommandPort).update(product.getId(), captorValue);

            assertThat(captorValue).isNotNull();
            assertThat(captorValue.getImageKeys()).isNotNull();
            assertThat(captorValue.getImageKeys().size()).isEqualTo(0);
        }

        @Test
        void delete_shouldReturnNotFound_whenProductNotFound() throws Exception {
            String imageKey = "products/key.png";
            Product product = ProductDataBuilder.withAllFields().ownerId(DEFAULT_USER_ID).imageKeys(List.of(imageKey)).build();

            when(productQueryPort.findById(product.getId())).thenThrow(new ProductNotFoundException("Product not found."));

            String content = performDelete(DEFAULT_USER_ID, product.getId(), imageKey)
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getMessage()).isEqualTo("Product not found.");
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getPath()).isEqualTo("/images");

            verify(productQueryPort).findById(product.getId());
            verifyNoInteractions(productCommandPort);
        }

        @Test
        void delete_shouldReturnForbidden_whenNotProductOwner() throws Exception {
            String imageKey = "products/key.png";
            Product product = ProductDataBuilder.withAllFields().ownerId("not_product_owner_id").imageKeys(List.of(imageKey)).build();

            when(productQueryPort.findById(product.getId())).thenReturn(product);

            String content = performDelete(DEFAULT_USER_ID, product.getId(), imageKey)
                    .andExpect(status().isForbidden())
                    .andReturn().getResponse().getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("You're not product's owner.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/images");

            verify(productQueryPort).findById(product.getId());
            verifyNoInteractions(productCommandPort);
        }

        @Test
        void delete_shouldReturnNotFound_whenImageKeyNotFound() throws Exception {
            String imageKey = "products/key.png";
            Product product = ProductDataBuilder.withAllFields().ownerId(DEFAULT_USER_ID).imageKeys(List.of("another_image_key")).build();

            when(productQueryPort.findById(product.getId())).thenReturn(product);

            String content = performDelete(DEFAULT_USER_ID, product.getId(), imageKey)
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("%s does not exist in product.".formatted(imageKey));
            assertThat(exceptionResponse.getPath()).isEqualTo("/images");

            verify(productQueryPort).findById(product.getId());

            verifyNoInteractions(productCommandPort);
        }

        @Test
        void delete_shouldReturnBadRequest_whenProductIdIsNotPresent() throws Exception {
            String imageKey = "products/key.png";

            String content = performDelete(DEFAULT_USER_ID, null, imageKey)
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
            assertThat(exceptionResponse.getErrors()).isNotNull();
            assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
            assertThat(exceptionResponse.getErrors().get("productId")).isEqualTo("Product ID is required.");

            assertThat(exceptionResponse.getPath()).isEqualTo("/images");

            verifyNoInteractions(productCommandPort, productQueryPort);
        }

        @Test
        void delete_shouldReturnBadRequest_whenImageKeyIsNotPresent() throws Exception {
            String imageKey = "products/key.png";
            Product product = ProductDataBuilder.withAllFields().ownerId(DEFAULT_USER_ID).imageKeys(List.of(imageKey)).build();

            String content = performDelete(DEFAULT_USER_ID, product.getId(), null)
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/images");
            assertThat(exceptionResponse.getErrors()).isNotNull();
            assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
            assertThat(exceptionResponse.getErrors().get("imageKey")).isEqualTo("Image key is required.");

            verifyNoInteractions(productCommandPort, productQueryPort);
        }
    }
}
