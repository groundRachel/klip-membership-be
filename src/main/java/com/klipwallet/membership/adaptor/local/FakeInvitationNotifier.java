package com.klipwallet.membership.adaptor.local;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.entity.InviteOperatorNotifiable;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.entity.OpenChatting;
import com.klipwallet.membership.service.InvitationNotifier;

@Profile("local")
@Component
@Slf4j
public class FakeInvitationNotifier implements InvitationNotifier {
    @Override
    public void notifyToInviteOpenChatting(OpenChatting openChatting, KlipUser klipUser) {
        log.info("[FAKE] Succeed notifyToInviteChatRoom@local\n{}\n{}", openChatting, klipUser);
    }

    @Override
    public void notifyToInviteOperator(InviteOperatorNotifiable command) {
        log.info("[FAKE] Succeed notifyToInviteChatRoom@local\n{}", command);
    }
}
