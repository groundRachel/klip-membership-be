package com.klipwallet.membership.adaptor.klip;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.NonNull;

import com.klipwallet.membership.entity.KlipUser;

public record KlipAccount(
        @JsonValue @NonNull Long id,
        @JsonValue @NonNull String kakaoSocialId,
        @JsonValue String email,
        @JsonValue String phone
) implements KlipUser {
    @JsonCreator
    public KlipAccount {}

    @Override
    public Long getKlipAccountId() {
        return this.id;
    }

    @Override
    public String getKakaoUserId() {
        return this.kakaoSocialId;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public String getPhone() {
        return this.phone;
    }
}
