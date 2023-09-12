package com.klipwallet.membership.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.hibernate.annotations.DynamicUpdate;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

/**
 * 추상 게시물 Entity
 *
 * @see com.klipwallet.membership.entity.Notice
 * @see com.klipwallet.membership.entity.Faq
 */
@MappedSuperclass
@DynamicUpdate
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class AbstractArticle<T extends AbstractArticle<T>> extends BaseEntity<T> {
    @SuppressWarnings("unused")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String body;
    /**
     * <pre>draft, live, inactive, delete</pre>
     */
    @Column(nullable = false)
    private ArticleStatus status;
    /**
     * 최근 {@literal Live}일시
     *
     * @see com.klipwallet.membership.entity.ArticleStatus#LIVE
     */
    private LocalDateTime livedAt;

    @ForJpa
    protected AbstractArticle() {

    }

    /**
     * 기본 생성자.
     */
    public AbstractArticle(String title, String body, MemberId creatorId) {
        this.title = title;
        this.body = body;
        this.status = ArticleStatus.DRAFT;
        this.createBy(creatorId);
    }

    protected void update(ArticleUpdatable command) {
        this.title = command.getTitle();
        this.body = command.getBody();
        updateBy(command.getUpdaterId());
    }

    public void changeStatus(@NonNull ArticleStatus status, MemberId updater) {
        if (this.status == status) {        // 멱등성
            return;
        }
        updateBy(updater);
        if (status == ArticleStatus.LIVE) {
            live();
        } else {
            this.status = status;
        }
    }

    private void live() {
        this.status = ArticleStatus.LIVE;
        this.livedAt = LocalDateTime.now();
    }

    /**
     * 게시물이 이용자(파트너)에게 노출 가능한 상태인가?
     */
    public boolean isLive() {
        return status == ArticleStatus.LIVE;
    }

    /**
     * 공지사항이 (논리적) 삭제되었는가?
     *
     * @see #isEnabled()
     */
    public boolean isDeleted() {
        return status == ArticleStatus.DELETE;
    }

    /**
     * 공지사항이 관리자가 접근 가능한 유효한 상태인가?
     * <p>
     * Not {@link #isDeleted()}
     *
     * @see #isDeleted()
     * @see #isLive()
     */
    public boolean isEnabled() {
        return this.status.isEnabled();
    }

    public void deleteBy(MemberId deleterId) {
        if (isDeleted()) {  // 멱등성
            return;
        }
        status = ArticleStatus.DELETE;
        updateBy(deleterId);
    }
}
