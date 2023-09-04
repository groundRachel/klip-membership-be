package com.klipwallet.membership.dto.chatroom;


import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.NonNull;
import org.hibernate.validator.constraints.URL;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.ChatRoom;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.kakao.OpenChatRoomSummary;

@Schema(description = "채팅방 생성 DTO", accessMode = AccessMode.WRITE_ONLY)
public record ChatRoomCreate(
        @Schema(description = "오픈채팅방 제목", minLength = 1, maxLength = 30)
        @NotBlank @Size(max = 30)
        String title,
        @Schema(description = "오픈채팅방 설명", maxLength = 80, requiredMode = RequiredMode.NOT_REQUIRED)
        @Size(max = 80)
        String description,
        @Schema(description = "오픈채팅방 커버 이미지", requiredMode = RequiredMode.NOT_REQUIRED)
        @URL
        String coverImageUrl,
        @Schema(description = "오픈채팅방 방장 정보")
        @Valid @NotNull
        ChatRoomOperatorCreate host,
        @Schema(description = "오픈채팅방 운영자 정보")
        @NonNull @Size(max = MAX_OPERATOR_SIZE)
        List<@Valid ChatRoomOperatorCreate> operators,
        @Schema(description = "오픈채팅방에 연결되는 드롭 아이디")
        @NotNull
        List<@Valid ChatRoomNftCreate> nfts
) {
    private static final int MAX_OPERATOR_SIZE = 4;

    public ChatRoom toChatRoom(@NonNull OpenChatRoomSummary openChatRoomSummary, Address nftSca,
                               MemberId creatorId) {
        return new ChatRoom(title, coverImageUrl, openChatRoomSummary, nftSca, creatorId);
    }
}
