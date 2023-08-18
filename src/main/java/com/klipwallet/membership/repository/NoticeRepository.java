package com.klipwallet.membership.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.Notice;
import com.klipwallet.membership.entity.Notice.Status;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    /**
     * 고정 공지 여부로 목록 조회
     *
     * @param isPrimary 고정 공지 여부
     * @return 고정 공지 목록
     */
    List<Notice> findAllByPrimary(boolean isPrimary);

    /**
     * 상태 검색한 정렬된 공지 목록 조회
     *
     * @param status 검색 상태
     * @param sort   정렬
     * @return 상태 검색한 정렬된 공지 목록
     */
    List<Notice> findAllByStatus(Status status, Sort sort);
}
