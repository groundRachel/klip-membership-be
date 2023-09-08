package com.klipwallet.membership.adaptor.local;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.entity.ChatRoom;
import com.klipwallet.membership.entity.InviteOperatorNotifiable;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.service.InvitationNotifier;

@Profile("local")
@Component
@Slf4j
public class FakeInvitationNotifier implements InvitationNotifier {
    @Override
    public void notifyToInviteChatRoom(ChatRoom chatRoom, KlipUser klipUser) {
        log.info("[FAKE] Succeed notifyToInviteChatRoom@local\n{}\n{}", chatRoom, klipUser);
    }

    @Override
    public void notifyToInviteOperator(InviteOperatorNotifiable command) {
        log.info("[FAKE] Succeed notifyToInviteChatRoom@local\n{}", command);
    }
}
