package com.klipwallet.membership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.klipwallet.membership.dto.chatroom.ChatRoomMemberCreate;
import com.klipwallet.membership.dto.chatroom.ChatRoomMemberSummary;
import com.klipwallet.membership.dto.chatroom.ChatRoomOperatorCreate;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.ChatRoom;
import com.klipwallet.membership.entity.ChatRoomMember;
import com.klipwallet.membership.entity.ChatRoomMember.Role;
import com.klipwallet.membership.entity.Operator;
import com.klipwallet.membership.exception.OperatorNotFoundException;
import com.klipwallet.membership.exception.kakao.OperatorNotInPartnerAccountException;
import com.klipwallet.membership.repository.ChatRoomMemberRepository;
import com.klipwallet.membership.repository.ChatRoomRepository;
import com.klipwallet.membership.repository.OperatorRepository;

@Service
@RequiredArgsConstructor
public class ChatRoomMemberService {
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final KlipAccountService klipAccountService;
    private final OperatorRepository operatorRepository;

    /**
     * 외부 API 에서 사용
     */
    public ChatRoomMemberSummary create(ChatRoomMemberCreate command) {
        if (command.role() == Role.NFT_HOLDER) {
            // TODO: Check NFT
        }
        return null;
    }

    public ChatRoomMember createOperator(ChatRoomOperatorCreate command, Role role, AuthenticatedUser user) {
        Operator operator = tryGetOperator(command.operatorId());
        checkOperatorPartnerId(operator, user.getMemberId().value());

        ChatRoomMember entity = command.toChatRoomMember(operator, role);
        return chatRoomMemberRepository.save(entity);
    }

    public ChatRoom getChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).get();
    }

    private Operator tryGetOperator(Long operatorId) {
        return operatorRepository.findById(operatorId).orElseThrow(() -> new OperatorNotFoundException(operatorId));

    }

    private void checkOperatorPartnerId(Operator operator, Integer partnerId) {
        if (!operator.getPartnerId().equals(partnerId)) {
            throw new OperatorNotInPartnerAccountException(operator.getId());
        }
    }
}
