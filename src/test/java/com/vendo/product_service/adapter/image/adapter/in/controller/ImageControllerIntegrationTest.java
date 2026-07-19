package com.vendo.product_service.adapter.image.adapter.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.image.PresignPort;
import com.vendo.product_service.port.product.ProductCommandPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

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

    void upload_shouldReturnBadRequest_whenProductIdParameterIsMissing() {

    }

    void upload_shouldReturnBadRequest_whenProductIdParameterIsBlank() {

    }

    void upload_shouldReturnBadRequest_whenImagesParameterIsMissing() {

    }

    void upload_shouldReturnBadRequest_whenImagesParameterIsEmpty() {

    }

    void upload_shouldReturnBadRequest_whenMultipartImageIsEmpty() {

    }

    void upload_shouldBadRequest_whenImageSizeExceeded() {

    }

    void upload_shouldBadRequest_whenFileTypeIsNotImage() {

    }

    void upload_shouldReturnNotFound_whenProductNotFound() {

    }

    void upload_shouldReturnForbidden_whenNotProductOwner() {

    }

    void upload_shouldReturnInternalError_whenImageNotFoundById_whileMappingByUrl() {

    }

}
