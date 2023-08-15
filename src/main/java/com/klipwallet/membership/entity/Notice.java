package com.klipwallet.membership.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

import static java.lang.Boolean.TRUE;

/**
 * 공지사항 Entity
 */
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
     * 메인 공지 노출 여부
     * <p>
     * 메인 공지는 모든 공지사항 중 단 하나만 선정될 수 있음.
     * </p>
     */
    @Column(nullable = false)
    private boolean main = false;

    @Embedded
    private BaseMeta base;

    @ForJpa
    protected Notice() {
    }

    public Notice(String title, String body, MemberId createdBy) {
        this.title = title;
        this.body = body;
        this.base = new BaseMeta(createdBy);
    }

    /**
     * 메인 공지 활성화
     */
    private void activateMain() {
        if (this.main) {    // 멱등성
            return;
        }
        this.main = true;
        this.registerEvent(new MainNoticeActivated(this.getId()));
    }

    /**
     * 메인 공지 비활성화
     */
    public void deactivateMain() {
        this.main = false;
    }

    public void update(NoticeUpdatable command) {
        this.title = command.getTitle();
        this.body = command.getBody();
        changeMain(command.isMain());
    }

    private void changeMain(Boolean main) {
        if (main == null) {
            return;
        }
        if (TRUE.equals(main)) {    // 메인 공지 여부인 경우
            this.activateMain();
            return;
        }
        this.deactivateMain();
    }

    public boolean equalId(@NonNull Integer noticeId) {
        return noticeId.equals(this.id);
    }
}
