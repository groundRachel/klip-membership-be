package com.klipwallet.membership.controller.tool;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.WebAttributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.klipwallet.membership.config.security.WithAuthenticatedUser;
import com.klipwallet.membership.config.security.WithPartnerUser;
import com.klipwallet.membership.entity.UploadType;

import static com.klipwallet.membership.config.SecurityConfig.OAUTH2_USER;
import static com.klipwallet.membership.entity.UploadType.COVER;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AttachFileToolControllerIntegrationTest {
    @DisplayName("Tool 이미지 파일 업로드 > 201")
    @WithPartnerUser
    @ParameterizedTest
    @EnumSource(value = UploadType.class, names = "EDITOR", mode = Mode.EXCLUDE)
    void uploadImage(UploadType type, @Autowired MockMvc mvc) throws Exception {
        // given
        String filename = "klip-sample.png";
        ClassPathResource resource = new ClassPathResource("/attachFile/%s".formatted(filename));
        MockMultipartFile sampleFile = new MockMultipartFile("file", filename, MediaType.IMAGE_PNG_VALUE, resource.getInputStream());
        // when/then
        mvc.perform(MockMvcRequestBuilders.multipart("/tool/v1/files/upload-image")
                                          .file(sampleFile)
                                          .param("type", type.toDisplay()))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.attachFileId").isNotEmpty())
           .andExpect(jsonPath("$.linkUrl").value(Matchers.startsWith("http://localhost:8080/common/v1/temp-files/")))
           .andExpect(jsonPath("$.contentType").value(MediaType.IMAGE_PNG_VALUE))
           .andExpect(jsonPath("$.contentLength").value(116_465));
    }

    @DisplayName("Tool 이미지 파일 업로드: 비회원 권한 > 403")
    @WithAuthenticatedUser(memberId = -1, authorities = OAUTH2_USER)
    @ParameterizedTest
    @EnumSource(value = UploadType.class, names = "EDITOR", mode = Mode.EXCLUDE)
    void uploadImageOnPartner(UploadType type, @Autowired MockMvc mvc) throws Exception {
        // given
        String filename = "klip-sample.png";
        ClassPathResource resource = new ClassPathResource("/attachFile/%s".formatted(filename));
        MockMultipartFile sampleFile = new MockMultipartFile("file", filename, MediaType.IMAGE_PNG_VALUE, resource.getInputStream());
        // when/then
        mvc.perform(MockMvcRequestBuilders.multipart("/tool/v1/files/upload-image")
                                          .file(sampleFile)
                                          .param("type", type.toDisplay()))
           .andExpect(status().isForbidden())
           .andExpect(forwardedUrl("/error/403"))
           .andExpect(request().attribute(WebAttributes.ACCESS_DENIED_403, instanceOf(AccessDeniedException.class)));
    }

    @DisplayName("Tool 이미지 파일 업로드: no 'type' query > 400")
    @WithPartnerUser
    @Test
    void uploadImageOnNoType(@Autowired MockMvc mvc) throws Exception {
        // given
        String filename = "klip-sample.png";
        ClassPathResource resource = new ClassPathResource("/attachFile/%s".formatted(filename));
        MockMultipartFile sampleFile = new MockMultipartFile("file", resource.getInputStream());
        // when/then
        mvc.perform(MockMvcRequestBuilders.multipart("/tool/v1/files/upload-image")
                                          .file(sampleFile))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value(400_000))
           .andExpect(jsonPath("$.err").value("Required parameter 'type' is not present."));
    }

    @DisplayName("Tool 이미지 파일 업로드: txt 파일 업로드 > 400")
    @WithPartnerUser
    @ParameterizedTest
    @EnumSource(value = UploadType.class, names = "EDITOR", mode = Mode.EXCLUDE)
    void uploadImageButTextFile(UploadType type, @Autowired MockMvc mvc) throws Exception {
        // given
        String filename = "dummy.txt";
        ClassPathResource resource = new ClassPathResource("/attachFile/%s".formatted(filename));
        MockMultipartFile sampleFile = new MockMultipartFile("file", filename, MediaType.TEXT_PLAIN_VALUE, resource.getInputStream());
        // when/then
        mvc.perform(MockMvcRequestBuilders.multipart("/tool/v1/files/upload-image")
                                          .file(sampleFile)
                                          .param("type", type.toDisplay()))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value(400_001))
           .andExpect(jsonPath("$.err").value("이미지 파일만 업로드 가능합니다.(jpeg, png)"))
           .andExpect(jsonPath("$.errors.length()").value(1))
           .andExpect(jsonPath("$.errors[0].field").value("uploadImage.file"))
           .andExpect(jsonPath("$.errors[0].message").value("이미지 파일만 업로드 가능합니다.(jpeg, png)"));
    }

    @DisplayName("Tool Cover 이미지 파일 업로드(498KB): 400KB 용량 초과 > 400")
    @WithPartnerUser
    @Test
    void uploadImageButSizeLimitOver(@Autowired MockMvc mvc) throws Exception {
        // given
        String filename = "489K.jpeg";   // Over 400K
        ClassPathResource resource = new ClassPathResource("/attachFile/%s".formatted(filename));
        MockMultipartFile sampleFile = new MockMultipartFile("file", filename, MediaType.IMAGE_JPEG_VALUE, resource.getInputStream());
        // when/then
        mvc.perform(MockMvcRequestBuilders.multipart("/tool/v1/files/upload-image")
                                          .file(sampleFile)
                                          .param("type", COVER.toDisplay()))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value(400_006))
           .andExpect(jsonPath("$.err").value("파일 업로드 제한 사이즈를 초과했습니다. limit: 400 KB, upload: 477 KB"));
    }
}