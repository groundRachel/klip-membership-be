package com.klipwallet.membership.adaptor.jpa.converter;

import jakarta.persistence.Converter;

import com.klipwallet.membership.entity.ChatRoom;

@Converter(autoApply = true)
public class ChatRoomStatusConverter extends StatusableConverter<ChatRoom.Status> {
    public ChatRoomStatusConverter() {
        super(ChatRoom.Status.class);
    }
}
