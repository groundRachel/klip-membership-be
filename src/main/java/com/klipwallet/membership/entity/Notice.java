package com.klipwallet.membership.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

import static java.lang.Boolean.TRUE;

/**
 * 공지사항 Entity
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@DynamicUpdate
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Notice extends AbstractArticle<Notice> {
    /**
     * 고정 공지 여부
     * <p>
     * 고정 공지는 모든 공지사항 중 단 하나만 선정될 수 있으며, 메인에 노출된다.
     * </p>
     * 칼럼명을 {@code is_primary}로 설정한 이유는 {@code primary}가 예약어
     */
    @Column(name = "is_primary", nullable = false)
    private boolean primary = false;

    @ForJpa
    protected Notice() {
        super();
    }

    /**
     * 기본 생성자.
     */
    public Notice(String title, String body, MemberId creatorId) {
        super(title, body, creatorId);
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
        super.update(command);
        changePrimary(command.isPrimary());
    }

    private void changePrimary(Boolean isPrimary) {
        if (isPrimary == null) {        // 멱등성
            return;
        }
        if (TRUE.equals(isPrimary)) {    // 메인 공지 여부인 경우
            this.primaryOn();
            return;
        }
        this.primaryOff();
    }

    public boolean equalId(Integer otherNoticeId) {
        return Objects.equals(this.getId(), otherNoticeId);
    }
}
