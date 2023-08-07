package com.klipwallet.membership.adaptor.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetOpenlinkResponseItem(
    @JsonProperty("link_type") String linkType,
    @JsonProperty("link_name") String linkName,
    @JsonProperty("link_id") int linkId,
    @JsonProperty("link_image") String linkImage,
    @JsonProperty("link_description") String linkDescription
) {
}
