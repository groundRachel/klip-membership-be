package com.klipwallet.membership.adaptor.kakao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.klipwallet.membership.adaptor.kakao.dto.CreateOpenlinkReq;
import com.klipwallet.membership.adaptor.kakao.dto.JoinOpenlinkReq;
import com.klipwallet.membership.adaptor.kakao.dto.JoinOpenlinkRes;
import com.klipwallet.membership.adaptor.kakao.dto.OpenlinkSummaryRes;
import com.klipwallet.membership.config.KakaoApiProperties;
import com.klipwallet.membership.entity.KakaoOpenChattingOpened;
import com.klipwallet.membership.entity.kakao.KakaoOpenlinkSummary;
import com.klipwallet.membership.entity.kakao.OpenChattingHost;
import com.klipwallet.membership.service.kakao.KakaoService;

@Component
@Slf4j
@RequiredArgsConstructor
public class KakaoAdaptor implements KakaoService {
    public static final String DEFAULT_TARGET_ID_TYPE = "user_id";
    public static final boolean DEFAULT_IGNORE_KICK_STATUS = false;
    private final KakaoApiClient apiClient;
    private final KakaoApiProperties kakaoApiProperties;

    @Override
    public KakaoOpenlinkSummary createOpenChatting(String title, String description, String coverImage, OpenChattingHost host) {
        OpenlinkSummaryRes res = apiClient.createOpenlink(
                new CreateOpenlinkReq(host.getKakaoId().getId(), kakaoApiProperties.getDomainId(), title, coverImage, description, host.getNickname(),
                                      host.getProfileImageUrl()));
        return new KakaoOpenlinkSummary(res.linkId(), res.linkUrl());
    }

    @Override
    public KakaoOpenlinkSummary joinOpenChatting(Long linkId, String nickname, String profileImage, String targetId) {
        JoinOpenlinkRes res = apiClient.joinOpenlink(
                new JoinOpenlinkReq(targetId, nickname, profileImage, kakaoApiProperties.getDomainId(), linkId));
        return new KakaoOpenlinkSummary(res.linkId(), res.linkUrl());
    }

    /**
     * {@link com.klipwallet.membership.entity.KakaoOpenChattingOpened} 이벤트 구독 메서드. <b>다른 용도로 명시적으로 호출하지 않는다.</b>
     * <p>
     * {@link com.klipwallet.membership.service.OpenChattingService#create(com.klipwallet.membership.dto.openchatting.OpenChattingCreate, com.klipwallet.membership.entity.AuthenticatedUser)}
     * 에서 카카오 오픈채팅 생성은 성공했지만 다른 이슈로 인해서 DB Rollback이 된 경우 이미 개설된 카카오 오픈 채팅을 제거하기 위해서 호출한다.
     * </p>
     *
     * @param event 카카오 오픈채팅이 생성됨 DomainEvent
     * @see com.klipwallet.membership.entity.KakaoOpenChattingOpened
     * @see com.klipwallet.membership.service.kakao.KakaoService#createOpenChatting(String, String, String, com.klipwallet.membership.entity.kakao.OpenChattingHost)
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void subscribeOnRollback(KakaoOpenChattingOpened event) {
        KakaoOpenlinkSummary kakaoOpenlinkSummary = event.getKakaoOpenlinkSummary();
        log.warn("[KAKAO][OPEN_MEETING] OpenChatting try remove: {}", kakaoOpenlinkSummary.getId());
        try {
            this.removeOpenChatting(kakaoOpenlinkSummary.getId());
            log.info("[KAKAO][OPEN_MEETING] OpenChatting is removed: {}", kakaoOpenlinkSummary.getId());
        } catch (Exception cause) {
            log.error("[KAKAO][OPEN_MEETING] Failed to remove OpenChatting: {}", kakaoOpenlinkSummary.getId());
        }
    }

    private void removeOpenChatting(Long id) {
        // TODO Impl
    }
}
