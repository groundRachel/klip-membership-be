package com.klipwallet.membership.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.OpenChatting;
import com.klipwallet.membership.entity.OpenChatting.Status;

public interface OpenChattingRepository extends JpaRepository<OpenChatting, Long> {
    /**
     * 상태에 따른 오픈채팅방 목록
     *
     * @param status   오픈채팅 상태
     * @param pageable 정렬
     * @return 상태에 따른 오픈채팅방 목록
     */
    Page<OpenChatting> findAllByStatus(Status status, Pageable pageable);
}
