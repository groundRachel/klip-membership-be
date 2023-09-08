package com.klipwallet.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.OpenChatting;

public interface OpenChattingRepository extends JpaRepository<OpenChatting, Long> {
}
