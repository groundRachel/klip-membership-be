package com.klipwallet.membership.adaptor.kakao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.klipwallet.membership.entity.KakaoOpenChatRoomOpened;
import com.klipwallet.membership.entity.kakao.OpenChatRoomHost;
import com.klipwallet.membership.entity.kakao.OpenChatRoomId;
import com.klipwallet.membership.service.KakaoService;

@Component
@Slf4j
@RequiredArgsConstructor
public class KakaoAdaptor implements KakaoService {
    public static final String DEFAULT_TARGET_ID_TYPE = "user_id";
    public static final boolean DEFAULT_IGNORE_KICK_STATUS = false;

    private final KakaoApiClient apiClient;

    @Override
    public OpenChatRoomId createOpenChatRoom(String title, String coverImage, OpenChatRoomHost host) {
        //TODO: Ian
        //         OpenlinkSummaryRes res = apiClient.createOpenlink(new CreateOpenlinkReq());
        //        return new OpenChatRoomId(res.getId());
        return null;
    }

    /**
     * {@link KakaoOpenChatRoomOpened} 이벤트 구독 메서드. <b>다른 용도로 명시적으로 호출하지 않는다.</b>
     * <p>
     * {@link com.klipwallet.membership.service.ChatRoomService#create(com.klipwallet.membership.dto.chatroom.ChatRoomCreate, com.klipwallet.membership.entity.AuthenticatedUser)}
     * 에서 카카오 오픈채팅 생성은 성공했지만 다른 이슈로 인해서 DB Rollback이 된 경우 이미 개설된 카카오 오픈 채팅을 제거하기 위해서 호출한다.
     * </p>
     *
     * @param event 카카오 오픈채팅이 생성됨 DomainEvent
     * @see com.klipwallet.membership.entity.KakaoOpenChatRoomOpened
     * @see com.klipwallet.membership.service.KakaoService#createOpenChatRoom(String, String, com.klipwallet.membership.entity.kakao.OpenChatRoomHost)
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void subscribeOnRollback(KakaoOpenChatRoomOpened event) {
        OpenChatRoomId id = event.getOpenChatRoomId();
        log.warn("[KAKAO][OPEN_MEETING] OpenChatRoom try remove: {}", id);
        try {
            this.removeOpenChatRoom(id);
            log.info("[KAKAO][OPEN_MEETING] OpenChatRoom is removed: {}", id);
        } catch (Exception cause) {
            log.error("[KAKAO][OPEN_MEETING] Failed to remove OpenChatRoom: {}", id);
        }
    }

    private void removeOpenChatRoom(OpenChatRoomId id) {
        // TODO Impl
    }
}
