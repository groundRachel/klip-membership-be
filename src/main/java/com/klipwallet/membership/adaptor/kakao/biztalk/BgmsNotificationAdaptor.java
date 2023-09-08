package com.klipwallet.membership.adaptor.kakao.biztalk;

import java.util.UUID;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.klipwallet.membership.adaptor.kakao.biztalk.dto.BgmsBaseRes;
import com.klipwallet.membership.adaptor.kakao.biztalk.dto.BgmsSendAlimTalkReq;
import com.klipwallet.membership.adaptor.kakao.biztalk.dto.BgmsSendAlimTalkReq.Button;
import com.klipwallet.membership.config.BgmsProperties;
import com.klipwallet.membership.entity.ChatRoom;
import com.klipwallet.membership.entity.InviteOperatorNotifiable;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.exception.InternalApiException;
import com.klipwallet.membership.service.InvitationNotifier;

/**
 * BGMS(Biztalk Global Message System)로 구현된 초대 알리미
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BgmsNotificationAdaptor implements InvitationNotifier {
    private final BgmsApiClient bgmsApiClient;
    private final BgmsTokenProvider bgmsTokenProvider;
    private final BgmsProperties properties;

    private BgmsToken prepareToken() {
        return bgmsTokenProvider.prepareToken();
    }

    /**
     * {@inheritDoc}
     * <p>
     * biztalk의 BGMS를 이용해서 카카오 알림톡 발송
     * </p>
     */
    @Override
    public void notifyToInviteChatRoom(ChatRoom chatRoom, KlipUser klipUser) {
        BgmsToken token = prepareToken();
        // TODO @Jordan
    }

    @Override
    public void notifyToInviteOperator(InviteOperatorNotifiable command) {
        BgmsToken token = prepareToken();

        BgmsSendAlimTalkReq req = toBgmsSendAlimTalkReq(command);
        try {
            BgmsBaseRes res = bgmsApiClient.sendAlimTalk(token.getToken(), req);
            if (!res.isSuccessful()) {
                throw InternalApiException.biztalk(res);
            }
        } catch (Exception cause) {
            throw InternalApiException.biztalk(cause);
        }
    }

    @NonNull
    private BgmsSendAlimTalkReq toBgmsSendAlimTalkReq(InviteOperatorNotifiable command) {
        String msgIdx = generateUuid();
        String senderKey = properties.getSenderKey();
        //@checkstyle:off
        String message = """
                         [클립 드롭스] 닥터자르트 쿠폰 번호 발급 안내
                                                  
                         안녕하세요, 클립 드롭스입니다.
                                                  
                         닥터자르트 NFT 에어드롭 특별 혜택으로 안내된 닥터자르트 공식몰 전제품 #{30%} 할인 쿠폰이 발급되었습니다. (쿠폰 번호: drjartnft)
                                                  
                         [쿠폰 사용 방법]
                         ☞ 공식몰 > 구매할 제품 장바구니 추가 > 주문하기 > 구매 창 하단 쿠폰 번호(drjartnft) 입력 > 결제 계속하기
                                                  
                         [안내 사항]
                         • 쿠폰 사용 기한: 2023년 1월 31일까지
                         • 1,000개 한정, 선착순 1인 1회 증정
                         • 구매 금액 2만 원 이상 적용 가능
                         • 신규 가입자 10% 쿠폰(Welcome)은 NFT 쿠폰 번호(drjartnft) 선 입력 후 적용 가능합니다.
                                                  
                         ※ 이 메시지는 고객님이 참여한 닥터자르트 NFT 에어드롭 특별 혜택으로 지급된 쿠폰 안내 메시지입니다.""";
        //@checkstyle:on

        String templateCode = properties.getInviteOperatorTemplateCode();
        String title = "클립 멤버십 서비스 관리자 초대";
        String url = command.invitationUrl();
        Button button = Button.wl("서비스 이용 동의하러 가기", url, url);
        return BgmsSendAlimTalkReq.recipient(msgIdx, senderKey, command.inviteeMobileNumber(), message, templateCode, title, button);
    }

    private String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }
}
