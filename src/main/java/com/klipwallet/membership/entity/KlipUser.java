package com.klipwallet.membership.entity;

import jakarta.annotation.Nonnull;

/**
 * Klip 이용자
 */
public interface KlipUser {

    /**
     * 카카오 이용자 아이디
     * <p>
     * 추후에는 카카오 아이디가 없는 글로벌 이용자가 있을 수 있는데, KlipMembership에서는 그런 계정은 이용할 없어서 Klip 이용자가 아닌 것으로 판정한다.
     * </p>
     */
    @Nonnull
    String getKakaoUserId();

    /**
     * Klip 계정 아이디
     */
    @Nonnull
    Long getKlipAccountId();

    @Nonnull
    String getEmail();

    @Nonnull
    String getPhone();
}
