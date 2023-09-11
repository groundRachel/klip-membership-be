package com.klipwallet.membership.controller.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.klipwallet.membership.config.security.WithAuthenticatedUser;
import com.klipwallet.membership.config.security.WithKakaoUser;
import com.klipwallet.membership.config.security.WithPartnerUser;
import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.entity.SignUpStatus;
import com.klipwallet.membership.repository.PartnerApplicationRepository;

import static com.klipwallet.membership.config.SecurityConfig.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberToolControllerIntegrationTest {
    @Autowired
    PartnerApplicationRepository partnerApplicationRepository;

    @BeforeEach
    void setUp() {
        partnerApplicationRepository.deleteAll();
        partnerApplicationRepository.flush();
    }

    @DisplayName("Tool 인증 정보: Jordan Company > 200")
    @WithPartnerUser(memberId = 9)
    @Test
    void authenticationOnPartnerJordan(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/tool/v1/members/me"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.isAuthenticated").value(true))
           .andExpect(jsonPath("$.isMember").value(true))
           .andExpect(jsonPath("$.partnerStatus").value(SignUpStatus.SIGNED_UP.toDisplay()))
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
           .andExpect(jsonPath("$.isAuthenticated").value(true))
           .andExpect(jsonPath("$.isMember").value(true))
           .andExpect(jsonPath("$.partnerStatus").value(SignUpStatus.SIGNED_UP.toDisplay()))
           .andExpect(jsonPath("$.profile.memberId").value(10))
           .andExpect(jsonPath("$.profile.name").value("Rachel Company"))
           .andExpect(jsonPath("$.profile.email").value("rachel.lee@groundx.xyz"))
           .andExpect(jsonPath("$.authorities.length()").value(1))
           .andExpect(jsonPath("$.authorities[0]").value(ROLE_PARTNER))
        ;
    }

    @WithAuthenticatedUser(memberId = 0, email = "before.partner@groundx.xyz", authorities = OAUTH2_USER)
    @DisplayName("Google OAuth2 인증한 경우 > 200: 파트너 가입 신청 전")
    @Test
    void authenticationOnGoogle(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/tool/v1/members/me"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.isAuthenticated").value(true))
           .andExpect(jsonPath("$.isMember").value(false))
           .andExpect(jsonPath("$.partnerStatus").value(SignUpStatus.NON_MEMBER.toDisplay()))
           .andExpect(jsonPath("$.profile").doesNotExist())
           .andExpect(jsonPath("$.authorities.length()").value(1))
           .andExpect(jsonPath("$.authorities[0]").value(OAUTH2_USER))
        ;
    }

    @WithAuthenticatedUser(memberId = 0, email = "pending.partner@groundx.xyz", name = "4959382822", authorities = OAUTH2_USER)
    @DisplayName("Google OAuth2 인증한 경우 > 200: 파트너 가입 대기 중")
    @Test
    void authenticationOnGoogleAndPending(@Autowired MockMvc mvc) throws Exception {
        submitPartnerApplication("pending.partner@groundx.xyz", "4959382822");

        mvc.perform(get("/tool/v1/members/me"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.isAuthenticated").value(true))
           .andExpect(jsonPath("$.isMember").value(false))
           .andExpect(jsonPath("$.partnerStatus").value(SignUpStatus.PENDING.toDisplay()))
           .andExpect(jsonPath("$.profile").doesNotExist())
           .andExpect(jsonPath("$.authorities.length()").value(1))
           .andExpect(jsonPath("$.authorities[0]").value(OAUTH2_USER))
        ;
    }

    @SuppressWarnings("SameParameterValue")
    private void submitPartnerApplication(String email, String oauthId) {
        PartnerApplication application = new PartnerApplication("Pending Company", "010-2345-7890", "111-22-33333", email, oauthId);
        partnerApplicationRepository.save(application);
        partnerApplicationRepository.flush();
    }

    @WithKakaoUser
    @DisplayName("카카오 OAuth2 인증한 경우 > 200: But not a member(authenticated: false, member: false)")
    @Test
    void authenticationOnKakao(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/tool/v1/members/me"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.isAuthenticated").value(false))
           .andExpect(jsonPath("$.isMember").value(false))
           .andExpect(jsonPath("$.partnerStatus").doesNotExist())
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
           .andExpect(jsonPath("$.isAuthenticated").value(false))
           .andExpect(jsonPath("$.isMember").value(false))
           .andExpect(jsonPath("$.partnerStatus").doesNotExist())
           .andExpect(jsonPath("$.profile").doesNotExist())
           .andExpect(jsonPath("$.authorities.length()").value(0))
        ;
    }
}