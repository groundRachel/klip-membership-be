package com.klipwallet.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
