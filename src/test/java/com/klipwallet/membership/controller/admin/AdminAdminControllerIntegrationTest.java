package com.klipwallet.membership.controller.admin;

import java.util.List;

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
import com.klipwallet.membership.entity.Admin;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.repository.AdminRepository;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @WithSuperAdminUser(memberId = 1)
    @DisplayName("Admin 어드민 목록 > 200")
    @Test
    void list(@Autowired MockMvc mvc) throws Exception {
        // given
        createAdmins();
        // when/then
        mvc.perform(get("/admin/v1/admins"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(5))
           .andExpect(jsonPath("$.[0].id").isNumber())
           .andExpect(jsonPath("$.[0].email").value("ted.jeong@groundx.xyz"))
           .andExpect(jsonPath("$.[0].createdAt").isNotEmpty())
           .andExpect(jsonPath("$.[0].creator.id").value(1))
           .andExpect(jsonPath("$.[0].creator.name").value("Deactivated"))
           .andExpect(jsonPath("$.[1].email").value("ian.han@groundx.xyz"))
           .andExpect(jsonPath("$.[4].email").value("jordan.jung@groundx.xyz"))
        ;
    }

    private void createAdmins() {
        List<Admin> admins = List.of(
                new Admin("jordan.jung@groundx.xyz", new MemberId(1)),
                new Admin("winnie.byun@groundx.xyz", new MemberId(2)),
                new Admin("rachel.lee@groundx.xyz", new MemberId(1)),
                new Admin("ian.han@groundx.xyz", new MemberId(2)),
                new Admin("ted.jeong@groundx.xyz", new MemberId(1))
        );
        adminRepository.saveAll(admins);
        adminRepository.flush();
    }
}