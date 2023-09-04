package com.klipwallet.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.ChatRoomNft;

public interface ChatRoomNftRepository extends JpaRepository<ChatRoomNft, Integer> {
}
