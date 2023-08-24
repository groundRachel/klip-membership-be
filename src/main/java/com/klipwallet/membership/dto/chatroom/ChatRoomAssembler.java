package com.klipwallet.membership.dto.chatroom;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.datetime.DateTimeAssembler;
import com.klipwallet.membership.entity.ChatRoom;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class ChatRoomAssembler {
    private final DateTimeAssembler dtAssembler;

    public List<ChatRoomRow> toRows(List<ChatRoom> entities) {
        return entities.stream()
                       .map(this::toRow)
                       .collect(toList());
    }

    private ChatRoomRow toRow(ChatRoom entity) {
        return new ChatRoomRow(entity.getId(), entity.getTitle(), entity.getContractAddress(), entity.getStatus(),
                               entity.getSource(), entity.getBase().getCreatedBy().value(), entity.getBase().getUpdatedBy().value(),
                               dtAssembler.toOffsetDateTime(entity.getBase().getCreatedAt()),
                               dtAssembler.toOffsetDateTime(entity.getBase().getUpdatedAt()));
    }

    public ChatRoomSummary toSummary(ChatRoom entity) {
        return new ChatRoomSummary(entity.getId(), entity.getOpenChatRoomSummary().getId(), entity.getOpenChatRoomSummary().getUrl(),
                                   entity.getTitle(), entity.getBase().getCreatedBy().value(),
                                   dtAssembler.toOffsetDateTime(entity.getBase().getCreatedAt()));
    }
}
