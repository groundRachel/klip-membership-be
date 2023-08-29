package com.klipwallet.membership.adaptor.klipdrops.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Drops(
        @JsonProperty("drops") List<Drop> drops,
        @JsonProperty("totalCount") Integer totalCount
) {
}
