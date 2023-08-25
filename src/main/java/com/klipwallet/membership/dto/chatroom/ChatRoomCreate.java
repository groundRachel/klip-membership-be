package com.klipwallet.membership.dto.chatroom;


import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

import com.klipwallet.membership.entity.ChatRoom;

@Schema(description = "채팅방 생성 DTO", accessMode = AccessMode.WRITE_ONLY)
public record ChatRoomCreate(
        @Schema(description = "오픈채팅방 제목", minLength = 1, maxLength = 30)
        @NotBlank @Size(min = 1, max = 30)
        String title,
        @Schema(description = "오픈채팅방 커버 이미지", requiredMode = RequiredMode.NOT_REQUIRED)
        MultipartFile coverImage,
        @Schema(description = "오픈채팅방 방장 ID")
        @NotNull
        Integer hostId,
        @Schema(description = "오픈채팅방 방장 닉네임")
        @NotBlank
        String hostNickname,

        @Schema(description = "오픈채팅방 방장 프로필 이미지")
        MultipartFile hostProfileImage,

        @Schema(description = "오픈채팅방에 연결되는 드롭 아이디")
        @NotNull
        List<Integer> dropIds
) {

    public ChatRoom toChatRoom(@NonNull ChatRoom chatRoom) {
        return new ChatRoom(chatRoom.getTitle(), chatRoom.getCoverImage(), chatRoom.getOpenChatRoomSummary(), chatRoom.getContractAddress());
    }
}
