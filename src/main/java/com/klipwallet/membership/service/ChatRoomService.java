package com.klipwallet.membership.service;

import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.config.NftProperties;
import com.klipwallet.membership.dto.chatroom.ChatRoomAssembler;
import com.klipwallet.membership.dto.chatroom.ChatRoomCreate;
import com.klipwallet.membership.dto.chatroom.ChatRoomNftCreate;
import com.klipwallet.membership.dto.chatroom.ChatRoomOperatorCreate;
import com.klipwallet.membership.dto.chatroom.ChatRoomRow;
import com.klipwallet.membership.dto.chatroom.ChatRoomSummary;
import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.ChatRoom;
import com.klipwallet.membership.entity.ChatRoomNft;
import com.klipwallet.membership.entity.Operator;
import com.klipwallet.membership.entity.kakao.KakaoId;
import com.klipwallet.membership.entity.kakao.OpenChatRoomHost;
import com.klipwallet.membership.entity.kakao.OpenChatRoomSummary;
import com.klipwallet.membership.exception.kakao.OperatorAlreadyExistsException;
import com.klipwallet.membership.exception.member.OperatorDuplicatedException;
import com.klipwallet.membership.repository.ChatRoomNftRepository;
import com.klipwallet.membership.repository.ChatRoomRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties(NftProperties.class)
public class ChatRoomService {
    private final NftProperties nftProperties;
    private final KakaoService kakaoService;
    private final ChatRoomMemberService chatRoomMemberService;
    private final OperatorService operatorService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomNftRepository chatRoomNftRepository;
    private final ChatRoomAssembler chatRoomAssembler;

    @Transactional
    public ChatRoomSummary create(ChatRoomCreate command, AuthenticatedUser user) {
        checkOperators(command.operators(), command.host().operatorId());
        Operator host = operatorService.tryGetOperator(command.host().operatorId());
        // Create openchat in Kakao
        OpenChatRoomSummary summary = kakaoService.createOpenChatRoom(command.title(), command.description(), command.coverImageUrl(),
                                                                      new OpenChatRoomHost(new KakaoId(host.getKakaoUserId()),
                                                                                           command.host().nickname(),
                                                                                           command.host().profileImageUrl()));
        // Save open chat room
        ChatRoom entity = command.toChatRoom(summary, new Address(nftProperties.getSca()), user.getMemberId());
        ChatRoom saved = chatRoomRepository.save(entity);
        // Save host
        chatRoomMemberService.createHost(saved, command.host(), user);
        // Save operators
        for (ChatRoomOperatorCreate chatRoomOperatorCreate : command.operators()) {
            Operator operator = operatorService.tryGetOperator(chatRoomOperatorCreate.operatorId());
            // Join kakao open chat room
            kakaoService.joinOpenChatRoom(summary.getId(), chatRoomOperatorCreate.nickname(), chatRoomOperatorCreate.profileImageUrl(),
                                          operator.getKakaoUserId());
            // Insert db
            chatRoomMemberService.createOperator(saved, chatRoomOperatorCreate, user);
        }
        for (ChatRoomNftCreate nftCommand : command.nfts()) {
            ChatRoomNft nftEntity = nftCommand.toChatRoomNft(user.getMemberId().value(), saved.getId(), user.getMemberId());
            chatRoomNftRepository.save(nftEntity);
        }
        return new ChatRoomSummary(saved.getId(), saved.getOpenChatRoomSummary().getId(), saved.getOpenChatRoomSummary().getUrl(), saved.getTitle(),
                                   user.getMemberId().value(),
                                   saved.getCreatedAt().atZone(ZoneId.systemDefault()).toOffsetDateTime());
    }

    @Transactional(readOnly = true)
    public List<ChatRoomRow> getAllChatRooms() {
        List<ChatRoom> entities = chatRoomRepository.findAll();
        return chatRoomAssembler.toRows(entities);
    }

    private void checkOperators(List<ChatRoomOperatorCreate> operatorsCommand, Long hostOperatorId) {
        Set<Long> operatorIds = new HashSet<>();
        for (ChatRoomOperatorCreate command : operatorsCommand) {
            if (command.operatorId().equals(hostOperatorId)) {
                throw new OperatorAlreadyExistsException(command.operatorId());
            }
            if (operatorIds.contains(command.operatorId())) {
                throw new OperatorDuplicatedException(command.operatorId());
            } else {
                operatorIds.add(command.operatorId());
            }
        }
    }

}
