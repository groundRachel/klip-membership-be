package com.klipwallet.membership.adaptor.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LinkUserOpenlinkRes(
        @JsonProperty("link_id")
        Long linkId,
        @JsonProperty("user_id")
        Long userId
) {
}
