package com.klipwallet.membership.adaptor.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JoinOpenlinkResp(
    @JsonProperty("link_id")
    Long linkId,
    @JsonProperty("link_url")
    String linkUrl,
    @JsonProperty("user_id")
    Long userId
) {
}
