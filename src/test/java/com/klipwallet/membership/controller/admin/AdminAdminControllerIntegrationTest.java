package com.klipwallet.membership.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.klipwallet.membership.config.security.WithAdminUser;
import com.klipwallet.membership.config.security.WithSuperAdminUser;
import com.klipwallet.membership.repository.AdminRepository;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminAdminControllerIntegrationTest {
    @Autowired
    AdminRepository adminRepository;
    @Autowired
    ObjectMapper om;

    @BeforeEach
    void setUp() {
        clearNotices();
    }

    @AfterEach
    void tearDown() {
        clearNotices();
    }

    private void clearNotices() {
        adminRepository.deleteAll();
        adminRepository.flush();
    }

    @WithSuperAdminUser
    @DisplayName("Admin 어드민 등록 > 201")
    @Test
    void register(@Autowired MockMvc mvc) throws Exception {
        String body = """
                      {
                        "email": "jordan.jung@groundx.xyz"
                      }
                      """;
        mvc.perform(post("/admin/v1/admins")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").isNumber())
           .andExpect(jsonPath("$.email").value("jordan.jung@groundx.xyz"));
    }

    @WithAdminUser
    @DisplayName("Admin 어드민 등록: 관리자 권한 > 403")
    @Test
    void registerOnAdmin(@Autowired MockMvc mvc) throws Exception {
        String body = """
                      {
                        "email": "jordan.jung@groundx.xyz"
                      }
                      """;
        mvc.perform(post("/admin/v1/admins")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isForbidden());
    }

    @WithSuperAdminUser
    @DisplayName("Admin 어드민 등록: 이메일 주소 없음 > 400")
    @Test
    void registerWithNonEmail(@Autowired MockMvc mvc) throws Exception {
        String body = """
                      {
                        "email": null
                      }
                      """;
        mvc.perform(post("/admin/v1/admins")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value(400_001))
           .andExpect(jsonPath("$.err").value("email: 'must not be null'"));
    }

    @WithSuperAdminUser
    @DisplayName("Admin 어드민 등록: 외부 이메일 주소 > 400")
    @Test
    void registerWithExternalEmail(@Autowired MockMvc mvc) throws Exception {
        String body = """
                      {
                        "email": "jordan.jung@gmail.com"
                      }
                      """;
        mvc.perform(post("/admin/v1/admins")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value(400_001))
           .andExpect(jsonPath("$.err").value("email: 'GroundX 임직원 이메일만 사용할 수 있습니다.'"));
    }
}