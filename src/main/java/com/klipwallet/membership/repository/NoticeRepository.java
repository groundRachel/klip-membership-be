package com.klipwallet.membership.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;

import com.klipwallet.membership.entity.ArticleStatus;
import com.klipwallet.membership.entity.Notice;

public interface NoticeRepository extends AbstractArticleRepository<Notice> {
    /**
     * 고정 공지 여부로 목록 조회
     *
     * @param isPrimary if true: 고정 공지, false: 일반 공지
     * @return 고정 공지 목록
     */
    List<Notice> findAllByPrimary(boolean isPrimary);

    Optional<Notice> findTopByPrimaryAndStatus(boolean isPrimary, ArticleStatus status, Sort sort);

    Optional<Notice> findTopByStatus(ArticleStatus articleStatus, Sort sort);
}
