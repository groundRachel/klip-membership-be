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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @DisplayName("Admin 어드민 목록 조회 > 200")
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

    @WithSuperAdminUser
    @DisplayName("Admin 어드민 상세 조회 > 200")
    @Test
    void detail(@Autowired MockMvc mvc) throws Exception {
        // given
        MemberId adminId = createAdmin();
        // when/then
        mvc.perform(get("/admin/v1/admins/{0}", adminId.value()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").isNumber())
           .andExpect(jsonPath("$.email").value("jordan.jung@groundx.xyz"))
           .andExpect(jsonPath("$.name").value("jordan.jung"))
           .andExpect(jsonPath("$.oAuthId").isEmpty())      // !!
           .andExpect(jsonPath("$.createdAt").isNotEmpty())
           .andExpect(jsonPath("$.creator.id").value(1))
           .andExpect(jsonPath("$.creator.name").value("Deactivated"))
           .andExpect(jsonPath("$.updatedAt").isNotEmpty())
           .andExpect(jsonPath("$.updater.id").value(1))
           .andExpect(jsonPath("$.updater.name").value("Deactivated"));
    }

    private MemberId createAdmin() {
        Admin admin = new Admin("jordan.jung@groundx.xyz", new MemberId(1));
        Admin persisted = adminRepository.save(admin);
        adminRepository.flush();
        return persisted.getMemberId();
    }

    @WithSuperAdminUser
    @DisplayName("Admin 어드민 상세 조회 > 404")
    @Test
    void detailNotExists(@Autowired MockMvc mvc) throws Exception {
        // when/then
        mvc.perform(get("/admin/v1/admins/{0}", Integer.MAX_VALUE))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.code").value(404_006))
           .andExpect(jsonPath("$.err").value("어드민을 찾을 수 없습니다. ID: %d".formatted(Integer.MAX_VALUE)));
    }

    @WithSuperAdminUser
    @DisplayName("Admin 어드민 탈퇴 > 204")
    @Test
    void withdraw(@Autowired MockMvc mvc) throws Exception {
        // given
        MemberId adminId = createAdmin();
        // when/then
        mvc.perform(delete("/admin/v1/admins/{0}", adminId))
           .andExpect(status().isNoContent());
    }
}