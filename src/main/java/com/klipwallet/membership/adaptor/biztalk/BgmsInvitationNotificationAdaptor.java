package com.klipwallet.membership.adaptor.biztalk;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.klipwallet.membership.config.BgmsProperties;
import com.klipwallet.membership.entity.ChatRoom;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.service.InvitationNotifier;

/**
 * BGMS(Biztalk Global Message System)로 구현된 초대 알리미
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BgmsInvitationNotificationAdaptor implements InvitationNotifier {
    private final BgmsProperties bgmsProperties;
    private final BgmsApiClient bgmsApiClient;
    private final BgmsTokenProvider bgmsTokenProvider;

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
}
