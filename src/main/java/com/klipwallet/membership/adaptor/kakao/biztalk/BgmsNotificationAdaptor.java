package com.klipwallet.membership.adaptor.kakao.biztalk;

import java.util.UUID;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.klipwallet.membership.adaptor.kakao.biztalk.dto.BgmsBaseRes;
import com.klipwallet.membership.adaptor.kakao.biztalk.dto.BgmsGetResultAllRes;
import com.klipwallet.membership.adaptor.kakao.biztalk.dto.BgmsGetResultAllRes.Response;
import com.klipwallet.membership.adaptor.kakao.biztalk.dto.BgmsSendAlimTalkReq;
import com.klipwallet.membership.adaptor.kakao.biztalk.dto.BgmsSendAlimTalkReq.Button;
import com.klipwallet.membership.config.BgmsProperties;
import com.klipwallet.membership.entity.InviteOperatorNotifiable;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.entity.OpenChatting;
import com.klipwallet.membership.exception.InternalApiException;
import com.klipwallet.membership.service.InvitationNotifier;

/**
 * BGMS(Biztalk Global Message System)로 구현된 초대 알리미
 */
@Profile("!local")
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
    public void notifyToInviteOpenChatting(OpenChatting openChatting, KlipUser klipUser) {
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
            verifyResponse(token, req);
        } catch (Exception cause) {
            throw InternalApiException.biztalk(cause);
        }
    }

    private void verifyResponse(BgmsToken token, BgmsSendAlimTalkReq req) {
        BgmsGetResultAllRes result = bgmsApiClient.getResultAll(token.getToken());
        for (Response response : result.getResponses()) {
            if (!req.msgIdx().equals(response.getMsgIdx())) {
                continue;
            }
            if (!response.isSuccessful()) {
                log.error("Fail to notifyToInviteOperator: {}", response);
            }
            return;
        }
        throw new IllegalStateException("Not Found Response of notifyToInviteOperator. %s".formatted(req.msgIdx()));
    }

    @NonNull
    private BgmsSendAlimTalkReq toBgmsSendAlimTalkReq(InviteOperatorNotifiable command) {
        // FIXME @Jordan 현재 웰컴카드로 구현되어 있음. 이것을 변경해야함.
        String msgIdx = generateUuid();
        String senderKey = properties.getSenderKey();
        //@checkstyle:off
        String message = """
                         Klip 가입을 환영합니다!
                         첫 번째 디지털 자산 웰컴 카드가 지급되었습니다.
                                                  
                         - 카드 확인: 메뉴 > 카드""";
        //@checkstyle:on

        String templateCode = properties.getInviteOperatorTemplateCode();
        String url = command.invitationUrl();
        Button button = Button.wl("Klip에서 확인하기", url, null);
        return BgmsSendAlimTalkReq.byRecipient(msgIdx, senderKey, command.inviteePhoneNumber(), message, templateCode, button);
    }

    private String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }
}
