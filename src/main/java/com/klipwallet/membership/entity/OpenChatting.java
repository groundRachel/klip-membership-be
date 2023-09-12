package com.klipwallet.membership.entity;

import java.time.LocalDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.Nullable;

import com.klipwallet.membership.adaptor.jpa.ForJpa;
import com.klipwallet.membership.entity.kakao.KakaoOpenlinkSummary;

import static com.klipwallet.membership.entity.Statusable.requireVerifiedCode;

/**
 * 채팅방 Entity
 */
@Entity
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class OpenChatting extends BaseEntity<OpenChatting> {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    /**
     * 연동된 카카오 오픈 채팅방 아이디
     */
    private KakaoOpenlinkSummary kakaoOpenlinkSummary;
    @Column(nullable = false)
    private String title;
    private String description;
    /**
     * 채팅방 커버 이미지
     */
    private String coverImage;
    /**
     * NFT Contract Address
     */
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "klipDropsSca"))
    private Address klipDropsSca;

    @Column(nullable = false)
    private Status status;
    @Column(nullable = false)
    private Source source;

    @ForJpa
    protected OpenChatting() {
    }

    /**
     * 채팅방 생성을 위한 기본 생성자.
     */
    public OpenChatting(String title, String description, String coverImage, KakaoOpenlinkSummary kakaoOpenlinkSummary, Address nftSca,
                        MemberId creatorId) {
        this.title = title;
        this.description = description;
        this.coverImage = coverImage;
        this.kakaoOpenlinkSummary = kakaoOpenlinkSummary;
        this.status = Status.ACTIVATED;
        this.source = Source.KLIP_DROPS;
        this.klipDropsSca = nftSca;
        this.createBy(creatorId);
        // 카카오 오픈 채팅방을 바로 삭제하는 경우(Rollback)를 위한 이벤트
        super.registerEvent(new KakaoOpenChattingOpened(kakaoOpenlinkSummary));
    }

    public boolean isActivated() {
        return status == Status.ACTIVATED;
    }

    public boolean isDeleted() {
        return status == Status.DELETED;
    }

    public LocalDateTime getDeletedAt() {
        if (isDeleted()) {
            return getUpdatedAt();
        }
        return null;
    }

    public void deleteBy(MemberId updater) {
        if (isDeleted()) {
            return;
        }
        this.status = Status.DELETED;
        updateBy(updater);
    }

    @Getter
    @Schema(name = "OpenChatting.Status", description = "채팅방 상태", example = "activated")
    public enum Status implements Statusable {
        /**
         * 활성화
         */
        ACTIVATED(1),
        /**
         * 삭제
         */
        DELETED(2);

        private final byte code;

        Status(int code) {
            this.code = requireVerifiedCode(code);
        }

        @JsonCreator
        @Nullable
        public static Status fromDisplay(String display) {
            return Statusable.fromDisplay(Status.class, display);
        }

        @JsonValue
        @Override
        public String toDisplay() {
            return Statusable.super.toDisplay();
        }
    }

    @Getter
    @Schema(name = "OpenChatting.Source", description = "채팅방 소스", example = "klipDrops")
    public enum Source implements Statusable {
        /**
         * For KLAYTN NFT
         */
        KLAYTN(0),
        /**
         * For KlipDrops NFT
         * <p>
         * KlipDrops에서 발행한 NFT도 Klaytn에서 발행되므로 {@link Source#KLAYTN}의 하위 집합.
         * 내부 서비스 간 연동 편의성을 위해서 제공.
         * </p>
         */
        KLIP_DROPS(1);

        private final byte code;

        Source(int code) {
            this.code = requireVerifiedCode(code);
        }

        @JsonCreator
        @Nullable
        public static Source fromDisplay(String display) {
            return Statusable.fromDisplay(Source.class, display);
        }

        @JsonValue
        @Override
        public String toDisplay() {
            return Statusable.super.toDisplay();
        }
    }
}
