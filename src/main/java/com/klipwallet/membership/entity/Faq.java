package com.klipwallet.membership.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

/**
 * FAQ Entity
 */

@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private Status status = Status.DRAFT;
    @Embedded
    private BaseMeta base;

    @ForJpa
    protected Faq() {
    }

    public Faq(String title, String body, MemberId createdBy) {
        this.title = title;
        this.body = body;
        this.base = new BaseMeta(createdBy);
    }

    public void update(FaqUpdatable command) {
        this.title = command.getTitle();
        this.body = command.getBody();
        this.status = command.getStatus();
    }

    public boolean equalId(@NonNull Integer noticeId) {
        return noticeId.equals(this.id);
    }

    @Getter
    @Schema(name = "FAQ.Status", description = "FAQ 상태", example = "draft")
    public enum Status implements Statusable {
        /**
         * 초안
         */
        DRAFT(1),
        /**
         * 사용자 공개
         */
        ACTIVE(2),
        /**
         * 사용자 비공개
         */
        INACTIVE(3);

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
