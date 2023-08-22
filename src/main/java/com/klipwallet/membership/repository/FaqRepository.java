package com.klipwallet.membership.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.Faq;
import com.klipwallet.membership.entity.Faq.Status;

public interface FaqRepository extends JpaRepository<Faq, Integer> {

    /**
     * 상태 검색한 정렬된 FAQ 목록 조회
     *
     * @param status 검색 상태
     * @param pageable page, size, sort
     * @return 상태 검색한 정렬된 FAQ 목록
     */
    Page<Faq> findByStatus(Status status, Pageable pageable);

    /**
     * 정렬된 FAQ 목록 조회
     *
     * @param pageable page, size, sort
     * @return 정렬된 FAQ 목록
     */
    Page<Faq> findAll(Pageable pageable);
}
