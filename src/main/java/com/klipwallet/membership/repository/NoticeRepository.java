package com.klipwallet.membership.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    /**
     * 고정 공지 여부로 목록 조회
     *
     * @param isPrimary 고정 공지 여부
     * @return 고정 공지 목록
     */
    List<Notice> findAllByPrimary(boolean isPrimary);

}
