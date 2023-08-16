package com.klipwallet.membership.adaptor.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenlinkResItem(
        @JsonProperty("link_type") String linkType,
        @JsonProperty("link_name") String linkName,
        @JsonProperty("link_id") Long linkId,
        @JsonProperty("link_image") String linkImage,
        @JsonProperty("link_description") String linkDescription
) {
}
