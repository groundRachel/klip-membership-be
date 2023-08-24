package com.klipwallet.membership.service;

import com.klipwallet.membership.entity.ChatRoom;
import com.klipwallet.membership.entity.KlipUser;

/**
 * 초대 알리미 인터페이스.
 * <p>
 * - NFT 홀더에게 오픈채팅 초대 알림<br/>
 * - 오픈 채팅 운영자 초대 알림<br/>
 * <p>
 * 카카오 알림톡으로 기본 구현될 예정
 * </p>
 */
public interface InvitationNotifier {
    /**
     * 오픈 채팅으로 초대 알림 발송
     */
    void notifyToInviteChatRoom(ChatRoom chatRoom, KlipUser klipUser);

    //    /**
    //     * 오픈 채팅 운영자 초대 알림 발송
    //     */
    //    void notifyToInviteOperator(Partner partner, OperatorCandidate candidate);
}
