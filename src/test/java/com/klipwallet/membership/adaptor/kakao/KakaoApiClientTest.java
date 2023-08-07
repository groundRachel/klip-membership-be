package com.klipwallet.membership.adaptor.kakao;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.klipwallet.membership.config.KakaoApiProperties;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class KakaoApiClientTest {
    @Autowired
    KakaoApiClient kakaoApiClient;
    @Autowired
    private KakaoApiProperties kakaoConf;

    @Test
    void OpenlinkTestAll() {
        var testLinkName = "TEST_OL";
        var changedLinkName = "TEST_OL_2";

        // Create
        ShortOpenlinkResp resp = kakaoApiClient.createOpenlink(new CreateOpenlinkReqDto(
            kakaoConf.getTestUserId(),
            kakaoConf.getUserIdType(),
            kakaoConf.getDomainId(),
            testLinkName,
            null,
            "test desc",
            "createOpenlinkTest",
            null
        ));

        // Get
        List<GetOpenlinkResponseItem> getResp = kakaoApiClient.getOpenlink(kakaoConf.getDomainId(), "[%d]".formatted(resp.linkId()));
        assertThat(getResp.get(0).linkName()).isEqualTo(testLinkName);

        // Update openlink
        resp = kakaoApiClient.updateOpenlink(new UpdateOpenlinkReqDto(
            kakaoConf.getTestUserId(),
            kakaoConf.getUserIdType(),
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
        JoinOpenlinkResp joinResp =
            kakaoApiClient.joinOpenlink(new JoinOpenlinkReqDto(kakaoConf.getTestParticipantId(), kakaoConf.getUserIdType(), "joinNickname",
                                                               null, kakaoConf.getDomainId(), resp.linkId()));

        // Update joined user
        kakaoApiClient.updateProfile(new UpdateProfileReqDto(kakaoConf.getTestParticipantId(), kakaoConf.getUserIdType(), "updateNickname", null,
                                                             kakaoConf.getDomainId(), joinResp.linkId()));
        // Leave openlink
        kakaoApiClient.leaveOpenlink(new LeaveOpenlinkReqDto(kakaoConf.getTestParticipantId(), kakaoConf.getUserIdType(),
                                                             kakaoConf.getDomainId(), joinResp.linkId()));
        // Delete openlink
        kakaoApiClient.deleteOpenlink(kakaoConf.getTestUserId(), kakaoConf.getUserIdType(), kakaoConf.getDomainId(), resp.linkId());
    }
}
