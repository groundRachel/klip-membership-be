package com.klipwallet.membership.adaptor.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LinkUserOpenlinkResp(
    @JsonProperty("link_id")
    Long linkId,
    @JsonProperty("user_id")
    Long userId
) {
}
