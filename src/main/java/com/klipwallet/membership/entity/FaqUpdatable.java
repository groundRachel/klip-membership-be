package com.klipwallet.membership.entity;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import com.klipwallet.membership.entity.Faq.Status;

public interface FaqUpdatable {
    /**
     * 변경할 제목
     */
    @Nonnull
    String getTitle();

    /**
     * 변경한 본문
     */
    @Nonnull
    String getBody();

    /**
     * FAQ 상태
     */
    @Nullable
    Status getStatus();

    /**
     * 수정 관리자 아이디
     */
    @Nonnull
    MemberId getUpdatedBy();
}
