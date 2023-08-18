package com.klipwallet.membership.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

import static java.lang.Boolean.TRUE;

/**
 * 공지사항 Entity
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Notice extends AbstractAggregateRoot<ChatRoom> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String body;
    /**
     * 고정 공지 여부
     * <p>
     * 고정 공지는 모든 공지사항 중 단 하나만 선정될 수 있으며, 메인에 노출된다.
     * </p>
     * 칼럼명을 {@code is_primary}로 설정한 이유는 {@code primary}가 예약어
     */
    @Column(name = "is_primary", nullable = false)
    private boolean primary = false;
    /**
     * <pre>draft, live, inactive</pre>
     */
    private Status status;

    /**
     * 발행일시
     * <p>
     * 최근 발행일시? 최초 발행일시?
     * </p>
     *
     * @see Status#LIVE
     */
    private LocalDateTime livedAt;

    @Embedded
    private BaseMeta base;

    @ForJpa
    protected Notice() {
    }

    /**
     * 기본 생성자.
     *
     * @param title     제목
     * @param body      본문
     * @param createdBy 생성자
     */
    public Notice(String title, String body, MemberId createdBy) {
        this.title = title;
        this.body = body;
        this.status = Status.DRAFT;
        this.base = new BaseMeta(createdBy);
    }

    /**
     * 고정 공지로 설정
     */
    private void primaryOn() {
        if (this.primary) {    // 멱등성
            return;
        }
        this.primary = true;
        this.registerEvent(new PrimaryNoticeChanged(this.getId()));
    }

    /**
     * 고정 공지 끄기
     */
    public void primaryOff() {
        this.primary = false;
    }

    public void update(NoticeUpdatable command) {
        this.title = command.getTitle();
        this.body = command.getBody();
        changePrimary(command.isPrimary());
        updatedBy(command.getUpdater());
    }

    private void changePrimary(Boolean isPrimary) {
        if (isPrimary == null) {
            return;
        }
        if (TRUE.equals(isPrimary)) {    // 메인 공지 여부인 경우
            this.primaryOn();
            return;
        }
        this.primaryOff();
    }

    public void changeStatus(@NonNull Status status, MemberId updater) {
        if (this.status == status) {
            return;
        }
        updatedBy(updater);
        if (status == Status.LIVE) {
            live();
        } else {
            this.status = status;
        }
    }

    private void updatedBy(MemberId updater) {
        this.base = this.base.withUpdatedBy(updater);
    }

    private void live() {
        this.status = Status.LIVE;
        this.livedAt = LocalDateTime.now();
    }

    public boolean equalId(@NonNull Integer noticeId) {
        return noticeId.equals(this.id);
    }

    @Getter
    @Schema(name = "Notice.Status", description = "공지사항 상태", example = "live")
    public enum Status implements Statusable {
        /**
         * 초안: 최초 등록 후 발행 전까지 상태. 이용자가 조회할 수 없음.
         */
        DRAFT(0),
        /**
         * 발행: 이용자가 조회할 수 있음.
         */
        LIVE(1),
        /**
         * 비활성화: 이용자가 조회할 수 없음.
         */
        INACTIVE(2);

        private final byte code;

        Status(int code) {
            this.code = Statusable.requireVerifiedCode(code);
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
}
