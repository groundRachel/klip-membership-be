package com.klipwallet.membership.dto.openchatting;

import java.time.OffsetDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import com.klipwallet.membership.entity.OpenChatting.Status;

@Schema(description = "오픈채팅방 상세 DTO", accessMode = AccessMode.READ_ONLY)
public record OpenChattingDetail(
        @Schema(description = "오픈채팅방 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
        Long id,
        @Schema(description = "오픈채팅방 소개", requiredMode = RequiredMode.NOT_REQUIRED, example = "NFT 홀더들을 위한 오픈채팅방 입니다.")
        String description,
        @Schema(description = "오픈채팅방 커버 이미지 URL", requiredMode = RequiredMode.NOT_REQUIRED,
                example = "https://klip-media.dev.klaytn.com/klip-membership/test.jpg")
        String coverImageUrl,
        @Schema(description = "카카오 오픈채팅방 ID", requiredMode = RequiredMode.REQUIRED, example = "302067190")
        Long openChattingId,
        @Schema(description = "카카오 오픈채팅방 URL", requiredMode = RequiredMode.REQUIRED, example = "https://open.kakao.com/o/gvJWpNBf")
        String openChattingUrl,
        @Schema(description = "오픈채팅방 제목", requiredMode = RequiredMode.REQUIRED, example = "Nyan-Cat Holder 모여라!")
        String title,
        @Schema(description = "오픈채팅방 상태", requiredMode = RequiredMode.REQUIRED, example = "1")
        Status status,
        @Schema(description = "오픈채팅방 생성 일시", requiredMode = RequiredMode.REQUIRED, example = "2023-07-24T15:38:24.005795+09:00")
        OffsetDateTime createdAt,
        @Schema(description = "오픈채팅방 종료 일시", requiredMode = RequiredMode.NOT_REQUIRED, example = "2023-07-25T00:00:00.000000+09:00")
        OffsetDateTime deletedAt,
        @Schema(description = "오픈채팅방 방장 정보", requiredMode = RequiredMode.REQUIRED)
        OpenChattingOperatorDetail host,
        @Schema(description = "오픈채팅방 운영자 정보", requiredMode = RequiredMode.NOT_REQUIRED)
        List<OpenChattingOperatorDetail> operators,
        @Schema(description = "오픈채팅방 NFT 정보", requiredMode = RequiredMode.NOT_REQUIRED)
        List<OpenChattingNftDetail> nfts
) {
}
