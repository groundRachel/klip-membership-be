package com.klipwallet.membership.adaptor.kas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetNftToken (
        @JsonProperty("tokenId")
        String tokenId,
        @JsonProperty("owner")
        String owner,
        @JsonProperty("previousOwner")
        String previousOwner,
        @JsonProperty("tokenUri")
        String tokenUri,
        @JsonProperty("transactionHash")
        String transactionHash,
        @JsonProperty("createdAt")
        int createdAt,
        @JsonProperty("updatedAt")
        int updatedAt
){
}

