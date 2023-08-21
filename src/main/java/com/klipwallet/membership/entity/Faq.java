package com.klipwallet.membership.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.github.resilience4j.core.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

/**
 * FAQ Entity
 */

@Entity
@DynamicUpdate
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Faq extends BaseEntity<Faq> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private Status status = Status.DRAFT;

    /**
     * 최근 {@literal Live}일시
     *
     * @see com.klipwallet.membership.entity.Faq.Status#LIVE
     */
    private LocalDateTime livedAt;

    @ForJpa
    protected Faq() {
    }

    public Faq(String title, String body, MemberId creatorId) {
        this.title = title;
        this.body = body;
        this.createBy(creatorId);
    }

    public void update(FaqUpdatable command) {
        this.title = command.getTitle();
        this.body = command.getBody();
        updateBy(command.getUpdater());
    }

    public void changeStatus(@NonNull Status status, MemberId updater) {
        if (this.status == status) {        // 멱등성
            return;
        }
        updateBy(updater);
        if (status == Status.LIVE) {
            live();
        } else {
            this.status = status;
        }
    }

    private void live() {
        this.status = Status.LIVE;
        this.livedAt = LocalDateTime.now();
    }

    public boolean equalId(@NonNull Integer noticeId) {
        return noticeId.equals(this.id);
    }

    @Getter
    @Schema(name = "FAQ.Status", description = "FAQ 상태", example = "draft")
    public enum Status implements Statusable {
        /**
         * 초안: 이용자가 조회할 수 없음.
         */
        DRAFT(0),
        /**
         * 사용자 공개: 이용자가 조회할 수 있음.
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
