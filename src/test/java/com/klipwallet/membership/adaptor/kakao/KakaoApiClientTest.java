package com.klipwallet.membership.adaptor.kakao;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.klipwallet.membership.adaptor.kakao.dto.CreateOpenlinkReq;
import com.klipwallet.membership.adaptor.kakao.dto.JoinOpenlinkReq;
import com.klipwallet.membership.adaptor.kakao.dto.JoinOpenlinkRes;
import com.klipwallet.membership.adaptor.kakao.dto.LeaveOpenlinkReq;
import com.klipwallet.membership.adaptor.kakao.dto.OpenlinkResItem;
import com.klipwallet.membership.adaptor.kakao.dto.OpenlinkSummaryRes;
import com.klipwallet.membership.adaptor.kakao.dto.UpdateOpenlinkReq;
import com.klipwallet.membership.adaptor.kakao.dto.UpdateProfileReq;
import com.klipwallet.membership.config.KakaoApiProperties;

import static com.klipwallet.membership.adaptor.kakao.KakaoAdaptor.DEFAULT_TARGET_ID_TYPE;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:kakao-api-test.properties")
@Disabled("수동으로 한번만 테스트")
class KakaoApiClientTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    KakaoApiClient kakaoApiClient;
    @Autowired
    private KakaoApiProperties kakaoConf;

    @Value("${user-id}")
    private String kakaoUserId;
    @Value("${participant-id}")
    private String kakaoParticipantId;

    @Test
    void OpenlinkTestAll() {
        var testLinkName = "TEST_OL";
        var changedLinkName = "TEST_OL_2";

        // Create
        OpenlinkSummaryRes resp = kakaoApiClient.createOpenlink(new CreateOpenlinkReq(
                kakaoUserId, kakaoConf.getDomainId(),
                testLinkName,
                null,
                "test desc",
                "createOpenlinkTest",
                null
        ));

        // Get
        List<OpenlinkResItem> getResp = kakaoApiClient.getOpenlink(kakaoConf.getDomainId(), "[%d]".formatted(resp.linkId()));
        assertThat(getResp.get(0).linkName()).isEqualTo(testLinkName);

        // Update openlink
        resp = kakaoApiClient.updateOpenlink(new UpdateOpenlinkReq(
                kakaoUserId,
                kakaoConf.getDomainId(),
                resp.linkId(),
                changedLinkName,
                null,
                null
        ));

        // Get updated openlink
        getResp = kakaoApiClient.getOpenlink(kakaoConf.getDomainId(), "[%d]".formatted(resp.linkId()));
        assertThat(getResp.get(0).linkName()).isEqualTo(changedLinkName);

        // Join openlink
        JoinOpenlinkRes joinResp =
                kakaoApiClient.joinOpenlink(new JoinOpenlinkReq(kakaoParticipantId, "joinNickname",
                                                                null, kakaoConf.getDomainId(), resp.linkId()));

        // Update joined user
        kakaoApiClient.updateProfile(new UpdateProfileReq(kakaoParticipantId, "updateNickname", null,
                                                          kakaoConf.getDomainId(), joinResp.linkId()));
        // Leave openlink
        kakaoApiClient.leaveOpenlink(new LeaveOpenlinkReq(kakaoParticipantId,
                                                          kakaoConf.getDomainId(), joinResp.linkId()));
        // Delete openlink
        kakaoApiClient.deleteOpenlink(kakaoUserId, DEFAULT_TARGET_ID_TYPE, kakaoConf.getDomainId(), resp.linkId());
    }
}
