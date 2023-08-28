package com.klipwallet.membership.adaptor.jpa.converter;

import jakarta.persistence.Converter;

import com.klipwallet.membership.entity.ArticleStatus;

@Converter(autoApply = true)
public class ArticleStatusConverter extends StatusableConverter<ArticleStatus> {
    public ArticleStatusConverter() {
        super(ArticleStatus.class);
    }
}
