package com.klipwallet.membership.dto.openchatting;


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
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.OpenChatting;
import com.klipwallet.membership.entity.kakao.KakaoOpenlinkSummary;

@Schema(description = "채팅방 생성 DTO", accessMode = AccessMode.WRITE_ONLY)
public record OpenChattingCreate(
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
        OpenChattingOperatorCreate host,
        @Schema(description = "오픈채팅방 운영자 정보")
        @NonNull @Size(max = MAX_OPERATOR_SIZE)
        List<@Valid OpenChattingOperatorCreate> operators,
        @Schema(description = "오픈채팅방에 연결되는 드롭 아이디")
        @NotNull
        List<@Valid OpenChattingNftCreate> nfts
) {
    private static final int MAX_OPERATOR_SIZE = 4;

    public OpenChatting toOpenChatting(@NonNull KakaoOpenlinkSummary kakaoOpenlinkSummary, Address nftSca,
                                       MemberId creatorId) {
        return new OpenChatting(title, coverImageUrl, kakaoOpenlinkSummary, nftSca, creatorId);
    }
}
