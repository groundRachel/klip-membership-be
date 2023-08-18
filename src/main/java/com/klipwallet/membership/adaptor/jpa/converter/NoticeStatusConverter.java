package com.klipwallet.membership.adaptor.jpa.converter;

import jakarta.persistence.Converter;

import com.klipwallet.membership.entity.Notice;

@Converter(autoApply = true)
public class NoticeStatusConverter extends StatusableConverter<Notice.Status> {
    public NoticeStatusConverter() {
        super(Notice.Status.class);
    }
}
