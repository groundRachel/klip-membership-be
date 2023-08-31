package com.klipwallet.membership.controller.admin;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import com.klipwallet.membership.config.security.WithAdminUser;
import com.klipwallet.membership.config.security.WithPartnerUser;

import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AttachFileAdminControllerIntegrationTest {
    @DisplayName("Admin 이미지 파일 업로드 > 201")
    @WithAdminUser
    @Test
    void uploadImage(@Autowired MockMvc mvc) throws Exception {
        // given
        String filename = "klip-sample.png";
        ClassPathResource resource = new ClassPathResource("/attachFile/%s".formatted(filename));
        MockMultipartFile sampleFile = new MockMultipartFile("file", filename, MediaType.IMAGE_PNG_VALUE, resource.getInputStream());
        // when/then
        mvc.perform(MockMvcRequestBuilders.multipart("/admin/v1/files/upload-image")
                                          .file(sampleFile))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.attachFileId").isNotEmpty())
           .andExpect(jsonPath("$.linkUrl").value(Matchers.startsWith("http://localhost:8080/common/v1/temp-files/")))
           .andExpect(jsonPath("$.contentType").value(MediaType.IMAGE_PNG_VALUE))
           .andExpect(jsonPath("$.contentLength").value(116_465));
    }

    @DisplayName("Admin 이미지 파일 업로드: 파트너 권한 > 403")
    @WithPartnerUser
    @Test
    void uploadImageOnPartner(@Autowired MockMvc mvc) throws Exception {
        // given
        String filename = "klip-sample.png";
        ClassPathResource resource = new ClassPathResource("/attachFile/%s".formatted(filename));
        MockMultipartFile sampleFile = new MockMultipartFile("file", filename, MediaType.IMAGE_PNG_VALUE, resource.getInputStream());
        // when/then
        mvc.perform(MockMvcRequestBuilders.multipart("/admin/v1/files/upload-image")
                                          .file(sampleFile))
           .andExpect(status().isForbidden())
           .andExpect(forwardedUrl("/error/403"))
           .andExpect(request().attribute(WebAttributes.ACCESS_DENIED_403, instanceOf(AccessDeniedException.class)));
    }

    @DisplayName("Admin 이미지 파일 업로드: txt 파일 업로드 > 400")
    @WithAdminUser
    @Test
    void uploadImageButTextFile(@Autowired MockMvc mvc) throws Exception {
        // given
        String filename = "dummy.txt";
        ClassPathResource resource = new ClassPathResource("/attachFile/%s".formatted(filename));
        MockMultipartFile sampleFile = new MockMultipartFile("file", filename, MediaType.TEXT_PLAIN_VALUE, resource.getInputStream());
        // when/then
        mvc.perform(MockMvcRequestBuilders.multipart("/admin/v1/files/upload-image")
                                          .file(sampleFile))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value(400_001))
           .andExpect(jsonPath("$.err").value("이미지 파일만 업로드 가능합니다.(jpeg, png)"))
           .andExpect(jsonPath("$.errors.length()").value(1))
           .andExpect(jsonPath("$.errors[0].field").value("uploadImage.file"))
           .andExpect(jsonPath("$.errors[0].message").value("이미지 파일만 업로드 가능합니다.(jpeg, png)"));
    }

    @DisplayName("Admin 이미지 파일 업로드: 유효하지 않은 ContentType > 400")
    @WithAdminUser
    @Test
    void uploadImageInvalidContentType(@Autowired MockMvc mvc) throws Exception {
        // given
        String filename = "klip-sample.png";
        ClassPathResource resource = new ClassPathResource("/attachFile/%s".formatted(filename));
        MockMultipartFile sampleFile = new MockMultipartFile("file", filename, "text/image", resource.getInputStream());
        // when/then
        mvc.perform(MockMvcRequestBuilders.multipart("/admin/v1/files/upload-image")
                                          .file(sampleFile))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value(400_001))
           .andExpect(jsonPath("$.err").value("이미지 파일만 업로드 가능합니다.(jpeg, png)"))
           .andExpect(jsonPath("$.errors.length()").value(1))
           .andExpect(jsonPath("$.errors[0].field").value("uploadImage.file"))
           .andExpect(jsonPath("$.errors[0].message").value("이미지 파일만 업로드 가능합니다.(jpeg, png)"));
    }

    @DisplayName("Admin 이미지 파일 업로드: null filename > 200 성공함 !!!")
    @WithAdminUser
    @Test
    void uploadImageNullFilename(@Autowired MockMvc mvc) throws Exception {
        // given
        String filename = "klip-sample.png";
        ClassPathResource resource = new ClassPathResource("/attachFile/%s".formatted(filename));
        MockMultipartFile sampleFile = new MockMultipartFile("file", null, MediaType.IMAGE_PNG_VALUE, resource.getInputStream());
        // when/then
        mvc.perform(MockMvcRequestBuilders.multipart("/admin/v1/files/upload-image")
                                          .file(sampleFile))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.attachFileId").isNotEmpty())
           .andExpect(jsonPath("$.linkUrl").value(Matchers.startsWith("http://localhost:8080/common/v1/temp-files/")))
           .andExpect(jsonPath("$.contentType").value(MediaType.IMAGE_PNG_VALUE))
           .andExpect(jsonPath("$.contentLength").value(116_465));
    }

    @DisplayName("Admin 이미지 파일 업로드: empty filename and contentType > 400")
    @WithAdminUser
    @Test
    void uploadImageNullContentType(@Autowired MockMvc mvc) throws Exception {
        // given
        String filename = "klip-sample.png";
        ClassPathResource resource = new ClassPathResource("/attachFile/%s".formatted(filename));
        MockMultipartFile sampleFile = new MockMultipartFile("file", filename, null, resource.getInputStream());
        // when/then
        mvc.perform(MockMvcRequestBuilders.multipart("/admin/v1/files/upload-image")
                                          .file(sampleFile))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value(400_001))
           .andExpect(jsonPath("$.err").value("이미지 파일만 업로드 가능합니다.(jpeg, png)"))
           .andExpect(jsonPath("$.errors.length()").value(1))
           .andExpect(jsonPath("$.errors[0].field").value("uploadImage.file"))
           .andExpect(jsonPath("$.errors[0].message").value("이미지 파일만 업로드 가능합니다.(jpeg, png)"));
    }
}