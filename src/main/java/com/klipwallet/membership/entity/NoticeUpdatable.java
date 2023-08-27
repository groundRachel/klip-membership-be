package com.klipwallet.membership.entity;

import jakarta.annotation.Nullable;

public interface NoticeUpdatable extends ArticleUpdatable {
    /**
     * 고정 공지 여부. 값이 {@code null} 이면 변경하지 않는다.
     */
    @Nullable
    Boolean isPrimary();
}
