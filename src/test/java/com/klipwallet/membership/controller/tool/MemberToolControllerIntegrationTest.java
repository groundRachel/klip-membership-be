package com.klipwallet.membership.controller.tool;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.klipwallet.membership.config.security.WithAuthenticatedUser;
import com.klipwallet.membership.config.security.WithKakaoUser;
import com.klipwallet.membership.config.security.WithPartnerUser;

import static com.klipwallet.membership.config.SecurityConfig.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberToolControllerIntegrationTest {

    @DisplayName("Tool 인증 정보: Jordan Company > 200")
    @WithPartnerUser(memberId = 9)
    @Test
    void authenticationOnPartnerJordan(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/tool/v1/members/me"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.authenticated").value(true))
           .andExpect(jsonPath("$.member").value(true))
           .andExpect(jsonPath("$.profile.memberId").value(9))
           .andExpect(jsonPath("$.profile.name").value("Jordan Company"))
           .andExpect(jsonPath("$.profile.email").value("jordan.jung@groundx.xyz"))
           .andExpect(jsonPath("$.authorities.length()").value(1))
           .andExpect(jsonPath("$.authorities[0]").value(ROLE_PARTNER))
        ;
    }

    @DisplayName("Tool 인증 정보: Rachel Company > 200")
    @WithPartnerUser(memberId = 10)
    @Test
    void authenticationOnPartnerRachel(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/tool/v1/members/me"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.authenticated").value(true))
           .andExpect(jsonPath("$.member").value(true))
           .andExpect(jsonPath("$.profile.memberId").value(10))
           .andExpect(jsonPath("$.profile.name").value("Rachel Company"))
           .andExpect(jsonPath("$.profile.email").value("rachel.lee@groundx.xyz"))
           .andExpect(jsonPath("$.authorities.length()").value(1))
           .andExpect(jsonPath("$.authorities[0]").value(ROLE_PARTNER))
        ;
    }

    @WithAuthenticatedUser(memberId = 0, authorities = OAUTH2_USER)
    @DisplayName("Google OAuth2 인증한 경우 > 200: But not a member(authenticated: true, member: false)")
    @Test
    void authenticationOnGoogle(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/tool/v1/members/me"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.authenticated").value(true))
           .andExpect(jsonPath("$.member").value(false))
           .andExpect(jsonPath("$.profile").doesNotExist())
           .andExpect(jsonPath("$.authorities.length()").value(1))
           .andExpect(jsonPath("$.authorities[0]").value(OAUTH2_USER))
        ;
    }

    @WithKakaoUser
    @DisplayName("카카오 OAuth2 인증한 경우 > 200: But not a member(authenticated: false, member: false)")
    @Test
    void authenticationOnKakao(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/tool/v1/members/me"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.authenticated").value(false))
           .andExpect(jsonPath("$.member").value(false))
           .andExpect(jsonPath("$.profile").doesNotExist())
           .andExpect(jsonPath("$.authorities.length()").value(1))
           .andExpect(jsonPath("$.authorities[0]").value(ROLE_KLIP_KAKAO))
        ;
    }

    @DisplayName("인증 없는 경우 > 200: But not Authenticated(authenticated: false)")
    @Test
    void authenticationNoAuth(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/tool/v1/members/me"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.authenticated").value(false))
           .andExpect(jsonPath("$.member").value(false))
           .andExpect(jsonPath("$.profile").doesNotExist())
           .andExpect(jsonPath("$.authorities.length()").value(0))
        ;
    }
}