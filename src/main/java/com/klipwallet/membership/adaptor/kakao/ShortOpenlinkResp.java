package com.klipwallet.membership.adaptor.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ShortOpenlinkResp(
    @JsonProperty("link_id")
    Long linkId,
    @JsonProperty("link_url")
    String linkUrl
) {
}
