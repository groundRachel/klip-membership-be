package com.klipwallet.membership.dto.chatroom;


import io.swagger.v3.oas.annotations.media.Schema;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.ChatRoom;
import com.klipwallet.membership.entity.kakao.OpenChatRoomId;

@Schema(description = "채팅방 요약 DTO")
public record ChatRoomCreate(
        String title,
        String coverImage,
        Address contractAddress) {
    public ChatRoom toChatRoom(OpenChatRoomId openChatRoomId, Integer creatorId) {
        return new ChatRoom(openChatRoomId, title, coverImage, contractAddress, creatorId);
    }
}
