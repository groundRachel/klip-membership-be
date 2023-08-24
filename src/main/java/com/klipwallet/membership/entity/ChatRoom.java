package com.klipwallet.membership.entity;

import java.util.List;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.lang.Nullable;

import com.klipwallet.membership.adaptor.jpa.ForJpa;
import com.klipwallet.membership.entity.kakao.OpenChatRoomSummary;

import static com.klipwallet.membership.entity.Statusable.requireVerifiedCode;

/**
 * 채팅방 Entity
 */
@Entity
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class ChatRoom extends AbstractAggregateRoot<ChatRoom> {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    /**
     * 연동된 카카오 오픈 채팅방 아이디
     */
    private OpenChatRoomSummary openChatRoomSummary;
    private String title;
    /**
     * 채팅방 커버 이미지
     */
    private String coverImage;
    /**
     * NFT Contract Address
     */
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "contractAddress"))
    private Address contractAddress;
    private Status status;
    private Source source;
    /**
     * 채팅방 생성자 아이디
     * <p>
     * 채팅방을 생성했다고, 무조건 방장이 되지 않는다. 방장은 {@link ChatRoomMember} 에서 방장 타입으로 조회한다.
     * </p>
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChatRoomMember> chatRoomMembers;
    @Embedded
    private BaseMeta base;

    @ForJpa
    protected ChatRoom() {
    }

    /**
     * 채팅방 생성을 위한 기본 생성자.
     */
    public ChatRoom(String title, String coverImage, OpenChatRoomSummary openChatRoomSummary, Address contractAddress) {
        this.title = title;
        this.coverImage = coverImage;
        this.openChatRoomSummary = openChatRoomSummary;
        this.status = Status.ACTIVATED;
        this.source = Source.KLIP_DROPS;
        this.contractAddress = contractAddress;
        // 카카오 오픈 채팅방을 바로 삭제하는 경우(Rollback)를 위한 이벤트
        super.registerEvent(new KakaoOpenChatRoomOpened(openChatRoomSummary));
    }

    @Getter
    @Schema(name = "ChatRoom.Status", description = "채팅방 상태", example = "activated")
    public enum Status implements Statusable {
        /**
         * 활성화
         */
        ACTIVATED(1),
        /**
         * 비활성화
         */
        DEACTIVATED(2),
        /**
         * 삭제
         */
        DELETED(3);

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
    @Schema(name = "ChatRoom.Source", description = "채팅방 소스", example = "klipDrops")
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
