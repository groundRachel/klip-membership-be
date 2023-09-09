package com.klipwallet.membership.controller.admin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.klipwallet.membership.config.security.WithAdminUser;
import com.klipwallet.membership.config.security.WithAuthenticatedUser;
import com.klipwallet.membership.config.security.WithSuperAdminUser;

import static com.klipwallet.membership.config.SecurityConfig.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberAdminControllerIntegrationTest {

    @DisplayName("Admin 인증 정보 > 200")
    @WithAdminUser(memberId = 2)
    @Test
    void authenticationOnAdmin(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/admin/v1/members/me"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.isAuthenticated").value(true))
           .andExpect(jsonPath("$.isMember").value(true))
           .andExpect(jsonPath("$.profile.memberId").value(2))
           .andExpect(jsonPath("$.profile.name").value("jordan.jung"))
           .andExpect(jsonPath("$.profile.email").value("jordan.jung@groundx.xyz"))
           .andExpect(jsonPath("$.authorities.length()").value(1))
           .andExpect(jsonPath("$.authorities[0]").value(ROLE_ADMIN))
        ;
    }

    @DisplayName("SuperAdmin 인증 정보 > 200 (authorities: [ROLE_ADMIN, ROLE_SUPER_ADMIN])")
    @WithSuperAdminUser(memberId = 1)
    @Test
    void authenticationOnSuperAdmin(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/admin/v1/members/me"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.isAuthenticated").value(true))
           .andExpect(jsonPath("$.isMember").value(true))
           .andExpect(jsonPath("$.profile.memberId").value(1))
           .andExpect(jsonPath("$.profile.name").value("gene.goh"))
           .andExpect(jsonPath("$.profile.email").value("gene.goh@groundx.xyz"))
           .andExpect(jsonPath("$.authorities.length()").value(2))
           .andExpect(jsonPath("$.authorities").value(containsInAnyOrder(ROLE_ADMIN, ROLE_SUPER_ADMIN)))
        ;
    }

    @WithAuthenticatedUser(memberId = 0, authorities = OAUTH2_USER)
    @DisplayName("Google OAuth2 인증한 경우 > 200: But not a member(authenticated: true, member: false)")
    @Test
    void authenticationOnGoogle(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/admin/v1/members/me"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.isAuthenticated").value(true))
           .andExpect(jsonPath("$.isMember").value(false))
           .andExpect(jsonPath("$.profile").doesNotExist())
           .andExpect(jsonPath("$.authorities.length()").value(1))
           .andExpect(jsonPath("$.authorities[0]").value(OAUTH2_USER))
        ;
    }

    @DisplayName("인증 없는 경우 > 200: But not Authenticated(authenticated: false)")
    @Test
    void authenticationNoAuth(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/admin/v1/members/me"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.isAuthenticated").value(false))
           .andExpect(jsonPath("$.isMember").value(false))
           .andExpect(jsonPath("$.profile").doesNotExist())
           .andExpect(jsonPath("$.authorities.length()").value(0))
        ;
    }
}