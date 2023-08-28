package com.klipwallet.membership.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.klipwallet.membership.entity.AbstractArticle;
import com.klipwallet.membership.entity.ArticleStatus;

@NoRepositoryBean
public interface AbstractArticleRepository<T extends AbstractArticle<T>> extends JpaRepository<T, Integer> {
    /**
     * 상태 검색한 정렬된 게시물 목록 조회
     *
     * @param status 검색 상태
     * @param sort   정렬
     * @return 상태 검색한 정렬된 게시물 목록
     */
    List<T> findAllByStatus(ArticleStatus status, Sort sort);
}
