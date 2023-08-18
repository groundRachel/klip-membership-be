package com.klipwallet.membership.dto.notice;

import java.time.OffsetDateTime;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.NonNull;

import com.klipwallet.membership.dto.member.MemberSummary;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Notice;
import com.klipwallet.membership.entity.NoticeUpdatable;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;

public class NoticeDto {
    @Schema(description = "공지사항 생성 DTO", accessMode = AccessMode.WRITE_ONLY)
    public record Create(
            @Schema(description = "제목", minLength = 1, maxLength = 200, example = "클립 멤버십 툴이 공식 오픈하였습니다.")
            @NotBlank @Size(min = 1, max = 200)
            String title,

            @Schema(description = "본문", example = "<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다.</p>")
            @NotBlank
            String body) {

        @JsonIgnore
        public Notice toNotice(AuthenticatedUser user) {
            return new Notice(title, body, user.getMemberId());
        }
    }

    @Schema(description = "공지사항 요약 DTO", accessMode = AccessMode.READ_ONLY)
    public record Summary(@NonNull @Schema(description = "공지사항 ID", example = "2") Integer id) {
        public Summary(Notice saved) {
            this(saved.getId());
        }
    }

    @Schema(description = "공지사항 수정 DTO", accessMode = AccessMode.WRITE_ONLY)
    public record Update(
            @Schema(description = "제목", minLength = 1, maxLength = 200, example = "클립 멤버십 툴이 공식 오픈하였습니다.")
            @NotBlank @Size(min = 1, max = 200)
            String title,
            @Schema(description = "본문", example = "<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다.</p>")
            @NotBlank
            String body,
            @Schema(description = "고정 공지 여부. null 이면 수정하지 않는다.", requiredMode = NOT_REQUIRED)
            Boolean primary) {

        @JsonIgnore
        public NoticeUpdatable toUpdatable(AuthenticatedUser user) {
            return new Updatable(this.title(), this.body(), this.primary(), user.getMemberId());
        }
    }

    public record Updatable(String title,
                            String body,
                            Boolean main,
                            MemberId updatedBy) implements NoticeUpdatable {
        @Nonnull
        @Override
        public String getTitle() {
            return this.title();
        }

        @Nonnull
        @Override
        public String getBody() {
            return this.body();
        }

        @Nullable
        @Override
        public Boolean isPrimary() {
            return this.main();
        }

        @Nonnull
        @Override
        public MemberId getUpdater() {
            return this.updatedBy();
        }
    }

    @Schema(description = "공지사항 상세 DTO", accessMode = AccessMode.READ_ONLY)
    public record Detail(
            @Schema(description = "공지사항 ID", example = "2")
            Integer id,
            @Schema(description = "제목", minLength = 1, maxLength = 200, example = "클립 멤버십 툴이 공식 오픈하였습니다.")
            String title,
            @Schema(description = "본문", example = "<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다.</p>")
            String body,
            @Schema(description = "고정 공지 여부", example = "false")
            boolean primary,
            Notice.Status status,
            @Schema(description = "최근Live일시", example = "2023-07-24T15:38:24.005795+09:00")
            OffsetDateTime livedAt,
            @Schema(description = "생성일시", example = "2023-07-24T15:38:24.005795+09:00")
            OffsetDateTime createdAt,
            @Schema(description = "마지막 수정일시", example = "2023-07-24T15:38:24.005795+09:00")
            OffsetDateTime updatedAt,
            @Schema(description = "생성자")
            MemberSummary creator,
            @Schema(description = "마지막 수정자")
            MemberSummary updater
    ) {
    }

    @Schema(description = "공지사항 상태 DTO", accessMode = AccessMode.READ_WRITE)
    public record Status(@NonNull @NotNull @JsonProperty("value") Notice.Status value) {
    }
}
