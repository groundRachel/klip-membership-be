package com.klipwallet.membership.controller.tool;

import java.io.IOException;

import jakarta.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.UriComponentsBuilder;

import com.klipwallet.membership.adaptor.klip.KlipAccount;
import com.klipwallet.membership.config.security.WithKakaoUser;
import com.klipwallet.membership.config.security.WithPartnerUser;
import com.klipwallet.membership.controller.LocalController.InviteResult;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.entity.OperatorInvitation;
import com.klipwallet.membership.entity.kakao.KakaoId;
import com.klipwallet.membership.service.InvitationRegistry;
import com.klipwallet.membership.service.KlipAccountService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 운영진 초대 전체 흐름을 확인하는 통합테스트
 * <p>
 * 1. 파트너가 운영진 초대 API 호출
 * 2. 초대자가 가입 링크로 접근 후 약관 동의(이건 api로 처리 안 함)
 * 3. 운영진 가입 완료
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OperatorInvitationFlowIntegrationTest {
    /**
     * 테스트 상 9번 파트너가 존재함.
     */
    static final int PARTNER_ID = 9;

    static final String KAKAO_ID = "2959264750";
    static final String EMAIL = "jordan.gx@kakaocorp.com";
    static String INVITATION_URL;

    @Autowired
    ObjectMapper om;

    @MockBean
    KlipAccountService klipAccountService;

    @WithPartnerUser(memberId = PARTNER_ID)
    @Test
    @Order(1)
    void inviteOperator(@Autowired MockMvc mvc) throws Exception {
        var ra = mvc.perform(post("/tool/v1/operators/invite-local")
                                     .param("phone", "01026382580"))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.invitationUrl").value(startsWith("http")));
        setInvitationUrl(ra);
    }

    private void setInvitationUrl(ResultActions ra) throws IOException {
        InviteResult result = om.readValue(ra.andReturn().getResponse().getContentAsString(), InviteResult.class);
        INVITATION_URL = result.invitationUrl();
        log.info("invitationUrl: {}", INVITATION_URL);
    }

    @SuppressWarnings("DefaultAnnotationParam")
    @WithKakaoUser(name = KAKAO_ID)
    @Test
    @Order(2)
    void joinOperator(@Autowired MockMvc mvc, @Autowired InvitationRegistry invitationRegistry) throws Exception {
        if (INVITATION_URL == null) {
            throw new AssertionError("%s 테스트 전체를 실행해야 합니다.".formatted(getClass().getSimpleName()));
        }
        String invitationCode = getInvitationCode();

        KlipUser klipUser = new KlipAccount(641L, KAKAO_ID, EMAIL, "+82 10-2638-2580");
        given(klipAccountService.getKlipUser(new KakaoId(KAKAO_ID))).willReturn(klipUser);

        HttpSession session =
                mvc.perform(post("/external/v1/operators")
                                    .sessionAttr(OperatorInvitation.STORE_KEY, invitationCode))
                   .andExpect(status().isCreated())
                   .andExpect(jsonPath("$.id").isNotEmpty())
                   .andExpect(jsonPath("$.klipId").value(klipUser.getKlipAccountId()))
                   .andReturn().getRequest().getSession();

        then(klipAccountService).should().getKlipUser(new KakaoId(KAKAO_ID));
        // 운영진 초대 후 가입이 완료되었기 때문에 운영진 초대 정보는 없어야함.
        assertThat(invitationRegistry.lookup(invitationCode)).isNull();
        assert session != null;
        // 세션에서도 초대코드는 제거되어야 한다.
        assertThat(session.getAttribute(OperatorInvitation.STORE_KEY)).isNull();
    }

    private String getInvitationCode() {
        return UriComponentsBuilder.fromHttpUrl(INVITATION_URL).build()
                                   .getQueryParams().getFirst("code");
    }
}
