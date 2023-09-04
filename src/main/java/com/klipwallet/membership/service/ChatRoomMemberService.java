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
import com.klipwallet.membership.repository.ChatRoomMemberRepository;

@Service
@RequiredArgsConstructor
public class ChatRoomMemberService {
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final OperatorService operatorService;

    /**
     * 외부 API 에서 사용
     */
    public ChatRoomMemberSummary create(ChatRoomMemberCreate command) {
        if (command.role() == Role.NFT_HOLDER) {
            // TODO: Check NFT
        }
        return null;
    }

    public ChatRoomMember createHost(ChatRoom chatRoom, ChatRoomOperatorCreate command, AuthenticatedUser user) {
        return registerOperator(chatRoom, command, user, Role.HOST);
    }

    public ChatRoomMember createOperator(ChatRoom chatRoom, ChatRoomOperatorCreate command, AuthenticatedUser user) {
        return registerOperator(chatRoom, command, user, Role.OPERATOR);
    }

    private ChatRoomMember registerOperator(ChatRoom chatRoom, ChatRoomOperatorCreate command, AuthenticatedUser user, Role role) {
        Operator operator = operatorService.tryGetOperator(command.operatorId());
        // Check operator in partner
        operator.checkPartnerId(user.getMemberId());

        ChatRoomMember entity = command.toChatRoomMember(chatRoom, operator, role);
        return chatRoomMemberRepository.save(entity);
    }
}
