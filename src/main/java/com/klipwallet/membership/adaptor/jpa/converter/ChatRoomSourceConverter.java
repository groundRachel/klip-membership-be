package com.klipwallet.membership.adaptor.jpa.converter;

import jakarta.persistence.Converter;

import com.klipwallet.membership.entity.ChatRoom;

@Converter(autoApply = true)
public class ChatRoomSourceConverter extends StatusableConverter<ChatRoom.Source> {
    public ChatRoomSourceConverter() {
        super(ChatRoom.Source.class);
    }
}
