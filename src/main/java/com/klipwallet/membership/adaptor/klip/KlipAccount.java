package com.klipwallet.membership.adaptor.klip;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

import com.klipwallet.membership.entity.KlipUser;

public record KlipAccount(
        @JsonProperty("id") @NonNull Long id,
        @JsonProperty("kakaoUserId") @NonNull String kakaoUserId,
        @JsonProperty("email") String email,
        @JsonProperty("phone") String phone
) implements KlipUser {
    @JsonCreator
    public KlipAccount {}

    @Override
    public Long getKlipAccountId() {
        return this.id;
    }

    @Override
    public String getKakaoUserId() {
        return this.kakaoUserId;
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
