package com.klipwallet.membership.repository;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.klipwallet.membership.entity.ArticleStatus;
import com.klipwallet.membership.entity.Faq;

public interface FaqRepository extends AbstractArticleRepository<Faq> {

    /**
     * 특정 상태를 제외한 정렬된 FAQ 목록 조회
     *
     * @param statuses   검색될 상태들
     * @param pageable page, size, sort
     * @return 특정 상태를 제외하여 검색한 정렬된 FAQ 목록
     */
    Page<Faq> findAllByStatusIn(Collection<ArticleStatus> statuses, Pageable pageable);

    /**
     * 상태 검색한 정렬된 FAQ 목록 조회
     *
     * @param status   검색 상태
     * @param pageable page, size, sort
     * @return 상태 검색한 정렬된 FAQ 목록
     */
    Page<Faq> findByStatus(ArticleStatus status, Pageable pageable);
}
