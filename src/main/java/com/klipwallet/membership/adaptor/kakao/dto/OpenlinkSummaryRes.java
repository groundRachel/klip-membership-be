package com.klipwallet.membership.adaptor.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenlinkSummaryRes(
        @JsonProperty("link_id") Long linkId,
        @JsonProperty("link_url") String linkUrl
) {
}
